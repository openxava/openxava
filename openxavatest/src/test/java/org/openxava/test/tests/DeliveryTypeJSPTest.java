package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class DeliveryTypeJSPTest extends ModuleTestBase {
	
	public DeliveryTypeJSPTest(String testName) {
		super(testName, "DeliveryTypeJSP");		
	}
	
	public void testHandmadeWebViewNotLost_labelTaglib() throws Exception {
		setLocale("es"); 
		
		String number = getValueInList(0, "number");
		String description = getValueInList(0, "description");
		
		execute("List.viewDetail", "row=0");
		assertExists("number");
		assertExists("description");
		assertNotExists("comboDeliveries");
		// Asserting that values are displayed
		assertValue("number", number);
		assertValue("description", description);
				
		// It does not lose the web-view changing mode
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertExists("number");
		assertExists("description");
		assertNotExists("comboDeliveries");		
		
		// <xava:label/> taglib
		String pageText = getHtmlPage().asText();
		assertTrue(pageText.contains("N\u00famero:"));
		assertTrue(pageText.contains("Descripci\u00f3n:"));
	}
			
}
