package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class SimulationMarginTest extends ModuleTestBase {
	
	public SimulationMarginTest(String testName) {
		super(testName, "SimulationMargin");		
	}

	public void testNoHibernateProxyInModelSettingViewDataFromOnChangeInElementCollectionWithCompositeKeyWithLazyReference() throws Exception { 		
		assertValue("simulation.product.description", "");
		assertCollectionRowCount("details", 0);
		setValue("simulation.product.number", "3");
		assertValue("simulation.product.description", "XAVA");
		assertCollectionRowCount("details", 1);
		assertValueInCollection("details", 0, "simulationDetail.product.description", "SIETE");
		setValueInCollection("details", 0, "sellingPrice", "2");
		assertNoErrors();
		assertMessage("Simulation data reloaded");
	}
	
	
}
