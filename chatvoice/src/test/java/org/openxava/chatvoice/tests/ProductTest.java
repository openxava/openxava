package org.openxava.chatvoice.tests;

import org.openxava.tests.*;

public class ProductTest extends ModuleTestBase {
	
	public ProductTest(String testName) {
		super(testName, "Product");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		// Create
		execute("CRUD.new");
		setValue("number", "99999");
		setValue("description", "Test Product");
		setValue("unitPrice", "125.50");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Product created successfully");
		
		// Read - search for the created product
		execute("CRUD.new");
		setValue("number", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "99999");
		assertValue("description", "Test Product");
		assertValue("unitPrice", "125.50");
		
		// Update
		setValue("description", "Modified Test Product");
		setValue("unitPrice", "200.00");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Product modified successfully");
		
		// Verify the modification using list
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=0");
		assertValue("number", "99999");
		assertValue("description", "Modified Test Product");
		assertValue("unitPrice", "200.00");
		
		// Delete
		execute("CRUD.delete");
		assertMessage("Product deleted successfully");
	}	
	
}
