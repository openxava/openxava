package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Invoice2NoModifyDetailsTest extends ModuleTestBase {
	

	public Invoice2NoModifyDetailsTest(String testName) {
		super(testName, "Invoice2NoModifyDetails");		
	}

	public void testNoModifyInCollection_largeDisplay() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertLargeDisplay();
		assertNoModifyInCollection();
	}

	private void assertLargeDisplay() throws Exception { 
		assertSmallLabels(); // It also tests default-label-format for editor in editors.xml
		assertDisplayValue("amountsSum", "\u20AC2,500.00"); // Euro and prefix
		assertDisplayValue("vatPercentage", "16.0%"); // Percentage and suffix
		assertDisplayValue("total", "2,650.00\u20AC"); // Automatic @Money suffix. Symbol and position depend on the server locale, not browser locale
		// assertDisplayValue("total", "$2,650.00"); // Automatic @Money prefix. Starting openxavatest with -Duser.language=en -Duser.country=US
		
		assertEditorContains("vatPercentage", "<i class=\"mdi mdi-label-percent-outline\">"); // Icon
		assertEditorNotContain("amountsSum", "<i class=\"mdi mdi"); // Icon
		assertEditorContains("discount", "ox-large-display-negative"); // Negative style
		assertEditorNotContain("amountsSum", "ox-large-display-negative"); // Negative style
	}

	private void assertEditorContains(String property, String expectedContainedContent) {
		assertTrue(getEditor(property).asXml().contains(expectedContainedContent));
	}
	
	private void assertEditorNotContain(String property, String expectedContainedContent) {
		assertFalse(getEditor(property).asXml().contains(expectedContainedContent));
	}	

	private void assertDisplayValue(String property, String expectedValue) {
		assertEquals(expectedValue, getEditor(property).asNormalizedText());
	}

	private HtmlElement getEditor(String property) {
		return getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice2NoModifyDetails__editor_" + property);
	}

	private void assertSmallLabels() {
		assertSmallLabel("amountsSum");  
		assertSmallLabel("vatPercentage");
		assertSmallLabel("discount");
		assertSmallLabel("total");
	}

	private void assertSmallLabel(String property) { 
		HtmlElement label = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice2NoModifyDetails__label_" + property);
		assertEquals("small-label", label.getAttribute("class").trim());
	}

	private void assertNoModifyInCollection() throws Exception { 
		assertCollectionNotEmpty("details");
		execute("Collection.view", "row=0,viewObject=xava_view_details");
		assertNoEditable("quantity");
		assertNoAction("Collection.save");
	}	
	
}
