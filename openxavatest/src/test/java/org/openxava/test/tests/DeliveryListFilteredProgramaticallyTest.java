package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryListFilteredProgramaticallyTest extends ModuleTestBase {
	
		
	public DeliveryListFilteredProgramaticallyTest(String testName) {
		super(testName, "DeliveryListFilteredProgramatically");		
	}
	
	public void testSetBaseConditionOnEachRequestOnFirstModuleExecution() throws Exception {
		assertListRowCount(1);
	}
	
}
