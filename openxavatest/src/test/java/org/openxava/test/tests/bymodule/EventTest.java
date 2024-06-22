package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Chungyen Tsai
 */
public class EventTest extends ModuleTestBase {

	public EventTest(String testName) {
		super(testName, "Event");
	}
	
	public void testComboShowValueWhenHasCondition_tabGoPage1WhenChangeTab() throws Exception {
		execute("List.goPage", "page=2");
		execute("Event.changeTab");
		assertValueInList(0, 0, "DEBT"); 
		execute("Event.changeTab");
		assertValueInList(0, 6, "DEBT"); 
		execute("List.viewDetail", "row=0");
		assertValue("debtAtDate.id", "4028e4ab8a8966d7018a896a5bf80000");
	}
	
}
