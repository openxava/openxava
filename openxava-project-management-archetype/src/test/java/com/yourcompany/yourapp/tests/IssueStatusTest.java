package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class IssueStatusTest extends ModuleTestBase {

	public IssueStatusTest(String nameTest) {
		super(nameTest, "yourapp", "IssueStatus");
	}
	
	public void testUseAsDefaultValue() throws Exception {
		login("admin", "admin");
		assertListRowCount(5);
		assertValueInList(0, 0, "Pending");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "Use as default value");
		assertValueInList(1, 0, "Done");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "Not reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Rejected");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Planned");
		assertValueInList(4, 2, "Use as default value for My calendar");
		assertValueInList(4, 3, "");
		
		execute("List.viewDetail", "row=3");
		assertValue("name", "Rejected");
		setValue("useAsDefaultValue", "true");
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pending");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "");		
		assertValueInList(1, 0, "Done");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "Not reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Rejected");
		assertValueInList(3, 2, "Use as default value for My calendar");
		assertValueInList(3, 3, "Use as default value");
		assertValueInList(4, 0, "Planned");
		assertValueInList(4, 2, "");
		assertValueInList(4, 3, "");
		
		execute("List.viewDetail", "row=4");
		assertValue("name", "Planned");
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		execute("List.viewDetail", "row=0");
		assertValue("name", "Pending");
		setValue("useAsDefaultValue", "true");
		execute("CRUD.save");
		execute("Mode.list");		
		
		assertValueInList(0, 0, "Pending");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "Use as default value");
		assertValueInList(1, 0, "Done");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "Not reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Rejected");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Planned");
		assertValueInList(4, 2, "Use as default value for My calendar");
		assertValueInList(4, 3, "");
	}
	
	public void testUseAsDefaultValueForMyCalendar() throws Exception {
		login("admin", "admin");
		assertListRowCount(5);
		
		// Verify that "Planned" is the default value for MyCalendar
		execute("List.viewDetail", "row=4");
		assertValue("name", "Planned");
		assertValue("useAsDefaultValueForMyCalendar", "true");
		
		// Change the default value for MyCalendar to "Pending"
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("name", "Pending");
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verify that "Pending" is now the default value for MyCalendar
		// and "Planned" is no longer
		execute("List.viewDetail", "row=0");
		assertValue("name", "Pending");
		assertValue("useAsDefaultValueForMyCalendar", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=4");
		assertValue("name", "Planned");
		assertValue("useAsDefaultValueForMyCalendar", "false");
		
		// Restore "Planned" as the default value for MyCalendar
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verify that "Planned" is again the default value for MyCalendar
		// and "Pending" is no longer
		execute("List.viewDetail", "row=4");
		assertValue("name", "Planned");
		assertValue("useAsDefaultValueForMyCalendar", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("name", "Pending");
		assertValue("useAsDefaultValueForMyCalendar", "false");
		
		// Restore the original state
		execute("CRUD.save");
	}
	
}
