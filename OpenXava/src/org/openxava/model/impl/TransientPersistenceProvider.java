package org.openxava.model.impl;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;
import javax.ejb.ObjectNotFoundException;

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

	public Object findByAnyProperty(MetaModel metaModel, Map searchingValues) throws ObjectNotFoundException, FinderException, XavaException {
		throw new UnsupportedOperationException(); 
	}

	public Object find(MetaModel metaModel, Map keyValues) throws ObjectNotFoundException, FinderException, XavaException {
		throw new UnsupportedOperationException(); 
	}

	public Object find(MetaModel metaModel, Object key) throws ObjectNotFoundException, FinderException {
		throw new UnsupportedOperationException(); 
	}
	
	public IPropertiesContainer toPropertiesContainer(MetaModel metaModel, Object modelObject) throws XavaException {
		return new POJOPropertiesContainerAdapter(modelObject);
	}

	public Object create(MetaModel metaModel, Map values) throws DuplicateKeyException, CreateException, ValidationException, XavaException {
		throw new UnsupportedOperationException(); 
	}

	public void moveCollectionElement(MetaModel metaModel, Map keyValues, String collectionName, int from, int to) throws FinderException, XavaException {
		throw new UnsupportedOperationException(); 
	}

	public Object createAggregate(MetaModel metaModel, Map values, MetaModel metaModelContainer, Object containerModel, int number) throws CreateException, ValidationException, RemoteException, XavaException {
		throw new UnsupportedOperationException(); 
	}

	public Object getKey(MetaModel metaModel, Map keyValues) throws XavaException {
		throw new UnsupportedOperationException(); 
	}

	public Map keyToMap(MetaModel metaModel, Object key) throws XavaException {
		throw new UnsupportedOperationException(); 
	}

	public void remove(MetaModel metaModel, Map keyValues) throws RemoveException, XavaException {
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

	public Object getContainer(MetaModel metaModel, Map containerKeyValues) throws XavaException {
		throw new UnsupportedOperationException(); 
	}

	public void refreshIfManaged(Object object) {
	}

	public ITabProvider createTabProvider() {
		throw new UnsupportedOperationException(); 
	}

	public String getModelName(Object modelObject) {
		if (modelObject == null) return null;
		return modelObject.getClass().getSimpleName();
	}

}
