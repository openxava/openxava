package org.openxava.test.tests.bymodule;

import java.text.*;
import java.util.*;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Invoice5Test extends ModuleTestBase {
	
	public Invoice5Test(String testName) {
		super(testName, "Invoice5");		
	}
	
	
	public void testDatesInCroatian() throws Exception { 
		assertDatesInCroatian();
		changeModule("Invoice6"); // To test LocalDate
		assertDatesInCroatian();
	}
	
	public void assertDatesInCroatian() throws Exception { 
		setLocale("hr");
		execute("CRUD.new");
		assertValue("date", getToday());
		assertDateInCroatian("20.9.2031", "20.09.2031");
		execute("CRUD.new");
		assertDateInCroatian("1.12.31", "01.12.2031");		
	}

	private void assertDateInCroatian(String dateEntered, String dateFormatted) throws Exception {
		setValue("year", "2031");
		setValue("number", "66"); 
		setValue("date", dateEntered);
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		setConditionValues("2031", "66");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "year", "2031");
		assertValueInList(0, "number", "66");
		assertValueInList(0, "date", dateFormatted);
		execute("List.viewDetail", "row=0");
		assertValue("date", dateFormatted);
		execute("CRUD.delete");
	}
	
	private String getToday() {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		return df.format(new Date());
	}


	public void testImportFromExcel() throws Exception {  
		execute("Mode.list");
		assertListRowCount(0);
		assertImportFromExcel("invoices5.xlsx", "2017", "1", "9/25/2017", "", "1,258.26", "");   
		assertImportFromExcel("invoices5.xls", "2017", "1", "9/25/2017", "", "1,258.26", "DEFAULTER;FOLLOW UP NEEDED");
		assertImportFromExcel("invoices5-number-comment.xlsx", "2017", "1", "9/25/2017", "", "1,258.26", "30005673496"); 
		setLocale("es");
		execute("Mode.list"); 
		assertImportFromExcel("invoices5.xlsx", "2017", "1", "25/09/2017", "", "1.258,26", ""); 
	}
	
	public void testMapFacadeIntegerToBigDecimalConversion() throws Exception {
		setValue("number", "66");
		setValue("amount", "200");
		execute("Invoice5.createWithFiveAmount");
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(0, "number", "66");
		assertValueInList(0, "amount", "5.00");
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
		assertListRowCount(0);
	}
	
	private void assertImportFromExcel(String file, String value0, String value1, String value2, String value3, String value4, String value5) throws Exception { 
		execute("ImportData.importData");
		uploadFile("file", "test-files/" + file); 
		execute("ConfigureImport.configureImport");
		execute("Import.import");
		assertNoErrors(); 
		
		assertListRowCount(1); // We want to test import just one record
		assertValueInList(0, 0, value0);
		assertValueInList(0, 1, value1);
		assertValueInList(0, 2, value2);
		assertValueInList(0, 3, value3);
		assertValueInList(0, 4, value4);
		assertValueInList(0, 5, value5); 
		
		execute("CRUD.deleteRow", "row=0");
		assertListRowCount(0);
	}

		
}
