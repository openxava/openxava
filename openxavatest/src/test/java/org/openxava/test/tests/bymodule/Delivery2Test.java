package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class Delivery2Test extends ModuleTestBase {

	public Delivery2Test(String testName) {
		super(testName, "Delivery2");
	}

	public void testTabWithBaseConditionWithCompositeKeyWithReferenceWhoseNameDoesNotContainsTheEntityName() throws Exception {
		// That is with Invoice deliveryInvoice works, but with Invoice delinv doesn't work

		assertListRowCount(1); // So we have a baseCondition
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("description", "DELIVERY JUNIT 666");
	}

}
