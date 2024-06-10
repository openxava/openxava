package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class IssueStatusTest extends ModuleTestBase {

	public IssueStatusTest(String nameTest) {
		super(nameTest, "xavaprojects", "IssueStatus");
	}
	
	public void testUseAsDefaultValue() throws Exception {
		login("admin", "admin");
		assertListRowCount(4);
		assertValueInList(0, 0, "Pending");
		assertValueInList(0, 2, "Use as default value");
		assertValueInList(1, 0, "Done");
		assertValueInList(1, 2, "");
		assertValueInList(2, 0, "Not reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(3, 0, "Rejected");
		assertValueInList(3, 2, "");
		
		execute("List.viewDetail", "row=3");
		assertValue("name", "Rejected");
		setValue("useAsDefaultValue", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pending");
		assertValueInList(0, 2, "");
		assertValueInList(1, 0, "Done");
		assertValueInList(1, 2, "");
		assertValueInList(2, 0, "Not reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(3, 0, "Rejected");
		assertValueInList(3, 2, "Use as default value");
		
		execute("List.viewDetail", "row=0");
		assertValue("name", "Pending");
		setValue("useAsDefaultValue", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pending");
		assertValueInList(0, 2, "Use as default value");
		assertValueInList(1, 0, "Done");
		assertValueInList(1, 2, "");
		assertValueInList(2, 0, "Not reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(3, 0, "Rejected");
		assertValueInList(3, 2, "");
		
	}
	
}
