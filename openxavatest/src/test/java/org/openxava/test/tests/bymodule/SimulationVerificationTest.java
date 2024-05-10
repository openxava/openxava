package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class SimulationVerificationTest extends ModuleTestBase {
	
	public SimulationVerificationTest(String testName) {
		super(testName, "SimulationVerification");		
	}

	public void testSaveNotResetWithReferenceReadOnlyOnCreateFalseWithNestedReferenceKeys() throws Exception {
		execute("CRUD.new");
		assertValue("simulation.product.description", "");
		setValue("simulation.product.number", "3");
		assertValue("simulation.product.description", "XAVA");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		assertMessage("Simulation verification created successfully");
		assertValue("simulation.product.number", "3");
		assertValue("simulation.product.description", "XAVA");		
	}
	
	
}
