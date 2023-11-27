package org.openxava.test.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerReadOnlyTest extends ModuleTestBase {
	
	public CustomerReadOnlyTest(String testName) {
		super(testName, "CustomerReadOnly");				
	}
	
	public void testSearhReadOnlyAction_enumSizeNotDependOnColumnSize() throws Exception { 
		setConditionValues("Cuatrero");
		execute("List.filter");
		execute("List.viewDetail", "row=0");
		assertValue("number", "4"); 
		assertValue("name", "Cuatrero");
		assertNoEditable("number");
		assertNoEditable("name");
		assertTypeSize(); 
		execute("Sections.change", "activeSection=1");
		assertNoAction("Collection.new");
		execute("Sections.change", "activeSection=0");
		execute("Collection.view", "row=0,viewObject=xava_view_section0_deliveryPlaces");
		assertNoEditable("name");
		assertNoAction("Collection.save");
	}

	private void assertTypeSize() throws Exception {
		HtmlInput input = (HtmlInput) getHtmlPage().getElementByName("ox_openxavatest_CustomerReadOnly__type_DESCRIPTION_");
		assertEquals("Normal", input.getValue());
		assertEquals("7", input.getSize()); // 7, that is the size of "Normal", 6, plus 1 to ensure is always visible, even with MMMMMM
		
	}

	
}
