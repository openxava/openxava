package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WarehouseSplitTest extends ModuleTestBase {
	
		
	public WarehouseSplitTest(String testName) {
		super(testName, "WarehouseSplit");		
	}
		
	public void testSplitMode() throws Exception {
		// Since v6 SplitMode does no longer exist, OpenXava uses always an implicit (without buttons) detail/mode system.
		// This test is to assure the if you use SplitMode it does not fail, but uses "Mode" controller instead.
		assertNoAction("Mode.detailAndFirst");
		assertNoAction("Mode.list");
		assertNoAction("Mode.split");
		
		assertAction("List.filter"); // List is shown
		assertNotExists("zoneNumber"); // Detail is not shown, actually it is not SplitMode but Detail/List and we are in list
		
		execute("List.viewDetail", "row=0");
		
		assertNoAction("List.filter"); // List is not shown
		assertExists("zoneNumber"); // Detail is shown
		
		assertNoAction("Mode.detailAndFirst");
		assertAction("Mode.list");
		assertNoAction("Mode.split");
	}
	
	public void testCheckedRows() throws Exception { 
		checkRow(1);
		checkRow(3); 
		execute("List.filter");
		assertRowsChecked(1, 3);;		
		uncheckRow(1);
		uncheckRow(3);
		assertRowUnchecked(1);
		assertRowUnchecked(3);
		execute("List.filter");
		assertRowUnchecked(1);
		assertRowUnchecked(3);		
	}
		
}
