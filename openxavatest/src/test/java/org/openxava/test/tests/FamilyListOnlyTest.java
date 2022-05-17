package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class FamilyListOnlyTest extends ModuleTestBase {
	
	
	public FamilyListOnlyTest(String testName) {
		super(testName, "FamilyListOnly");		
	}	
	
	public void testWithoutXavaListAction() throws Exception {
		assertNoAction("Mode.list");
		assertNoAction("List.viewDetail");
	}
					
}
