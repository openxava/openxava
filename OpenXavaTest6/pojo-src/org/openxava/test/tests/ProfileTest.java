package org.openxava.test.tests;

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
		
		setConditionValues("", "", "1:_:1 APPLICATION 1");
		assertListRowCount(1);
		assertValueInList(0, "description", "PROFILE 111");
		
		setConditionValues("", "", "3.0:_:3.0 APPLICATION 3.0");
		assertListRowCount(1);
		assertValueInList(0, "description", "PROFILE 333");
		
	}
				
}
