package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */

public class OrderDocumentTest extends ModuleTestBase {
		
	public OrderDocumentTest(String testName) {
		super(testName, "OrderDocument");		
	}
			
	public void testCalculationPropertyInReferenceInsideHiddenSection() throws Exception {
		getWebClient().getOptions().setCssEnabled(true);
		execute("List.viewDetail", "row=0");
		assertValue("year", "2024");
		assertFalse(getHtmlPage().getElementById("xava_loading").isDisplayed());
		
	}
				
}
