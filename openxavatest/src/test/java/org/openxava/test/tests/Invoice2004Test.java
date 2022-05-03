package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Invoice2004Test extends ModuleTestBase {
	
	public Invoice2004Test(String testName) {
		super(testName, "Invoice2004");		
	}
	
		
	public void testFilterUsingEnvironmentVariable() throws Exception {
		int count = getListRowCount();
		for (int i = 0; i < count; i++) {
			assertValueInList(i, "year", "2004");	
		}		
		changeModule("Invoice2002Env");
		count = getListRowCount();
		for (int i = 0; i < count; i++) {
			assertValueInList(i, "year", "2002");	
		}				
	}
	
}
