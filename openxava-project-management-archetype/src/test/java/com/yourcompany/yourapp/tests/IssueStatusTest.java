package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class IssueStatusTest extends ModuleTestBase {

	public IssueStatusTest(String nameTest) {
		super(nameTest, "yourapp", "IssueStatus");
	}
	
	public void testUseAsDefaultValue() throws Exception {
		login("admin", "admin");
		assertListRowCount(5);
		assertValueInList(0, 0, "Planned");
		assertValueInList(0, 2, "Use as default value for My calendar");
		assertValueInList(0, 3, "");
		assertValueInList(1, 0, "Pending");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "Use as default value");
		assertValueInList(2, 0, "Done");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Not reproducible");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Rejected");
		assertValueInList(4, 2, "");
		assertValueInList(4, 3, "");
		
		execute("List.viewDetail", "row=4");
		assertValue("name", "Rejected");
		setValue("useAsDefaultValue", "true");
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Planned");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "");		
		assertValueInList(1, 0, "Pending");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "Done");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Not reproducible");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Rejected");
		assertValueInList(4, 2, "Use as default value for My calendar");
		assertValueInList(4, 3, "Use as default value");
		
		execute("List.viewDetail", "row=0");
		assertValue("name", "Planned");
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		execute("List.viewDetail", "row=1");
		assertValue("name", "Pending");
		setValue("useAsDefaultValue", "true");
		execute("CRUD.save");
		execute("Mode.list");		
		
		assertValueInList(0, 0, "Planned");
		assertValueInList(0, 2, "Use as default value for My calendar");
		assertValueInList(0, 3, "");
		assertValueInList(1, 0, "Pending");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "Use as default value");
		assertValueInList(2, 0, "Done");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Not reproducible");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Rejected");
		assertValueInList(4, 2, "");
		assertValueInList(4, 3, "");
	}
	
}
