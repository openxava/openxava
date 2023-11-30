package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class MammalTest extends ModuleTestBase {
	
	public MammalTest(String testName) {
		super(testName, "Mammal");		
	}

	// LivingBeing and Animal modules cannot be executed before executing this,
	// so we can reproduce the bug
	public void testThirdLevelViewInheritanceWhenGrandchildIsFirstAccessedModule() throws Exception {
		// execute("CRUD.new"); // Not needed because there is no record, but if we have records we can uncomment this
		assertExists("name");
		assertExists("type");
		assertExists("pregnancyPeriodInWeeks");
	}
			
}
