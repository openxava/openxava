package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoicesByYearTest extends ModuleTestBase {
	
	public InvoicesByYearTest(String testName) {
		super(testName, "InvoicesByYear");		
	}
			
	public void testCollectionCountInSectionNotRefreshedOnChangeCollection() throws Exception {
		assertCollectionRowCount("invoices", 0);
		assertInvoicesCountInSection(0);
		setValue("year", "2004");
		assertCollectionRowCount("invoices", 5);
		assertInvoicesCountInSection(5);
		
		// tmr ini
		// tmr Cambiar nombre método
		assertTotalInCollection("invoices", "total", "9,060.10");
		execute("InvoicesByYear.setModel");
		setValue("year", "2002");
		assertTotalInCollection("invoices", "total", "2,900.00");
		// tmr fin
	}
	
	private void assertInvoicesCountInSection(int expectedCount) throws Exception {
		HtmlElement countElement = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_InvoicesByYear__xava_view_section0_collectionSize");
		assertEquals("(" + expectedCount + ")", countElement.asText());
	}
				
}
