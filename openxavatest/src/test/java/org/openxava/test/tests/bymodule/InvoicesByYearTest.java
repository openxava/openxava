package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

import org.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoicesByYearTest extends ModuleTestBase {
	
	public InvoicesByYearTest(String testName) {
		super(testName, "InvoicesByYear");		
	}
			
	public void testCollectionCountInSectionNotRefreshedOnChangeCollection_collectionTotalsInTransientClassRecalculated_filterInCollection() throws Exception {
		assertCollectionRowCount("invoices", 0);
		assertInvoicesCountInSection(0);
		setValue("year", "2004");
		assertCollectionRowCount("invoices", 5);
		assertInvoicesCountInSection(5);
		
		assertTotalInCollection("invoices", "total", "9,060.10");
		execute("InvoicesByYear.setModel");
		setValue("year", "2002");
		assertTotalInCollection("invoices", "total", "2,900.00");
		
		// Test IFilter in collections (section 1)
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("activeInvoices", 5);
		for (int i = 0; i < 5; i++) {
			assertValueInCollection("activeInvoices", i, "year", "2004");
		}
		assertCollectionRowCount("activeInvoicesPlusYear", 6);
		for (int i = 0; i < 6; i++) {
			assertTrue("Year should be 2004 or 2002", 
				getValueInCollection("activeInvoicesPlusYear", i, "year").equals("2004") || 
				getValueInCollection("activeInvoicesPlusYear", i, "year").equals("2002"));
		}
	}
	
	private void assertInvoicesCountInSection(int expectedCount) throws Exception {
		HtmlElement countElement = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoicesByYear__xava_view_section0_collectionSize");
		assertEquals("(" + expectedCount + ")", countElement.asNormalizedText());
	}
				
}
