package org.openxava.test.tests;

import java.math.*;

import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class IsTest extends TestCase {
	
	public void testAnyEqual() throws Exception { 
		assertTrue(Is.anyEqual("Juan", "Antonio", "Juan", "Marisa"));
		assertFalse(Is.anyEqual("Juan", "Antonio", "Pedro", "Marisa"));
		assertTrue(Is.anyEqual(null, "Antonio", "Pedro", null));
	}

	
	public void testEmptyForBigDecimal() {
		BigDecimal fraction = new BigDecimal("0.1");
		BigDecimal zero = new BigDecimal("0.0");
		assertTrue(!Is.empty(fraction));
		assertTrue(Is.empty(zero));
	}
	
	public void testEmptyString() {
		assertTrue(Is.emptyString(""));
		assertTrue(Is.emptyString(null));
		assertFalse(Is.emptyString("a"));
		assertTrue(Is.emptyString("", "", ""));
		assertTrue(Is.emptyString("a", "", "b"));
		assertTrue(Is.emptyString("a", "b", null));
		assertFalse(Is.emptyString("a", "b", "c"));
	}
	
	public void testEmptyStringAll() {		
		assertTrue(Is.emptyStringAll("", "", ""));
		assertTrue(Is.emptyStringAll("", "", null));
		assertTrue(Is.emptyStringAll(null, null, null));
		assertFalse(Is.emptyStringAll("a", "", "b"));
		assertFalse(Is.emptyStringAll("a", "b", null));
		assertFalse(Is.emptyStringAll("a", "b", "c"));
	}	

}
