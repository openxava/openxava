package org.openxava.test.tests.byfeature;


import java.util.*;

import org.openxava.jpa.*;
import org.openxava.model.meta.*;
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
	
	public void testIsMoneyProperty() throws Exception {
		XPersistence.setPersistenceUnit("junit");
		MetaModel model = MetaModel.get("Invoice");
		
		// Annotated with @Money
		MetaProperty yearDiscountProperty = model.getMetaProperty("yearDiscount");
		assertTrue(Moneys.isMoneyProperty(yearDiscountProperty));
		
		// Annotated with @Stereotype("MONEY")
		MetaProperty amountsSumProperty = model.getMetaProperty("amountsSum");
		assertTrue(Moneys.isMoneyProperty(amountsSumProperty));
		
		// Annotated with @Stereotype("DINERO")
		MetaProperty sellerDiscountProperty = model.getMetaProperty("sellerDiscount");
		assertTrue(Moneys.isMoneyProperty(sellerDiscountProperty));			
		
		// No money
		MetaProperty vatPercentageProperty = model.getMetaProperty("vatPercentage");
		assertFalse(Moneys.isMoneyProperty(vatPercentageProperty));					
	}
	
}
