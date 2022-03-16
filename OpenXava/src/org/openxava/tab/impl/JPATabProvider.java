package org.openxava.tab.impl;

import java.rmi.*;
import java.util.*;
import java.util.stream.*;

import javax.persistence.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/** 
 * An <code>ITabProvider</code> that obtain data via JPA. <p>
 *
 * @author  Javier Paniza
 */

public class JPATabProvider extends TabProviderBase {
	
	private static Log log = LogFactory.getLog(JPATabProvider.class);
	
	protected String translateCondition(String condition) {
		int i = 0;
		while (condition.contains("?")) {		
			condition = condition.replaceFirst("\\?", ":p" + (i++));
		}
		return changePropertiesByJPAProperties(condition);
	}
	
	public String toQueryField(String propertyName) {		
		return "e." + propertyName;
	}

	public String getSelectBase() {
		return changePropertiesByJPAProperties(getSelectWithEntityAndJoins());
	}
	
	public Collection<TabConverter> getConverters() {	
		return null;
	}
	
	private String getSelectWithEntityAndJoins() {
		String select = getMetaTab().getSelect();
		int i = select.indexOf("from ${");
		if (i < 0) return select; 
		int f = select.indexOf("}", i);
		StringBuffer entityAndJoins = new StringBuffer();
		entityAndJoins.append("from ");
		entityAndJoins.append(getMetaModel().getName());
		entityAndJoins.append(" e");
		
		if (hasReferences()) {
			// the tables
			
			Iterator itReferencesMappings = getEntityReferencesMappings().iterator();			
			while (itReferencesMappings.hasNext()) {
				ReferenceMapping referenceMapping = (ReferenceMapping) itReferencesMappings.next();				
				String reference = referenceMapping.getReference();			
				int idx = reference.lastIndexOf('_');				
				if (idx >= 0) {
					// In the case of reference to entity in aggregate only we will take the last reference name
					reference = reference.substring(idx + 1);
				}								 			
				entityAndJoins.append(" left join e");
				String nestedReference = (String) getEntityReferencesReferenceNames().get(referenceMapping);
				if (!Is.emptyString(nestedReference)) {					
					entityAndJoins.append(isAggregate(nestedReference)?".":"_");
					entityAndJoins.append(nestedReference);
				}				
				entityAndJoins.append(".");
				entityAndJoins.append(reference);				
				entityAndJoins.append(" e_");
				if (!Is.emptyString(nestedReference)) {					
					entityAndJoins.append(nestedReference);
					entityAndJoins.append("_");
				}				
				entityAndJoins.append(reference);
			}
		}
		
		resetEntityReferencesMappings();
		
		StringBuffer result = new StringBuffer(select);
		result.replace(i, f + 2, entityAndJoins.toString());
		return result.toString();
	}	
	
	private boolean isAggregate(String reference) {
		try {
			MetaReference ref = getMetaModel().getMetaReference(reference);					
			return ref.isAggregate();
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
	}

	private String changePropertiesByJPAProperties(String source) { 
		if (!source.contains("${")) return source;
		StringBuffer r = new StringBuffer(source);		
		int i = r.toString().indexOf("${");
		int f = 0;
		while (i >= 0) {
			f = r.toString().indexOf("}", i + 2);
			if (f < 0) break;
			String modelElement = r.substring(i + 2, f);
			String jpaElement = "e." + modelElement; // The more common case
			if (isPropertyFromCollection(modelElement)) {
				jpaElement = "__COL__[" + modelElement + "]";
			}
			else if (getMetaModel().isCalculated(modelElement)) {
				jpaElement = "0";
			}
			else if (modelElement.contains(".")) {				
				String reference = modelElement.substring(0, modelElement.lastIndexOf('.'));
				String suffix = "";
				if (modelElement.contains("[")) { // For date functions, like [month] or [year]
					String [] tokens = modelElement.split("\\[");
					modelElement = tokens[0];
					suffix = "[" + tokens[1];
				}
				if (!isAggregate(reference)) {
					if (!getMetaModel().getMetaProperty(modelElement).isKey()) {
						StringBuffer qualifiedElement = new StringBuffer(modelElement.replaceAll("\\.", "_"));
						int last = qualifiedElement.lastIndexOf("_");
						qualifiedElement.replace(last, last + 1, ".");
						jpaElement = "e_" + qualifiedElement + suffix;
					}
				}
			}						
			else if (Strings.isModelName(modelElement)) { 
				jpaElement = modelElement;
			}			
			r.replace(i, f + 1, jpaElement);
			i = r.toString().indexOf("${");
		}
		return r.toString();
	}
	
	/** @since 6.4 */
	protected String toSearchByCollectionMemberSelect(String select) { 
		if (!select.contains("__COL__[")) return select;
		String originalSelect = select;
		String firstKey = getMetaModel().getAllKeyPropertiesNames().iterator().next().toString();
		StringBuffer ins = new StringBuffer();
		int i=0;
		for (String qualifiedMember: collectCollectionMember(select)) {
			String [] memberTokens = qualifiedMember.split("\\.", 2);
			String collection = memberTokens[0];
			String member = memberTokens[1];
			if (StringUtils.countMatches(select, "__COL__[" + qualifiedMember + "]") > 1) { 
				ins.append(", in (e.");
				ins.append(collection);
				ins.append(") d");
				ins.append(i);
				String matchingLabel = Strings.firstUpper(XavaResources.getString("matching", Labels.get(collection)).toLowerCase());
				select = select.replaceFirst("__COL__\\[" + qualifiedMember.replace(".", "\\.") + "\\]", "concat('" + matchingLabel + ": ', count(e." + firstKey + "))");				
				select = select.replace("__COL__[" + qualifiedMember + "]", "d" + i + "." + member);  
			}
			else {
				select = select.replace("__COL__[" + qualifiedMember + "]", "'...'");
			}
			i++;
		}
		
		if (ins.length() > 0) {
			select = select.replace(" from " + getMetaModel().getName()	+ " e", " from " + getMetaModel().getName()	+ " e " + ins);
			String groupByColumns = extractColumnsFromSelect(originalSelect);
			select = insertGroupBy(select, groupByColumns);	
		}
		
		return select;
	}
	
	private String insertGroupBy(String select, String groupByColumns) { 
		if (select.contains(" order by ")) {
			return select.replace(" order by ", " group by " + groupByColumns + " order by ");
		}
		return select + " group by " + groupByColumns;
	}

	private String extractColumnsFromSelect(String select) { 
		int f = select.indexOf("from");
		String columns = select.substring(7, f);
		return Arrays.stream(columns.split(","))
			.map(String::trim)
			.filter(c -> c.startsWith("e.") || c.startsWith("e_"))
			.distinct()
			.collect( Collectors.joining( "," ) );
	}

	private Collection<String> collectCollectionMember(String select) {
		Collection<String> result = new ArrayList<>();
		int i = select.indexOf("__COL__[");
		while (i >= 0) {
			int f = select.indexOf(']', i + 8);
			result.add(select.substring(i + 8, f));
			i = select.indexOf("__COL__[", f);
		}
		return result;
	}
	
	private boolean isPropertyFromCollection(String modelElement) { 
		if (!modelElement.contains(".")) return false;				
		String collection = modelElement.substring(0, modelElement.indexOf('.'));
		return getMetaModel().containsMetaCollection(collection);
	}

	public DataChunk nextChunk() throws RemoteException {
		if (getSelect() == null || isEOF()) { // search not called yet
			return new DataChunk(Collections.EMPTY_LIST, true, getCurrent()); // Empty
		}		
		try {
			// If you change this code verify that Cards editor do the minimum amount of SELECTs on scroll loading, 
			// testing edge cases, such as 119, 120 and 121 if chunk size is 120 
			List data = nextBlock();			
			setCurrent(getCurrent() + data.size());			
			setEOF(data.size() <= getChunkSize());
			if (!isEOF()) {
				// We remove the one we add to know if EOF
				data.remove(data.size() - 1);
				setCurrent(getCurrent() - 1);
			}
			return new DataChunk(data, isEOF(), getCurrent());
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("select_error", getSelect()), ex);
			throw new RemoteException(XavaResources.getString("select_error", getSelect()));
		}
	}
		
	private List<Object []> nextBlock() {		
		if (keyHasNulls()) return Collections.EMPTY_LIST; // Because some databases (like Informix) have problems setting nulls
				
		Query query = XPersistence.getManager().createQuery(getSelect()); 
		// Fill key values
		StringBuffer message =
			new StringBuffer("[JPATabProvider.nextBlock] ");
		message.append(XavaResources.getString("executing_select", getSelect()));		
		
		Object [] key = getKey(); 
		for (int i = 0; i < key.length; i++) {
			query.setParameter("p" + i, key[i]);
			message.append(key[i]);
			if (i < key.length - 1)
				message.append(", ");
		}
		log.debug(message);
		
		query.setMaxResults(getChunkSize()==Integer.MAX_VALUE?Integer.MAX_VALUE:getChunkSize() + 1); // One more to know if EOF
		query.setFirstResult(getCurrent());
		return query.getResultList();						
	}

	protected Number executeNumberSelect(String select, String errorId) {
		if (select == null) return Integer.MAX_VALUE; 
		if (keyHasNulls()) return 0;						
		try {			
			Query query = XPersistence.getManager().createQuery(select);
			Object [] key = getKey();
			for (int i = 0; i < key.length; i++) {
				query.setParameter("p" + i, key[i]);				
			}			
			return (Number) query.getSingleResult();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException(errorId);
		}
	}

	public boolean usesConverters() {
		return false;
	}

	protected String translateProperty(String property) {
		return "e." + property;
	}

	protected String noValueInSelect() { 
		return "''";
	}
	
	/** @since 6.2.1 */
	protected void addEntityReferenceMapping(Collection<ReferenceMapping> entityReferencesMappings, 
		Map<ReferenceMapping, String> entityReferencesReferenceNames, ReferenceMapping referenceMapping, String parentReference) 
	{
		if (entityReferencesMappings.contains(referenceMapping)) referenceMapping = referenceMapping.clone();  
		entityReferencesReferenceNames.put(referenceMapping, parentReference); 
		entityReferencesMappings.add(referenceMapping);
	}
	
}
