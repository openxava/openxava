package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

public class DeliveryTypeReadOnlyNumberTest extends ModuleTestBase {

	public DeliveryTypeReadOnlyNumberTest(String testName) {
		super(testName, "DeliveryTypeReadOnlyNumber");		
	}
	
	public void testKeyReadOnlyOnCreate() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoEditable("number");
		execute("CRUD.new");
		assertNoEditable("number");
	}
	
}
