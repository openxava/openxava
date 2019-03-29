package org.openxava.model.impl;

import java.io.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.logging.*;
import org.hibernate.*;

import org.openxava.hibernate.*;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;

/**
 * @author M� Carmen Gimeno Alabau
 */
public class HibernatePersistenceProvider extends POJOPersistenceProviderBase {
	
	private static Log log = LogFactory.getLog(HibernatePersistenceProvider.class);
	
	private HibernatePersistenceProvider() {
	}	
	private static HibernatePersistenceProvider instance;
	public static HibernatePersistenceProvider getInstance() {
		if (instance == null) instance = new HibernatePersistenceProvider();
		return instance;
	}
	
	protected Object find(Class pojoClass, Serializable key) {
		flush(); 
		Object result = XHibernate.getSession().get(pojoClass, (Serializable) key);  
		if (result != null) {			
			refreshIfManaged(result);
		}
		return result;
	}
	
	protected void persist(Object object) {
		XHibernate.getSession().save(object);		
	}
	
	public void remove(MetaModel metaModel, Map keyValues)
			throws RemoveException, XavaException {
		try {
			Object model = find(metaModel, keyValues, false);
			XHibernate.getSession().delete(model);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoveException(XavaResources.getString("remove_error",
					metaModel.getName(), ex.getMessage()));
		}
	}

	public void begin() {
		XHibernate.setCmt(XavaPreferences.getInstance().isMapFacadeAsEJB()); 
	}
		
	public void commit() {
		flush(); 
		XHibernate.commit();
		XHibernate.setCmt(false); 				
	}

	public void rollback() {	
		XHibernate.rollback();
		XHibernate.setCmt(false); 
	}
	
	public void reassociate(Object entity) {
		XHibernate.getSession().lock(entity, LockMode.NONE);  		
	}
	
	public void flush() {
		XHibernate.getSession().flush();		
	}

	protected Object createQuery(String query) { 
		return XHibernate.getSession().createQuery(query);
	}

	protected void setParameterToQuery(Object query, String name, Object value) {
		((Query) query).setParameter(name, value);
	}

	protected Object getUniqueResult(Object query) {		
		Iterator it = ((Query) query).list().iterator();
		if (!it.hasNext()) return null;
		return it.next();
	}

	public void refreshIfManaged(Object object) { 		
		if (XHibernate.getSession().contains(object)) {	
			try {
				XHibernate.getSession().refresh(object);			
			}
			catch (UnresolvableObjectException ex) { 				
			}			
		}
	}

	public ITabProvider createTabProvider() {
		return new JDBCTabProvider();
	}

}
