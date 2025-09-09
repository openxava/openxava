package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
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
		assertValueInList(0, 5, "DEBT"); 
		execute("List.viewDetail", "row=0");
		assertAutomaticMappingForOldIconNames(); 
	}

	private void assertAutomaticMappingForOldIconNames() {
        // Verify that the expected Material Design Icon is rendered specifically in the 'New' action anchor
        HtmlElement newAction = (HtmlElement) getHtmlPage().getElementById("ox_openxavatest_Event__Event___new");
        assertNotNull(newAction);
        assertTrue(newAction.asXml().contains("<i class=\"mdi mdi-plus-box-multiple\"")); // Not library-plus
    }
	
}
