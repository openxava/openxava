package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ProjectTaskTest extends ModuleTestBase {
	
	public ProjectTaskTest(String testName) {
		super(testName, "ProjectTask");		
	}
	
	public void testGeneratePDFWithEnumIcon() throws Exception {
		String row0 = getHtmlPage().getElementById("ox_openxavatest_ProjectTask__0").asXml();
		assertTrue(row0.contains("<i class=\"mdi mdi-transfer-down\" title=\"LOW\">")); // Icon in the list 
		execute("Print.generatePdf"); 		
		assertContentTypeForPopup("application/pdf");
		assertPopupPDFLine(3, "THE BIG PROJECT ANALYSIS LOW 4/28/2015"); // The important thing is the "LOW"
	}
	
	
}
