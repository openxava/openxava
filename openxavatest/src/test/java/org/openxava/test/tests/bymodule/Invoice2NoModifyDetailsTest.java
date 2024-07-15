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

	/* tmr
	public void testNoModifyInCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionNotEmpty("details");
		execute("Collection.view", "row=0,viewObject=xava_view_details");
		assertNoEditable("quantity");
		assertNoAction("Collection.save");
	}
	*/
	
	public void testNoModifyInCollection_largeDisplay() throws Exception { // tmr
		execute("List.viewDetail", "row=0");
		assertLargeDisplay();
		assertNoModifyInCollection();
	}

	private void assertLargeDisplay() { 
		assertSmallLabels(); // It also tests default-label-format for editor in editors.xml
		HtmlElement amountsSumEditor = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice2NoModifyDetails__editor_amountsSum");
		// TMR ME QUEDÉ POR AQUÍ
		System.out.println("[Invoice2NoModifyDetailsTest.assertLargeDisplay] amountsSumEditor.asXml()=" + amountsSumEditor.asXml()); // tmr
		System.out.println("[Invoice2NoModifyDetailsTest.assertLargeDisplay] amountsSumEditor.asNormalizedText()=" + amountsSumEditor.asNormalizedText());
	}

	private void assertSmallLabels() {
		assertSmallLabel("amountsSum");  
		assertSmallLabel("vatPercentage");
		assertSmallLabel("discount");
		assertSmallLabel("total");
	}

	private void assertSmallLabel(String property) { // tmr
		HtmlElement label = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice2NoModifyDetails__label_" + property);
		assertEquals("small-label", label.getAttribute("class").trim());
	}

	private void assertNoModifyInCollection() throws Exception { // tmr
		assertCollectionNotEmpty("details");
		execute("Collection.view", "row=0,viewObject=xava_view_details");
		assertNoEditable("quantity");
		assertNoAction("Collection.save");
	}	
	
}
