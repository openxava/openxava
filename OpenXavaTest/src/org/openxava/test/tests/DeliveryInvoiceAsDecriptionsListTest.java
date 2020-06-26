package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class DeliveryInvoiceAsDecriptionsListTest extends ModuleTestBase {

	
	public DeliveryInvoiceAsDecriptionsListTest(String testName) {
		super(testName, "DeliveryInvoiceAsDecriptionsList");		
	}
	
	
	public void testFilterInDescriptionsList() throws Exception { // tmp
		// TMP ME QUEDÉ POR: YA FUNCIONA. HECHO CON XML. TRATANDO DE LANZAR LA SUITE (FALLA EN ExamTest.testChangeDataSource)
		// TMP   LA SUITE FALLÓ DOS VECES. AUNQUE CREO QUE ES EL ECLIPSE. PROBAR CON 2020-06 PURO.
		// TMP   HICE ALGO DE DOCUMENTACIÓN.
		execute("CRUD.new");
		String [][] invoices2004 = {
			{"", ""},	
			{"[.10.2004.]", "2004 10 Juanillo"},
			{"[.11.2004.]", "2004 11 Carmelo"},
			{"[.12.2004.]", "2004 12 Cuatrero"},
			{"[.2.2004.]", "2004 2 Juanillo"},
			{"[.9.2004.]", "2004 9 Javi"}
		};
		assertValidValues("invoice.KEY", invoices2004);
	}
		
}
