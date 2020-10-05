package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class FilterBySubfamilyAndYearTest extends ModuleTestBase {
	
	public FilterBySubfamilyAndYearTest(String testName) {
		super(testName, "FilterBySubfamilyAndYear");		
	}
	
	public void testTransientClassInheritance_setEntityExtendingTransient() throws Exception { 
		assertExists("year"); 
		assertExists("subfamily.number");		
		
		execute("FilterBySubfamilyAndYear.changeToRecord");
		assertValue("number", "1");
		assertValue("description", "ONE");
		assertNoErrors();
		HtmlUnitUtils.assertPageURI(getHtmlPage(), "/FilterBySubfamilyAndYear");
	}
		
}
