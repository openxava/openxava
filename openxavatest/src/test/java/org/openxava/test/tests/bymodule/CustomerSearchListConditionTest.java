package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * @author Chungyen Tsai
 */

public class CustomerSearchListConditionTest extends ModuleTestBase {
	
	
	public CustomerSearchListConditionTest(String testName) {
		super(testName, "CustomerSearchListCondition");		
	}
	
	public void testSearchListCondition() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=transientSeller.number"); 
		assertListRowCount(1);
		assertValueInList(0, 0, "4");
		execute("ReferenceSearch.cancel");
		execute("Reference.search", "keyProperty=alternateSeller.number"); 
		assertListRowCount(1);
		assertValueInList(0, 0, "1");
	}
	
}
