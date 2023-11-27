package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProfileTest extends ModuleTestBase {
	
	public ProfileTest(String testName) {
		super(testName, "Profile");		
	}
			
	public void testFilteringInListByDescriptionsListWhenKeyWithDot() throws Exception {
		assertListRowCount(3);
		
		setConditionValues("", "", "APPLICATION 1"); 
		assertListRowCount(1); 
		assertValueInList(0, "description", "PROFILE 111");
		
		setConditionValues("", "", "APPLICATION 3.0"); 
		assertListRowCount(1);
		assertValueInList(0, "description", "PROFILE 333");
	}
				
}
