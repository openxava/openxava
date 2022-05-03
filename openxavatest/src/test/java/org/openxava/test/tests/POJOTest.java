package org.openxava.test.tests;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class POJOTest extends TestCase {
	
	static {
		XHibernate.setConfigurationFile("hibernate-junit.cfg.xml");
		XPersistence.setPersistenceUnit("junit");
		DataSourceConnectionProvider.setUseHibernateConnection(true);
	}
	
	public POJOTest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		XHibernate.commit();
		XPersistence.commit();
	}
		
	public void testCalculatedPropertyOnAggregateDependsOnPropertyOfContainerModel() throws Exception {
		Customer c = Customer.findByNumber(1);
		assertEquals("DOCTOR PESSET46540EL PUIGNew York1", c.getAddress().getAsString());
	}
					
	public void testFinderByAggregateProperty() throws Exception {
		Collection customers = Customer.findByStreet("XXX");
		for (Iterator it = customers.iterator(); it.hasNext(); it.next()); // This mustn't fail 
	}
	
	public void testFinderOrderedByReferencePropertyInAnAggregate() throws Exception {
		Collection customers = Customer.findOrderedByState();
		for (Iterator it = customers.iterator(); it.hasNext(); it.next()); // This mustn't fail
	}
	
	
	
		
	public void testOrderBy() throws Exception {
		Collection customers = Customer.findByNameLike("%");		
		String previous = "{}";
		for (Iterator it = customers.iterator(); it.hasNext();) {
			Customer customer = (Customer) it.next();
			String name = customer.getName();			
			if (name.compareToIgnoreCase(previous) > 0) {				
				fail("The names must to be ordered");
			}
			previous = name;
		}
	}
	
	public void testCalculatedPropertyDependOnMultiLevelProperty() throws Exception {
		Invoice invoice = new Invoice();
		invoice.setYear(2005);
		invoice.setNumber(66);				
		assertEquals(null,  invoice.getSellerDiscount());
	}
	
	public void testImplementInterfaces() throws Exception {
		assertTrue("Customer should implement IWithName", IWithName.class.isAssignableFrom(Customer.class));
		assertTrue("Address should implemenr IWithState", IWithCity.class.isAssignableFrom(Address.class));
	}
	
			
	public void testXavaMethods() throws Exception {
		Product product = new Product();
		product.setNumber(66);
		product.setFamilyNumber(1);
		product.setSubfamilyNumber(1);
		product.setDescription("DESCRIPTION JUNIT");
		product.setUnitPrice(new BigDecimal("100.00"));		
		
		assertEquals(new BigDecimal("100.00"), product.getUnitPrice());
		product.increasePrice();
		assertEquals(new BigDecimal("102.00"), product.getUnitPrice());

		assertEquals(new BigDecimal("102.00"), product.getPrice("España", new BigDecimal("0.00"))); 		 			
		assertEquals(new BigDecimal("103.00"), product.getPrice("Guatemala", new BigDecimal("1.00")));
		try {
			product.getPrice("El Puig", new BigDecimal("2.00"));
			fail("It should fail: 'El Puig' is not recognized as country");
		}
		catch (PriceException ex) {
		}		
	}	
		
}
