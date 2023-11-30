package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TravelExpensesTest extends ModuleTestBase {
	
	public TravelExpensesTest(String testName) {
		super(testName, "TravelExpenses");		
	}
			
	public void testDefaultValueCalculatorInIdPropertyDependentOnOtherPropertyInAnEntityWithElementCollection_defaultValueCalculatorNotChangeIdOnExistentEntity() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("year", "2022");
		assertValue("number", "1");
		assertValue("date", "2/15/2022");
		setValue("date", "1/1/2022");
		assertValue("number", "1");
		
		execute("CRUD.new");
		assertCollectionRowCount("expenses", 0); // To verify that the element collection exists, needed to reproduce de case
		assertTotalInCollection("expenses", "amount", "0.00"); // To verify that the total of collection exists, needed to reproduce de case
		assertValue("number", "1");
		
		setValue("date", "1/1/2022");
		assertValue("number", "2");
	}
					
}
