package org.openxava.test.tests;

import java.util.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class AppointmentTest extends ModuleTestBase {
	
	public AppointmentTest(String testName) {
		super(testName, "Appointment");		
	}
	
	public void testDateAsDATETIME() throws Exception {   		
		assertListRowCount(4); 
		setConditionValues("5/26/15");
		execute("List.filter");
		assertListRowCount(3); 
		setConditionValues("5/26/15 10:15 AM");
		execute("List.filter");
		assertListRowCount(1); 
		assertValueInList(0, 0, "5/26/15 10:15 AM");
		assertValueInList(0, 1, "ALMUERZO");		
		execute("Print.generateExcel"); 
		assertContentTypeForPopup("text/x-csv");
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		excel.nextToken(); // To skip the header
		String line1 = excel.nextToken();
		assertEquals("line1", "\"5/26/15 10:15 AM\";\"ALMUERZO\";0", line1); 
	}
	
	public void testImport() throws Exception {
		execute("List.orderBy", "property=time");
		assertListRowCount(4); 
		assertValueInList(0, 0, "5/26/15 8:15 AM"); 
		assertValueInList(0, 1, "DESAYUNO");
		assertValueInList(1, 0, "5/26/15 10:15 AM");
		assertValueInList(1, 1, "ALMUERZO");
		assertValueInList(2, 0, "5/26/15 12:34 PM");
		assertValueInList(2, 1, "PAUSA CAFE");
		assertValueInList(3, 0, "6/1/15 7:25 PM");
		assertValueInList(3, 1, "IR A LA PISCINA");
		
		execute("ImportData.importData");
		execute("ConfigureImport.configureImport");
		assertErrorsCount(1);
		assertError("You must choose a file");
		
		execute("ImportData.importData");
		String fileURL = System.getProperty("user.dir") + "/test-images/cake.gif";
		setFileValue("newFile", fileURL);
		execute("ConfigureImport.configureImport");
		assertErrorsCount(1);
		assertError("File type not supported. Supported types: CSV, XLSX, XLS"); 
		
		execute("ImportData.importData");
		fileURL = System.getProperty("user.dir") + "/test-files/empty-file.csv";
		setFileValue("newFile", fileURL);
		execute("ConfigureImport.configureImport");
		assertErrorsCount(1);
		assertError("Empty file");
		
		assertImport("appointments.csv"); 
		assertImport("appointments.xlsx"); 
	}
	
	private void assertImport(String file) throws Exception {
		execute("ImportData.importData");
		String fileURL = System.getProperty("user.dir") + "/test-files/" + file;
		setFileValue("newFile", fileURL);
		execute("ConfigureImport.configureImport");
		assertNoErrors();
		
		assertCollectionRowCount("columns", 5);
		
		assertValueInCollection("columns", 0, 0, "Time");
		assertValueInCollection("columns", 0, 1, "Time"); 
		assertValueInCollection("columns", 0, 2, "9/11/17 11:30 AM"); 
		assertValueInCollection("columns", 0, 3, "9/12/17 4:00 PM");
		
		assertValueInCollection("columns", 1, 0, "");
		assertValueInCollection("columns", 1, 1, "Some comments"); 
		assertValueInCollection("columns", 1, 2, ""); 
		assertValueInCollection("columns", 1, 3, "NAP");
		
		assertValueInCollection("columns", 2, 0, "Amount of people");
		assertValueInCollection("columns", 2, 1, "Amount of people"); 
		assertValueInCollection("columns", 2, 2, "4"); 
		assertValueInCollection("columns", 2, 3, "");
		
		assertValueInCollection("columns", 3, 0, "");
		assertValueInCollection("columns", 3, 1, "Start time"); 
		assertValueInCollection("columns", 3, 2, "9/11/17 11:45 AM"); 
		assertValueInCollection("columns", 3, 3, "9/12/17 4:15 PM");
		
		assertValueInCollection("columns", 4, 0, "Description");
		assertValueInCollection("columns", 4, 1, "Descrip"); 
		assertValueInCollection("columns", 4, 2, "Meeting with my friend");  
		assertValueInCollection("columns", 4, 3, "");
		
		String [][] availableProperties = {
			{ "", "" },
			{ "id", "Id" },
			{ "time", "Time" },
			{ "description", "Description" },
			{ "amountOfPeople", "Amount of people" }
		};
		assertValidValuesInCollection("columns", 2, "nameInApp", availableProperties);
		
		setValueInCollection("columns", 0, "nameInApp", "");
		execute("Import.import");
		assertDialog();
		assertErrorsCount(1); 
		assertError("Some required columns are not mapped: Time");		
		
		setValueInCollection("columns", 3, "nameInApp", "time");
		
		execute("Import.import");
		assertErrorsCount(1); 
		assertError("Error importing {time=2017-09-12 16:15:00.0, amountOfPeople=null}: Value for description in appointment is required"); 
		assertMessage("2 records imported");
		
		assertListRowCount(6);
		assertValueInList(0, 0, "5/26/15 8:15 AM");
		assertValueInList(0, 1, "DESAYUNO");
		assertValueInList(1, 0, "5/26/15 10:15 AM");
		assertValueInList(1, 1, "ALMUERZO");
		assertValueInList(2, 0, "5/26/15 12:34 PM");
		assertValueInList(2, 1, "PAUSA CAFE");
		assertValueInList(3, 0, "6/1/15 7:25 PM");
		assertValueInList(3, 1, "IR A LA PISCINA");
		assertValueInList(4, 0, "9/11/17 11:45 AM");
		assertValueInList(4, 1, "MEETING WITH MY FRIEND");
		assertValueInList(4, 2, "4");
		assertValueInList(5, 0, "9/13/17 8:00 PM");
		assertValueInList(5, 1, "DRIVING MY BMW");
		assertValueInList(5, 2, "0");
		
		// Restoring
		checkRow(4);
		checkRow(5);
		execute("CRUD.deleteSelected");
		assertListRowCount(4);				
	}

}
