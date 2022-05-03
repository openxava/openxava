package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Javier Paniza
 */

public class Invoice3Test extends ModuleTestBase {
	
	public Invoice3Test(String testName) {
		super(testName, "Invoice3");		
	}
	
	public void testDefaultTabs() throws Exception {
		assertDefaultTabs(false);
		changeModule("Invoice3Ascending");
		assertDefaultTabs(true);
	}
	
	// Default tabs are those from tabs.xml
	private void assertDefaultTabs(boolean ascending) throws Exception {
		assertListRowCount(5); 		
		assertListColumnCount(3);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertValueInList(0, 0, "2004");
		assertValueInList(0, 1, ascending?"2":"12");
		assertValueInList(4, 0, "2004");
		assertValueInList(4, 1, ascending?"12":"2");		
	}
	
}
