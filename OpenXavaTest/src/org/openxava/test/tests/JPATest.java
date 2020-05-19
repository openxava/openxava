package org.openxava.test.tests;

import java.math.*;
import java.util.*;
import java.util.stream.*;

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
	
	public void testQueryByCollectionValue() throws Exception { // tmp No dejar
		Query query = XPersistence.getManager().createQuery(
			"SELECT DISTINCT i" +
			"	FROM Invoice i, IN(i.details) d" +
			"   WHERE d.product.description like '%XAVA%'"	
		);
		Collection<Invoice> invoices = query.getResultList();
		for (Invoice invoice: invoices) {
			print(invoice);
		}
		
	}
	
	public void testQueryByElementCollectionValue() throws Exception { // tmp No dejar
		Query query = XPersistence.getManager().createQuery(
			"SELECT DISTINCT q" +
			"	FROM Quote q, IN(q.details) d" +
			"   WHERE d.product.description like '%MULTA%'"	
		);
		Collection<Quote> quotes = query.getResultList();
		for (Quote quote: quotes) {
			print(quote);
		}
		
	}
	
	private void print(Invoice invoice) { // tmp
		System.out.println("[JPATest.print] Invoice: " + invoice.getYear() + "/" + invoice.getNumber()); // tmp
	}
	
	private void print(Quote quote) { // tmp
		System.out.println("[JPATest.print] Quote: " + quote.getYear() + "/" + quote.getNumber()); // tmp
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
