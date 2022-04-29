package org.openxava.test.tests;

import java.math.*;

import org.openxava.util.*;
import org.openxava.validators.*;


import junit.framework.*;

/**
 * @author Javier Paniza
 */
public class NotZeroValidatorTest extends TestCase {
	
	private NotZeroValidator validator = new NotZeroValidator();

	public NotZeroValidatorTest(String testName) {
		super(testName);
	}
	
	public void testIntegerPositive() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Integer(1), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testIntegerZero() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Integer(0), "Test", "test");
		assertTrue(errors.contains());
	}
	
	public void testIntegerNegative() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Integer(-1), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testShortPositive() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Short((short)1), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testShortZero() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Short((short)0), "Test", "test");
		assertTrue(errors.contains());
	}
	
	public void testShortNegative() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Short((short)-1), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testFloatPositive() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Float(0.0001), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testFloatZero() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Float(0), "Test", "test");
		assertTrue(errors.contains());
	}
	
	public void testFloatNegative() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Float(-0.0001), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	

	public void testDoublePositive() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Double(0.0000000001), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testDoubleZero() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Double(0), "Test", "test");
		assertTrue(errors.contains());
	}
	
	public void testDoubleNegative() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new Double(-0.0000000001), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testBigDecimalPositive() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new BigDecimal("0.0000000001"), "Test", "test");
		assertTrue(errors.isEmpty());
	}
	
	public void testBigDecimalZero() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new BigDecimal("0"), "Test", "test");
		assertTrue(errors.contains());
	}
	
	public void testBigDecimalNegative() throws Exception {
		Messages errors = new Messages();
		validator.validate(errors, new BigDecimal("-0.0000000001"), "Test", "test");
		assertTrue(errors.isEmpty());
	}
		
}
