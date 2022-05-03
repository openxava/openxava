package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WarehouseListOnlyTest extends ModuleTestBase {
	
		
	public WarehouseListOnlyTest(String testName) {
		super(testName, "WarehouseListOnly");		
	}
		
	public void testListOnlyModeController() throws Exception {  		
		assertNoAction("Mode.list");
		
		assertAction("List.filter"); // List is shown
		assertNotExists("zoneNumber"); // Detail is not shown
	}
	
}
