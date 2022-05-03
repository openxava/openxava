package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class VehicleReadOnlyTest extends ModuleTestBase {
	
	public VehicleReadOnlyTest(String testName) {
		super(testName, "VehicleReadOnly");		
	}
	
	public void testReadOnlyWithViewInheritanceInsideGroupsAndSections() throws Exception { 
		execute("CRUD.new");
		assertNoEditable("code");
		assertNoEditable("model");
		assertNoEditable("make");
	}
		
}
