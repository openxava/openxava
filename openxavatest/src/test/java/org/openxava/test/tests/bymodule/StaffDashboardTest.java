package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class StaffDashboardTest extends ModuleTestBase {
	
	public StaffDashboardTest(String testName) {
		super(testName, "StaffDashboard");		
	}
			
	public void testInitModelWithNewInstance_simpleList_largeDisplayIconMapping() throws Exception { 
		// Init model with new instance
		assertValue("staffCount", "223");
		
		// Simple list
		HtmlElement listEditor = getHtmlPage().getHtmlElementById("ox_openxavatest_StaffDashboard__collection_turnoverByYear___");
		listEditor.asXml().contains("ox-simple-list"); // The correct editor is chosen
		
		assertCollectionRowCount("turnoverByYear", 5);
		assertCollectionColumnCount("turnoverByYear", 3);
		assertValueInCollection("turnoverByYear", 0, 0, "2020");
		assertValueInCollection("turnoverByYear", 4, 0, "2024");
		
		assertNoAction("Collection.add");
		assertNoAction("Collection.edit");
        
        assertLargeDisplayIconMapping();
    }

    private void assertLargeDisplayIconMapping() {
        // Verify that the expected Material Design Icon is rendered in the LargeDisplay for 'menPercentage'
        HtmlElement menPercentageEditor = (HtmlElement) getHtmlPage().getElementById("ox_openxavatest_StaffDashboard__editor_menPercentage");
        assertNotNull(menPercentageEditor);
        assertTrue(menPercentageEditor.asXml().contains("<i class=\"mdi mdi-face-man\""));
    }
					
}
