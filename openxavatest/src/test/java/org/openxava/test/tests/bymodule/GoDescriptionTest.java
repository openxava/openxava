package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class GoDescriptionTest extends ModuleTestBase {
	
	public GoDescriptionTest(String testName) {
		super(testName, "GoDescription");		
	}
	
	public void testForwardActionOnEachRequestOnLoadModuleFirstTime() throws Exception {
		assertTrue((getHtml().contains("is used to test all OpenXava features"))); 
	}
	
}
