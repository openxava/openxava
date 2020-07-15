package org.openxava.tab.impl;

import java.rmi.*;
import java.util.*;
import java.util.stream.*;

import javax.ejb.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza 
 */
abstract public class TabProviderBase implements ITabProvider, java.io.Serializable {
	
	private static Log log = LogFactory.getLog(TabProviderBase.class);
	private static final int DEFAULT_CHUNK_SIZE = 50;	

	private Collection<ReferenceMapping> entityReferencesMappings; 
	private Map<ReferenceMapping, String>  entityReferencesReferenceNames;  	
	private String select; // Select ... from ...
	private String selectSize;
	private Object[] key;
	private int chunkSize = DEFAULT_CHUNK_SIZE;
	private int current;  
	private boolean eof = true;
	private MetaTab metaTab;
		
		
	abstract protected String translateProperty(String property);
	abstract protected String translateCondition(String condition);
	abstract protected Number executeNumberSelect(String select, String errorId);
	/** @since 6.4 */
	abstract protected String toSearchByCollectionMemberSelect(String select); 
	
	public void setMetaTab(MetaTab metaTab) {
		this.metaTab = metaTab;
	}
	
	protected MetaModel getMetaModel() {
		return metaTab.getMetaModel();
	}
	
	protected MetaTab getMetaTab() {
		return metaTab;
	}	
		
	public void search(String condition, Object key) throws FinderException, RemoteException {
		current = 0;
		eof = false;
		this.key = toArray(key);		
		condition = condition == null ? "" : condition.trim();
		select = translateCondition(condition);
		select = toGroupBySelect(select);
		select = toSearchByCollectionMemberSelect(select); 
		selectSize = createSizeSelect(select); 
	}
							
	private String toGroupBySelect(String select) { 
		if (!select.contains(" group by ")) return select;
		String groupByProperty = Strings.lastToken(removeOrder(select)).replace("[month]", "").replace("[year]", "");
		
		int endIndex = select.indexOf(" from ");
		String properties = select.substring(7, endIndex);
		StringTokenizer st = new StringTokenizer(properties, ",");
		
		int keysCount = getMetaModel().getAllMetaPropertiesKey().size();
		for (int i=0; i<keysCount && st.hasMoreTokens(); i++) {
			String original = st.nextToken().trim();
			select = select.replaceFirst(original, noValueInSelect()); 
		}
		while (st.hasMoreTokens()) {
			String original = st.nextToken().trim();
			if (original.equals(groupByProperty)) continue;
			if (original.equals("e." + Tab.GROUP_COUNT_PROPERTY)) {  
				select = select.replace("e." + Tab.GROUP_COUNT_PROPERTY, "count(*)");
			}
			else {
				String alias = "sum_" + original.replace(".", "_");
				select = select.replaceFirst(original, "sum(" + original + ") as " + alias);
				select = select.replace(original + " asc", alias + " asc");
				select = select.replace(original + " desc", alias + " desc");
			} 
		}
		String orderBy = null;
		if (select.contains(" order by " )) {
			String [] selectTokens = select.split(" order by ", 2);
			select = selectTokens[0];
			orderBy = selectTokens[1]; 
		}
		if (select.endsWith("[month]")) { 
			select = select.replace(groupByProperty + "[month]", "year(" + groupByProperty + "), month(" + groupByProperty + ")");
			select = select.replaceFirst(groupByProperty, "concat(year(" + groupByProperty + "), '/',month(" + groupByProperty + "))");
			if (orderBy != null) orderBy = orderBy.replaceFirst(groupByProperty, "concat(year(" + groupByProperty + "), '/',month(" + groupByProperty + "))"); 
		}
		else if (select.endsWith("[year]")) { 
			select = select.replace(groupByProperty + "[year]", "year(" + groupByProperty + ")");
			select = select.replaceFirst(groupByProperty, "year(" + groupByProperty + ")");
			if (orderBy != null) orderBy = orderBy.replaceFirst(groupByProperty, "year(" + groupByProperty + ")"); 
		} 
		
		if (orderBy != null) select = select + " order by " + orderBy; 

		return select;
	}
	
	private String removeOrder(String select) { 
		int idx = select.indexOf(" order by ");
		if (idx < 0) return select;
		return select.substring(0, idx);
	}
	
	/** Size of chunk returned by {@link #nextChunk}. */
	public int getChunkSize() {
		return chunkSize;
	}
	
				
	/** Size of chunk returned by {@link #nextChunk}. */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	

	protected boolean keyHasNulls() {
		if (key == null) return true;
		for (int i=0; i < key.length; i++) {
			if (key[i] == null) return true;
		}
		return false;
	}
	
	protected Object[] getKey() {
		return key;
	}
	
	abstract protected String noValueInSelect(); 
	
	/**
	 * Return an array from the sent object.
	 * Si obj == null return Object[0]
	 */
	private Object[] toArray(Object obj) { 
		if (obj == null)
			return new Object[0];
		if (obj instanceof Object[]) {
			return (Object[]) obj;
		}
		else {
			Object[] rs = { obj };
			return rs;
		}
	}
	public int getCurrent() {
		return current;
	}

	public void setCurrent(int i) {
		current = i;
	}
	public int getResultSize() throws RemoteException { 
		return executeNumberSelect(this.selectSize, "tab_result_size_error").intValue();
	}
	
	public Number getSum(String property) {
		return executeNumberSelect(createSumSelect(property), "column_sum_error");
	}		
	
	private String createSizeSelect(String select) {
		if (select == null) return null;
		if (select.contains(" group by ")) return null;
		String selectUpperCase = Strings.changeSeparatorsBySpaces(select.toUpperCase());
		int iniFrom = selectUpperCase.indexOf(" FROM ");
		int end = selectUpperCase.indexOf("ORDER BY ");
		StringBuffer sb = new StringBuffer("SELECT COUNT(*) ");
		if (end < 0) sb.append(select.substring(iniFrom));
		else sb.append(select.substring(iniFrom, end - 1));
		return sb.toString();
	}
	
	private String createSumSelect(String property) { 
		if (select == null) return null;	
		String selectUpperCase = Strings.changeSeparatorsBySpaces(select.toUpperCase());
		int iniFrom = selectUpperCase.indexOf(" FROM ");
		int end = selectUpperCase.indexOf("GROUP BY ");
		if (end < 0) end = selectUpperCase.indexOf("ORDER BY ");
		StringBuffer sb = new StringBuffer();
		if (Tab.GROUP_COUNT_PROPERTY.equals(property)) {
			sb = new StringBuffer("SELECT COUNT(*) ");
		}
		else {
			sb = new StringBuffer("SELECT SUM(");	
			sb.append(translateProperty(property));  		
			sb.append(") ");
		} 
		if (end < 0) sb.append(select.substring(iniFrom));
		else sb.append(select.substring(iniFrom, end - 1));
		
		return sb.toString();
	}
	
	public void reset() throws RemoteException {
		current = 0;
		eof = false;
	}
	
	protected String getSelect() {
		return select;
	}
	
	protected boolean isEOF() {
		return eof;
	}
	
	protected void setEOF(boolean eof) {
		this.eof = eof;
	}
	
	protected void resetEntityReferencesMappings() {
		entityReferencesMappings = null;
		entityReferencesReferenceNames = null; 		
	}

	protected boolean hasReferences() throws XavaException {
		return !getEntityReferencesMappings().isEmpty();
	}
	
	protected Map<ReferenceMapping, String> getEntityReferencesReferenceNames() { 
		return entityReferencesReferenceNames;
	}
	
	protected Collection<ReferenceMapping> getEntityReferencesMappings() throws XavaException { 	
		if (entityReferencesMappings == null) {
			entityReferencesMappings = new ArrayList<>(); 
			entityReferencesReferenceNames = new HashMap<>(); 
			for (Iterator itProperties = getMetaTab().getPropertiesNames().iterator(); itProperties.hasNext();) {
				String property = (String) itProperties.next();		
				fillEntityReferencesMappings(entityReferencesMappings, property, getMetaModel(), "", ""); 
			}
			for (Iterator itProperties = getMetaTab().getHiddenPropertiesNames().iterator(); itProperties.hasNext();) {
				String property = (String) itProperties.next();				
				fillEntityReferencesMappings(entityReferencesMappings, property, getMetaModel(), "", ""); 
			}						
		}		
		return entityReferencesMappings;
	}
	
	private void fillEntityReferencesMappings(Collection<ReferenceMapping> result, String property, MetaModel metaModel, String parentReference, String aggregatePrefix) throws XavaException { 
		fillEntityReferencesMappings(result, property, metaModel, parentReference, aggregatePrefix, null);
	}
	
	private void fillEntityReferencesMappings(Collection<ReferenceMapping> result, String property, MetaModel metaModel, String parentReference, String aggregatePrefix, MetaModel parentMetaModel) throws XavaException {		
		int idx = property.indexOf('.');
		if (idx >= 0) {
			String referenceName = property.substring(0, idx);	
			if (!metaModel.containsMetaReference(referenceName)) return; 
			MetaReference ref = metaModel.getMetaReference(referenceName);
			String memberName = property.substring(idx + 1);
			boolean hasMoreLevels = memberName.indexOf('.') >= 0;
			if (!ref.isAggregate()) {												
				if (hasMoreLevels || !ref.getMetaModelReferenced().isKey(memberName)) {
					ReferenceMapping rm = null;
					if (Is.emptyString(aggregatePrefix)) {
						rm = metaModel.getMapping().getReferenceMapping(referenceName);
					}
					else {
						if (parentMetaModel == null) parentMetaModel = metaModel.getMetaModelContainer(); 
						rm = parentMetaModel.getMapping().getReferenceMapping(aggregatePrefix + referenceName);
					}
					addEntityReferenceMapping(entityReferencesMappings, entityReferencesReferenceNames, rm, parentReference);
				}
			}			
			 
			if (hasMoreLevels) {
				MetaModel refModel = null;
				if (ref.isAggregate()) {
					refModel = metaModel.getMetaComponent().getMetaAggregate(ref.getReferencedModelName());
					fillEntityReferencesMappings(result, memberName, refModel, concat(parentReference, referenceName), referenceName + "_", metaModel);
				}
				else {
					refModel = MetaComponent.get(ref.getReferencedModelName()).getMetaEntity();
					fillEntityReferencesMappings(result, memberName, refModel, concat(parentReference, referenceName), ""); 
				}
			}
		}		
	}
	
	/** @since 6.2.1 */
	abstract protected void addEntityReferenceMapping(Collection<ReferenceMapping> entityReferencesMappings, 
			Map<ReferenceMapping, String> entityReferencesReferenceNames, ReferenceMapping referenceMapping, String parentReference); 
	
	private String concat(String parentReference, String referenceName) { 
		if (Is.emptyString(parentReference)) return referenceName; 
		return parentReference + "_" + referenceName;
	}	
	

}
