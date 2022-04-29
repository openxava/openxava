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
		setValue("description", "AN IMPORTANT DEBT");
		setValue("paid", "true");
		setValue("important", "true"); // "true" instead of "IMPORTANT" because in tests the checkboxs only allow true/false to check and uncheck		
		execute("CRUD.save");				
		setValue("description", "A NOT SO IMPORTANT DEBT");
		setValue("paid", "false");
		setValue("important", "false"); 		
		execute("CRUD.save");
		
		execute("Mode.list");
		assertListRowCount(2);
		assertValueInList(0, 0, "AN IMPORTANT DEBT");
		assertValueInList(0, 1, "Paid"); 
		assertValueInList(0, 2, "IMPORTANT");
		assertValueInList(0, 3, "IMPORTANT: TRUE");
		assertValueInList(1, 0, "A NOT SO IMPORTANT DEBT");
		assertValueInList(1, 1, ""); 
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "NULL: FALSE");
		
		execute("List.viewDetail", "row=0");
		assertValue("description", "AN IMPORTANT DEBT");
		assertValue("paid", "true");
		assertValue("important", "true"); 
		assertValue("status", "IMPORTANT: TRUE");
		execute("Navigation.next");
		assertValue("description", "A NOT SO IMPORTANT DEBT");
		assertValue("paid", "false");
		assertValue("important", "false"); 
		assertValue("status", "NULL: FALSE");
		
		execute("Navigation.previous");
		assertValue("description", "AN IMPORTANT DEBT");
		setValue("paid", "false");
		setValue("important", "false");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(0, 0, "AN IMPORTANT DEBT");
		assertValueInList(0, 1, ""); 
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "NULL: FALSE");
		
		checkAll();
		execute("CRUD.deleteSelected");
		assertListRowCount(0);
	}
			
}
