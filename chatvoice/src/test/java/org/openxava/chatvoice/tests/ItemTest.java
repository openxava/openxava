package org.openxava.chatvoice.tests;

import org.openxava.tests.*;

public class ItemTest extends ModuleTestBase {
	
	public ItemTest(String testName) {
		super(testName, "Item");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		login("admin", "admin");
		// Create
		execute("CRUD.new");
		setValue("number", "99999");
		setValue("description", "Test Item");
		setValue("unitPrice", "125.50");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Item created successfully");
		
		// Read - search for the created item
		execute("CRUD.new");
		setValue("number", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "99999");
		assertValue("description", "Test Item");
		assertValue("unitPrice", "125.50");
		
		// Update
		setValue("description", "Modified Test Item");
		setValue("unitPrice", "200.00");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Item modified successfully");
		
		// Verify the modification using list
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=0");
		assertValue("number", "99999");
		assertValue("description", "Modified Test Item");
		assertValue("unitPrice", "200.00");
		
		// Delete
		execute("CRUD.delete");
		assertMessage("Item deleted successfully");
	}	
	
}
