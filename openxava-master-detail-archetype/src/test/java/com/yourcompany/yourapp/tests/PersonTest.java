package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class PersonTest extends ModuleTestBase {
	
	public PersonTest(String testName) {
		super(testName, "Person");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		// Create
		execute("CRUD.new");
		setValue("number", "99999");
		setValue("name", "Test Person");
		setValue("address", "123 Test Street");
		setValue("city", "Test City");
		setValue("country", "Test Country");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Person created successfully");
		
		// Read - search for the created person
		execute("CRUD.new");
		setValue("number", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "99999");
		assertValue("name", "Test Person");
		assertValue("address", "123 Test Street");
		assertValue("city", "Test City");
		assertValue("country", "Test Country");
		
		// Update
		setValue("name", "Modified Test Person");
		setValue("address", "456 Modified Street");
		setValue("city", "Modified City");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Person modified successfully");
		
		// Verify the modification using list
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=0");
		assertValue("number", "99999");
		assertValue("name", "Modified Test Person");
		assertValue("address", "456 Modified Street");
		assertValue("city", "Modified City");
		assertValue("country", "Test Country");
		
		// Delete
		execute("CRUD.delete");
		assertMessage("Person deleted successfully");
	}	
	
}
