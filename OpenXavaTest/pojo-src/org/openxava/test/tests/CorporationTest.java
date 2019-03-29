package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class CorporationTest extends ModuleTestBase {
	
	public CorporationTest(String testName) {
		super(testName, "Corporation");		
	}
	
	public void testSimpleHTMLReportWithCollections() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Corporation.report");
		assertNoErrors();
		assertTrue(getPopupText().contains("<tr><td>Name:</td><td>RANONE</td></tr>")); 
	}
	
	public void testIconEditor() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true);
		execute("List.viewDetail", "row=0");
		assertNoIconInEditor(); 
		execute("Icon.add", "newIconProperty=icon");
		executeIconChoose("alarm-check"); 

		assertIconInEditor("alarm-check");
		
		execute("CRUD.save");
		assertNoIconInEditor();
		
		execute("Mode.list");
		assertTrue(getHtml().contains("<i class=\"mdi mdi-alarm-check\"")); 
		
		execute("List.viewDetail", "row=0");
		assertIconInEditor("alarm-check");
		
		execute("Icon.change", "newIconProperty=icon");
		executeIconChoose("arrow-expand"); 
		assertIconInEditor("arrow-expand");
		
		execute("Icon.remove", "newIconProperty=icon");
		assertNoIconInEditor();
		
		execute("CRUD.save");
		execute("Mode.list");
		assertFalse(getHtml().contains("<i class=\"mdi mdi-alarm-check\"")); 
	}

	private void executeIconChoose(String icon) throws Exception {
		HtmlElement view = (HtmlElement) getHtmlPage().getElementById("ox_OpenXavaTest_Corporation__view");
		HtmlElement i = (HtmlElement) view.getOneHtmlElementByAttribute("i", "class", "mdi mdi-" + icon);
		i.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
	}

	private void assertIconInEditor(String icon) { 
		HtmlElement editor = (HtmlElement) getHtmlPage().getElementById("ox_OpenXavaTest_Corporation__editor_icon");
		assertFalse(editor.asXml().contains("<i class=\"mdi mdi-plus\""));
		assertTrue(editor.asXml().contains("<i class=\"mdi mdi-" + icon + "\""));		
		HtmlElement close = (HtmlElement) editor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-close-circle");
		assertTrue(close.isDisplayed());
	}

	private void assertNoIconInEditor() { 
		HtmlElement editor = (HtmlElement) getHtmlPage().getElementById("ox_OpenXavaTest_Corporation__editor_icon");
		assertTrue(editor.asXml().contains("<i class=\"mdi mdi-plus\""));
		assertFalse(editor.asXml().contains("<i class=\"mdi mdi-alarm-check\""));
		assertFalse(editor.asXml().contains("<i class=\"mdi mdi-arrow-expand\""));
		HtmlElement close = (HtmlElement) editor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-close-circle");
		assertFalse(close.isDisplayed());
	}
	
}
