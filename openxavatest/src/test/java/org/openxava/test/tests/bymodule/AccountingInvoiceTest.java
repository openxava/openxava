package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class AccountingInvoiceTest extends ModuleTestBase {
	
	public AccountingInvoiceTest(String testName) {
		super(testName, "AccountingInvoice");		
	}
			
	public void testManyToManyNotRemoveEntityWhenReferencedEntityIsAlsoAOneToManyCascadeRemoveFromOtherEntity_excludeMembersOfParentOfCollectionOnAddingColumnsUsingInheritance_editorForAnnotationWithInheritance_dateNumericType() throws Exception {
		execute("List.addColumns");
		assertNoAction("AddColumns.showMoreColumns");
		// We could have more columns if we modify the model, 
		// the important thing is that columns of the parent of detail (document) are not shown, 
		// like Date of document of positions, Description of document of positions, Number of document of positions
		// That is no column with "of document of positions", not even after pressing AddColumns.showMoreColumns (not shown by now)
		assertCollectionRowCount("xavaPropertiesList", 1); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Positions description");
		closeDialog();

		execute("List.viewDetail", "row=0");
		assertValue("description", "INVOICE 1");
		assertDescriptionIsTextArea(); 
		
		assertCollectionRowCount("receipts", 0);
		execute("Collection.add", "viewObject=xava_view_receipts");
		execute("AddToCollection.add", "row=0");
		assertCollectionRowCount("receipts", 1);
		assertValueInCollection("receipts", 0, 0, "POSITION 1");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_receipts");
		assertCollectionRowCount("receipts", 0);
		
		changeModule("AccountingDocument");
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("description", "DOCUMENT 1");
		assertCollectionRowCount("positions", 1);
		assertValueInCollection("positions", 0, 0, "POSITION 1");
		assertValue("date", "6/10/2019");
		setValue("date", "8/7/2019");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("date", "8/7/2019");
		setValue("date", "6/10/2019");
		execute("CRUD.save");
		execute("Mode.list");
		execute("CRUD.new");
		assertNoErrors();
	}

	private void assertDescriptionIsTextArea() {
		HtmlElement el = getHtmlPage().getHtmlElementById("ox_openxavatest_AccountingInvoice__description");
		assertTrue(el instanceof HtmlTextArea);
	}
					
}
