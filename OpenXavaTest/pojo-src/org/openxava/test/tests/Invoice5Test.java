package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Invoice5Test extends ModuleTestBase {
	
	public Invoice5Test(String testName) {
		super(testName, "Invoice5");		
	}
	
	public void testImportFromExcel() throws Exception {  
		execute("Mode.list");
		assertListRowCount(0);
		assertImportFromExcel("invoices5.xlsx", "2017", "1", "9/25/17", "", "1,258.26");  
		assertImportFromExcel("invoices5.xls", "2017", "1", "9/25/17", "", "1,258.26");
		setLocale("es");
		execute("Mode.list"); 
		assertImportFromExcel("invoices5.xlsx", "2017", "1", "25/09/2017", "", "1.258,26"); 
	}
	
	private void assertImportFromExcel(String file, String value0, String value1, String value2, String value3, String value4) throws Exception {
		execute("ImportData.importData");
		String fileURL = System.getProperty("user.dir") + "/test-files/" + file;
		setFileValue("newFile", fileURL);
		execute("ConfigureImport.configureImport");
		execute("Import.import");
		assertNoErrors(); 
		
		assertListRowCount(1); // We want to test import just one record
		assertValueInList(0, 0, value0);
		assertValueInList(0, 1, value1);
		assertValueInList(0, 2, value2);
		assertValueInList(0, 3, value3);
		assertValueInList(0, 4, value4);
		
		execute("CRUD.deleteRow", "row=0");
		assertListRowCount(0);
	}

		
}
