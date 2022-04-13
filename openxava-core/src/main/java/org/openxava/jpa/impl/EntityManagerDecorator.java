/**
 * 
 */
package org.openxava.jpa.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.annotations.PostCreate;
import org.openxava.annotations.PreCreate;
import org.openxava.annotations.PreDelete;
import org.openxava.util.Classes;
import org.openxava.util.XavaException;
import org.openxava.validators.ValidationException;

/**
 * @author Federico Alcantara
 *
 */
public class EntityManagerDecorator implements EntityManager {
	private static final Log log = LogFactory.getLog(EntityManagerDecorator.class);
	
	private EntityManager decoratedManager;
	
	/**
	 * Constructor for attaching decoration to EntityManager
	 * @param unDecoratedManager
	 */
	public EntityManagerDecorator(EntityManager unDecoratedManager) {
		this.decoratedManager = unDecoratedManager;
	}

	/**
	 * Before and after persisting an object the
	 * PreCreate and PostCreate call backs found in the object are executed.
	 * These calls occurs within a transaction.
	 * @param object Object to be persisted
	 */
	public void persist(Object object) {
		executeCallbacks(object, PreCreate.class);
		decoratedManager.persist(object);
		executeCallbacks(object, PostCreate.class);
	}

	/**
	 * Before removing an object the
	 * PreDelete call backs encountered in the object are executed.
	 * These calls occurs within a transaction.
	 * @param arg0 Object to be removed
	 */
	public void remove(Object arg0) {
		executeCallbacks(arg0, PreDelete.class);
		decoratedManager.remove(arg0);		
	}

	/**
	 * Calls all methods annotated with the given annotation.
	 * @param object Object with methods.
	 * @param annotation Annotation to look for.
	 */
	private void executeCallbacks(Object object, Class<? extends Annotation> annotation) {
		for (Method method : Classes.getMethodsAnnotatedWith(object.getClass(), annotation)) {
			try {
				method.invoke(object, new Object[]{});
			} catch (InvocationTargetException e) { // In this way the XavaException doesn't swallow the real cause.
				if (e.getCause() != null) {
					log.error(e.getCause().getMessage(), e.getCause());
					if (e.getCause() instanceof ValidationException) {
						throw ((ValidationException) e.getCause());
					}
					throw new XavaException(e.getCause().getMessage());
				}
				log.error(e.getMessage(), e);
				throw new XavaException(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new XavaException(e.getMessage());
			}
		}		
	}

	public void clear() {
		decoratedManager.clear();
	}

	public void close() {
		decoratedManager.close();
	}

	public boolean contains(Object arg0) {
		return decoratedManager.contains(arg0);
	}

	public Query createNamedQuery(String arg0) {
		return decoratedManager.createNamedQuery(arg0);
	}

	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
		return decoratedManager.createNamedQuery(arg0, arg1);
	}

	public Query createNativeQuery(String arg0) {
		return decoratedManager.createNativeQuery(arg0);
	}

	@SuppressWarnings("rawtypes")
	public Query createNativeQuery(String arg0, Class arg1) {
		return decoratedManager.createNativeQuery(arg0, arg1);
	}

	public Query createNativeQuery(String arg0, String arg1) {
		return decoratedManager.createNativeQuery(arg0, arg1);
	}

	public Query createQuery(String arg0) {
		return decoratedManager.createQuery(arg0);
	}

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
		return decoratedManager.createQuery(arg0);
	}

	public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
		return decoratedManager.createQuery(arg0, arg1);
	}

	public void detach(Object arg0) {
		decoratedManager.detach(arg0);		
	}

	public <T> T find(Class<T> arg0, Object arg1) {
		return decoratedManager.find(arg0, arg1);
	}

	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
		return decoratedManager.find(arg0, arg1, arg2);
	}

	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		return decoratedManager.find(arg0, arg1, arg2);
	}

	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2,
			Map<String, Object> arg3) {
		return decoratedManager.find(arg0, arg1, arg2, arg3);
	}

	public void flush() {
		decoratedManager.flush();		
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return decoratedManager.getCriteriaBuilder();
	}

	public Object getDelegate() {
		return decoratedManager.getDelegate();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return decoratedManager.getEntityManagerFactory();
	}

	public FlushModeType getFlushMode() {
		return decoratedManager.getFlushMode();
	}

	public LockModeType getLockMode(Object arg0) {
		return decoratedManager.getLockMode(arg0);
	}

	public Metamodel getMetamodel() {
		return decoratedManager.getMetamodel();
	}

	public Map<String, Object> getProperties() {
		return decoratedManager.getProperties();
	}

	public <T> T getReference(Class<T> arg0, Object arg1) {
		return decoratedManager.getReference(arg0, arg1);
	}

	public EntityTransaction getTransaction() {
		return decoratedManager.getTransaction();
	}

	public boolean isOpen() {
		return decoratedManager.isOpen();
	}

	public void joinTransaction() {
		decoratedManager.joinTransaction();		
	}

	public void lock(Object arg0, LockModeType arg1) {
		decoratedManager.lock(arg0, arg1);
	}

	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		decoratedManager.lock(arg0, arg1, arg2);		
	}

	public <T> T merge(T arg0) {
		return decoratedManager.merge(arg0);
	}

	public void refresh(Object arg0) {
		decoratedManager.refresh(arg0);
	}

	public void refresh(Object arg0, Map<String, Object> arg1) {
		decoratedManager.refresh(arg0, arg1);
	}

	public void refresh(Object arg0, LockModeType arg1) {
		decoratedManager.refresh(arg0, arg1);
	}

	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		decoratedManager.refresh(arg0, arg1, arg2);		
	}

	public void setFlushMode(FlushModeType arg0) {
		decoratedManager.setFlushMode(arg0);		
	}

	public void setProperty(String arg0, Object arg1) {
		decoratedManager.setProperty(arg0, arg1);		
	}

	public <T> T unwrap(Class<T> arg0) {
		return decoratedManager.unwrap(arg0);
	}

	public <T> EntityGraph<T> createEntityGraph(Class<T> arg0) { 
		return decoratedManager.createEntityGraph(arg0);
	}

	public EntityGraph<?> createEntityGraph(String arg0) { 
		return decoratedManager.createEntityGraph(arg0);
	}

	public StoredProcedureQuery createNamedStoredProcedureQuery(String arg0) { 
		return decoratedManager.createNamedStoredProcedureQuery(arg0);
	}

	public Query createQuery(CriteriaUpdate arg0) { 
		return decoratedManager.createQuery(arg0);
	}

	public Query createQuery(CriteriaDelete arg0) { 
		return decoratedManager.createQuery(arg0);
	}

	public StoredProcedureQuery createStoredProcedureQuery(String arg0) { 
		return decoratedManager.createStoredProcedureQuery(arg0);
	}

	public StoredProcedureQuery createStoredProcedureQuery(String arg0, Class... arg1) { 
		return decoratedManager.createStoredProcedureQuery(arg0, arg1);
	}

	public StoredProcedureQuery createStoredProcedureQuery(String arg0, String... arg1) { 
		return decoratedManager.createStoredProcedureQuery(arg0, arg1);
	}

	public EntityGraph<?> getEntityGraph(String arg0) { 
		return decoratedManager.getEntityGraph(arg0);
	}

	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> arg0) { 
		return decoratedManager.getEntityGraphs(arg0);
	}

	public boolean isJoinedToTransaction() { 
		return decoratedManager.isJoinedToTransaction();
	}

}
