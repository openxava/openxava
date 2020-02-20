package org.openxava.model;

import java.util.*;
import java.rmi.*;
import javax.ejb.*;
import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.ejbx.*;
import org.openxava.model.impl.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;


/** 
 * Allows manage model objects in <code>Map</code> format. <p>
 * 
 * It's used in generic OpenXava action to make CRUD operations,
 * but if it's convenient for you, you can use directly.<p>
 * 
 * A principle a good design is use maps for generic or automatic
 * things, but in all other cases the use of the model objects directly
 * is better, because the compiler do a good work for us, we can use
 * method calls, etc.<p>
 * 
 * We use the EJB exceptions (CreateException, FinderException, RemoveException,
 * etc) with the typical semantic associated to each. Although the implementation
 * does not use EJB.<br>
 * We use RemoteException to indicate a system error. Although the implementation
 * is local.<br>
 *
 * Since version 3.0 MapFacade uses runtime exception for system errors,
 * before (in v2.x) it used RemoteException.<br>
 * 
 * The first parameter of each method is <code>modelName</code>, this is a
 * name of a OpenXava component (Customer, Invoice, etc) or a qualified aggregate 
 * (Invoice.InvoiceDetail for example).<p>  
 * 
 * <h4>Transactional behaviour</h4>
 * <code>MapFacade</code> has transactional behaviour inside your action or test (since 2.2.5). 
 * That is, you can write the next code inside an action or test:
 * <pre>
 * public void execute() throws Exception {
 * 		...
 * 		MapFacade.create("Customer", customerValue);
 * 		MapFacade.create("Invoice", invoiceValue);
 * 		... 
 * }
 * </pre>
 * If <code>Invoice</code> creation fails, the Customer will not be saved; moreover 
 * if any other exception is thrown by other sentence of the action. Both Customer
 * and Invoice data will not be saved.<br>
 * The data is flushed in each MapFacade call, but not committed.<br>
 * This behavior can be modified for your application with the next property
 * in xava.properties file:
 * <pre>
 * mapFacadeAutoCommit=true
 * </pre>  
 * If you mapFacadeAutoCommit=true or mapFacadeAsEJB=true and you execute the above
 * code, if the creation of <code>Invoice</code> fails, the <code>Customer</code>
 * is already saved and committed and it will not be removed.
 * <p>
 * When autocommit is not used (the default) you can do a commit programatically, using
 * the @{link #commit()} method. In this way: 
 *<pre>
 * public void execute() throws Exception {
 * 		...
 * 		MapFacade.create("Customer", customerValue);
 * 		MapFacade.commit();
 * 		MapFacade.create("Invoice", invoiceValue);
 * 		... 
 * }
 * </pre>
 * 
 * @author Javier Paniza
 */

public class MapFacade {
	
	public static final String MODEL_NAME = "__MODEL_NAME__";
	private static Log log = LogFactory.getLog(MapFacade.class);
	private static Map remotes;
	private static boolean usesEJBObtained;
	private static boolean usesEJB;
	private static IMapFacadeImpl localImpl;
	

	/**
	 * Creates a new entity from a map with its initial values. <p> 
	 * 
	 * @param modelName  OpenXava model name. Not null
	 * @param values  Initial values for create the entity. Not null. <i>By value</i> semantics.
	 * @return Created entity, not a map it's the created object
	 *          (EntityBean, POJO object o the form used in the underlying model). Not null.
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction. 
	 * @exception SystemException  System problem. Rollback transaction. 
	 */
	public static Object create(String modelName, Map values) 
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, values);					
		try {									
			return getImpl(modelName).create(Users.getCurrentUserInfo(), modelName, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).create(Users.getCurrentUserInfo(), modelName, values);
			}
			catch (RemoteException rex) { 
				throw new SystemException(rex);
			}
		}							
	}
		
		
	/**
	 * Commit in database the changes done using MapFacade. <p>
	 * 
	 * It's used rarely because OpenXava module controller commits automatically
	 * after each action execution. ModuleTestBase also commits automatically.
	 * 
	 * It's cannot be used if MapFacade auto commit mode is on or it's used as EJB.
	 * 
	 * @throws IllegalStateException  If mapFacadeAutoCommit=true or mapFacadeAsEJB=true in xava.properties  
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static void commit() throws SystemException {						
		if (usesEJB()) {
			throw new IllegalStateException(XavaResources.getString("not_commit_when_facade_as_ejb"));
		}
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) {
			throw new IllegalStateException(XavaResources.getString("not_commit_when_facade_autocommit"));
		}
		try {
			getLocalImpl().commit(Users.getCurrentUserInfo());
		}
		catch (RemoteException rex) {
			throw new SystemException(rex);
		}
	}
	
	/**
	 * Creates a new aggregate from a map with its initial values. <p>	 
	 *
	 * @deprecated Use createAggregate(String modelName, Map containerKey, String collectionName, Map values) instead
	 * @param modelName  OpenXava model name. Not null
	 * @param containerKey  Key of entity or aggregate that contains this aggregate. <i>By value</i> semantics.
	 * @param counter Counter used to generate the aggregate key, indicates the
	 * 		order number. The aggregate implementation can ignorate it.  
	 * @param values  Initial values for create the aggregate. Not null. <i>By value</i> semantics.
	 * @return Aggregate created, not a map but the create object
	 *          (EntityBean, POJO object o the form used in the underlying model). Not null.
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction. 
	 * @exception SystemException  System problem. Rollback transaction. 
	 */
	public static Object createAggregate(String modelName, Map containerKey, int counter, Map values) 
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, containerKey, values);					
		try {		
			return getImpl(modelName).createAggregate(Users.getCurrentUserInfo(), modelName, containerKey, counter, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createAggregate(Users.getCurrentUserInfo(), modelName, containerKey, counter, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(ex);
			}
		}							
	}
	
	/**
	 * Creates a new aggregate from a map with its initial values. <p>	 
	 *
	 * @since 5.3
	 * @param modelName  OpenXava model name. Not null
	 * @param containerKey  Key of entity or aggregate that contains this aggregate. <i>By value</i> semantics.
	 * @param collectionName  The name of the collection.  
	 * @param values  Initial values for create the aggregate. Not null. <i>By value</i> semantics.
	 * @return Aggregate created, not a map but the create object
	 *          (EntityBean, POJO object o the form used in the underlying model). Not null.
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction. 
	 * @exception SystemException  System problem. Rollback transaction. 
	 */
	public static Object createAggregate(String modelName, Map containerKey, String collectionName, Map values)  
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, containerKey, values);					
		try {		
			return getImpl(modelName).createAggregate(Users.getCurrentUserInfo(), modelName, containerKey, collectionName, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createAggregate(Users.getCurrentUserInfo(), modelName, containerKey, collectionName, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(ex);
			}
		}							
	}

	
	/** 
	 * Creates a new aggregate from a map with its initial values. <p>	 
	 * 
	 * @deprecated Use createAggregate(String modelName, Map containerKey, String collectionName, Map values) instead.
	 * @param modelName  OpenXava model name. Not null
	 * @param container  Container object (or container key in object format) that contains
	 * 		the aggregate.
	 * @param counter Counter used to generate the aggregate key, indicates the
	 * 		order number. The aggregate implementation can ignorate it.  
	 * @param values  Initial values for create the aggregate. Not null. <i>By value</i> semantics.
	 * @return Aggregate created, not a map but the create object
	 *          (EntityBean, POJO object o the form used in the underlying model). Not null.
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction. 
	 * @exception SystemException  System problem. Rollback transaction. 
	 */
	public static Object createAggregate(String modelName, Object container, int counter, Map values) 
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, container, values);					
		try {									
			return getImpl(modelName).createAggregate(Users.getCurrentUserInfo(), modelName, container, counter, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createAggregate(Users.getCurrentUserInfo(), modelName, container, counter, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}							
	}
	
	/**	
	 * Creates a new entity from a map with its initials values and
	 * return a map with the values of created entity. <p>
	 *  
	 * @param modelName  OpenXava model name. Not null
	 * @param values  Initial values to create entity. Not null. <i>By value</i> semantics.
	 * @return A map with the created object values. The properties are the
	 * 		sent ones on create. 
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static Map createReturningValues(String modelName, Map values)
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, values);		
		try {
			return getImpl(modelName).createReturningValues(Users.getCurrentUserInfo(), modelName, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createReturningValues(Users.getCurrentUserInfo(), modelName, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}
		
	}
	
	
	/**
	 * Creates a new entity from a map with its initial values and
	 * return a map with the key values of the created entity. <p>
	 *
	 * @param modelName  OpenXava model name. Not null
	 * @param values  Initial values to create the entity. Not null. <i>By value</i> semantics.
	 * @return A map with key value of created object
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static Map createReturningKey(String modelName, Map values)
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, values);		
		try {
			return getImpl(modelName).createReturningKey(Users.getCurrentUserInfo(), modelName, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createReturningKey(Users.getCurrentUserInfo(), modelName, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}		
	}
	
	/**
	 * Creates a new entity from a map with its initial values and
	 * return a map with the key values of the created entity. <p>
	 * 
	 * This method does not validate collections.
	 *
	 * @param modelName  OpenXava model name. Not null
	 * @param values  Initial values to create the entity. Not null. <i>By value</i> semantics.
	 * @return A map with key value of created object
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static Map createNotValidatingCollections(String modelName, Map values)
		throws
			CreateException,ValidationException,
			XavaException, SystemException
	{
		Assert.arg(modelName, values);		
		try {
			return getImpl(modelName).createNotValidatingCollections(Users.getCurrentUserInfo(), modelName, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createNotValidatingCollections(Users.getCurrentUserInfo(), modelName, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}		
	}
	
				
	/**	
	 * Creates a new aggregate from a map with its initial values
	 * and return a map with the key. <p>  	 
	 *
	 * @deprecated Use createAggregateReturningKey(String modelName, Map containerKey, String collectionName, Map values) instead. 
	 * @param modelName  OpenXava model name. Not null
	 * @param containerKey  Key of entity or aggregate that contains this aggregate. <i>By value</i> semantics.	
 	 * @param counter Counter used to generate the aggregate key, indicates the
	 * 		order number. The aggregate implementation can ignore it.  
	 * @param values  Initial values for create the aggregate. Not null. <i>By value</i> semantics.
	 * @return Key values of created aggregate.
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static Map createAggregateReturningKey(String modelName, Map containerKey, int counter, Map values) 
		throws
			CreateException,ValidationException, 
			XavaException, SystemException 
	{
		Assert.arg(modelName, containerKey, values);					
		try {		
			return getImpl(modelName).createAggregateReturningKey(Users.getCurrentUserInfo(), modelName, containerKey, counter, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createAggregateReturningKey(Users.getCurrentUserInfo(), modelName, containerKey, counter, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}							
	}
	
	/**
	 * Creates a new aggregate from a map with its initial values
	 * and return a map with the key. <p>  	 
	 *
	 * @since 5.3
	 * @param modelName  OpenXava model name. Not null
	 * @param containerKey  Key of entity or aggregate that contains this aggregate. <i>By value</i> semantics.	
 	 * @param collectionName  Name of the collection.  
	 * @param values  Initial values for create the aggregate. Not null. <i>By value</i> semantics.
	 * @return Key values of created aggregate.
	 * @exception CreateException  Logic problem on creation.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static Map createAggregateReturningKey(String modelName, Map containerKey, String collectionName, Map values) 
		throws
			CreateException,ValidationException, 
			XavaException, SystemException 
	{
		Assert.arg(modelName, containerKey, values);					
		try {		
			return getImpl(modelName).createAggregateReturningKey(Users.getCurrentUserInfo(), modelName, containerKey, collectionName, values);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).createAggregateReturningKey(Users.getCurrentUserInfo(), modelName, containerKey, collectionName, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}							
	}
	
	
	
	/**
	 * Obtain the specified values from entity/aggregate from a map with 
	 * primary key values. <p>
	 * 
	 * The <code>memberNames</code> parameter is a map to use a treelike structure.<br>
	 * The property names are in key part. If it's a simple property the value
	 * is null, otherwise it has a map with the same structure.<br>
	 * For example, if we have a <code>Customer</tt> that references
	 * to a <code>Seller</code>,
	 * we can send a map with the next values:
	 * <pre> 
	 * { "number", null }
	 * { "name", null }
	 * { "seller", { {"number", null}, {"name", null} } }
	 * </pre>
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param keyValues  Key values of object to find. Not null. <i>By value</i> semantics.
	 * @param memberNames Member names to obtain its values. Not null. <i>By value</i> semantics.  
	 * @return Map with entity values. Not null.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static Map getValues(
		String modelName,
		Map keyValues,
		Map memberNames)
		throws FinderException, XavaException, SystemException 
	{							
		Assert.arg(modelName, keyValues, memberNames);		
		if (keyValues.isEmpty()) {
			throw new ObjectNotFoundException(XavaResources.getString("empty_key_object_not_found", modelName));						
		}
		try {			
			return getImpl(modelName).getValues(Users.getCurrentUserInfo(), modelName, keyValues, memberNames);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).getValues(Users.getCurrentUserInfo(), modelName, keyValues, memberNames);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}		
	}

	/**
	 * Obtain the specified values from entity/aggregate searching it by any property. <p>
	 * 
	 * The <code>memberNames</tt> parameter is a map to use a treelike structure.<br>
	 * The property names are in key part. If it's a simple property the value
	 * is null, otherwise it has a map with the same structure.<br>
	 * For example, if we have a <code>Customer</tt> that references
	 * to a <code>Seller</code>,
	 * we can send a map with the next values:
	 * <pre> 
	 * { "number", null }
	 * { "name", null }
	 * { "seller", { {"number", null}, {"name", null} } }
	 * </pre>
	 * 
	 * The <code>searchingValues</code> parameters are the values used to search. 
	 * For example, if you can search by <i>name</i> and <i>surname</i> you can 
	 * send to <code>searchingValues</code> a map with the next values: 	 
	 * <pre> 
	 * { "name", "JUAN" }
	 * { "surname", "PEREZ" }
	 * </pre>
	 * In this case it returns the map with the value of the first "JUAN PEREZ" of database.<p>
	 * 
	 * If you use:
	 * <pre>
	 * { "name", "J" }
	 * </pre> 
	 * Then it returns the values for the first object of which <i>name</i> starts with 'J'.<p>
	 * 
	 * If you use:
	 * <pre>
	 * { "description", "%BIG" }
	 * </pre>  
	 * Then it returns the values for the first object of which <i>description</i> contains
	 * "BIG".
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param searchingValues  Values used for search the object. Not null. <i>By value</i> semantics.
	 * @param memberNames Member names to obtain its values. Not null. <i>By value</i> semantics.  
	 * @return Map with entity values. Not null.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */		
	public static Map getValuesByAnyProperty(  
		String modelName,
		Map searchingValues,
		Map memberNames)
		throws FinderException, XavaException, SystemException 
	{						
		Assert.arg(modelName, searchingValues, memberNames);		
		if (searchingValues.isEmpty()) {
			throw new ObjectNotFoundException(XavaResources.getString("empty_key_object_not_found", modelName));						
		}
		try {
			return getImpl(modelName).getValuesByAnyProperty(Users.getCurrentUserInfo(), modelName, searchingValues, memberNames);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).getValuesByAnyProperty(Users.getCurrentUserInfo(), modelName, searchingValues, memberNames);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}					
	}
			
	/**
	 * Obtain the values of the entity/aggregate from the own entity. <p> 
	 * 
	 * The <code>memberNames</tt> parameter is a map to use a treelike structure.<br>
	 * The property names are in key part. If it's a simple property the value
	 * is null, otherwise it has a map with the same structure.<br>
	 * For example, if we have a <code>Customer</tt> that references
	 * to a <code>Seller</code>,
	 * we can send a map with the next values:
	 * <pre> 
	 * { "number", null }
	 * { "name", null }
	 * { "seller", { {"number", null}, {"name", null} } }
	 * </pre>
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param entity  Object to obtain values from it. Not null.
	 * @param memberNames Member names to obtain its values. Not null. <i>By value</i> semantics.  
	 * @return Map with entity values. Not null.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static Map getValues(String modelName, Object entity, Map memberNames)
		throws XavaException, SystemException 
	{		
		Assert.arg(modelName, entity, memberNames);
		try {
			return getImpl(modelName).getValues(Users.getCurrentUserInfo(), modelName, entity, memberNames);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				return getImpl(modelName).getValues(Users.getCurrentUserInfo(), modelName, entity, memberNames);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}			
	}
	
	/** 
	 * Obtains the values of the key of entity/aggregate. <p> 
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param entity  Object to obtain key values from it. Not null.  
	 * @return Map with key values. Not null.
	 * @exception XavaException  Any problem related to OpenXava.
	 * @exception SystemException  System problem.
	 */	
	public static Map getKeyValues(String modelName, Object entity) 
		throws XavaException, SystemException 
	{		
		Assert.arg(modelName, entity);
		try {
			return getLocalImpl().getKeyValues(Users.getCurrentUserInfo(), modelName, entity);
		}
		catch (RemoteException rex) {
			throw new SystemException(rex);
		}		
	}
	
	
	/**
	 * Obtain the entity/aggregate from a map with key values. <p>
	 * 
	 * @param modelName  OpenXava model name. Not null
	 * @param keyValues  Key values of entity to find. Not null. <i>By value</i> semantics.
 	 * @return The entity or aggregate. Not null
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception SystemException  System problem. Rollback transaction.
	 */ 
	public static Object findEntity(String modelName, Map keyValues)
		throws ObjectNotFoundException, FinderException, SystemException 
	{	
		if (keyValues==null) return null;
		Assert.arg(modelName, keyValues);
		Object entity = null;
		try {
			entity = getImpl(modelName).findEntity(Users.getCurrentUserInfo(), modelName, keyValues);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				entity = getImpl(modelName).findEntity(Users.getCurrentUserInfo(), modelName, keyValues);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}					
		reassociate(entity);		
		return entity;
	}	

	/**
	 * Reassociate the entity to its persistent storage. <p>
	 * 
	 * It's called when an object is receive from the an EJB server.
	 */
	private static void reassociate(Object entity) throws SystemException {
		if (XavaPreferences.getInstance().isMapFacadeAsEJB()) {	
			try {
				getLocalImpl().reassociate(entity);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}
	}

	/**
	 * Remove the entity/aggregate from a map with its key. <p> 
	 * 
	 * @param modelName  OpenXava model name. No puede ser nulo.
	 * @param keyValues  Valores con la clave de la entidad a borrar. Nunca nulo. <i>By value</i> semantics.
	 * @exception RemoveException  Logic problem on remove.
	 * @exception ValidationException  Data validation problems.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */
	public static void remove(String modelName, Map keyValues)
		throws RemoveException, SystemException, XavaException, ValidationException {		
		Assert.arg(modelName, keyValues);
		try {
			getImpl(modelName).delete(Users.getCurrentUserInfo(), modelName, keyValues);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				getImpl(modelName).delete(Users.getCurrentUserInfo(), modelName, keyValues);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}		
	}

	/**
	 * Set new values to a entity/aggregate that is found from its key values. <p>
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param keyValues  Key values of object. Not null. <i>By value</i> semantics.
	 * @param values  New values to set. Not null. <i>By value</i> semantics.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception ValidationException  Data validation problems.	  
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static void setValues(String modelName, Map keyValues, Map values)
		throws ObjectNotFoundException, FinderException, ValidationException,
				XavaException, SystemException 
	{		
		Assert.arg(modelName, keyValues, values);				
		try {
			getImpl(modelName).setValues(Users.getCurrentUserInfo(), modelName, keyValues, values);								
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				getImpl(modelName).setValues(Users.getCurrentUserInfo(), modelName, keyValues, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}			
		}						
	}
	
	/**
	 * Set new values to a entity/aggregate that is found from its key values without tracking the changes. <p>
	 * 
	 * All methods of MapFacade track the changes (using AccessTracker), but this one.
	 * 
	 * @since 5.9
	 * @param modelName  OpenXava model name. Not null.
	 * @param keyValues  Key values of object. Not null. <i>By value</i> semantics.
	 * @param values  New values to set. Not null. <i>By value</i> semantics.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception ValidationException  Data validation problems.	  
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static void setValuesNotTracking(String modelName, Map keyValues, Map values) 
		throws ObjectNotFoundException, FinderException, ValidationException,
				XavaException, SystemException 
	{		
		Assert.arg(modelName, keyValues, values);				
		try {
			getImpl(modelName).setValuesNotTracking(Users.getCurrentUserInfo(), modelName, keyValues, values);								
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				getImpl(modelName).setValuesNotTracking(Users.getCurrentUserInfo(), modelName, keyValues, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}			
		}						
	}
	
	/**	 
	 * Validates the sent values but does not create or update the object. <p>
	 *
	 * Only validates the sent data, it does not certify that exist all needed data
	 * to create a new object.<br> 
	 * 
	 * @param modelName  OpenXava model name, can be an qualified aggregate. Not null.
	 * @param values  Values to validate. Not null. <i>By value</i> semantics.
	 * @return Message list with validation errors. Not null.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static Messages validate(String modelName, Map values)
		throws XavaException, SystemException 
	{
		Assert.arg(modelName, values);			
		try {
			return getImpl(modelName).validate(Users.getCurrentUserInfo(), modelName, values);								
		}
		catch (RemoteException ex) {			
			annulImpl(modelName);
			try {
				return getImpl(modelName).validate(Users.getCurrentUserInfo(), modelName, values);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}				
	}
	
	
	/**
	 * Validates the sent values and if required values are included, but does not create or update the object. <p>
	 *
	 * Validates the sent data and certify that exist all needed data to create a new object.<br> 
	 * 
	 * @since 6.0
	 * @param modelName  OpenXava model name, can be an qualified aggregate. Not null.
	 * @param values  Values to validate. Not null. <i>By value</i> semantics.
	 * @return Message list with validation errors. Not null.
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static Messages validateIncludingMissingRequired(String modelName, Map values)
		throws XavaException, SystemException 
	{
		Assert.arg(modelName, values);			
		try {
			return getImpl(modelName).validateIncludingMissingRequired(Users.getCurrentUserInfo(), modelName, values, null); 							
		}
		catch (RemoteException ex) {			
			annulImpl(modelName);
			try {
				return getImpl(modelName).validateIncludingMissingRequired(Users.getCurrentUserInfo(), modelName, values, null); 
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}				
	}
	
	public static Messages validateIncludingMissingRequired(String modelName, Map values, String containerReference) 
		throws XavaException, SystemException 
	{
		Assert.arg(modelName, values);			
		try {
			return getImpl(modelName).validateIncludingMissingRequired(Users.getCurrentUserInfo(), modelName, values, containerReference);								
		}
		catch (RemoteException ex) {			
			annulImpl(modelName);
			try {
				return getImpl(modelName).validateIncludingMissingRequired(Users.getCurrentUserInfo(), modelName, values, containerReference);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}	
		}				
	}									
	
	private static IMapFacadeImpl getImpl(String modelName) throws SystemException {
		if (!usesEJB()) return getLocalImpl();
		try {			
			int idx = modelName.indexOf('.'); 
			if (idx >=0) {
				modelName = modelName.substring(0, idx);				 				
			}			
			String paquete = MetaComponent.get(modelName).getPackageNameWithSlashWithoutModel();			
			MapFacadeRemote remote = (MapFacadeRemote) getRemotes().get(paquete);
			if (remote == null) {					
				MapFacadeHome home = (MapFacadeHome) BeansContext.get().lookup("ejb/"+paquete+"/MapFacade");
				remote = home.create();
				getRemotes().put(paquete, remote);				
			}		
			return remote;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new SystemException(XavaResources.getString("facade_remote", modelName));
		}		
	}
			
	/**
	 * Convert from a map with primary key values to primary key object. <p> 
	 */		
	public static Object toPrimaryKey(String entityName, Map keyValues) throws XavaException {
		try {
			MetaEntity m = (MetaEntity) MetaComponent.get(entityName).getMetaEntity();
			return getLocalImpl().getKey(m, keyValues);
		}
		catch (RemoteException ex) { 
			log.error(ex.getMessage(), ex);
			throw new XavaException(ex.getMessage());
		}
		catch (ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("no_entity_bean", entityName);
		}
	}
	
	private static Map getRemotes() {
		if (remotes == null) {
			remotes = new HashMap();
		}
		return remotes;
	}
	
	private static void annulImpl(String modelName) {
		if (!usesEJB()) return;
		try {
			int idx = modelName.indexOf('.'); 
			if (idx >=0) {
				modelName = modelName.substring(0, idx);				 				
			}			
			String paquete = MetaComponent.get(modelName).getPackageNameWithSlashWithoutModel();			
			getRemotes().remove(paquete);			
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("cache_facade_remote_warning"), ex);		
		}		
	}


	/**
	 * Removes an elemente from a collection. <p>
	 * 
	 * If it's a aggregate remove the aggregate, and if it's a entity reference
	 * make the left to point to the parent object, hence left the collection.<br>
	 *
	 * Does not delete aggregates directly, but with this method, because
	 * thus the needed logic for remove a element from a collection is executed.<br>   
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param keyValues  Key value of the container of the collection. Not null. <i>By value</i> semantics.
	 * @param collectionName  Collection name of the container collection of element to remove. Not null.
	 * @param collectionElementKeyValues  Key value of element to remove. Not null. <i>By value</i> semantics.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception ValidationException  Data validation problems.
	 * @exception RemoveException  Logic problem on remove. 
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */	
	public static void removeCollectionElement(String modelName, Map keyValues, String collectionName, Map collectionElementKeyValues) 
		throws ObjectNotFoundException, FinderException, ValidationException, RemoveException,
			XavaException, SystemException 
	{
		Assert.arg(modelName, keyValues, collectionName, collectionElementKeyValues);
		try {
			getImpl(modelName).removeCollectionElement(Users.getCurrentUserInfo(), modelName, keyValues, collectionName, collectionElementKeyValues);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				getImpl(modelName).removeCollectionElement(Users.getCurrentUserInfo(), modelName, keyValues, collectionName, collectionElementKeyValues);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}
	}
	
	/** 
	 * Add an element to a collection. <p>
	 * 
	 * It does not create the element, only adds it to the collection, therefore
	 * for aggregate collections it's not useful using this method, it's better to create
	 * the aggregate using <code>createAggregate</code> methods.
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param keyValues  Key value of the container of the collection. Not null. <i>By value</i> semantics.
	 * @param collectionName  Collection name of the container collection of element to add. Not null.
	 * @param collectionElementKeyValues  Key value of element to add. Not null. <i>By value</i> semantics.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception ValidationException  Data validation problems. 
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction. 
	 */	
	public static void addCollectionElement(String modelName, Map keyValues, String collectionName, Map collectionElementKeyValues) 
		throws ObjectNotFoundException, FinderException, ValidationException, XavaException, SystemException 
	{
		Assert.arg(modelName, keyValues, collectionName, collectionElementKeyValues);
		try {
			getImpl(modelName).addCollectionElement(Users.getCurrentUserInfo(), modelName, keyValues, collectionName, collectionElementKeyValues);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				getImpl(modelName).addCollectionElement(Users.getCurrentUserInfo(), modelName, keyValues, collectionName, collectionElementKeyValues);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}		
	}	
	
	/** 
	 * Move an element from a collection to another. <p>
	 * 
	 * @since 5.9
	 * @param sourceContainerModelName  OpenXava model name of the container of the source collection. Not null.
	 * @param sourceContainerKeyValues  Key values of the container of the source collection. Not null. <i>By value</i> semantics.
	 * @param sourceCollectionName  Source collection name. Not null.
	 * @param targetContainerModelName  OpenXava model name of the container of the target collection. Not null.
	 * @param targetContainerKeyValues  Key values of the container of the target collection. Not null. <i>By value</i> semantics.
	 * @param targetCollectionName  Target collection name. Not null.
	 * @param collectionElementKeyValues  Key value of element to move. Not null. <i>By value</i> semantics.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception ValidationException  Data validation problems. 
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction.
	 */ 
	public static void moveCollectionElementToAnotherCollection( 
		String sourceContainerModelName, Map sourceContainerKeyValues, String sourceCollectionName, 
		String targetContainerModelName, Map targetContainerKeyValues, String targetCollectionName,
		Map collectionElementKeyValues) 
		throws ObjectNotFoundException, FinderException, ValidationException, XavaException, SystemException
	{
		Assert.arg(sourceContainerModelName, sourceContainerKeyValues, sourceCollectionName, targetContainerModelName, targetContainerKeyValues, targetCollectionName, collectionElementKeyValues);
		try {
			getImpl(sourceContainerModelName).moveCollectionElementToAnotherCollection(Users.getCurrentUserInfo(),
				sourceContainerModelName, sourceContainerKeyValues, sourceCollectionName, 
				targetContainerModelName, targetContainerKeyValues, targetCollectionName,
				collectionElementKeyValues);
		}
		catch (RemoteException ex) {
			annulImpl(sourceContainerModelName);
			try {
				getImpl(sourceContainerModelName).moveCollectionElementToAnotherCollection(Users.getCurrentUserInfo(),
					sourceContainerModelName, sourceContainerKeyValues, sourceCollectionName, 
					targetContainerModelName, targetContainerKeyValues, targetCollectionName,
					collectionElementKeyValues);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}		
	}
	
	/** 
	 * Move an element in a collection. <p>
	 * 
	 * The collection must be sortable, in JPA it means to be a List with @OrderColumn.
	 * 
	 * @param modelName  OpenXava model name. Not null.
	 * @param keyValues  Key value of the container of the collection. Not null. <i>By value</i> semantics.
	 * @param collectionName  Collection name of the container collection of element to move. Not null.
	 * @param from  Original position of the element in the collection. Zero based.
	 * @param to  Position in the collection where the element will be moved. Zero based.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @exception SystemException  System problem. Rollback transaction. 
	 * @since 5.6.1
	 */	
	public static void moveCollectionElement(String modelName, Map keyValues, String collectionName, int from, int to) 
		throws ObjectNotFoundException, FinderException, XavaException, SystemException 
	{
		Assert.arg(modelName, keyValues, collectionName);
		try {
			getImpl(modelName).moveCollectionElement(Users.getCurrentUserInfo(), modelName, keyValues, collectionName, from, to);
		}
		catch (RemoteException ex) {
			annulImpl(modelName);
			try {
				getImpl(modelName).moveCollectionElement(Users.getCurrentUserInfo(), modelName, keyValues, collectionName, from, to);
			}
			catch (RemoteException rex) {
				throw new SystemException(rex);
			}
		}		
	}	
	

	private static boolean usesEJB() {
		if (!usesEJBObtained) {
			usesEJB = XavaPreferences.getInstance().isMapFacadeAsEJB();
			usesEJBObtained = true;
		}		
		return usesEJB;
	}
	
	private static IMapFacadeImpl getLocalImpl() {
		if (localImpl==null) {
			localImpl = new MapFacadeBean();
		}
		return localImpl;
	}
	
}