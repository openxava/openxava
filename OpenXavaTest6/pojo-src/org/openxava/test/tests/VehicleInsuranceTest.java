package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class VehicleInsuranceTest extends ModuleTestBase {
		
	public VehicleInsuranceTest(String testName) {
		super(testName, "VehicleInsurance");		
	}
		
	public void testViewAnnotationsInSubclassFromMappingSuperclass() throws Exception {
		assertExists("description");
		assertAction("HidingActions.hideSave");
		assertExists("vehicle.code");
		assertExists("vehicle.model");
		assertNotExists("vehicle.make");
		assertExists("policyNumber");

		int descriptionCount = getHtmlPage().getBody().getElementsByAttribute("input", "name", "ox_OpenXavaTest_VehicleInsurance__description").size();
		assertEquals(1, descriptionCount);

		int vehicleCount = getHtmlPage().getBody().getElementsByAttribute("input", "name", "ox_OpenXavaTest_VehicleInsurance__vehicle___code").size();
		assertEquals(1, vehicleCount);
	}
				
}
