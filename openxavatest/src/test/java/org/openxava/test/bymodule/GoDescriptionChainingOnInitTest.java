package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoDescriptionChainingOnInitTest extends ModuleTestBase {
	
	public GoDescriptionChainingOnInitTest(String testName) {
		super(testName, "GoDescriptionChainingOnInit");		
	}
	
	public void testChainToForwardActionOnInit() throws Exception { 
		assertTrue((getHtml().contains("is used to test all OpenXava features"))); 
	}
	
}
