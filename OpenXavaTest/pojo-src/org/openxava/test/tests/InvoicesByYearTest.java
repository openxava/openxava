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
		// TMR ME QUEDÉ POR AQUÍ: YA SALE VERDE. FALTA PROBAR SIN EL setModel() EN ACCION Y REVISAR EL CÓDIGO 
		// TMR Probar a mano cambiar de año, cuando esté el código definitivo
		assertTotalInCollection("invoices", "total", "9,060.10");
		// tmr fin
	}
	
	private void assertInvoicesCountInSection(int expectedCount) throws Exception {
		HtmlElement countElement = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_InvoicesByYear__xava_view_section0_collectionSize");
		assertEquals("(" + expectedCount + ")", countElement.asText());
	}
				
}
