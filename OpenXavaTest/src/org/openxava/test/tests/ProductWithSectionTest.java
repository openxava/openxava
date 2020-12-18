package org.openxava.test.tests;

import org.openxava.test.model.*;


/**
 * 
 * @author Javier Paniza
 */

public class ProductWithSectionTest extends ProductTest { 
		
	public ProductWithSectionTest(String testName) {
		super(testName, "ProductWithSection");		
	}
	
	public void testOnEachRequestActions() throws Exception {	
		assertMessagesCount(2);
		assertMessage("Action on-each-request on list");
		assertNoMessage("Action on-each-request on detail");
		assertMessage("Action on-each-request");
		execute("List.orderBy", "property=number");
		assertMessage("Action on-each-request on list");
		assertNoMessage("Action on-each-request on detail");
		assertMessage("Action on-each-request");
		execute("CRUD.new");
		assertMessage("Action on-each-request on list");
		assertNoMessage("Action on-each-request on detail");
		assertMessage("Action on-each-request");
		execute("CRUD.new");
		assertNoMessage("Action on-each-request on list");
		assertMessage("Action on-each-request on detail");
		assertMessage("Action on-each-request");		
	}
		
	public void testNextFocusToAndFromGroup() throws Exception {
		execute("CRUD.new");
		setValue("warehouseKey", "[.1.1.]");
		assertFocusOn("unitPrice");
		setValue("unitPrice", "33");
		assertFocusOn("remarks");
	}
	
	public void testEmptyCombosUsingComposeKeys() throws Exception {
		execute("CRUD.new");
		execute("Product.setLimitZoneTo1"); 
		
		Warehouse key1 = new Warehouse();
		key1.setZoneNumber(1);
		key1.setNumber(1);
		Warehouse key2 = new Warehouse();
		key2.setZoneNumber(1);
		key2.setNumber(2);
		Warehouse key3 = new Warehouse();
		key3.setZoneNumber(1);
		key3.setNumber(3);		
		
		String [][] zone1WarehouseValues = new String [][] {
			{ "", "" },
			{ toKeyString(key1), "CENTRAL VALENCIA" },
			{ toKeyString(key3), "VALENCIA NORTE" },
			{ toKeyString(key2), "VALENCIA SURETE" }
		};
			
		assertValidValues("warehouseKey", zone1WarehouseValues); 
		
		execute("Product.setLimitZoneTo0");		
		String [][] zoneEmptyWarehouseValues = new String [][] { 
			{ "", "" },		
		};		
		assertValidValues("warehouseKey", zoneEmptyWarehouseValues);
		
	}
	
}
