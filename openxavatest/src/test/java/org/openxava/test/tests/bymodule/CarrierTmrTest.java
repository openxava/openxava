package org.openxava.test.tests.bymodule;

/**
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
	
	/**
	 * Test to verify the availability and functionality of the removeRemarks action.
	 * The action should be available only for carriers that have remarks.
	 * After executing the action, the remarks should be removed and the action should no longer be available.
	 */
	public void testRemoveRemarksActionAvailability() throws Exception {
		// Select the first carrier to access its detail view
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=0");
				
		// Verify that the removeRemarks action is available for the carriers with remarks (rows 0 and 1)
		// In the collection, carrier 2 is at index 0 and carrier 3 is at index 1
		assertAction("Carrier.removeRemarks", "row=0,viewObject=xava_view_fellowCarriersCalculated"); // Carrier 2 has remarks
		assertAction("Carrier.removeRemarks", "row=1,viewObject=xava_view_fellowCarriersCalculated"); // Carrier 3 has remarks
		
		// Verify that the action is not available for carrier 4 (index 2) which doesn't have remarks
		assertNoAction("Carrier.removeRemarks", "row=2,viewObject=xava_view_fellowCarriersCalculated");
		
		// Execute the action on carrier 3 (index 1)
		execute("Carrier.removeRemarks", "row=1,viewObject=xava_view_fellowCarriersCalculated");
		
		// Verify that the action is no longer available for carrier 3 (index 1) after removing its remarks
		assertNoAction("Carrier.removeRemarks", "row=1,viewObject=xava_view_fellowCarriersCalculated");
		
		// The action should still be available for carrier 2 (index 0)
		assertAction("Carrier.removeRemarks", "row=0,viewObject=xava_view_fellowCarriersCalculated");
	}
}
