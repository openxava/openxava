package org.openxava.test.tests.bymodule;

import java.util.*;

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
		assertCustomLabels(); // It also tests default-label-format for editor in editors.xml
		assertDisplayValue("amountsSum", "\u20AC2,500.00"); // Euro and prefix
		assertDisplayValue("vatPercentage", "16.0%"); // Percentage and suffix
		if (inEuroCountry()) { // It works if user.country for server and test in the same 
			assertDisplayValue("total", "2,650.00\u20AC"); // Automatic @Money suffix. Symbol and position depend on the server locale, not browser locale
		}
		else {
			assertDisplayValue("total", "$2,650.00"); // Automatic @Money prefix. 
		}
		
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
		HtmlElement editor = getEditor(property);
		HtmlElement largeDisplayDiv = editor.getFirstByXPath(".//div[contains(@class, 'ox-large-display ')]");
		assertNotNull("Div with class ox-large-display not found inside editor for " + property, largeDisplayDiv);
		assertEquals(expectedValue, largeDisplayDiv.asNormalizedText());
	}

	private HtmlElement getEditor(String property) {
		return getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice2NoModifyDetails__editor_" + property);
	}

	private void assertCustomLabels() {
		assertCustomLabel("amountsSum");  
		assertCustomLabel("vatPercentage");
		assertCustomLabel("discount");
		assertCustomLabel("total");
	}

	private void assertCustomLabel(String property) { 
		String id = "ox_openxavatest_Invoice2NoModifyDetails__label_" + property;
		List<?> elements = getHtmlPage().getElementsById(id);
		assertEquals(1, elements.size()); // Only the label inside the editor, so the default-label-format="no-label" in editors.xml works
		HtmlElement label = (HtmlElement) elements.get(0);
		assertEquals("ox-large-display-label", label.getAttribute("class").trim());
	}

	private void assertNoModifyInCollection() throws Exception { 
		assertCollectionNotEmpty("details");
		execute("Collection.view", "row=0,viewObject=xava_view_details");
		assertNoEditable("quantity");
		assertNoAction("Collection.save");
	}
	
	private boolean inEuroCountry() { 
		return false; // Because we use Locale.setDefault(Locale.US) in openxavatest launcher
	}

	
}
