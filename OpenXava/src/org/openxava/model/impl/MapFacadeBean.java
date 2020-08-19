package org.openxava.model.impl;

import java.lang.reflect.*;
import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.collections.*;
import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.validators.*;
import org.openxava.validators.hibernate.*;
import org.openxava.validators.meta.*;


/**
 * Implement the logic of MapFacade. <p>
 * 
 * 
 * @author Javier Paniza
 */

public class MapFacadeBean implements IMapFacadeImpl, SessionBean {
	
	private static Log log = LogFactory.getLog(MapFacadeBean.class);
	private static javax.validation.ValidatorFactory validatorFactory; 
	private javax.ejb.SessionContext sessionContext = null;
	private final static long serialVersionUID = 3206093459760846163L;
	
	
	
	public Object create(UserInfo userInfo, String modelName, Map values)
		throws CreateException, XavaException, ValidationException, RemoteException {
		Users.setCurrentUserInfo(userInfo);
		values = Maps.recursiveClone(values);
		MetaModel metaModel = getMetaModel(modelName); 
		try {
			beginTransaction(metaModel); 		
			Object result = create(metaModel, values, null, null, null, 0, true); 
			commitTransaction(metaModel);			
			return result;
		} 
		catch (CreateException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) {
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}		
	}	
		
	public void commit(UserInfo userInfo) {
		Users.setCurrentUserInfo(userInfo);
		for (MetaComponent component: MetaComponent.getAllLoaded()) {
			getPersistenceProvider(component.getMetaEntity()).commit(); 
		}
	}

	private void commitTransaction(MetaModel metaModel) {		
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) {
			getPersistenceProvider(metaModel).commit(); 
		}
		else {
			getPersistenceProvider(metaModel).flush();
		}
		HibernateValidatorInhibitor.setInhibited(false); 
	}
	
	private void beginTransaction(MetaModel metaModel) {
		HibernateValidatorInhibitor.setInhibited(true); 
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) {
			getPersistenceProvider(metaModel).begin();  
		}		
	}	
	
	private void beginTransactionForAddCollectionElement(MetaModel metaModel) {
		HibernateValidatorInhibitor.setInhibited(true);
		getPersistenceProvider(metaModel).begin();
	}
	
	private void commitTransactionForAddCollectionElement(MetaModel metaModel) {
		getPersistenceProvider(metaModel).commit();
		HibernateValidatorInhibitor.setInhibited(false);		
	}
	
	public Map getValues(
			UserInfo userInfo, 
			String modelName,
			Map keyValues,
			Map membersNames)
			throws FinderException, XavaException, RemoteException {		
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 
		membersNames = Maps.recursiveClone(membersNames);
		MetaModel metaModel = getMetaModel(modelName);
		keyValues = extractKeyValues(metaModel, keyValues); 
		try {		
			beginTransaction(metaModel);
			Map result = getValuesImpl(metaModel, keyValues, membersNames);
			AccessTracker.consulted(modelName, keyValues); 
			commitTransaction(metaModel);			
			return result;
		} 
		catch (ObjectNotFoundException ex) {
			commitTransaction(metaModel); 
			throw ex;
		}
		catch (FinderException ex) {
			log.error(ex.getMessage(), ex);
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	private Map extractKeyValues(MetaModel metaModel, Map values) {  
		Iterator it = values.keySet().iterator();
		Map result = new HashMap();
		while (it.hasNext()) {
			String name = (String) it.next();
			if (metaModel.isKey(name)) {
				result.put(name, values.get(name));
			}
		}
		return result;
	}	

	public Map getValuesByAnyProperty( 
			UserInfo userInfo, 
			String modelName,
			Map searchingValues,
			Map membersNames)
			throws FinderException, XavaException, RemoteException {		
		Users.setCurrentUserInfo(userInfo);
		searchingValues = Maps.recursiveClone(searchingValues); 
		membersNames = Maps.recursiveClone(membersNames);
		MetaModel metaModel = getMetaModel(modelName); 
		try {			
			beginTransaction(metaModel);
			Map result = getValuesByAnyPropertyImpl(metaModel, searchingValues, membersNames);			
			commitTransaction(metaModel);			
			return result;
		} 
		catch (ObjectNotFoundException ex) {
			commitTransaction(metaModel); 
			throw ex;
		}
		catch (FinderException ex) {
			log.error(ex.getMessage(), ex);
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	
	public void delete(UserInfo userInfo, String modelName, Map keyValues)
		throws RemoveException, ValidationException, XavaException, RemoteException 
	{		
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 		
		MetaModel metaModel = getMetaModel(modelName); 
		try {
			beginTransaction(metaModel); 
			remove(metaModel, keyValues);
			commitTransaction(metaModel); 
		}
		catch (RemoveException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	public void setValues(UserInfo userInfo, String modelName, Map keyValues, Map values) 
		throws FinderException, ValidationException, XavaException, RemoteException  
	{							
		setValues(userInfo, modelName, keyValues, values, true);
	}
	
	public void setValuesNotTracking(UserInfo userInfo, String modelName, Map keyValues, Map values) 
		throws FinderException, ValidationException, XavaException, RemoteException  
	{							
		setValues(userInfo, modelName, keyValues, values, false);
	}
	
	private void setValues(UserInfo userInfo, String modelName, Map keyValues, Map values, boolean tracking) 
		throws FinderException, ValidationException, XavaException, RemoteException  
	{							
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 
		values = Maps.recursiveClone(values); 		
		MetaModel metaModel = getMetaModel(modelName); 
		try {
			beginTransaction(metaModel); 
			setValues(metaModel, keyValues, values, true, tracking, true); 
			commitTransaction(metaModel); 
		}
		catch (FinderException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	public Object findEntity(UserInfo userInfo, String modelName, Map keyValues)
		throws FinderException, RemoteException {
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 			
		return findEntity(modelName, keyValues);
	}
	
	public Map createReturningValues(UserInfo userInfo, String modelName, Map values) 
		throws CreateException, XavaException, ValidationException, RemoteException {
		Users.setCurrentUserInfo(userInfo);
		values = Maps.recursiveClone(values);
		MetaModel metaModel = getMetaModel(modelName); 
		try {
			beginTransaction(metaModel);
			Map result = createReturningValues(metaModel, values);
			commitTransaction(metaModel);			
			return result;
		}	
		catch (CreateException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}		
	}
	
	private Map createReturningKey(UserInfo userInfo, String modelName, Map values, boolean validateCollections)  
		throws CreateException, XavaException, ValidationException, RemoteException {
		Users.setCurrentUserInfo(userInfo);
		values = Maps.recursiveClone(values);
		MetaModel metaModel = getMetaModel(modelName); 
		try {		
			beginTransaction(metaModel);
			Map result = createReturningKey(metaModel, values, validateCollections);
			commitTransaction(metaModel);			
			return result;
		}	
		catch (CreateException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	public Map createReturningKey(UserInfo userInfo, String modelName, Map values) 
		throws CreateException, XavaException, ValidationException, RemoteException {
		return createReturningKey(userInfo, modelName, values, true);
	}

	
	public Map createNotValidatingCollections(UserInfo userInfo, String modelName, Map values)  
		throws CreateException, XavaException, ValidationException, RemoteException {
		return createReturningKey(userInfo, modelName, values, false);
	}

	
	private Object createAggregate(UserInfo userInfo, String modelName, Map containerKeyValues, String collectionName, int counter, Map values)  
		throws CreateException,ValidationException, XavaException, RemoteException   
	{		
		Users.setCurrentUserInfo(userInfo);
		containerKeyValues = Maps.recursiveClone(containerKeyValues);
		values = Maps.recursiveClone(values);
		MetaModel metaModel = getMetaModel(modelName);
		MetaModel metaModelContainer = getMetaModelContainer(metaModel, modelName);  
		try {		
			beginTransaction(metaModel);	
			Object result = createAggregate(metaModel, metaModelContainer, containerKeyValues, collectionName, counter, values); 
			commitTransaction(metaModel);			
			return result;
		}	
		catch (CreateException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}

	private MetaModel getMetaModelContainer(MetaModel metaModel, String modelName) {
		String containerModelName = metaModel.getContainerModelName();
		if (!Is.emptyString(containerModelName)) return metaModel.getMetaModelContainer();
		if (modelName.contains(".")) {
			containerModelName = modelName.split("\\.")[0];
		}
		return MetaComponent.get(containerModelName).getMetaEntity();
	}
	
	public Object createAggregate(UserInfo userInfo, String modelName, Map containerKeyValues, String collectionName, Map values)  
		throws CreateException,ValidationException, XavaException, RemoteException   
	{
		return createAggregate(userInfo, modelName, containerKeyValues, collectionName, -1, values);
	}


	public Object createAggregate(UserInfo userInfo, String modelName, Map containerKeyValues, int counter, Map values)  
		throws CreateException,ValidationException, XavaException, RemoteException 
	{
		return createAggregate(userInfo, modelName, containerKeyValues, null, counter, values);
	}
	
	public Object createAggregate(UserInfo userInfo, String modelName, Object container, int counter, Map values)  
		throws CreateException,ValidationException, XavaException, RemoteException
	{		
		Users.setCurrentUserInfo(userInfo);
		values = Maps.recursiveClone(values);
		MetaModel metaModel = getMetaModel(modelName); 
		try {		
			beginTransaction(metaModel);
			MetaModel metaModelContainer = getMetaModelContainer(metaModel, modelName);  
			Object result = createAggregate(metaModel, metaModelContainer, container, counter, values); 
			commitTransaction(metaModel);			
			return result;
		}	
		catch (CreateException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) { 
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	private Map createAggregateReturningKey(UserInfo userInfo, String modelName, Map containerKeyValues, String collectionName, int counter, Map values)  
		throws CreateException,ValidationException, XavaException, RemoteException 
	{		
		Users.setCurrentUserInfo(userInfo);
		containerKeyValues = Maps.recursiveClone(containerKeyValues); 
		values = Maps.recursiveClone(values); 	
		MetaModel metaModel = getMetaModel(modelName); 
		try {			
			beginTransaction(metaModel);
			MetaModel metaModelContainer = getMetaModelContainer(metaModel, modelName);  
			Map result = createAggregateReturningKey(metaModel, metaModelContainer, containerKeyValues, collectionName, counter, values); 
			commitTransaction(metaModel);			
			return result;
		}	
		catch (CreateException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {		
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	public Map createAggregateReturningKey(UserInfo userInfo, String modelName, Map containerKeyValues, int counter, Map values)  
			throws CreateException,ValidationException, XavaException, RemoteException 
	{
		return createAggregateReturningKey(userInfo, modelName, containerKeyValues, null, counter, values);
	}
	
	public Map createAggregateReturningKey(UserInfo userInfo, String modelName, Map containerKeyValues, String collectionName, Map values)  
			throws CreateException,ValidationException, XavaException, RemoteException 
	{		
		return createAggregateReturningKey(userInfo, modelName, containerKeyValues, collectionName, -1, values);
	}
	
	public Map getValues( 
		UserInfo userInfo, 
		String modelName,
		Object modelObject,
		Map memberNames) throws XavaException, RemoteException  
		 {				
		Users.setCurrentUserInfo(userInfo);
		memberNames = Maps.recursiveClone(memberNames);
		MetaModel metaModel = getMetaModel(modelName); 
		try {	
			beginTransaction(metaModel);
			Map result = getValues(metaModel, modelObject, memberNames);
			commitTransaction(metaModel);			
			return result;
		}	
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	public Messages validate(UserInfo userInfo, String modelName, Map values) throws XavaException, RemoteException { 
		return validate(userInfo, modelName, values, false, null); 
	}
	
	public Messages validateIncludingMissingRequired(UserInfo userInfo, String modelName, Map values, String containerReference) throws XavaException, RemoteException { 
		return validate(userInfo, modelName, values, true, containerReference); 
	}
	
	private Messages validate(UserInfo userInfo, String modelName, Map values, boolean includingMissingRequired, String containerReference) throws XavaException, RemoteException {   
		Users.setCurrentUserInfo(userInfo);
		values = Maps.recursiveClone(values); 	
		MetaModel metaModel = getMetaModel(modelName); 
		try {			
			beginTransaction(metaModel);
			Messages result = new Messages();
			Map key = null;
			if (includingMissingRequired) {
				validateExistRequired(result, metaModel, values, true, containerReference); 
			}
			else key = metaModel.extractKeyValues(values);
			validate(result, metaModel, values, key, null, false);
			commitTransaction(metaModel);			
			return result;
		}	
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}
	}
	
	public void removeCollectionElement(UserInfo userInfo, String modelName, Map keyValues, String collectionName, Map collectionElementKeyValues)   
		throws FinderException,	ValidationException, XavaException, RemoveException, RemoteException 
	{
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 
		collectionElementKeyValues = Maps.recursiveClone(collectionElementKeyValues);
		MetaModel metaModel = getMetaModel(modelName); 
		try {		
			beginTransaction(metaModel);
			removeCollectionElement(metaModel, keyValues, collectionName, collectionElementKeyValues);
			commitTransaction(metaModel);			
		} 
		catch (FinderException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RemoveException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}						
	}	
	
	private void removeCollectionElement(MetaModel parentMetaModel, Map keyValues, String collectionName, Map collectionElementKeyValues) 
		throws FinderException,	ValidationException, XavaException, RemoveException, RemoteException, InvocationTargetException, PropertiesManagerException 
	{
		removeCollectionElement(parentMetaModel, keyValues, collectionName, collectionElementKeyValues, true);
	}
	
	// Does not remove the element itself
	private void removeElementFromCollection(MetaModel parentMetaModel, Map keyValues, String collectionName, Map collectionElementKeyValues) 
		throws FinderException,	ValidationException, XavaException, RemoveException, RemoteException, InvocationTargetException, PropertiesManagerException 
	{
		removeCollectionElement(parentMetaModel, keyValues, collectionName, collectionElementKeyValues, false);
	}
	
	private void removeCollectionElement(MetaModel parentMetaModel, Map keyValues, String collectionName, Map collectionElementKeyValues, boolean deletingElement) 
		throws FinderException,	ValidationException, XavaException, RemoveException, RemoteException, InvocationTargetException, PropertiesManagerException 
	{
		MetaCollection metaCollection = parentMetaModel.getMetaCollection(collectionName);
		MetaModel childMetaModel = metaCollection.getMetaReference().getMetaModelReferenced();
		String refToParent = metaCollection.getMetaReference().getRole();
		if ((!childMetaModel.containsMetaReference(refToParent) || metaCollection.isSortable()) && parentMetaModel.isPOJOAvailable()) { 
			// If not (as in ManyToMany relationship), we update the collection in parent
			Object parent = findEntity(parentMetaModel, keyValues);
			Object child = findEntity(childMetaModel, collectionElementKeyValues);
			if (metaCollection.hasInverseCollection()) {
				Object theChild = child;
				child = parent;
				parent = theChild;
				collectionName = metaCollection.getInverseCollection(); 
			}				
			PropertiesManager pm = new PropertiesManager(parent);				
			Collection collection = (Collection) pm.executeGet(collectionName);
			collection.remove(child);
		}
		if (deletingElement && (metaCollection.isAggregate() || metaCollection.isOrphanRemoval())) {
			remove(childMetaModel, collectionElementKeyValues);
		}		
		else if (childMetaModel.containsMetaReference(refToParent)) {
			// If the child contains the reference to its parent we simply update this reference
			Map nullParentKey = new HashMap();
			nullParentKey.put(refToParent, null); 
			setValues(childMetaModel, collectionElementKeyValues, nullParentKey, deletingElement, true, false);   
		}
		if (metaCollection.hasPostRemoveCalculators()) {
			executePostremoveCollectionElement(parentMetaModel, keyValues, metaCollection);			
		}						
	}

	public void addCollectionElement(UserInfo userInfo, String modelName, Map keyValues, String collectionName, Map collectionElementKeyValues)   
		throws FinderException,	ValidationException, XavaException, RemoteException 
	{
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 
		collectionElementKeyValues = Maps.recursiveClone(collectionElementKeyValues);
		MetaModel metaModel = getMetaModel(modelName); 
		try {		
			beginTransactionForAddCollectionElement(metaModel);
			addCollectionElement(metaModel, keyValues, collectionName, collectionElementKeyValues);
			commitTransactionForAddCollectionElement(metaModel);		
		} 
		catch (FinderException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (ValidationException ex) {
			freeTransaction(); 
			throw ex;
		}
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}						
	}	
		
	private void addCollectionElement(MetaModel parentMetaModel, Map keyValues, String collectionName, Map collectionElementKeyValues) 
		throws FinderException,	ValidationException, XavaException, RemoteException, InvocationTargetException, PropertiesManagerException 
	{		
		MetaCollection metaCollection = parentMetaModel.getMetaCollection(collectionName);		
		String refToParent = metaCollection.getMetaReference().getRole();
		MetaModel childMetaModel = metaCollection.getMetaReference().getMetaModelReferenced();
		if ((!childMetaModel.containsMetaReference(refToParent) || metaCollection.isSortable()) && parentMetaModel.isPOJOAvailable()) { 
			// If not (as in ManyToMany relationship), we update the collection in parent
			Object parent = findEntity(parentMetaModel, keyValues);			
			Object child = findEntity(childMetaModel, collectionElementKeyValues);
			if (metaCollection.hasInverseCollection()) {
				Object theChild = child;
				child = parent;
				parent = theChild;
				collectionName = metaCollection.getInverseCollection(); 
			}			
			addToCollection(parent, collectionName, child);
		}
		if (childMetaModel.containsMetaReference(refToParent)) {
			// If the child contains the reference to its parent we simply update this reference
			Map parentKey = new HashMap();
			parentKey.put(refToParent, keyValues);	
			setValues(childMetaModel, collectionElementKeyValues, parentKey, true, true, false); 
		}
	}
	
	public void moveCollectionElementToAnotherCollection(UserInfo userInfo, 
		String sourceModelName, Map sourceKeyValues, String sourceCollectionName, 
		String targetModelName, Map targetKeyValues, String targetCollectionName,
		Map collectionElementKeyValues)   
		throws FinderException,	ValidationException, XavaException, RemoteException 
		{
			Users.setCurrentUserInfo(userInfo);
			sourceKeyValues = Maps.recursiveClone(sourceKeyValues);
			targetKeyValues = Maps.recursiveClone(targetKeyValues);
			collectionElementKeyValues = Maps.recursiveClone(collectionElementKeyValues);
			MetaModel sourceMetaModel = getMetaModel(sourceModelName);
			MetaModel targetMetaModel = getMetaModel(targetModelName);
			try {		
				beginTransactionForAddCollectionElement(sourceMetaModel);
				removeElementFromCollection(sourceMetaModel, sourceKeyValues, sourceCollectionName, collectionElementKeyValues);
				addCollectionElement(targetMetaModel, targetKeyValues, targetCollectionName, collectionElementKeyValues);
				commitTransactionForAddCollectionElement(sourceMetaModel);		
			} 
			catch (FinderException ex) {
				freeTransaction(); 
				throw ex;
			}
			catch (ValidationException ex) {
				freeTransaction(); 
				throw ex;
			}
			catch (RuntimeException ex) { 
				rollback(sourceMetaModel); 
				throw ex;
			}
			catch (Exception ex) {
				rollback(sourceMetaModel); 
				throw new RemoteException(ex.getMessage());
			}						
		}	

	
	public void moveCollectionElement(UserInfo userInfo, String modelName, Map keyValues, String collectionName, int from, int to)   
		throws FinderException, XavaException, RemoteException 
	{
		Users.setCurrentUserInfo(userInfo);
		keyValues = Maps.recursiveClone(keyValues); 
		MetaModel metaModel = getMetaModel(modelName); 
		try {		
			beginTransaction(metaModel);
			getPersistenceProvider(metaModel).moveCollectionElement(metaModel, keyValues, collectionName, from, to);
			commitTransaction(metaModel);			
		} 
		catch (RuntimeException ex) { 
			rollback(metaModel); 
			throw ex;
		}
		catch (Exception ex) {
			rollback(metaModel); 
			throw new RemoteException(ex.getMessage());
		}						
	}	
		
	private Map getValues(		 	
		String modelName,
		Object modelObject,
		Map memberNames) throws XavaException, RemoteException
		 {		
		try {			
			MetaModel metaModel = getMetaModel(modelName);
			Map result = getValues(metaModel, modelObject, memberNames);						
			return result;
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_values_error", modelName);
		}
	}
	
	private Map createAggregateReturningKey(MetaModel metaModel, MetaModel metaModelContainer, Map containerKeyValues, String collectionName, int counter, Map values)
		throws CreateException,ValidationException, XavaException, RemoteException 
	{		
		addKeyToValues(metaModelContainer, collectionName, containerKeyValues, values); 
		try {								
			Object container = metaModelContainer.toPOJO(containerKeyValues); 
			Object aggregate = createAggregate(metaModel, metaModelContainer, container, collectionName, counter, values, false);
			return getValues(metaModel, aggregate, getKeyNames(metaModel));			
		}
		catch (ClassCastException ex) {
			throw new XavaException("aggregate_must_be_persistent_for_create", metaModelContainer.getName());					
		}
	}

	private void addKeyToValues(MetaModel metaModelContainer, String collectionName, Map containerKeyValues, Map values) { 
		if (collectionName == null) return;
		MetaCollection metaCollection = metaModelContainer.getMetaCollection(collectionName);
		Map parentKey = new HashMap();
		parentKey.put(metaCollection.getMetaReference().getRole(), containerKeyValues);
		values.putAll(parentKey);
	}
	
	private Object createAggregate(MetaModel metaModel, MetaModel metaModelContainer, Object container, int counter, Map values)
		throws CreateException,ValidationException, XavaException, RemoteException
	{		
		return createAggregate(metaModel, metaModelContainer, container, null, counter, values, true);
	}
	
	private Object createAggregate(MetaModel metaModel, MetaModel metaModelContainer, Map containerKeyValues, String collectionName, int counter, Map values) 
		throws CreateException,ValidationException, XavaException, RemoteException 
	{		
		addKeyToValues(metaModelContainer, collectionName, containerKeyValues, values);

		try {	
			Object containerKey = getPersistenceProvider(metaModel).find(metaModelContainer, containerKeyValues);
			return createAggregate(metaModel, metaModelContainer, containerKey, collectionName, counter, values, true); 
		}
		catch (ClassCastException ex) {
			throw new XavaException("aggregate_must_be_persistent_for_create", metaModel.getMetaModelContainer().getName());					
		}
		catch (FinderException ex) {
			log.warn(ex.getMessage(), ex);
			throw new XavaException("container_for_pojo_error");
		}
	}	
	
	private Map createReturningKey(MetaModel metaModel, Map values, boolean validateCollection) 
		throws CreateException, XavaException, ValidationException, RemoteException {
		Object entity = create(metaModel, values, null, null, null, 0, validateCollection);
		if (metaModel.hasDefaultCalculatorOnCreate()) {
			getPersistenceProvider(metaModel).flush(); // to execute calculators 
		}
		return getValues(metaModel, entity, getKeyNames(metaModel));
	}
	
	private Map createReturningValues(MetaModel metaModel, Map values) 
		throws CreateException, XavaException, ValidationException, RemoteException {
		Object entity = create(metaModel, values, null, null, null, 0, true); 
		if (metaModel.hasDefaultCalculatorOnCreate()) {
			getPersistenceProvider(metaModel).flush(); // to execute calculators 
		}
		return getValues(metaModel, entity, values);
	}
		
	private Map getValuesImpl(	
		MetaModel metaModel, 	
		Map keyValues,
		Map membersNames)
		throws FinderException, XavaException, RemoteException {		
		try {			
			Map result =
				getValues(					 
					metaModel,
					findEntity(metaModel, keyValues), 
					membersNames); 	
			return result;
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_values_error", metaModel.getName()); 
		}
	}
	
	private Map getValuesByAnyPropertyImpl( 	
		MetaModel metaModel, 	
		Map keyValues,
		Map membersNames)
		throws FinderException, XavaException, RemoteException {		
		try {
			Map result =
				getValues(					 
					metaModel,
					findEntityByAnyProperty(metaModel, keyValues), 
					membersNames); 						
			return result;
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_values_error", metaModel.getName());
		}
	}
	
		
	private Map getKeyNames(MetaModel metaModel) throws XavaException {
		Iterator itProperties = metaModel.getKeyPropertiesNames().iterator();
		Map names = new HashMap();
		while (itProperties.hasNext()) {
			names.put(itProperties.next(), null);
		}
		Iterator itReferences = metaModel.getMetaReferencesKey().iterator();
		while (itReferences.hasNext()) {
			MetaReference ref = (MetaReference) itReferences.next();
			names.put(ref.getName(), getKeyNames(ref.getMetaModelReferenced()));
		}	
		return names;
	}
	
	private Object createAggregate(MetaModel metaModel, MetaModel metaModelContainer, Object container, String collectionName, int counter, Map values, boolean validateCollections) 
		throws CreateException,ValidationException, XavaException, RemoteException 
	{
		// counter is ignored, we keep it for backward compatibility in method signatures
		if (metaModel.isAnnotatedEJB3()) {
			return create(metaModel, values, metaModelContainer, container, collectionName, -1, validateCollections);
		}
		else {
			counter = 0;	
			int attempts = 0;
			// Loop with 50 attempts, so we don't need the correct counter in the argument
			do {				 
				try {
					return create(metaModel, values, metaModelContainer, container, collectionName, counter, validateCollections); 
				}
				catch (DuplicateKeyException ex) {
					if (attempts > 50) throw ex; 
					counter++;
					attempts++;
				}				
			}
			while (true);
		} 
	}
	
	/**
	 * Allows create independent entities or aggregates to another entities. <p>
	 *
	 * If the argument <tt>metaModelContainer</tt> is null it assume
	 * a independent entity else a aggregate.<p>
	 * 
	 * @param metaModel  of entity or aggregate to create. It must to implement IMetaEjb
	 * @param values  to assign to entity to create.
	 * @param metaModelContainer  Only if the object is a aggregate. It's the MetaModel of container model.
	 * @param containerModel Only if object to create is a aggregate.
	 * @param number  Only if object to create is a aggregate. It's a secuential number.
	 * @return The created entity.
	 */
	private Object create(	
		MetaModel metaModel,
		Map values,
		MetaModel metaModelContainer,
		Object container,
		String collectionName, 
		int number, boolean validateCollections) 
		throws CreateException, ValidationException, XavaException, RemoteException {
		try {			
			//removeReadOnlyFields(metaModel, values); // not remove the read only fields because it maybe needed initialized on create
			removeReadOnlyWithFormulaFields(metaModel, values); 			
			removeCalculatedFields(metaModel, values); 						
			Messages validationErrors = new Messages();	
			validateExistRequired(validationErrors, metaModel, values, metaModelContainer != null, null); 
			validate(validationErrors, metaModel, values, null, container, true);
			if (validateCollections) validateCollections(validationErrors, metaModel);
			removeViewProperties(metaModel, values); 			
			if (validationErrors.contains()) {
				throw new ValidationException(validationErrors);			
			}		
			updateReferencedEntities(metaModel, values);			
			Map convertedValues = convertSubmapsInObject(metaModel, values);
			if (!validateCollections) {
				setCollectionsWithMinimumToNull(metaModel, convertedValues);
			}			
			Object newObject = null;			
			if (container == null) {
				newObject = getPersistenceProvider(metaModel).create(metaModel, convertedValues); 
			} 
			else {
				if (metaModelContainer == null) {
					metaModelContainer = metaModel.getMetaModelContainer();
				}
				if (number < 0) {
					newObject = getPersistenceProvider(metaModel).create(metaModel, convertedValues);
				}
				else {
					newObject = getPersistenceProvider(metaModel).createAggregate(
						metaModel,
						convertedValues,
						metaModelContainer,
						container,
						number);
				}
				addToCollection(container, collectionName, newObject);		
			}
			Map key = getValues(metaModel, newObject, getKeyNames(metaModel), false);
			if (container == null) updateSortableCollections(metaModel, key, values); 
			AccessTracker.created(metaModel.getName(), key); 
			// Collections are not managed			
			return newObject;
		} catch (ValidationException ex) {
			throw ex;
		} catch (DuplicateKeyException ex) {
			throw new DuplicateKeyException(
				XavaResources.getString("no_create_exists", metaModel.getName()));	
		} catch (CreateException ex) {
			log.error(ex.getMessage(), ex);
			throw new CreateException(XavaResources.getString("create_error", metaModel.getName()));		
		} catch (RemoteException ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(XavaResources.getString("create_error", metaModel.getName()));
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_error", metaModel.getName());
		} catch (ObjectNotFoundException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_error", metaModel.getName());
		} catch (RuntimeException ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	private void addToCollection(/* MetaModel metaModel, */Object containerModel, String collectionName, Object newAggregate) throws XavaException{ 
		try {
			PropertiesManager containerPM = new PropertiesManager(containerModel);
			Collection collection = (Collection) containerPM.executeGet(collectionName);
			if (collection == null) {
				collection = new ArrayList();				
				containerPM.executeSet(collectionName, collection);
			}
			collection.add(newAggregate);
		}
		catch (Exception ex) { 
			log.error(ex.getMessage(), ex);
			throw new XavaException("add_element_to_collection_error"); 
		}	
	}

	private void updateReferencedEntities(MetaModel metaModel, Map values) throws XavaException, RemoteException, CreateException, ValidationException {		
		for (Iterator it = metaModel.getMetaReferencesToEntity().iterator(); it.hasNext(); ) {
			MetaReference ref = (MetaReference) it.next();		
			Map referenceValues = (Map) values.get(ref.getName());
			if (referenceValues != null) {
				int hiddenKeyNotPresent = getHiddenKeyNotPressent(ref, referenceValues);
				if (referenceValues.size() + hiddenKeyNotPresent > ref.getMetaModelReferenced().getMetaMembersKey().size()) {
					try {	
						findEntity(ref.getMetaModelReferenced(), referenceValues);
						setValues(ref.getMetaModelReferenced(), new HashMap(referenceValues), new HashMap(referenceValues));
					}
					catch (FinderException ex) {					
						referenceValues = createReturningValues(ref.getMetaModelReferenced(), new HashMap(referenceValues)); 
						values.put(ref.getName(), referenceValues);						
					}
				}
			}			
		}
	}
	
	private void updateSortableCollections(MetaModel metaModel, Map key, Map values) throws XavaException, RemoteException, CreateException, ValidationException { 
		for (Iterator it = metaModel.getMetaReferencesToEntity().iterator(); it.hasNext(); ) {
			MetaReference ref = (MetaReference) it.next();		
			Map referenceValues = (Map) values.get(ref.getName());
			if (referenceValues != null) {
				if (!Is.emptyString(ref.getReferencedModelCorrespondingCollection())) {
					if (ref.getMetaModelReferenced().containsMetaCollection(ref.getReferencedModelCorrespondingCollection())) {
						MetaCollection col = ref.getMetaModelReferenced().getMetaCollection(ref.getReferencedModelCorrespondingCollection());
						if (col.isSortable()) {
							try {
								Map referenceKey = extractKeyValues(ref.getMetaModelReferenced(), referenceValues);
								if (!Is.empty(referenceKey)) { 
									addCollectionElement(ref.getMetaModelReferenced(), referenceKey, ref.getReferencedModelCorrespondingCollection(), key);
								}
							} 
							catch (PropertiesManagerException | InvocationTargetException | FinderException ex) {
								log.error(ex.getMessage(), ex);
								throw new XavaException("add_element_to_collection_error"); 
							}
						}
					}
				}
			}			
		}	
	}


	private int getHiddenKeyNotPressent(MetaReference ref, Map referenceValues) throws XavaException {
		int hiddenKeyNotPresent = 0;
		for (Iterator itKeys = ref.getMetaModelReferenced().getMetaMembersKey().iterator(); itKeys.hasNext(); ) {
			MetaMember key = (MetaMember) itKeys.next();					
			if (key.isHidden()) {
				if (!referenceValues.containsKey(key.getName())) {
					hiddenKeyNotPresent++;
				}
			}
		}
		return hiddenKeyNotPresent;
	}

	public void ejbActivate() throws java.rmi.RemoteException {
	}
	public void ejbCreate()
		throws javax.ejb.CreateException, java.rmi.RemoteException {
	}
	public void ejbPassivate() throws java.rmi.RemoteException {
	}
	public void ejbRemove() throws java.rmi.RemoteException {
	}

	private Object getReferencedObject(MetaModel metaModel, Object container, String memberName) throws XavaException, RemoteException {
		if (container == null) return null;
		IPropertiesContainer r = getPersistenceProvider(metaModel).toPropertiesContainer(null, container);	
		return r.executeGets(memberName).get(memberName);
	}

	public javax.ejb.SessionContext getSessionContext() {
		return sessionContext;
	}
	
	private Map getValues( 	
		MetaModel metaModel,
		Map keyValues,
		Map memberNames)
		throws FinderException, XavaException, RemoteException {  
		try {									 
			Map result =
				getValues(	
					metaModel,
					findEntity(metaModel, keyValues),
					memberNames);			
			return result;
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_values_error", metaModel.getName());
		}
	}
	
	private MetaModel getMetaModel(String modelName) throws XavaException {
		int idx = modelName.indexOf('.');			
		if (idx < 0) {
			return MetaComponent.get(modelName).getMetaEntity();
		}
		else {
			String component = modelName.substring(0, idx);
			idx = modelName.lastIndexOf('.'); // just in case we have: MyEntity.MyAggregate.MyAnotherAggregate --> It get MyAnotherAggregate within MyEntity Component
			String aggregate = modelName.substring(idx + 1);
			try {  
				return MetaComponent.get(component).getMetaAggregate(aggregate);
			}
			catch (ElementNotFoundException ex) {
				return MetaComponent.get(aggregate).getMetaEntity();
			}
		}
	}
	
	private Map getValues(		
			MetaModel metaModel, 
			Object modelObject,
			Map membersNames) throws XavaException, RemoteException {
		return getValues(metaModel, modelObject, membersNames, true);
	}
	
	private Map getValues(  		
			MetaModel metaModel, 
			Object modelObject,
			Map membersNames, boolean includeModelName) throws XavaException, RemoteException { 
		return getValues(metaModel, modelObject, membersNames, includeModelName, true);
	}
	
	private Map getValues(		
		MetaModel metaModel, 
		Object modelObject,
		Map membersNames, boolean includeModelName, boolean includeKey) throws XavaException, RemoteException {  
		try {
			if (modelObject == null)
				return null;						
			if (membersNames == null) return Collections.EMPTY_MAP;			 
			IPersistenceProvider persistenceProvider = getPersistenceProvider(metaModel);  
			IPropertiesContainer r = persistenceProvider.toPropertiesContainer(metaModel, modelObject);
			StringBuffer names = new StringBuffer();
			if (includeKey) addKey(metaModel, membersNames); // always return the key althought it is not demanded 
			addVersion(metaModel, membersNames); // always return the version property 			
			removeViewProperties(metaModel, membersNames);			
			Iterator it = membersNames.keySet().iterator();			
			Map result = new HashMap();			
			while (it.hasNext()) {
				String memberName = (String) it.next();
				if (metaModel.containsMetaProperty(memberName) ||
					(metaModel.isKey(memberName) && !metaModel.containsMetaReference(memberName))) {
					names.append(memberName);
					names.append(":");
				} 
				else if (!MapFacade.MODEL_NAME.equals(memberName)) {					
					Map submemberNames = (Map) membersNames.get(memberName);
					if (metaModel.containsMetaReference(memberName)) {						
						result.put(
							memberName,
							getReferenceValues(metaModel, modelObject, memberName, submemberNames));
					}
					else if (metaModel.containsMetaCollection(memberName)) {						
						result.put(
							memberName,
							getCollectionValues(metaModel, modelObject, memberName, submemberNames));
					} 					
					else {
						throw new XavaException("member_not_found", memberName, metaModel.getName());
					}
				}
			}			
			result.putAll(r.executeGets(names.toString()));
			if (includeModelName) result.put(MapFacade.MODEL_NAME, persistenceProvider.getModelName(modelObject)); 
			return result;
		} catch (RemoteException ex) {			
			log.error(ex.getMessage(), ex);
			rollback(metaModel);
			throw new RemoteException(XavaResources.getString("get_values_error", metaModel.getName()));
		}
	}
	
	public Map getKeyValues(UserInfo userInfo, String modelName, Object entity) throws RemoteException, XavaException {
		Users.setCurrentUserInfo(userInfo);
		MetaModel metaModel = getMetaModel(modelName);
		return getValues(metaModel, entity, getKeyNames(metaModel), false); 
	}
			
	private void addKey(MetaModel metaModel, Map memberNames) throws XavaException {
		Iterator it = metaModel.getKeyPropertiesNames().iterator();		
		while (it.hasNext()) {
			String name = (String) it.next();
			memberNames.put(name, null);
		}		
		Iterator itRef = metaModel.getMetaReferencesKey().iterator();
		while (itRef.hasNext()) {
			MetaReference ref = (MetaReference) itRef.next();
			Map referenceKeyNames = null;
			referenceKeyNames = (Map) memberNames.get(ref.getName());
			if (referenceKeyNames == null) {
				referenceKeyNames = new HashMap();
			}
			addKey(ref.getMetaModelReferenced(), referenceKeyNames);
			memberNames.put(ref.getName(), referenceKeyNames);
		}				
	}
	
	private void addVersion(MetaModel metaModel, Map memberNames) throws XavaException { 		
		if (metaModel.hasVersionProperty()) {
			memberNames.put(metaModel.getVersionPropertyName(), null);
		}
	}	
	
	/**
	 * If we send null as <tt>nombresPropiedades</tt> it return a empty Map. <p>
	 * @throws RemoteException 
	 */
	private Map getAggregateValues(MetaAggregate metaAggregate, Object aggregate, Map memberNames) throws XavaException, RemoteException { 		
		if (memberNames == null || aggregate == null) return Collections.EMPTY_MAP;
		PropertiesManager man = new PropertiesManager(aggregate);
		StringBuffer names = new StringBuffer();
				
		Map result = new HashMap();
		
		Iterator itKeys = metaAggregate.getKeyPropertiesNames().iterator();
		while (itKeys.hasNext()) {
			names.append(itKeys.next());
			names.append(":");			
		}
		
		removeViewProperties(metaAggregate, memberNames); 
		 
		Iterator it = memberNames.keySet().iterator();		
		while (it.hasNext()) {
			String memberName = (String) it.next();
			if (metaAggregate.containsMetaProperty(memberName)) {
				names.append(memberName);
				names.append(":");
			} else
				if (metaAggregate.containsMetaReference(memberName)) {
					Map submemberNames = (Map) memberNames.get(memberName);
					result.put(
						memberName,
						getReferenceValues(metaAggregate, aggregate, memberName, submemberNames));
				} else {
					throw new XavaException("member_not_found", memberName, metaAggregate.getName());
				}
		}
		try {
			result.putAll(man.executeGets(names.toString()));
		} catch (PropertiesManagerException ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaAggregate); 
			throw new RemoteException(XavaResources.getString("get_values_error", metaAggregate.getName()));
		} catch (InvocationTargetException ex) {
			Throwable th = ex.getTargetException();
			log.error(th.getMessage(), th);
			rollback(metaAggregate); 
			throw new RemoteException(XavaResources.getString("get_values_error", metaAggregate.getName()));
		}
		return result;
	}


	/**
	 * If <tt>memberNames</tt> is null then return a empty map.
	 * @throws RemoteException 
	 */
	private Map getAssociatedEntityValues(MetaEntity metaEntity, Object modelObject, Map memberNames) throws XavaException, FinderException, RemoteException { 
		if (memberNames == null) return Collections.EMPTY_MAP;
		if (modelObject instanceof Map) return getValues(metaEntity, (Map) modelObject, memberNames); // modelObject can be a Map with the key with non-POJO IPersistenceProvider 
		return getValues(metaEntity, modelObject, memberNames);
	}
	
	private Map getReferenceValues(	
		MetaModel metaModel,
		Object model,
		String memberName,
		Map submembersNames) throws XavaException, RemoteException { 		
		try {								
			MetaReference r = metaModel.getMetaReference(memberName);
			Object object = getReferencedObject(metaModel, model, memberName); 
			if (r.isAggregate()) {
				return getAggregateValues((MetaAggregate) r.getMetaModelReferenced(), object, submembersNames);
			} 
			else {		
				String referencedModel = getPersistenceProvider(metaModel).getModelName(object);
				MetaEntity metaEntityReferenced = (MetaEntity) (referencedModel==null?r.getMetaModelReferenced():MetaModel.get(referencedModel)); 
				return getAssociatedEntityValues(metaEntityReferenced, object, submembersNames);
			}
		} catch (FinderException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_reference_error", memberName, metaModel.getName());
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_reference_error", memberName, metaModel.getName());
		}
	}
	
	private Collection getCollectionValues(	
		MetaModel metaModel,
		Object modelObject,
		String memberName,
		Map memberNames) throws XavaException, RemoteException {
		try {
			MetaCollection c = metaModel.getMetaCollection(memberName);
			Object object = getReferencedObject(metaModel, modelObject, memberName); 
			return getCollectionValues( 
					c.getMetaReference().getMetaModelReferenced(),
					c.isAggregate(),	object, memberNames);
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("get_collection_elements_error", memberName, metaModel.getName());
		}
	}
	
	private Collection getCollectionValues(	
		MetaModel metaModel,
		boolean aggregate,
		Object elements, Map memberNames) throws XavaException, RemoteException {
		Collection result = new ArrayList();
		Enumeration enumeration = null;
		if (elements instanceof Enumeration) {
			enumeration = (Enumeration) elements;
		}
		else if (elements instanceof Collection) {
			enumeration = Collections.enumeration((Collection) elements);
		}
		else if (elements == null) {
			enumeration = Collections.enumeration(Collections.EMPTY_LIST);
		}
		else {	
			String collectionType = elements.getClass().getName();
			throw new XavaException("collection_type_not_supported", collectionType);
		}		
		while (enumeration.hasMoreElements()) {
			Object object = enumeration.nextElement();
			result.add(getValues(metaModel, object, memberNames));
		}
		return result;
	}


	private Object instanceAggregate(MetaAggregate metaAggregate, Map values) 
		throws ValidationException, XavaException, RemoteException {
		try {
			Object object = metaAggregate.getPropertiesClass().newInstance(); 
			PropertiesManager man = new PropertiesManager(object);			
			removeViewProperties(metaAggregate, values);
			removeCalculatedFields(metaAggregate, values); 
			values = convertSubmapsInObject(metaAggregate, values); 
			man.executeSets(values);
			return object;
		} catch (IllegalAccessException ex) {			
			log.error(ex.getMessage(), ex);
			rollback(metaAggregate); 
			throw new RemoteException(XavaResources.getString("instantiate_error", metaAggregate.getPropertiesClass().getName())); 
		} catch (InstantiationException ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaAggregate); 
			throw new RemoteException(XavaResources.getString("instantiate_error", metaAggregate.getPropertiesClass().getName())); 
		} catch (InvocationTargetException ex) {
			throwsValidationException(
				ex, XavaResources.getString("assign_values_error", metaAggregate.getPropertiesClass().getName(), ex.getLocalizedMessage())); 
			rollback(metaAggregate); 
			throw new RemoteException(); // Never
		} catch (PropertiesManagerException ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaAggregate); 
			throw new RemoteException(XavaResources.getString("assign_values_error", metaAggregate.getPropertiesClass().getName(), ex.getLocalizedMessage())); 
		}
	}

	private void throwsValidationException(
		InvocationTargetException ex,
		String ejbmessage)
		throws ValidationException, RemoteException {
		Throwable th = ex.getTargetException();
		if (th instanceof ValidationException) {
			throw (ValidationException) th;
		}
		log.error(ex.getMessage(), ex);
		throw new RemoteException(ejbmessage);
	}

	private Object mapToReferencedObject(	
		MetaModel metaModel,
		String memberName,
		Map values)
		throws ValidationException, XavaException, RemoteException {		
		MetaReference r = null;
		try {		
			r = metaModel.getMetaReference(memberName);
			if (r.isAggregate()) {
				return instanceAggregate((MetaAggregateForReference) r.getMetaModelReferenced(), values);
			} else {
				if (Maps.isEmpty(values)) return null;
				return findAssociatedEntity(r.getMetaModelReferenced(), values); 
			}
		}
		catch (ObjectNotFoundException ex) {
			return null; 
		} 
		catch (FinderException ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(XavaResources.getString("map_to_reference_error",
					r.getName(),					
					metaModel.getName(),					
					memberName));
		} catch (XavaException ex) {
			throw ex;
		}
	}
	
	private Collection mapsToElements(MetaCollection metaCollection, Collection<Map> collectionValues) throws RemoteException {
		MetaReference r = metaCollection.getMetaReference();
		Collection result = new ArrayList();
		for (Map elementValues: collectionValues) {
			result.add(instanceAggregate((MetaAggregate) r.getMetaModelReferenced(), elementValues));
		}		
		return result;
	}
	
	private void validateElements(Collection elements) { 
		javax.validation.Validator validator = getValidatorFactory().getValidator();
		Set<javax.validation.ConstraintViolation<?>> allViolations = new HashSet<javax.validation.ConstraintViolation<?>>(); 		
		for (Object e: elements) {
			Set<javax.validation.ConstraintViolation<Object>> violations = validator.validate(e);
			allViolations.addAll(violations);
		}
		if (!allViolations.isEmpty()) {
			throw new javax.validation.ConstraintViolationException(allViolations);
		}
	}
	
	private javax.validation.ValidatorFactory getValidatorFactory() { 
		if (validatorFactory == null) {
			validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory(); 
		}
		return validatorFactory;	
	}
	
	private Object findAssociatedEntity(MetaModel metaEntity, Map values) 
		throws FinderException, XavaException, RemoteException {
		Map keyValues = metaEntity.extractKeyValues(values);
		if (Maps.isEmpty(keyValues)) {
			return findEntityByAnyProperty(metaEntity, metaEntity.extractSearchKeyValues(values));
		}
		else {
			return findEntity(metaEntity.getQualifiedName(), keyValues);
		}
	}

	private void removeKeyFields(MetaModel metaModel, Map values)
		throws XavaException {
		Iterator keys = metaModel.getKeyPropertiesNames().iterator();
		while (keys.hasNext()) {
			values.remove(keys.next());
		}
		Iterator itRef = metaModel.getMetaReferencesKey().iterator();
		while (itRef.hasNext()) {
			MetaReference ref = (MetaReference) itRef.next();
			values.remove(ref.getName());
		}		
	}

	private void removeReadOnlyFields(MetaModel metaModel, Map values)
		throws XavaException {
		Iterator toRemove = metaModel.getOnlyReadPropertiesNames().iterator();
		while (toRemove.hasNext()) {
			values.remove(toRemove.next());
		}
	}
	
	private void removeReadOnlyWithFormulaFields(MetaModel metaModel, Map values) 
		throws XavaException {
		Iterator toRemove = metaModel.getOnlyReadWithFormulaPropertiesNames().iterator();
		while (toRemove.hasNext()) {
			values.remove(toRemove.next());
		}
	}
	
	private void setCollectionsWithMinimumToNull(MetaModel metaModel, Map values) throws XavaException { 
		for (MetaCollection col: metaModel.getMetaCollections()) {
			if (!col.isElementCollection() && col.getMinimum() > 0) {
				values.put(col.getName(), null);
			}
		}
	}
		
	private void removeCalculatedFields(MetaModel metaModel, Map values)
		throws XavaException {
		Iterator toRemove = metaModel.getCalculatedPropertiesNames().iterator();
		while (toRemove.hasNext()) {
			values.remove(toRemove.next());
		}
	}
	
	private void removeViewProperties(MetaModel metaModel, Map values)
		throws XavaException {		
		Iterator toRemove = metaModel.getViewPropertiesNames().iterator();
		while (toRemove.hasNext()) {
			values.remove(toRemove.next());
		}
	}	
		
	private void remove(MetaModel metaModel, Map keyValues) 
		throws RemoveException, ValidationException, XavaException, RemoteException {
		try {
			Messages errors = validateOnRemove(metaModel, keyValues);			
			if (!errors.isEmpty()) {
				throw new ValidationException(errors);
			}			
			// removing collections are resposibility of persistence provider						
			getPersistenceProvider(metaModel).remove(metaModel, keyValues); 
			AccessTracker.removed(metaModel.getName(), keyValues); 
		} catch (ValidationException ex) {			
			throw ex; 					
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("remove_error", metaModel.getName(), ex.getLocalizedMessage());				
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("remove_error",
				metaModel.getName(), ex.getLocalizedMessage()));
		}
	}
	
	private Messages validateOnRemove(MetaModel metaModel, Map keyValues) throws Exception {
		Messages errors = new Messages();
		Collection validators = metaModel.getMetaValidatorsRemove();		
		if (validators.isEmpty()) return errors;		
		Iterator it = validators.iterator();
		Object modelObject = findEntity(metaModel, keyValues);
		while (it.hasNext()) {
			MetaValidator metaValidator = (MetaValidator) it.next();
			IRemoveValidator validator = null;
			if (metaValidator.containsMetaSetsWithoutValue()) {
				validator = metaValidator.createRemoveValidator();
				PropertiesManager pmValidator = new PropertiesManager(validator);				
				PropertiesManager pmModelObject = new PropertiesManager(modelObject);
				for (Iterator itSets=metaValidator.getMetaSetsWithoutValue().iterator(); itSets.hasNext();) {
					MetaSet metaSet = (MetaSet) itSets.next();
					try {
						pmValidator.executeSet(metaSet.getPropertyName(), pmModelObject.executeGet(metaSet.getPropertyNameFrom()));
					}
					catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						throw new XavaException("validator_set_property_error", metaSet.getPropertyName(), validator.getClass(), metaSet.getPropertyNameFrom(), modelObject.getClass(), ex.getMessage());
					}
				}
			}
			else {
				validator = metaValidator.getRemoveValidator();
			}
			validator.setEntity(modelObject);
			validator.validate(errors);			
		}				 		
		return errors;		
	}
		
	public void setSessionContext(javax.ejb.SessionContext ctx)
		throws java.rmi.RemoteException {
		sessionContext = ctx;
	}
	
	private void setValues(MetaModel metaModel, Map keyValues, Map values) 
		throws FinderException, ValidationException, XavaException 
	{
		setValues(metaModel, keyValues, values, true, true, false); 
	}

	private void setValues(MetaModel metaModel, Map keyValues, Map values, boolean validate, boolean tracking, boolean updateSortableCollections)  
		throws FinderException, ValidationException, XavaException 
	{ 		
		try {								
			Object entity = findEntity(metaModel, keyValues);
			updateReferencedEntities(metaModel, values);			
			removeKeyFields(metaModel, values);			
			removeReadOnlyFields(metaModel, values);						
			if (validate) validate(metaModel, values, keyValues, null, false);
			removeViewProperties(metaModel, values);
			verifyVersion(metaModel, entity, values);			 			
			if (tracking) {
				Map oldValues = getValues(metaModel, entity, toMembersNames(metaModel, values), false, false); 					
				trackModification(metaModel, keyValues, oldValues, values);
			}
			IPersistenceProvider provider = (IPersistenceProvider) getPersistenceProvider(metaModel);
			if (provider instanceof IExplicitModifyPersistenceProvider) {
				((IExplicitModifyPersistenceProvider) provider).modify(metaModel, keyValues, values);
			}
			else {
				IPropertiesContainer r = provider.toPropertiesContainer(metaModel, entity); 				
				Map objects = convertSubmapsInObject(metaModel, values);
				r.executeSets(objects);
			}
			if (updateSortableCollections) updateSortableCollections(metaModel, keyValues, values); 
			// Collections are not managed			
		} 
		catch (FinderException ex) { 
			throw ex;
		}		
		catch (ValidationException ex) {
			throw ex;
		}
		catch (RuntimeException ex) { 
			// For preserving the exception message
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("assign_values_error", metaModel.getName(), ex.getLocalizedMessage()); 
		}
	}
	
	private Map toMembersNames(MetaModel metaModel, Map values) { 
		Map membersNames = new HashMap();
		for (Map.Entry e: (Set<Map.Entry>) values.entrySet()) {
			if (e.getValue() instanceof Map) {
				Map refValues = (Map) e.getValue();
				MetaReference metaRef = metaModel.getMetaReference((String) e.getKey());
				membersNames.put(e.getKey(), toMembersNames(metaRef.getMetaModelReferenced(), refValues));
			}
			else if (e.getValue() instanceof Collection) {
				Collection collection = (Collection) e.getValue();
				if (!collection.isEmpty()) {
					Object element = collection.iterator().next();
					if (element instanceof Map) {
						MetaCollection metaCollection = metaModel.getMetaCollection((String) e.getKey()); 
						membersNames.put(e.getKey(), toMembersNames(metaCollection.getMetaReference().getMetaModelReferenced(), (Map) element));
					}
				}
			}
			else if (e.getValue() == null && metaModel.containsMetaReference((String)e.getKey())) {
				Map refValues = new HashMap();
				MetaReference metaRef = metaModel.getMetaReference((String) e.getKey());
				for (String key: metaRef.getMetaModelReferenced().getAllKeyPropertiesNames()) {
					refValues.put(key, null);
				}
				membersNames.put(e.getKey(), toMembersNames(metaRef.getMetaModelReferenced(), refValues));				
			}
			else {
				membersNames.put(e.getKey(), null);
			}
		}
		return membersNames;
	}

	private void trackModification(MetaModel metaModel, Map key, Map<String, Object> oldValues, Map<String, Object> newValues) {  
		Map<String, Object> oldChangedValues = new HashMap();
		Map<String, Object> newChangedValues = new HashMap();
		oldValues = Maps.treeToPlainIncludingCollections(oldValues, 1);
		newValues = Maps.treeToPlainIncludingCollections(newValues, 1);
		Collection<String> properties = CollectionUtils.union(newValues.keySet(), oldValues.keySet()); 
		for (String property: properties) {
			if (property.endsWith(MapFacade.MODEL_NAME)) continue;
			Object oldValue = oldValues.get(property);
			Object value = newValues.get(property);
			if (areDifferent(oldValue, value)) {
				oldChangedValues.put(property, oldValue);
				newChangedValues.put(property, value);
			}
		}
		if (!oldChangedValues.isEmpty()) {
			AccessTracker.modified(metaModel.getName(), key, oldChangedValues, newChangedValues);
		}
	}
		
	private boolean areDifferent(Object a, Object b) { 
		if ((a instanceof String || b instanceof String) && Is.equalAsStringIgnoreCase(a, b)) return false;
		if (a instanceof Map || b instanceof Map) return areDifferent((Map) a, (Map) b);
		if (Is.equal(a, b)) return false;
		if (XArrays.isArray(a) || XArrays.isArray(b)) {
			return !(Is.empty(a) && Is.empty(b));
		}
		return true;
	}
	
	private void verifyVersion(MetaModel metaModel, Object entity, Map values) throws Exception { 
		if (!metaModel.hasVersionProperty()) return;
		Object newVersion = values.get(metaModel.getVersionPropertyName()); 
		if (newVersion == null) return;		
		PropertiesManager pm = new PropertiesManager(entity);
		Object oldVersion = pm.executeGet(metaModel.getVersionPropertyName());		
		if (!Is.equal(oldVersion, newVersion)) {
			throw new SystemException(XavaResources.getString("concurrency_error")); 
		}
		
	}

	private void validate(	
		Messages errors,
		MetaModel metaModel,
		String memberName,
		Object values,
		boolean creating,
		String containerMember 
		) throws XavaException, RemoteException {			
		try {			
			if (metaModel.containsMetaProperty(memberName)) {
				metaModel.getMetaProperty(memberName).validate(errors, values, creating, containerMember); 
			} 
			else if (metaModel.containsMetaReference(memberName)) {
				MetaReference ref = metaModel.getMetaReference(memberName); 
				MetaModel referencedModel = ref.getMetaModelReferenced();
				Map mapValues = (Map) values;		
				if (referenceHasValue(mapValues)) {
					if (ref.isAggregate()) validate(errors, referencedModel, mapValues, mapValues, null, creating, memberName); 
				} 
				else if (metaModel
					.getMetaReference(memberName)
					.isRequired()) {
					String container = containerMember == null?metaModel.getName():containerMember;
					errors.add("required", memberName, container);
				}						
			} 
			else if (metaModel.containsMetaCollection(memberName)) {
				MetaCollection metaCollection = metaModel.getMetaCollection(memberName);
				if (metaCollection.isElementCollection()) {
					metaCollection.validate(errors, values, null, null);
					MetaModel elementMetaModel = metaCollection.getMetaReference().getMetaModelReferenced();
					Collection collection = (Collection) values;
					for (Object e: collection) {
						validate(errors, elementMetaModel, (Map) e, null, null, creating); 
					}	
				}
			} else if (metaModel.containsMetaPropertyView(memberName)) {
				metaModel.getMetaPropertyView(memberName).validate(errors, values, creating);									
			} else {					
				log.warn(XavaResources.getString("not_validate_member_warning", memberName, metaModel.getName()));
			}
		} catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("validate_error", memberName, metaModel.getName());
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel); 
			throw new RemoteException(XavaResources.getString("validate_error", memberName, metaModel.getName()));				
		}
	}
	
	private boolean referenceHasValue(Map values) {
		if (values == null) return false;
		Iterator it = values.values().iterator();
		while (it.hasNext()) {
			Object v = it.next();
			if (v == null) continue;
			if (v instanceof String && ((String) v).trim().equals("")) continue;
			// 0 is not treated as no-value, because 0 can be a valid key for a reference 
			return true;
		}		
		return false;
	}
	
	private void validate(Messages errors, MetaModel metaModel, Map values, Map keyValues, Object container, boolean creating) 
		throws ObjectNotFoundException, XavaException, RemoteException 
	{ 
		validate(errors, metaModel, values, keyValues, container, creating, null);
	}
	
	private void validate(Messages errors, MetaModel metaModel, Map values, Map keyValues, Object container, boolean creating, String containerMember) 
		throws ObjectNotFoundException, XavaException, RemoteException {		
		Iterator it = values.entrySet().iterator();		
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			String name = (String) en.getKey();
			Object value = en.getValue();
			validate(errors, metaModel, name, value, creating, containerMember); 
		}
		if (metaModel.containsValidadors()) {
			validateWithModelValidator(errors, metaModel, values, keyValues, container, creating);			
		}
	}
	
	private void validateWithModelValidator(Messages errors, MetaModel metaModel, Map values, Map keyValues, Object container, boolean creating) 
			throws ObjectNotFoundException, XavaException {
		try {
			Iterator itValidators = metaModel.getMetaValidators().iterator();
			while (itValidators.hasNext()) {
				MetaValidator metaValidator = (MetaValidator) itValidators.next();
				Iterator itSets =  metaValidator.getMetaSetsWithoutValue().iterator();
				if (!creating && metaValidator.isOnlyOnCreate()) continue; 
				IValidator v = metaValidator.createValidator();
				PropertiesManager mp = new PropertiesManager(v);
				while (itSets.hasNext()) {
					MetaSet set = (MetaSet) itSets.next();					
					Object value = values.get(set.getPropertyNameFrom());
					if (metaModel.containsMetaReference(set.getPropertyNameFrom())) {
						MetaReference ref = metaModel.getMetaReference(set.getPropertyNameFrom());
						if (!creating && keyValues != null && value == null && !values.containsKey(set.getPropertyNameFrom())) {
							Object model = findEntity(metaModel, keyValues);
							PropertiesManager modelPM = new PropertiesManager(model);
							value = modelPM.executeGet(set.getPropertyNameFrom());							
						}
						else if (ref.isAggregate()) {							
							value = mapToReferencedObject(metaModel, set.getPropertyNameFrom(), (Map) value);
						}
						else {							
							MetaModel referencedEntity = ref.getMetaModelReferenced();								
							try {
								if (value != null) {
									value = findEntity(referencedEntity, (Map) value);										
								}
							}
							catch (ObjectNotFoundException ex) {			
								value = null;
								if (set.getPropertyNameFrom().equals(metaModel.getContainerReference())) {
									if (container == null) {							
										Object object = findEntity(metaModel, keyValues);
										value = XObjects.execute(object, "get" + Strings.firstUpper(metaModel.getContainerReference()));
									}
								}									
							}																															
						}		
					}
					else if (value == null && !values.containsKey(set.getPropertyNameFrom())) {		
						if (keyValues != null) {							
							Map memberName = new HashMap();
							memberName.put(set.getPropertyNameFrom(), null);
							Map memberValue = getValues(metaModel, keyValues, memberName);
							value = memberValue.get(set.getPropertyNameFrom());							
						}
						else {
							Map valuesForPOJO = new HashMap(values); 
							removeViewProperties(metaModel, valuesForPOJO);
							Object model = metaModel.toPOJO(valuesForPOJO);
							PropertiesManager modelPM = new PropertiesManager(model);
							value = modelPM.executeGet(set.getPropertyNameFrom());
						}
					}		

					mp.executeSet(set.getPropertyName(), value);									
				}							
				v.validate(errors);
			}		
		} catch (ObjectNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("validate_model_error", metaModel.getName());
		}
	}
	
	private void validate(MetaModel metaModel, Map values, Map keyValues, Object containerKey, boolean creating)
		throws ObjectNotFoundException, ValidationException, XavaException, RemoteException {
		Messages errors = new Messages();		
		validate(errors, metaModel, values, keyValues, containerKey, creating);		
		if (errors.contains()) {
			throw new ValidationException(errors);
		}
	}

	private void validateExistRequired(Messages errors, MetaModel metaModel, Map values, boolean excludeContainerReference, String containerReference)  
		throws XavaException {		
		Iterator it = metaModel.getRequiredMemberNames().iterator();	
		if (metaModel.getContainerReference() != null) containerReference = metaModel.getContainerReference(); 
		while (it.hasNext()) {
			String name = (String) it.next(); 
			if (excludeContainerReference && name.equals(containerReference)) continue; 
			if (!values.containsKey(name)) {				
				errors.add("required", name, metaModel.getName());
			}
		}
	}
	
	private void validateCollections(Messages errors, MetaModel metaModel)  
		throws XavaException {		
		for (MetaCollection collection: metaModel.getMetaCollections()) {			 
			if (!collection.isElementCollection() && collection.getMinimum() > 0) { 			 
				int minimum = collection.getMinimum(); 
				String elements = XavaResources.getString(minimum == 1?"element":"elements");
				errors.add("minimum_elements", minimum, "'" + elements + "'", collection.getName(), collection.getMetaModel().getName());
			}
		}		
	}
				
	private Object findEntity(MetaModel metaModel, Map keyValues) throws FinderException, XavaException, RemoteException { 		
		return getPersistenceProvider(metaModel).find(metaModel, keyValues); 
	}
	
	private Object findEntityByAnyProperty(MetaModel metaModel, Map keyValues) throws FinderException, XavaException, RemoteException {
		return getPersistenceProvider(metaModel).findByAnyProperty(metaModel, keyValues); 
	}
	
	private void rollback(MetaModel metaModel) throws RemoteException {
		if (getSessionContext() != null) getSessionContext().setRollbackOnly();
		getPersistenceProvider(metaModel).rollback();
		HibernateValidatorInhibitor.setInhibited(false); 
	}
	
	private void freeTransaction() throws RemoteException { 
		HibernateValidatorInhibitor.setInhibited(false); 
	}
			
	private void executePostremoveCollectionElement(MetaModel metaModel, Map keyValues, MetaCollection metaCollection) throws FinderException, ValidationException, XavaException, RemoteException {
		Iterator itCalculators = metaCollection.getMetaCalculatorsPostRemove().iterator();
		while (itCalculators.hasNext()) {
			MetaCalculator metaCalculator = (MetaCalculator) itCalculators.next();			
			ICalculator calculator = metaCalculator.createCalculator();
			PropertiesManager mp = new PropertiesManager(calculator);
			Collection sets =  metaCalculator.getMetaSetsWithoutValue();
			if (metaCalculator.containsMetaSetsWithoutValue()) {
				Map neededPropertyNames = new HashMap();
				Iterator itSets = sets.iterator();
				while (itSets.hasNext()) {
					MetaSet set = (MetaSet) itSets.next();
					neededPropertyNames.put(set.getPropertyNameFrom(), null);
				}												
				Map values = getValues(metaModel, keyValues, neededPropertyNames);
				
				itSets = sets.iterator();											
				while (itSets.hasNext()) {
					MetaSet set = (MetaSet) itSets.next();
					Object value = values.get(set.getPropertyNameFrom());
					if (value == null && !values.containsKey(set.getPropertyNameFrom())) {
						if (keyValues != null) { 
							Map memberName = new HashMap();
							memberName.put(set.getPropertyNameFrom(), null);
							Map memberValue = getValues(metaModel, keyValues, memberName);
							value = memberValue.get(set.getPropertyNameFrom());
						}											
					}
						
					if (metaModel.containsMetaReference(set.getPropertyNameFrom())) {
						MetaReference ref = metaModel.getMetaReference(set.getPropertyNameFrom());
						if (ref.isAggregate()) {
							value = mapToReferencedObject(metaModel, set.getPropertyNameFrom(), (Map) value);
						}
						else {
							MetaModel referencedEntity = ref.getMetaModelReferenced();
							try {
								value = findEntity(referencedEntity, (Map) value);
							}
							catch (ObjectNotFoundException ex) {
								value = null;
							}
																																
						}						
					}
					try {			
						mp.executeSet(set.getPropertyName(), value);
					}
					catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						throw new XavaException("calculator_property_error", value, set.getPropertyName());
					}									
				}
			}			
			
			if (calculator instanceof IModelCalculator) {
				Object entity = findEntity(metaModel, keyValues);
				try {
					((IModelCalculator) calculator).setModel(entity);
				}
				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					throw new XavaException("assign_entity_to_calculator_error", metaModel.getName(), keyValues);
				}									
				
			}
			if (calculator instanceof IEntityCalculator) {
				Object entity = findEntity( metaModel, keyValues);
				try {
					((IEntityCalculator) calculator).setEntity(entity);
				}
				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					throw new XavaException("assign_entity_to_calculator_error", metaModel.getName(), keyValues);
				}									
				
			}
			
			try {
				calculator.calculate();
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				rollback(metaModel); 
				throw new RemoteException(XavaResources.getString("postremove_error", metaModel.getName(), keyValues));
			}
		}				
	}
	
	private Map convertSubmapsInObject(MetaModel metaModel, Map values) throws ValidationException, XavaException, RemoteException { 
		Map result = new HashMap();
		Iterator it = values.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			String memberName = (String) en.getKey();
			if (!memberName.equals(MapFacade.MODEL_NAME)) {
				Object value = null;
				if (metaModel.containsMetaProperty(memberName)) {
					value = en.getValue();
				}
				else if (metaModel.containsMetaReference(memberName)) {
					value = mapToReferencedObject(metaModel, memberName, (Map) en.getValue());
				}
				else if (metaModel.getMapping().hasPropertyMapping(memberName)) {
					value = en.getValue();
				}
				else if (metaModel.containsMetaCollection(memberName)) {
					MetaCollection collection = metaModel.getMetaCollection(memberName);
					if (collection.isElementCollection()) {
						Collection elementValues = mapsToElements(collection, (Collection<Map>) en.getValue());
						validateElements(elementValues); // To execute Bean Validation and Hibernate Validator 
							// because they are no executed by default in element collection, 
							// unless you use @Valid even so it's only valid for Bean Validation.
						value = elementValues;
					}
					else {
						throw new XavaException("mapfacade_only_element_collections", memberName, metaModel.getName()); 
					}
				}
				else {				
					throw new XavaException("member_not_found", memberName, metaModel.getName());
				}
				result.put(memberName, value);
			}
		}
		return result;
	}
	
	private Object findEntity(String modelName, Map keyValues)
		throws FinderException, RemoteException {
		MetaModel metaModel = getMetaModel(modelName);
		try {
			return findEntity(metaModel, keyValues);			
		} catch (FinderException ex) {
			throw ex;
		} catch (ElementNotFoundException ex) {			
			rollback(metaModel);
			throw new RemoteException(XavaResources.getString("model_not_found", modelName));
		} catch (RuntimeException ex) {
			rollback(metaModel);
			throw ex;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel);
			throw new RemoteException(XavaResources.getString("find_error", modelName));
		}
	}
	
	private Object findEntityByAnyProperty(String modelName, Map keyValues) 
		throws FinderException, RemoteException {
		MetaModel metaModel = getMetaModel(modelName);
		try {
			return findEntityByAnyProperty(metaModel, keyValues);			
		} catch (FinderException ex) {
			throw ex;
		} catch (ElementNotFoundException ex) {
			rollback(metaModel);
			throw new RemoteException(XavaResources.getString("model_not_found", modelName));
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			rollback(metaModel);
			throw new RemoteException(XavaResources.getString("find_error", modelName));
		}
	}		
	
	public Object getKey(MetaModel metaModel, Map keyValues) throws XavaException, RemoteException {
		return getPersistenceProvider(metaModel).getKey(metaModel, keyValues); 
	}
	
	public void reassociate(Object entity) throws RemoteException {
		getPersistenceProvider(MetaModel.getForPOJO(entity)).reassociate(entity); 
	}

	private IPersistenceProvider getPersistenceProvider(MetaModel metaModel) { 
		return metaModel.getMetaComponent().getPersistenceProvider();
	}

}


