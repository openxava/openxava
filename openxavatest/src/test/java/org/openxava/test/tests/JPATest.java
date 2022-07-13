package org.openxava.test.tests;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.model.meta.*;
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
	
	public void testKeyWithReference() throws Exception { // tmr
		// keyValues={number=666, invoice.year=2002, type.number=1, invoice.number=1}
		// keyValues={delivery.type.number=null, delivery.invoice.year=null, delivery.invoice.number=null, delivery.number=null}
		/*
		String queryString="from TransportCharge o where o.delivery.type.number = :delivery_type_number and o.delivery.invoice.year = :delivery_invoice_year and o.delivery.invoice.number = :delivery_invoice_number and o.delivery.number = :delivery_number";
		Query query = XPersistence.getManager().createQuery(queryString);
		query.setParameter("delivery_number", 666);
		query.setParameter("delivery_invoice_year", 2002);
		query.setParameter("delivery_type_number", 1);
		query.setParameter("delivery_invoice_number", 1);
		*/
		
		
		
		/* 
		String queryString="from TransportCharge o where o.delivery.invoice.year = :delivery_invoice_year and o.delivery.invoice.number = :delivery_invoice_number";
		Query query = XPersistence.getManager().createQuery(queryString);
		query.setParameter("delivery_invoice_year", 2002);
		query.setParameter("delivery_invoice_number", 1);
		*/
		/*
		String queryString="from TransportCharge o where o.delivery.invoice.year = :delivery_invoice_year";
		Query query = XPersistence.getManager().createQuery(queryString);
		query.setParameter("delivery_invoice_year", 2002);
		*/
		/* FUNCIONA
		String queryString="from TransportCharge o where o.delivery.invoice = :delivery_invoice";
		Query query = XPersistence.getManager().createQuery(queryString);
		Invoice key = new Invoice();
		key.setNumber(1);
		key.setYear(2002);
		query.setParameter("delivery_invoice", key);
		*/
		
		String queryString="from TransportCharge o where o.delivery.invoice = :delivery_invoice";
		Query query = XPersistence.getManager().createQuery(queryString);
		Map key = new HashMap();
		key.put("number", 1);
		key.put("year", 2002);
		
		query.setParameter("delivery_invoice", MetaModel.get("Invoice").toPOJO(key));		
		
		
		Collection result = query.getResultList();
		System.out.println("[JPATest.testKeyWithReference] result.size()=" + result.size()); // tmr
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
