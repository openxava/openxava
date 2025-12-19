package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class IssueTypeTest extends ModuleTestBase {

	public IssueTypeTest(String nameTest) {
		super(nameTest, "yourapp", "IssueType");
	}
	
	public void testUseAsDefaultValueForMyCalendar() throws Exception {
		login("admin", "admin");
		assertListRowCount(3);
		
		// Verify that "Task" is the default value for MyCalendar
		execute("List.viewDetail", "row=2");
		assertValue("name", "Task");
		assertValue("useAsDefaultValueForMyCalendar", "true");
		
		// Change the default value for MyCalendar to "Bug"
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("name", "Bug");
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verify that "Bug" is now the default value for MyCalendar
		// and "Task" is no longer
		execute("List.viewDetail", "row=0");
		assertValue("name", "Bug");
		assertValue("useAsDefaultValueForMyCalendar", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=2");
		assertValue("name", "Task");
		assertValue("useAsDefaultValueForMyCalendar", "false");
		
		// Restore "Task" as the default value for MyCalendar
		setValue("useAsDefaultValueForMyCalendar", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verify that "Task" is again the default value for MyCalendar
		// and "Bug" is no longer
		execute("List.viewDetail", "row=2");
		assertValue("name", "Task");
		assertValue("useAsDefaultValueForMyCalendar", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("name", "Bug");
		assertValue("useAsDefaultValueForMyCalendar", "false");
		
		// Restore the original state
		execute("CRUD.save");
	}
}
