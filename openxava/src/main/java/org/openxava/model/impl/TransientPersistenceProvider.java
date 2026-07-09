package org.openxava.model.impl;

import java.util.*;

import org.openxava.model.*;
import org.openxava.model.ObjectNotFoundException;

import org.hibernate.*;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @since 5.9.1
 * @author Javier Paniza
 */

public class TransientPersistenceProvider implements IPersistenceProvider {
	
	private static TransientPersistenceProvider instance;
	public static TransientPersistenceProvider getInstance() {
		if (instance == null) instance = new TransientPersistenceProvider();
		return instance;
	}

	/**
	* @throws XavaException
	 */
	public Object findByAnyProperty(MetaModel metaModel, Map searchingValues) throws ObjectNotFoundException, FinderException {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws XavaException
	 */
	public Object find(MetaModel metaModel, Map keyValues) throws ObjectNotFoundException, FinderException {
		throw new UnsupportedOperationException(); 
	}

	public Object find(MetaModel metaModel, Object key) throws ObjectNotFoundException, FinderException {
		throw new UnsupportedOperationException(); 
	}
	
	/**
	* @throws XavaException
	 */
	public IPropertiesContainer toPropertiesContainer(MetaModel metaModel, Object modelObject) {
		return new POJOPropertiesContainerAdapter(modelObject);
	}

	/**
	* @throws XavaException
	 */
	public Object create(MetaModel metaModel, Map values) throws DuplicateKeyException, CreateException, ValidationException {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws XavaException
	 */
	public void moveCollectionElement(MetaModel metaModel, Map keyValues, String collectionName, int from, int to) throws FinderException {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws SystemException
	* @throws XavaException
	 */
	public Object createAggregate(MetaModel metaModel, Map values, MetaModel metaModelContainer, Object containerModel, int number) throws CreateException, ValidationException {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws XavaException
	 */
	public Object getKey(MetaModel metaModel, Map keyValues) {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws XavaException
	 */
	public Map keyToMap(MetaModel metaModel, Object key) {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws XavaException
	 */
	public void remove(MetaModel metaModel, Map keyValues) throws RemoveException {
		throw new UnsupportedOperationException(); 
	}

	public void begin() {
	}

	public void commit() {
	}

	public void rollback() {
	}

	public void flush() {
	}

	public void reassociate(Object entity) {
		throw new UnsupportedOperationException(); 
	}

	/**
	* @throws XavaException
	 */
	public Object getContainer(MetaModel metaModel, Map containerKeyValues) {
		throw new UnsupportedOperationException(); 
	}

	public void refreshIfManaged(Object object) {
	}

	public ITabProvider createTabProvider() {
		throw new UnsupportedOperationException(); 
	}

	public String getModelName(Object modelObject) {
		if (modelObject == null) return null;
		return Hibernate.getClass(modelObject).getSimpleName(); // Because sometime some reference in a transient object could be not so transient
	}

}
