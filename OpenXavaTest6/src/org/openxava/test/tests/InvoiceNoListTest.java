package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class InvoiceNoListTest extends ModuleTestBase {
	

	public InvoiceNoListTest(String testName) {
		super(testName, "InvoiceNoList");		
	}

	public void testSearchReferenceWithListInANoListModule() throws Exception {
		assertNoAction("Mode.list"); 
		// execute("CRUD.new"); // Does not call to new in order to test a bug that arise in this case
		setValue("year", "2002");
		setValue("number", "1");
		execute("CRUD.refresh");
		execute("Sections.change", "activeSection=1");
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertValue("product.description", "");
		execute("Reference.search", "keyProperty=product.number");
		int lastIndex = getListRowCount() - 1;		
		String description = getValueInList(lastIndex, 1);		
		execute("ReferenceSearch.choose", "row=" + lastIndex);
		assertNoErrors(); 
		assertValue("product.description", description); 
	}
								
}
