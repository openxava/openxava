package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class DeliveryInvoiceAsDecriptionsListTest extends ModuleTestBase {

	
	public DeliveryInvoiceAsDecriptionsListTest(String testName) {
		super(testName, "DeliveryInvoiceAsDecriptionsList");		
	}
	
	
	public void testFilterInDescriptionsList() throws Exception { 
		String [][] invoices2004comparator = {
			{"", ""},	
			{"[.10.2004.]:_:2004 10 Juanillo", "2004 10 Juanillo"},
			{"[.11.2004.]:_:2004 11 Carmelo", "2004 11 Carmelo"},
			{"[.12.2004.]:_:2004 12 Cuatrero", "2004 12 Cuatrero"},
			{"[.2.2004.]:_:2004 2 Juanillo", "2004 2 Juanillo"},
			{"[.9.2004.]:_:2004 9 Javi", "2004 9 Javi"}
		};
		assertValidValues("conditionValue___0", invoices2004comparator);			
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
