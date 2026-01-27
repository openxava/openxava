package org.openxava.chattest.tests;

import org.openxava.tests.*;

public class CustomerTest extends ModuleTestBase {
	
	public CustomerTest(String testName) {
		super(testName, "Customer");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		login("admin", "admin");
		// Create
		execute("CRUD.new");
		setValue("number", "99999");
		setValue("name", "Test Customer");
		setValue("address", "123 Test Street");
		setValue("city", "Test City");
		setValue("country", "Test Country");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Customer created successfully");
		
		// Read - search for the created customer
		execute("CRUD.new");
		setValue("number", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "99999");
		assertValue("name", "Test Customer");
		assertValue("address", "123 Test Street");
		assertValue("city", "Test City");
		assertValue("country", "Test Country");
		
		// Update
		setValue("name", "Modified Test Customer");
		setValue("address", "456 Modified Street");
		setValue("city", "Modified City");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Customer modified successfully");
		
		// Verify the modification using list
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=0");
		assertValue("number", "99999");
		assertValue("name", "Modified Test Customer");
		assertValue("address", "456 Modified Street");
		assertValue("city", "Modified City");
		assertValue("country", "Test Country");
		
		// Delete
		execute("CRUD.delete");
		assertMessage("Customer deleted successfully");
	}	
	
}
