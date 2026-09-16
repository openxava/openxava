package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

import org.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class DealTest extends ModuleTestBase {
	
	public DealTest(String testName) {
		super(testName, "Deal");		
	}
	
	public void testListWithOneToOneWithPrimaryKeyJoinColumn_themeChooser() throws Exception {  
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "THE BIG DEAL");
		assertValueInList(0, 2, "JUAN");
		
		assertTrue(getHtml().contains("/xava/style/auto.css"));
		assertFalse(getHtml().contains("/xava/style/dark.css"));
		HtmlElement darkLink = getHtmlPage().getBody().getOneHtmlElementByAttribute("a","href", "?theme=dark.css");
		HtmlPage newPage = darkLink.click();
		assertFalse(newPage.asXml().contains("/xava/style/auto.css"));
		assertTrue(newPage.asXml().contains("/xava/style/dark.css"));

		resetModule();
		assertFalse(getHtml().contains("/xava/style/auto.css"));
		assertTrue(getHtml().contains("/xava/style/dark.css"));
	}
	
	public void testIdInsideASection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoErrors(); // The first attempt does not fail when the test was written, but just in case it would fail 
		execute("Mode.list");		
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("id", "1");
		assertValue("name", "THE BIG DEAL");
	}
	
}
