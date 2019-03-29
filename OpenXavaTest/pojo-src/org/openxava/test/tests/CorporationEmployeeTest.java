package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CorporationEmployeeTest extends ModuleTestBase {
	
	public CorporationEmployeeTest(String testName) {
		super(testName, "CorporationEmployee");		
	}
	
	public void testSimpleHTMLReport() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("CorporationEmployee.report");
		assertNoErrors();
		assertTrue(getPopupText().contains("<tr><td>Corporation:</td><td>RANONE</td></tr>"));
	}
	
	public void testTabEditorForModel() throws Exception {
		assertNoErrors(); // To test a bug on init
		assertListRowCount(2); 
		setValue("chooseSegment", "low");
		assertListRowCount(1);
		assertValueInList(0, 1, "MIGUEL"); 
		setValue("chooseSegment", "high");
		assertListRowCount(1);
		assertValueInList(0, 1, "ANA"); 
		
		assertAction("ListFormat.select", "editor=Charts");
		assertAction("ListFormat.select", "editor=__NONAME__");
		assertNoAction("ListFormat.select", "editor=List");
		
		execute("ListFormat.select", "editor=Charts");
		assertExists("xColumn");
		assertNoAction("List.filter");
		execute("ListFormat.select", "editor=__NONAME__");
		assertNotExists("xColumn");
		assertAction("List.filter");	
		assertExists("chooseSegment");
	}
			
}
