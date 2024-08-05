package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * @author Chungyen Tsai
 */

public class CustomerSearchListCondition extends ModuleTestBase {
	
	
	public CustomerSearchListCondition(String testName) {
		super(testName, "CustomerSearchListCondition");		
	}
	
	public void testSearchListCondition() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=alternateSeller.number"); 
		assertListRowCount(1);
		assertValueInList(0, 0, "1");
	}
	
}
