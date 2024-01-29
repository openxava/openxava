package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryListFilteredProgramaticallyTest extends ModuleTestBase {
	
		
	public DeliveryListFilteredProgramaticallyTest(String testName) {
		super(testName, "DeliveryListFilteredProgramatically");		
	}
	
	public void testSetBaseConditionOnEachRequestOnFirstModuleExecution_backToNoGroupingWithSetBaseCondition() throws Exception {
		assertListRowCount(1);
		assertValueInList(0, 6, "FOR TEST SEARCHING BY DESCRIPTION");
		
		selectGroupBy("Group by invoice year");
		assertListRowCount(1);
		assertListColumnCount(2);
		assertValueInList(0, 0, "2004");
		assertValueInList(0, 1, "1");
		
		selectGroupBy("No grouping");
		assertListRowCount(1);
		assertValueInList(0, 6, "FOR TEST SEARCHING BY DESCRIPTION");
		assertNoErrors(); // Because in some point it worked but showing an error message
	}
	
}
