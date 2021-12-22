package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * tmr 
 * @author Javier Paniza
 */

public class TravelExpensesTest extends ModuleTestBase {
	
	public TravelExpensesTest(String testName) {
		super(testName, "TravelExpenses");		
	}
			
	public void testDefaultValueCalculatorInIdPropertyDependentOnOtherPropertyInAnEntityWithElementCollection() throws Exception {
		assertCollectionRowCount("expenses", 0); // To verify that the element collection exists
		assertValue("number", "1");
	}
					
}
