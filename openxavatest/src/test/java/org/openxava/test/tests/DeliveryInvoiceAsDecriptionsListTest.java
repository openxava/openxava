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
		/* tmr
		String [][] invoices2004comparator = {
			{"", ""},	
			{"[.10.2004.]:_:2004 10 Juanillo", "2004 10 Juanillo"},
			{"[.11.2004.]:_:2004 11 Carmelo", "2004 11 Carmelo"},
			{"[.12.2004.]:_:2004 12 Cuatrero", "2004 12 Cuatrero"},
			{"[.2.2004.]:_:2004 2 Juanillo", "2004 2 Juanillo"},
			{"[.9.2004.]:_:2004 9 Javi", "2004 9 Javi"}
		};
		assertValidValues("conditionValue___0", invoices2004comparator);
		*/
		// tmr ini
		String [][] invoices2004comparator0 = {
			{"", ""},	
			{"2004", "2004"}
		};
		assertValidValues("conditionValue___0", invoices2004comparator0);
		String [][] invoices2004comparator1 = {
			{"", ""},	
			{"2", "2"},
			{"9", "9"},
			{"10", "10"},
			{"11", "11"},
			{"12", "12"}
		};
		assertValidValues("conditionValue___1", invoices2004comparator1);		
		// tmr fin
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
