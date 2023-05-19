package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *  * @author Javier Paniza
 */

public class FirstStepsTest extends ModuleTestBase {
	
	public FirstStepsTest(String testName) {
		super(testName, "Carrier"); // Whatever		
	}
	
	public void testNoHiddingModuleMenuInFirstSteps() throws Exception {
		getWebClient().getOptions().setCssEnabled(true);
		reload();
		HtmlElement modulesList = getHtmlPage().getHtmlElementById("modules_list");
		assertTrue(modulesList.isDisplayed());
		assertTrue(modulesList.getAttribute("class").contains("ox-display-block-important"));
		
		assertTrue(getHtmlPage().getElementsById("module_header_menu_button").isEmpty());
		assertTrue(getHtmlPage().getElementsById("module_extended_title").isEmpty());
		assertTrue(getHtmlPage().getElementsById("modules_list_hide").isEmpty());
		assertTrue(getHtmlPage().getElementsById("modules_list_show").isEmpty());
	}
	
	protected String getModuleURL() { 
		return "http://" + getHost() + ":" + getPort() + getContextPath() + "m/FirstSteps"; 
	}
	
}
