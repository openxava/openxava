package org.openxava.test.tests;

import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class StringsTest extends TestCase {
	
	public void testNaturalLabelToIdentifier() throws Exception {
		assertEquals("LeonEspana", Strings.naturalLabelToIdentifier("León, España"));
	}
		
	public void testJavaIdentifierToNaturalLabel() throws Exception {
		assertEquals("Number", Strings.javaIdentifierToNaturalLabel("number"));
		assertEquals("Product number", Strings.javaIdentifierToNaturalLabel("productNumber"));
		assertEquals("RELEASE ONE", Strings.javaIdentifierToNaturalLabel("RELEASE_ONE"));
		assertEquals("VAT", Strings.javaIdentifierToNaturalLabel("VAT"));
		assertEquals("Total VAT", Strings.javaIdentifierToNaturalLabel("totalVAT"));
		assertEquals("Total VAT in invoice", Strings.javaIdentifierToNaturalLabel("totalVATinInvoice"));
	}

	public void testIsNumeric() throws Exception { 
		assertTrue(Strings.isNumeric("1"));
		assertTrue(Strings.isNumeric("1.0")); 
		assertTrue(Strings.isNumeric("1,0"));
		assertTrue(Strings.isNumeric(" 1,0"));
		assertFalse(Strings.isNumeric("1,a0"));	
		assertTrue(Strings.isNumeric("+1,0")); 
		assertTrue(Strings.isNumeric("-1,0"));
		assertFalse(Strings.isNumeric("#1,0")); 
	}


}
