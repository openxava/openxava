package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerNewOnInitInheritedTest extends ModuleTestBase {

	public CustomerNewOnInitInheritedTest(String testName) {
		super(testName, "CustomerNewOnInitInherited");		
	}
	
	public void testNewOnInit() throws Exception { 
		assertNoErrors();
		assertAction("Mode.list");
	}
					
}
