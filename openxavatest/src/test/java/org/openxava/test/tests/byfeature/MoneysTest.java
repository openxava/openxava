package org.openxava.test.tests.byfeature;


import java.util.*;

import org.openxava.util.*;

import junit.framework.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */

public class MoneysTest extends TestCase {
	
	public void testIsCurrencySymbolAtStart() throws Exception {
		Locale es = new Locale("es", "ES");
		assertFalse(Moneys.isCurrencySymbolAtStart(es));
		Locale us = new Locale("en", "US");
		assertTrue(Moneys.isCurrencySymbolAtStart(us));
	}

	public void testGetCurrencySymbol() throws Exception {
		Locale es = new Locale("es", "ES");
		assertEquals("\u20AC", Moneys.getCurrencySymbol(es));
		Locale us = new Locale("en", "US");
		assertEquals("$", Moneys.getCurrencySymbol(us));
	}	
	
}
