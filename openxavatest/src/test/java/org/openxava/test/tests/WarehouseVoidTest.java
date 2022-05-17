package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WarehouseVoidTest extends ModuleTestBase {
	
	public WarehouseVoidTest(String testName) {
		super(testName, "WarehouseVoid");		
	}
	
	public void testFilterActionNotShownInDetail() throws Exception { // A bug when before-each-request="true"
		assertNoAction("List.filter");
	}
	
}
