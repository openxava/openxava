package org.openxava.tab.impl;

import java.rmi.*;
import java.util.*;

import javax.persistence.*;

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
			if (getMetaModel().isCalculated(modelElement)) {
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
