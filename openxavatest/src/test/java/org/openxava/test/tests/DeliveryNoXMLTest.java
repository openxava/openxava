package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryNoXMLTest extends ModuleTestBase {
	
		
	public DeliveryNoXMLTest(String testName) {
		super(testName, "Delivery");		
	}
	
	public void testViewDynamicValidValueInSection() throws Exception { 
		execute("Delivery.addShortcutOptions"); 
		execute("CRUD.new");
		String [][] validValues = {
			{ "", "" },
			{ "a", "AA" },
			{ "b", "BB" }
		};
		assertValidValues("shortcut", validValues); 
		
		String [][] validValuesWithNoBlank = {
			{ "a", "AA" },
			{ "b", "BB" }
		};
		execute("Delivery.removeBlankShortcutOption");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0"); // To refresh, surely we should improve this, in order valid values View methods refresh automatically
		assertValidValues("shortcut", validValuesWithNoBlank);
		
		String [][] validValuesWithNoEntries = {
			{ "", "" }	
		};
		execute("Delivery.clearShortcutOptions");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0"); // To refresh, surely we should improve this, in order valid values View methods refresh automatically
		assertValidValues("shortcut", validValuesWithNoEntries); 		
		
		HtmlElement shortcut = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Delivery__shortcut");
		assertTrue(shortcut instanceof HtmlSelect);
		execute("Delivery.disableShortcutOptions");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0"); // To refresh, surely we should improve this, in order valid values View methods refresh automatically
		shortcut = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Delivery__shortcut");
		assertTrue(shortcut instanceof HtmlTextInput);
	}
	
}
