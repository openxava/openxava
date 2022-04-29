package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class MotoTest extends ModuleTestBase {
	
	public MotoTest(String testName) {
		super(testName, "Moto");		
	}
	
	public void testEntityInAPackageNotNamedModel() throws Exception {
		assertListNotEmpty();
		String make = getValueInList(0, 0);
		String model = getValueInList(0, 1);
		
		execute("List.viewDetail", "row=0");
		assertValue("make", make);
		assertValue("model", model);		
	}
	
}
