package org.openxava.test.tests;

import java.util.*;

import org.hibernate.*;
import org.openxava.hibernate.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class HibernateTest extends TestCase {
	
	static {
		XHibernate.setConfigurationFile("hibernate-junit.cfg.xml");
		DataSourceConnectionProvider.setUseHibernateConnection(true);
	}
	
	public HibernateTest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		XHibernate.commit();		
	}
		
	public void testConvertersAllPropertiesOnCreate() throws Exception { // One way to avoid nulls	
		Subfamily sf = new Subfamily();
		sf.setNumber(77);
		sf.setDescription("PROVA JUNIT 77");
		XHibernate.getSession().save(sf);
		XHibernate.getSession().flush();
		
		org.hibernate.Query query = XHibernate.getSession().createQuery(
				"select sf.remarks from Subfamily sf where sf.number = 77");
		
		String remarks = (String) query.uniqueResult();
				
		assertEquals("", remarks);
		
		XHibernate.getSession().delete(sf);
	}
	
	public void testConverterInReference() throws Exception {		
		Warehouse wh = new Warehouse();
		wh.setNumber(1);
		wh.setZoneNumber(1);
		Carrier c1 = new Carrier();
		c1.setWarehouse(wh);
		// driving licence is not set to test converters in references
		c1.setNumber(6);
		c1.setName("SIX");
		XHibernate.getSession().save(c1);
		XHibernate.commit(); 
		
		XHibernate.getSession().delete(c1);
	}
	
	public void testFinderThrowsObjectNotFound() throws Exception { 
		// Finders in POJOs has the semantics of the EJB CMP2 
		// in order to help in translation from EJB2 to POJO+Hibernate
		try {
			Customer.findByNumber(66); // 66 doesn't exist
			fail("ObjectNotFoundException expected"); 
			if (false) throw new javax.ejb.ObjectNotFoundException(); // In order to avoid a compilation error when JPA code is used 
		}
		catch (javax.ejb.ObjectNotFoundException ex) {
			// All fine
		}
	}

	public void testConvertersByDefault() throws Exception {
		// converters by defaults are not supported in annotated POJOs
		Iterator it = Invoice.findAll().iterator();
		Invoice invoice = null;
		while (it.hasNext()) {
			invoice  = (Invoice) it.next();
			if (invoice.getDetailsCount() > 0) break;
			invoice = null;
		}
		assertNotNull("At least one invoice with details is required for run this test", invoice);
		invoice.setComment(" INVOICE WITH SPACES ");
		InvoiceDetail detail = (InvoiceDetail) invoice.getDetails().iterator().next();
		detail.setRemarks(" DETAIL WITH SPACES ");
		
		assertEquals("INVOICE WITH SPACES", invoice.getComment());
		assertEquals("DETAIL WITH SPACES", detail.getRemarks());
	}
	
	
}
