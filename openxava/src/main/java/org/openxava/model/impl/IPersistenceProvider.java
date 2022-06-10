package org.openxava.model.impl;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * Provides the implementation of the persistence services
 * used in {@link MapFacadeBean}. <p>
 * 
 * For all methods you can use any RuntimeException as system exception
 * (this exception always abort the operation and rollback the transaction).
 * You can use PersistenceProviderException as RuntimeException, but this is
 * not mandatory. You can throw PersistenceException, JDOException, HibernateException, 
 * EJBException or whatever RuntimeException your want.<br>  
 * 
 * @author Mª Carmen Gimeno Alabau
 */
public interface IPersistenceProvider extends java.io.Serializable { 
	
	/**
	 * Find an object by any property (or properties). <p>
	 *  
	 * Returns the first object that match with the sent arguments (<code>searchingValues</code>). 
	 * 
	 * @return Never null.
	 */	
	Object findByAnyProperty(MetaModel metaModel, Map searchingValues) throws ObjectNotFoundException, FinderException, XavaException;
	
	/**
	 * Find an object from its key in map format. <p>
	 * 
	 * @return Never null.
	 */
	Object find(MetaModel metaModel, Map keyValues) throws ObjectNotFoundException, FinderException, XavaException;
	
	/**
	 * Find a object from its key object. <p>
	 * 
	 * @return Never null.
	 */	
	Object find(MetaModel metaModel, Object key) throws ObjectNotFoundException, FinderException;
	
	/**
	 * Return an IPropertiesContainer to manage using introspection the sent object. <p>
	 */
	IPropertiesContainer toPropertiesContainer(MetaModel metaModel, Object modelObject) throws XavaException;
	
	/**
	 * Create a persistent object (saved in database) from the data passed in map format. <p> 
	 */
	Object create(MetaModel metaModel, Map values) throws DuplicateKeyException, CreateException, ValidationException, XavaException;
	
	/** 
	 * Move an element in a collection. <p>
	 * 
	 * The collection must be sortable, in JPA it means to be a List with @OrderColumn.
	 * 
	 * @param metaModel  of the entity that contains the collection. Not null.
	 * @param keyValues  Key value of the container of the collection. Not null. 
	 * @param collectionName  Collection name of the container collection of element to move. Not null.
	 * @param from  Original position of the element in the collection. Zero based.
	 * @param to  Position in the collection where the element will be moved. Zero based.
	 * @exception ObjectNotFoundException  If object with this key does not exist 
	 * @exception FinderException  Logic problem on find.	
	 * @exception XavaException  Any problem related to OpenXava. Rollback transaction.
	 * @since 5.6.1
	 */	
	void moveCollectionElement(MetaModel metaModel, Map keyValues, String collectionName, int from, int to)   
			throws FinderException, XavaException;

	/**
	 * Create an aggregate (saving it in database) from the data passed in map format. <p>
	 * 
	 * @param metaModel  of the aggregate to create.
	 * @param values  Values to fill aggregate before save.
	 * @param metaModelContainer  of model that will contain the aggregate.
	 * @param containerModel  The object that will contain the new aggregate.
	 * @param number  This number will be passed to calculator of type IAggregateOidCalculator, it can
	 * 		use this number to calculate the oid. It's a simple counter.   
	 */	
	Object createAggregate(MetaModel metaModel, Map values, MetaModel metaModelContainer,
			Object containerModel, int number)
			throws CreateException, ValidationException, RemoteException, XavaException;
	
	/**
	 * Return an object that can be used as primary key in model layer. <p>
	 * 
	 * For example, in EJB2 will be the Key class, in Hibernate can be the
	 * POJO class, and JPA ...
	 */
	Object getKey(MetaModel metaModel, Map keyValues) throws XavaException;
	
	/**
	 * Returns a map that contains the value of primary key sent as object. <p>
	 * 
	 * The map must contain at least the primary key value, but it can contains
	 * more, the rest is ignored. 
	 */
	Map keyToMap(MetaModel metaModel, Object key) throws XavaException;
	
	/**
	 * Remove the object from persistent storage.
	 */
	void remove(MetaModel metaModel, Map keyValues) throws RemoveException, XavaException;
	
	/**
	 * Mark the starting of the unit of work associated to this thread. <p> 
	 *
	 * This method may be empty (for example in case of using CMT).
	 */
	void begin();
	
	/**
	 * Commit the work made by this persistent provider. <p>
	 * 
	 * This method may be empty (for example in case of using CMT).	 
	 */
	void commit();
	
	/**
	 * Rollback the work made by this persistent provider. <p>
	 * 
	 * This method may be empty (for example in case of using CMT).	 
	 */	
	void rollback();
	
	/**
	 * Save in database all persistent data still in memory. <p>
	 * 
	 * This method may be empty, because in some technologies has no sense.<br>	 
	 */
	void flush();	
	
	/**
	 * Reassociates a detached object to its persistent storage. <p>
	 * 
	 * This is for use when an object is serialized using RMI/IIOP, and
	 * need to reassociato to its persistent storage.<br>
	 * This method may be empty, because in some technologies has no sense.<br>
	 */
	void reassociate(Object entity);

	/**
	 * Return the object that represents the container object. <p>
	 * 
	 * The container object apply only to aggregates, and it's the
	 * object that containt to the aggregate.
	 * 
	 * It can be a POJO or key class, depends on the implementation.<br> 
	 */
	Object getContainer(MetaModel metaModel, Map containerKeyValues) throws XavaException;
	
	/**
	 * Refresh the state of the instance from the database, 
	 * overwriting changes made to the entity, if any.<p> 
	 * If the object is null or it's not managed simply do nothing,
	 * but not fails.<br>
	 * This method may be empty, because in some technologies has no sense.<br>
	 */
	void refreshIfManaged(Object object);
	
	/**
	 * Provides tabular data. <p>
	 */
	ITabProvider createTabProvider();
	
	/**
	 * Model name from the model object.
	 * 
	 * It can be null. This value is useful when inheritance is used and the official
	 * model name does not match the real model name, so if inheritance does not apply
	 * it can be null.
	 * 
	 * @since 5.6
	 */
	String getModelName(Object modelObject); 
	
}
