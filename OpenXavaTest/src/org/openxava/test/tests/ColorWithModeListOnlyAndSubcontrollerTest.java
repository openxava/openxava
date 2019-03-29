package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ColorWithModeListOnlyAndSubcontrollerTest extends ModuleTestBase {
	
	public ColorWithModeListOnlyAndSubcontrollerTest(String testName) {
		super(testName, "ColorWithModeListOnlyAndSubcontroller");		
	}
	
	public void testSubcontrollerWithOnlyListAndWithoutActions() throws Exception{
		assertAction("ColorSub.firstAction");
	}
		
}