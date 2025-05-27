package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderDocumentTest extends ModuleTestBase {
		
	public OrderDocumentTest(String testName) {
		super(testName, "OrderDocument");		
	}
			
	// tmr Cambiar nombre del test
	public void testCalculationPropertyInReferenceInsideHiddenSection() throws Exception {
		getWebClient().getOptions().setCssEnabled(true);
		
		// Configure MessageConfirmHandler to detect confirmation dialogs
		MessageConfirmHandler confirmHandler = new MessageConfirmHandler();
		getWebClient().setConfirmHandler(confirmHandler);
		
		// Go to detail mode
		execute("List.viewDetail", "row=0");
		assertValue("year", "2024");
		assertFalse(getHtmlPage().getElementById("xava_loading").isDisplayed());
		
		// Return to list mode without making any changes
		execute("Mode.list");
		
		// Verify that no confirmation dialog has been shown
		confirmHandler.assertNoMessage();
	}
				
}
