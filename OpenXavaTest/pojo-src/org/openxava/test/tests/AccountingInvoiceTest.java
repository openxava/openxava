package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class AccountingInvoiceTest extends ModuleTestBase {
	
	public AccountingInvoiceTest(String testName) {
		super(testName, "AccountingInvoice");		
	}
			
	public void testManyToManyNotRemoveEntityWhenReferencedEntityIsAlsoAOneToManyCascadeRemoveFromOtherEntity() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("description", "INVOICE 1");
		
		assertCollectionRowCount("receipts", 0);
		execute("Collection.add", "viewObject=xava_view_receipts");
		execute("AddToCollection.add", "row=0");
		assertCollectionRowCount("receipts", 1);
		assertValueInCollection("receipts", 0, 0, "POSITION 1");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_receipts");
		assertCollectionRowCount("receipts", 0);
		
		changeModule("AccountingDocument");
		execute("List.viewDetail", "row=0");
		assertValue("description", "DOCUMENT 1");
		assertCollectionRowCount("positions", 1);
		assertValueInCollection("positions", 0, 0, "POSITION 1");
	}
				
}
