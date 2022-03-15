package org.openxava.test.tests;

import java.util.*;

import javax.persistence.*;

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
	
	public void testProva() throws Exception { // tmr
		Query query = XPersistence.getManager().createQuery("from Product2 p where p.family = :family");
		Family2 family = new Family2();
		family.setNumber(2);
		query.setParameter("family", 2);
		Collection<Product2> products = query.getResultList();
		for (Product2 p: products) {
			System.out.println("[JPATest.testProva] product=" + p.getNumber() + " - " + p.getDescription()); // tmp
		}
		
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
