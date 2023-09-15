package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class DebtTest extends ModuleTestBase {
	
	public DebtTest(String testName) {
		super(testName, "Debt");
	}
	
	public void testCheckBoxEditorWithNotBooleanValue() throws Exception {
		execute("CRUD.new");
		setValue("description", "IMPORTANT DEBT");
		setValue("paid", "true");
		setValue("important", "true"); // "true" instead of "IMPORTANT" because in tests the checkboxs only allow true/false to check and uncheck 	
		execute("CRUD.save");				
		setValue("description", "NOT SO IMPORTANT DEBT");
		setValue("paid", "false");
		setValue("important", "false"); 		
		execute("CRUD.save");
		
		execute("Mode.list");
		execute("List.orderBy", "property=description"); 
		assertListRowCount(3);
		assertValueInList(0, 0, "DEBT");
		assertValueInList(0, 1, "Paid"); 
		assertValueInList(0, 3, "NULL: TRUE");
		assertValueInList(1, 0, "IMPORTANT DEBT");
		assertValueInList(1, 1, "Paid"); 
		assertValueInList(1, 2, "IMPORTANT");
		assertValueInList(1, 3, "IMPORTANT: TRUE");
		assertValueInList(2, 0, "NOT SO IMPORTANT DEBT");
		assertValueInList(2, 1, ""); 
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "NULL: FALSE");		
		
		execute("List.viewDetail", "row=1");
		assertValue("description", "IMPORTANT DEBT");
		assertValue("paid", "true");
		assertValue("important", "true"); 
		assertValue("status", "IMPORTANT: TRUE");
		execute("Navigation.next");
		assertValue("description", "NOT SO IMPORTANT DEBT");
		assertValue("paid", "false");
		assertValue("important", "false"); 
		assertValue("status", "NULL: FALSE");
		
		execute("Navigation.previous");
		assertValue("description", "IMPORTANT DEBT");
		setValue("paid", "false");
		setValue("important", "false");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(1, 0, "IMPORTANT DEBT");
		assertValueInList(1, 1, ""); 
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "NULL: FALSE");
		
		checkRow(1);
		checkRow(2);
		execute("CRUD.deleteSelected");
		
		//checkAll();
		//execute("CRUD.deleteSelected");
		assertListRowCount(1);
	}
			
}
