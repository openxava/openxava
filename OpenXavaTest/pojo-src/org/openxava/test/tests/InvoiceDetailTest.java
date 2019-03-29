package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * Testing InvoiceDetail as a module is somewhat unorthodox, 
 * but practical for testing some case. 
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailTest extends ModuleTestBase {
	
	public InvoiceDetailTest(String testName) {
		super(testName, "InvoiceDetail"); 		
	}
	
	public void testGroupByWithCalculatedProperties_ImagesGalleryInsideAReference() throws Exception { 
		assertListRowCount(10);
		assertListColumnCount(14);
		assertLabelInList(12, "Amount");
		selectGroupBy("Group by year of invoice");
		assertNoErrors();
		assertListRowCount(4);
		assertListColumnCount(4);
		assertLabelInList(0, "Year of Invoice");
		assertLabelInList(1, "Quantity");
		assertLabelInList(2, "Unit price");
		assertLabelInList(3, "Record count");
		
		execute("CRUD.new");
		execute("Gallery.edit", "galleryProperty=photos,viewObject=xava_view_product");
		assertNoErrors(); 
		assertMessage("No images");
		assertDialog();
		execute("Close.close"); 
		assertNoDialog();
		assertExists("quantity");
	}
	
}
