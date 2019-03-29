package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerReadOnlyTest extends ModuleTestBase {
	
	public CustomerReadOnlyTest(String testName) {
		super(testName, "CustomerReadOnly");				
	}
	
	public void testSearhReadOnlyAction() throws Exception {
		setConditionValues("Cuatrero");
		execute("List.filter");
		execute("List.viewDetail", "row=0");
		assertValue("number", "4"); 
		assertValue("name", "Cuatrero");
		assertNoEditable("number");
		assertNoEditable("name");
		execute("Sections.change", "activeSection=1");
		assertNoAction("Collection.new");
		execute("Sections.change", "activeSection=0");
		execute("Collection.view", "row=0,viewObject=xava_view_section0_deliveryPlaces");
		assertNoEditable("name");
		assertNoAction("Collection.save");
	}

	
}
