package org.openxava.test.tests.bymodule;

import org.openxava.test.model.*;

/**
 * TMR ME QUEDÉ POR AQUÍ: ACABADO DE HACER, FUNCIONA Y ESTÁ BIEN. AHORA A POR LA COLECCIÓN.
 * tmr Move to CarrierTest
 * tmr Change all openxavatest to UTF-8
 * Specific tests for uppercase conversion actions in Carrier.
 * 
 * @author Javier Paniza
 */
public class CarrierTmrTest extends CarrierTestBase {
	
	public CarrierTmrTest(String testName) {
		super(testName, "Carrier");
	}
	
	/**
	 * Test to verify the availability of the toUpperCase action.
	 * The first 4 rows should not have the action available,
	 * but the fifth row should have it.
	 */
	public void testToUpperCaseActionAvailability() throws Exception {
		execute("List.orderBy", "property=number");
		
		// Verify that the action is not available for the first 4 rows
		for (int i = 0; i < 4; i++) {
			assertNoAction("Carrier.toUpperCase", "row=" + i);
		}
		
		// Verify that the action is available for the fifth row (index 4)
		assertAction("Carrier.toUpperCase", "row=4");
		
		// Execute the action on the fifth row
		execute("Carrier.toUpperCase", "row=4");
		
		// Verify that the action is no longer available for the fifth row
		assertNoAction("Carrier.toUpperCase", "row=4");
	}
}
