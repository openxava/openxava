package org.openxava.test.tests;

import java.math.*;
import java.util.*;

import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import junit.framework.*;

/**
 * @author Javier Paniza
 */
public class MapFacadeTest extends TestCase {
	
	static {
		XHibernate.setConfigurationFile("hibernate-junit.cfg.xml");
		XPersistence.setPersistenceUnit("junit");
	}

	public MapFacadeTest(String name) {
		super(name);
		Locale.setDefault(Locale.ENGLISH);
	}
	
	protected void tearDown() throws Exception {
		XHibernate.commit();
		XPersistence.commit();
	}
	
	public void testCreateReturningKey() throws Exception { 
		Map values = new HashMap();
		values.put("year", new Integer(2007));
		values.put("description", "JUNIT FROM MapFacade");
		Map key = MapFacade.createReturningKey("Course", values);
		Number number = (Number) key.get("number");		
		assertTrue("Number must have value", number.intValue() > 0);
	}
	
	public void testModelValidatorWithNullReference() throws Exception {
		Map values = new HashMap();
		values.put("oid", "2004:9:5"); // It must exist		
		values.put("product", null);		
		MapFacade.validate("Invoice.InvoiceDetail", values);		
	}
	
	public void testBigDecimalKeyMapAlthoughPropertyIsInt() throws Exception {
		Map deliveryKey = new HashMap();
		Map invoiceKey = new HashMap();
		invoiceKey.put("year", new BigDecimal("2002"));
		invoiceKey.put("number", new BigDecimal("1"));
		deliveryKey.put("invoice", invoiceKey);
		Map deliveryTypeKey = new HashMap();
		deliveryTypeKey.put("number", new BigDecimal("1"));
		deliveryKey.put("type", deliveryTypeKey);
		deliveryKey.put("number", new BigDecimal("666"));
		
		MapFacade.findEntity("Delivery", deliveryKey);
	}
	
	public void testMapArgumentByValue() throws Exception {
		Map key = new HashMap();
		key.put("number", new Integer("4"));
		Map newValues = new HashMap();
		newValues.put("number", new Integer("4"));
		newValues.put("name", "Cuatrero");
		assertEquals(2, newValues.size());
		MapFacade.setValues("Customer", key, newValues);
		assertEquals("Map mustn't be changed by MapFacade", 2, newValues.size());
		
	}
	
	public void testAccumulateExistsPropertiesAndRequiredPropertiesOnCreate() throws Exception {
		Map values = new HashMap();
		values.put("description", "");
		try {
			MapFacade.create("Shipment", values);
		}
		catch (ValidationException ex) {
			Collection errors = ex.getErrors().getStrings();			
			assertTrue("type", errors.contains("Value for Type in Shipment is required"));
			assertTrue("number", errors.contains("Value for Number in Shipment is required"));
			assertTrue("description", errors.contains("Value for Description in Shipment is required"));
		}		
	}
	
	public void testValidateOnly() throws Exception {
		Map values = new HashMap();
		values.put("description", "");		
		Messages errors = MapFacade.validate("Shipment", values);
		assertTrue("It has to have errors", errors.contains());
		Collection texts = errors.getStrings();			
		assertTrue("type", !texts.contains("Value for Type in Shipment is required"));
		assertTrue("number", !texts.contains("Value for Number in Shipment is required"));
		assertTrue("description", texts.contains("Value for Description in Shipment is required"));				
	}

}
