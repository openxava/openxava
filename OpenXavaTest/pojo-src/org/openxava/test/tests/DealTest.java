package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class DealTest extends ModuleTestBase {
	
	public DealTest(String testName) {
		super(testName, "Deal");		
	}
	
	public void testListWithOneToOneWithPrimaryKeyJoinColumn_themeChooser() throws Exception { // tmp themeChooser 
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "THE BIG DEAL");
		assertValueInList(0, 2, "JUAN");
		
		// tmp ini
		assertTrue(getHtml().contains("/xava/style/terra.css"));
		assertFalse(getHtml().contains("/xava/style/blue.css"));
		HtmlElement blueLink = getHtmlPage().getBody().getOneHtmlElementByAttribute("a","href", "?theme=blue.css");
		HtmlPage newPage = blueLink.click();
		assertFalse(newPage.asXml().contains("/xava/style/terra.css"));
		assertTrue(newPage.asXml().contains("/xava/style/blue.css"));
		
		resetModule();
		assertFalse(getHtml().contains("/xava/style/terra.css"));
		assertTrue(getHtml().contains("/xava/style/blue.css"));		
		// tmp fin
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
