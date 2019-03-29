package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class Invoice20022004Test extends ModuleTestBase {
	
	public Invoice20022004Test(String testName) {
		super(testName, "Invoice20022004");		
	}
	
	public void testTabSetConditionValue() throws Exception { 
		assertYear("2002");		
		
		execute("ChangeYearCondition.changeTo2004");
		assertYear("2004");
		
		execute("List.filter");
		assertYear("2004");
	}

	private void assertYear(String year) throws Exception {
		assertListNotEmpty();
		int rowCount = getListRowCount();
		for (int row=0; row<rowCount; row++) {
			assertValueInList(row, "year", year);		
		}
	}
	
}
