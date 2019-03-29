package org.openxava.test.tests;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.hibernate.validator.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class JPATest extends TestCase {
	
	static {
		XPersistence.setPersistenceUnit("junit");
		DataSourceConnectionProvider.setUseHibernateConnection(true);
	}
	
	public JPATest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		XPersistence.commit();
	}
	
	public void testConvertersAllPropertiesOnCreate() throws Exception { // One way to avoid nulls
		Subfamily sf = new Subfamily();
		sf.setNumber(77);
		sf.setDescription("PROVA JUNIT 77");
		sf.setFamilyNumber(1);
		XPersistence.getManager().persist(sf);
		XPersistence.getManager().flush();
		
		javax.persistence.Query query = XPersistence.getManager().createQuery(
				"select sf.remarks from Subfamily sf where sf.number = 77");
		
		String remarks = (String) query.getSingleResult();
				
		assertEquals("", remarks);
		
		XPersistence.getManager().remove(sf);
	}
		
}
