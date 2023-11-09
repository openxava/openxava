package org.openxava.test.tests;

import org.htmlunit.html.*;
import org.openxava.tests.*;

/**
 * 
 * @since 5.2
 * @author Javier Paniza
 */
abstract public class CustomizeListTestBase extends ModuleTestBase {
	
	private String module;
	
	public CustomizeListTestBase(String testName, String module) {
		super(testName, module);
		this.module = module;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	// With HtmlUnit the column is not removed from UI, but the AJAX call is done, 
	// so a reload() is needed most times
	protected void removeColumn(int index) throws Exception {  
		removeColumn("list", "xava_tab", index);
	}
	
	// With HtmlUnit the column is not removed from UI, but the AJAX call is done, 
	// so a reload() is needed most times	
	protected void removeColumn(String collection, int index) throws Exception { 
		removeColumn(collection, "xava_collectionTab_" + collection, index);
	}

	// This is not an exact mirror of the real behavior. 
	// It does not do drag & drop and it does a reload().
	protected void moveColumn(int from, int to) throws Exception { 
		getHtmlPage().executeJavaScript("Tab.moveProperty('ox_openxavatest_" + module +"__list', " + from + ", " + to + ")");
		Thread.sleep(30);
		reload();		
	}
		
	private void removeColumn(String collection, String tabObject, int index) throws Exception {
		getWebClient().getOptions().setCssEnabled(true); 
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement header = table.getRow(0).getCell(index + 2);
		HtmlElement icon = header.getElementsByAttribute("i", "class", "mdi mdi-close-circle").get(0);
		HtmlElement removeLink = icon.getEnclosingElement("a");
		// tmr ini
		if (!removeLink.isDisplayed()) {
			table.getOneHtmlElementByAttribute("a", "class", "xava_customize_list ox-image-link").click();
		}
		removeLink.click(); 
		// tmr fin
		// tmr getHtmlPage().executeJavaScript(getHrefAttribute(removeLink)); // Because removeLink.click() does not work with HtmlUnit 2.70
		Thread.sleep(800); 
	}

}
