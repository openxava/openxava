package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ChargeDescriptionTest extends ModuleTestBase {
	
	public ChargeDescriptionTest(String testName) {
		super(testName, "ChargeDescription");		
	}
			
	public void testReadOnlyReferenceToAEntityWithASingleReferenceAsKeyThatNotIncludeTheKeyReferenceInTheView() throws Exception {
		// We could add a execute("CRUD.new") when the module will have records. We just need to test the detail mode.
		assertNoEditable("transportCharge.amount"); // We test that the view is not broken, but using assertNoEditable() also test the @ReadOnly in the reference 
	}
				
}
