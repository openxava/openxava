package org.openxava.model.impl;

import java.io.*;
import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.logging.*;
import org.hibernate.Hibernate;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * Abstract class for create <i>Persistence Providers</i> based in POJOs. <p>
 * 
 * @author Javier Paniza
 */
abstract public class POJOPersistenceProviderBase implements IPersistenceProvider {
	
	private static Log log = LogFactory.getLog(POJOPersistenceProviderBase.class);
	
	private static Random aggregateNumberGenerator = new Random(); 	
	
	
	/**
	 * Return the object associated to the sent key.
	 */
	abstract protected Object find(Class pojoClass, Serializable key);
		
	/**
	 * Marks the object as persistent. <p> 
	 */
	abstract protected void persist(Object object);
	
	/**
	 * Creates a query, it can be Hibernate query or JPA query. <p>
	 */
	abstract protected Object createQuery(String query);
	
	/**
	 * Sets the parameter to the indicated query. <p>
	 * 
	 * The query is of the type returned by <code>createQuery</code> method.<br>
	 */
	abstract protected void setParameterToQuery(Object query, String name, Object value);
	
	/**
	 * Returns the unique result of the sent query. <p>
	 * 
	 * It does not fail if there more than one match, in this case must returns
	 * the first one.<br>
	 *  
	 * @param query  Of the type returned by <code>createQuery</code> method.
	 * @return Null if not result.
	 */
	abstract protected Object getUniqueResult(Object query);
		

	public Object find(MetaModel metaModel, Map keyValues) throws FinderException {
		return find(metaModel, keyValues, true);
	}
	
	protected Object find(MetaModel metaModel, Map keyValues, boolean useQueryForFind) throws FinderException {
		try {							
			Object key = null;		
			// The second question (metaModel.getMetaPropertiesKey().isEmpty())  
			// is for the case of one key reference with only one column in it, 
			// this case must be treated as multiple key
			boolean multipleKey = metaModel.getAllKeyPropertiesNames().size() > 1 || metaModel.getMetaPropertiesKey().isEmpty();
			if (!multipleKey) {
				String keyPropertyName = (String) metaModel.getKeyPropertiesNames().iterator().next();
				key = keyValues.get(keyPropertyName);
				if (key instanceof Number) { // Numbers can produce conversion problems. For example, NUMERIC to java.lang.Integer
					key = convertSingleKeyType(metaModel, keyPropertyName, key);
				}
			}
			else {
				if (useQueryForFind) {
					return findByKeyUsingQuery(metaModel, keyValues);
				}
				else {
					key = getKey(metaModel, keyValues);
				}
			}
			if (key == null) {
				throw new ObjectNotFoundException(XavaResources.getString(
						"object_with_key_not_found", metaModel.getName(), keyValues));
			}						
			Object result = find(metaModel.getPOJOClass(), (Serializable) key);				
			if (result == null) {
				throw new ObjectNotFoundException(XavaResources.getString(
						"object_with_key_not_found", metaModel.getName(), keyValues));
			}
			return result;
		}
		catch (FinderException ex) {
			throw ex;
		}
		catch (RuntimeException ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new PersistenceProviderException( 
					XavaResources.getString("find_error", metaModel.getName()));
		}
	}
	
	public Object find(MetaModel metaModel, Object key) throws FinderException { 
		try {														
			Object result = find(metaModel.getPOJOClass(), (Serializable) key);			
			if (result == null) {
				throw new ObjectNotFoundException(XavaResources.getString(
						"object_with_key_not_found", metaModel.getName(), key));
			}			
			return result;
		}
		catch (FinderException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new PersistenceProviderException( 
					XavaResources.getString("find_error", metaModel.getName()));
		}
	}
	
	public void moveCollectionElement(MetaModel metaModel, Map keyValues, String collectionName, int from, int to) throws FinderException, XavaException {
		try {
			Object container = find(metaModel, keyValues);
			PropertiesManager pm = new PropertiesManager(container); 
			List elements = (List) pm.executeGet(collectionName);
			XCollections.move(elements, from, to);
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("move_collection_element_error", collectionName), ex); 
			throw new XavaException("move_collection_element_error", collectionName); 
		}
	}
	
	public IPropertiesContainer toPropertiesContainer(MetaModel metaModel,
			Object o) throws XavaException {
		return new POJOPropertiesContainerAdapter(o);
	}

	public Object create(MetaModel metaModel, Map values)
			throws CreateException, ValidationException, XavaException {		
		try {			
			find(metaModel, values);			
			throw new DuplicateKeyException(XavaResources.getString("no_create_exists", metaModel.getName())); 
		}
		catch (DuplicateKeyException ex) {
			throw ex;
		}
		catch (Exception ex) {						
			// If it does not exist then continue
		}
		Object object = null;
		try {
			object = metaModel.getPOJOClass().newInstance();
			PropertiesManager mp = new PropertiesManager(object);
			removeCalculatedOnCreateValues(metaModel, values);
			mp.executeSets(values);					
			persist(object);			
			return object;
		}
		catch (RuntimeException ex) {
			throwValidationExceptionIfNeeded(ex); 
			throw ex;
		}
		catch (Exception ex) {
			throwValidationExceptionIfNeeded(ex); 
			log.error(ex.getMessage(), ex);
			throw new CreateException(XavaResources.getString(
					"create_persistent_error", metaModel.getName(), ex.getMessage()));
		}
	}
	
	private void throwValidationExceptionIfNeeded(Exception ex) throws ValidationException { 
		if (ex.getCause() instanceof ValidationException) {
			throw (ValidationException) ex.getCause();
		}	
		if (ex.getCause() instanceof javax.validation.ValidationException) {
			throw (javax.validation.ValidationException) ex.getCause();				
		}		
	}
			
	private void removeCalculatedOnCreateValues(MetaModel metaModel, Map values) throws XavaException { 
		for (Iterator it = metaModel.getMetaPropertiesKey().iterator(); it.hasNext();) {
			MetaProperty p = (MetaProperty) it.next();
			if (p.hasCalculatorDefaultValueOnCreate()) {
				values.remove(p.getName());
			}
		}		
	}

	public Object getKey(MetaModel metaModel, Map keyValues) throws XavaException {
		keyValues = Maps.plainToTree(keyValues);
		keyValues = metaModel.extractKeyValuesFlattenEmbeddedIds(keyValues);
		return getObject(metaModel.getPOJOKeyClass(), keyValues, "key_for_pojo_error"); 
	}
	
	public Object getContainer(MetaModel metaModel, Map containerKeyValues) throws XavaException {
		try {
			return find(metaModel.getMetaModelContainer(), containerKeyValues);
		} 
		catch (FinderException ex) {
			log.warn(ex.getMessage(), ex);
			throw new XavaException("container_for_pojo_error");
		}
	}
	
	private Object getObject(Class modelClass, Map values, String errorId) throws XavaException {
		try {
			Object key = modelClass.newInstance();
			PropertiesManager pm = new PropertiesManager(key);
			pm.executeSets(Maps.plainToTree(values));
			return key;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException(errorId);
		}
	}
		
	public Map keyToMap(MetaModel metaModel, Object key) throws XavaException {
		return metaModel.toKeyMap(key);
	}
	
	private Object convertSingleKeyType(MetaModel metaModel, String property, Object value) {
		try {
			Class modelClass = metaModel.getPOJOClass();
			Object key = modelClass.newInstance();
			PropertiesManager pm = new PropertiesManager(key);
			pm.executeSet(property, value);
			return pm.executeGet(property);
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("key_type_conversion_warning", property, value, metaModel.getName()), ex);
			return value;			
		}
	}

	public Object createAggregate(MetaModel metaModel, Map values, MetaModel metaModelContainer, Object containerModel, int number) throws CreateException, ValidationException, RemoteException, XavaException {
		// The next two lines use Hibernate. At the momment for Hibernate and EJB3 
		// In order to support a EJB3 no hibernate implementations we will need to change them
		org.openxava.hibernate.impl.DefaultValueIdentifierGenerator.setCurrentContainerKey(containerModel);
		if (number == 0) number = aggregateNumberGenerator.nextInt();
		org.openxava.hibernate.impl.DefaultValueIdentifierGenerator.setCurrentCounter(number);
		return create(metaModel, values);
	}
	
	public Object findByAnyProperty(MetaModel metaModel, Map keyValues) throws ObjectNotFoundException, FinderException, XavaException {		
		return findUsingQuery(metaModel, Maps.treeToPlain(keyValues), false);
	}
	
	public Object findByKeyUsingQuery(MetaModel metaModel, Map keyValues) throws ObjectNotFoundException, FinderException, XavaException {
		Map allValues = Maps.treeToPlain(keyValues);
		Map keys = new HashMap();		
		for (Iterator it = metaModel.getAllKeyPropertiesNames().iterator(); it.hasNext(); ) {
			String property = (String) it.next();
			keys.put(property, allValues.get(property));
		}
		return findUsingQuery(metaModel, keys, true);
	}
	
	private Object findUsingQuery(MetaModel metaModel, Map keyValues, boolean includeEmptyValues) throws ObjectNotFoundException, FinderException, XavaException {		
		StringBuffer queryString = new StringBuffer();
		queryString.append("from ");
		queryString.append(metaModel.getName());
		queryString.append(" o");
		boolean hasCondition = false;	
		Collection values = new ArrayList();
		for (Iterator it=keyValues.entrySet().iterator(); it.hasNext();) {
			Map.Entry en = (Map.Entry) it.next();
			if (includeEmptyValues || !Is.empty(en.getValue())) {
				if (!hasToIncludePropertyInCondition(metaModel, (String) en.getKey())) continue;
				if (!hasCondition) {
					queryString.append(" where ");
					hasCondition = true;
				}
				else {
					queryString.append(" and ");				
				}			
				queryString.append("o.");
				queryString.append(en.getKey());
				en.setValue(convert(metaModel, (String) en.getKey(), en.getValue())); 				
				queryString.append(en.getValue() instanceof String && ((String) en.getValue()).endsWith("%")?" like ":" = ");
				queryString.append(":");
				queryString.append(Strings.change((String)en.getKey(), ".", "_")); 
				values.add(en);
			}
		}
		
		if (!hasCondition) { 
			throw new ObjectNotFoundException(XavaResources.getString("object_by_any_property_not_found", values));
		}
										
		Object query = createQuery(queryString.toString());	
		for (Iterator it=values.iterator(); it.hasNext();) {
			Map.Entry en = (Map.Entry) it.next();
			String name = (String) en.getKey();			
			Object value = en.getValue();			
			setParameterToQuery(query, Strings.change(name, ".", "_"), value);			
		}		
		Object result = getUniqueResult(query);		
		if (result == null) {
			throw new ObjectNotFoundException(XavaResources.getString("object_by_any_property_not_found", values));
		}				
		refreshIfManaged(result); 
		return result;
	}
		
	private Object convert(MetaModel metaModel, String name, Object value) throws XavaException {		
		MetaProperty property = metaModel.getMetaProperty(name);
		PropertyMapping mapping = property.getMapping();
		if (property.hasValidValues() && !property.isNumber()) { // Java 5 enum			
			if (value instanceof Number) {
				// We have the ordinal, then we transform it in an Enum object
				value = property.getValidValue(((Number) value).intValue());
			}
			return value;
		}				
		
		Object result = value;

		if (mapping != null && mapping.hasConverter() && !metaModel.isAnnotatedEJB3()) {			
			result = mapping.getConverter().toDB(result);
		}
		else if (result instanceof java.math.BigDecimal) {
			// Sometimes programmers send BigDecimal directly as arguments for searching
			// even if the properties are int. Usually because they obtain data
			// from raw JDBC and in the DB the column is NUMERIC or DECIMAL
			// This code is for support this case
			if (int.class.isAssignableFrom(property.getType())) { 
				result = new Integer(((Number) result).intValue()); 
			} 
			else if (long.class.isAssignableFrom(property.getType())) { 
				result = new Long(((Number) result).longValue()); 
			}  
		}
		
		return result;
	}

	private boolean hasToIncludePropertyInCondition(MetaModel metaModel, String property) throws XavaException {
		try {
			if (property.indexOf('.') >= 0) return true; // We include all properties reference. They should be only key properties
			MetaProperty metaProperty = metaModel.getMetaProperty(property);
			if (metaProperty.isTransient()) return false;
			return !metaModel.getMapping().getPropertyMapping(property).hasMultipleConverter();
		}
		catch (ElementNotFoundException ex) {			
			return false; // Maybe a view property  
		}
	}
	
	/**
	 * @since 5.6
	 */
	public String getModelName(Object modelObject) { 
		if (modelObject == null) return null;
		return Hibernate.getClass(modelObject).getSimpleName();
	}

}
