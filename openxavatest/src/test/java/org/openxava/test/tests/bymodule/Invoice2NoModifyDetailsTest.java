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

	public void testNoModifyInCollection_largeDisplay_newViewAndEditViewForCollections() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertLargeDisplay(); 
		assertNoModifyInCollection();
		assertNewViewAndEditViewForCollections();
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
	
	private void assertNewViewAndEditViewForCollections() throws Exception {
		// @EditView("Simpler") on details collection: view dialog uses Simpler view of InvoiceDetail2
		// We're just in adialog opened by Collection.view, we don't need to call it again
		// execute("Collection.view", "row=0,viewObject=xava_view_details");
		
		// Fields from Simpler view must exist
		assertExists("quantity");
		assertExists("unitPrice");
		
		// Fields not in Simpler view must not exist
		assertNotExists("product.number");
		assertNotExists("amount");
		
		closeDialog();
		
		// @NewView("Simple") on details collection: creation dialog uses Simple view of InvoiceDetail2
		// InvoiceDetail2.new implementation extends Collection.new implementation, so we test the core functionality
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		
		// Fields from Simple view must exist
		assertExists("product.number");
		assertExists("quantity");
		assertExists("unitPrice");
		
		// Fields only in the default view must not exist
		assertNotExists("amount");
		
		closeDialog();
	}
	
	private boolean inEuroCountry() { 
		return false; // Because we use Locale.setDefault(Locale.US) in openxavatest launcher
	}

	
}
