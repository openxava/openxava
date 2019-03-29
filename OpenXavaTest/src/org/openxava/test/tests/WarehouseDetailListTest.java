package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WarehouseDetailListTest extends ModuleTestBase {
	
		
	public WarehouseDetailListTest(String testName) {
		super(testName, "WarehouseDetailList");		
	}
		
	public void testDetailListModeController() throws Exception {
		// Since v6 DetailList mode no longer exist, when use we assume Mode, for backward compatibility
		assertNoAction("DetailList.detailAndFirst"); // No longer exists, since v6
		assertNoAction("DetailList.list"); // No longer exists, since v6
		assertNoAction("Mode.detailAndFirst"); // No longer exists, since v6
		assertNoAction("Mode.split"); // No longer exists, since v6
		assertNoAction("Mode.list");
		
		assertAction("List.filter"); // List is shown
		assertNotExists("zoneNumber"); // Detail not is shown
		
		execute("List.viewDetail", "row=0"); 
		assertNoAction("DetailList.detailAndFirst"); // No longer exists, since v6
		assertNoAction("DetailList.list"); // No longer exists, since v6
		assertNoAction("Mode.detailAndFirst"); // No longer exists, since v6
		assertNoAction("Mode.split"); // No longer exists, since v6
		assertAction("Mode.list");
		
		assertNoAction("List.filter"); // List is shown
		assertExists("zoneNumber"); // Detail not is shown		
	}
	
}
