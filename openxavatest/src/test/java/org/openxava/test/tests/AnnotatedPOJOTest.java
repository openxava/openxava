package org.openxava.test.tests;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class AnnotatedPOJOTest extends TestCase {
	
	static {
		XPersistence.setPersistenceUnit("junit");
		DataSourceConnectionProvider.setUseHibernateConnection(true);
	}
	
	public AnnotatedPOJOTest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		XPersistence.commit();
	}
	
	public void testRequiredAsBeanValidationAnnotation() throws Exception { 
		DrivingLicence dl = new DrivingLicence();
		dl.setType("X");
		dl.setLevel(1);
		dl.setDescription(""); // This is annotated with @Required
		
		XPersistence.getManager().persist(dl);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {
			if (ex.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cex = (ConstraintViolationException) ex.getCause();
				assertEquals("1 constraint violation expected", 1, cex.getConstraintViolations().size());
				ConstraintViolation<?> v = cex.getConstraintViolations().iterator().next();
				assertEquals("Property", "description", v.getPropertyPath().toString());
				assertTrue("Message is not empty", v.getMessage().length() > 0);
				return;								
			}
		}
		fail("A constraint violation exception should be thrown"); 		
	}
	
	public void testPropertyValidatorAsBeanValidationAnnotation() throws Exception { 
		Product p = new Product();
		p.setNumber(66);
		p.setDescription("JUNIT");
		p.setFamilyNumber(1);
		p.setSubfamilyNumber(1);
		p.setWarehouseKey(new Warehouse());  
		p.setUnitPrice(new BigDecimal("1200")); // An UnitPriceValidator does not permit this
		
		XPersistence.getManager().persist(p);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {
			if (ex.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cex = (ConstraintViolationException) ex.getCause();
				assertEquals("1 constraint violation expected", 1, cex.getConstraintViolations().size());
				ConstraintViolation<?> v = cex.getConstraintViolations().iterator().next();
				assertEquals("Property", "unitPrice", v.getPropertyPath().toString());
				assertTrue("Message is not empty", v.getMessage().length() > 0);
				return;								
			}
		}
		fail("A constraint violation exception should be thrown"); 
	}
	
	public void testPropertyValidatorsAsBeanValidationAnnotation() throws Exception { 
		Product p = new Product();
		p.setNumber(66);
		p.setDescription("MOTO"); 
		p.setFamilyNumber(1);
		p.setSubfamilyNumber(1);
		p.setWarehouseKey(new Warehouse());
		p.setUnitPrice(new BigDecimal("900")); 
		
		XPersistence.getManager().persist(p);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {
			if (ex.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cex = (ConstraintViolationException) ex.getCause();
				assertEquals("1 constraint violation expected", 1, cex.getConstraintViolations().size());
				ConstraintViolation<?> v = cex.getConstraintViolations().iterator().next();
				assertEquals("Property", "description", v.getPropertyPath().toString());
				assertTrue("Message is not empty", v.getMessage().length() > 0);
				return;								
			}
		}
		fail("A constraint violation exception should be thrown"); 
	}
	
	public void testEntityValidatorsAsHibernateAnnotation() throws Exception {
		Product p = new Product();
		p.setNumber(66);
		p.setDescription("BUENO, BONITO, BARATO"); // It's cheap ('BARATO') thus...
		p.setFamilyNumber(1);
		p.setSubfamilyNumber(1);
		p.setWarehouseKey(new Warehouse());
		p.setUnitPrice(new BigDecimal("900")); // ... it cannot cost 900 (max 100) 
		
		XPersistence.getManager().persist(p);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {			
			if (ex.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cex = (ConstraintViolationException) ex.getCause();
				assertEquals("1 constraint violation expected", 1, cex.getConstraintViolations().size());
				ConstraintViolation<?> v = cex.getConstraintViolations().iterator().next();
				assertEquals("Bean", "Product", v.getRootBean().getClass().getSimpleName());
				assertTrue("Message is not empty", v.getMessage().length() > 0);
				return;								
			}
		}
		fail("A constraint violation exception should be thrown");
	}
		
	
	public void testFinderThrowsNoResult() throws Exception {
		try {
			Customer.findByNumber(66); // 66 doesn't exist
			fail("EntityNotFoundException expected"); 
		}
		catch (javax.persistence.NoResultException ex) {
			// All fine
		}
	}
	
	public void testBeanValidation() throws Exception {
		Artist a = new Artist();
		a.setName("TOO OLD ARTIST");
		a.setAge(120);
		
		XPersistence.getManager().persist(a);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {
						
			if (ex.getCause() instanceof  javax.validation.ConstraintViolationException) {
				javax.validation.ConstraintViolationException vex = (javax.validation.ConstraintViolationException) ex.getCause();				
				assertEquals("1 invalid value is expected", 1, vex.getConstraintViolations().size());
				ConstraintViolation<?> violation = vex.getConstraintViolations().iterator().next();
				assertEquals("Bean", "Artist", violation.getRootBeanClass().getSimpleName());
				String expectedMessage = "es".equals(Locale.getDefault().getLanguage())?"tiene que ser menor o igual que 90":"must be less than or equal to 90"; 
				assertEquals("Message text", expectedMessage, violation.getMessage());
				return;
			}
					
		}
		fail("A constraint violation exception should be thrown");
	}

	public void testConstraintValidatorMessage() throws Exception {
		
	}
}
