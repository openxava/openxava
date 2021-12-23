package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TravelExpensesTest extends ModuleTestBase {
	
	public TravelExpensesTest(String testName) {
		super(testName, "TravelExpenses");		
	}
			
	public void testDefaultValueCalculatorInIdPropertyDependentOnOtherPropertyInAnEntityWithElementCollection() throws Exception {
		assertCollectionRowCount("expenses", 0); // To verify that the element collection exists, needed to reproduce de case
		assertTotalInCollection("expenses", "amount", "0.00"); // To verify that the total of collection exists, needed to reproduce de case
		assertValue("number", "1");
	}
					
}
