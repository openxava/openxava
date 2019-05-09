package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Javier Paniza
 */
public class BuildingSimpleTest extends ModuleTestBase {
	
	
	public BuildingSimpleTest(String testName) {
		super(testName, "BuildingSimple");		
	}

	public void testOnChangeOnlyForPropertiesInTheView() throws Exception {		
		execute("CRUD.new");
		assertNoMessages();
		assertNoErrors();
	}
		
}
