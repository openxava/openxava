package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * Only for OX3 test.
 * 
 * @author Javier Paniza
 */

public class Warehouse2Test extends ModuleTestBase {
	
		
	public Warehouse2Test(String testName) {
		super(testName, "Warehouse2");		
	}
	
	public void testEmbeddedId() throws Exception {
		// List mode
		assertLabelInList(0, "Zone of Key");
		assertLabelInList(1, "Number of Key");
		assertLabelInList(2, "Name"); 
		assertListNotEmpty();
		String zoneNumber = getValueInList(0, 0);
		String number = getValueInList(0, 1);
		String name = getValueInList(0, 2);
		
		// View detail from list mode
		execute("List.viewDetail", "row=0");
		assertNoErrors(); 
		assertValue("key.zoneNumber", zoneNumber);
		assertValue("key.number", number);
		assertValue("name", name);
		
		// Create 
		execute("CRUD.new");
		setValue("key.zoneNumber", "6"); 
		setValue("key.number", "66");
		setValue("name", "WAREHOUSE JUNIT");
		execute("Collection.new", "viewObject=xava_view_buildings");
		setValue("name", "BUILDING JUNIT");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("buildings", 1);
		assertValueInCollection("buildings", 0, 0, "BUILDING JUNIT");
		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("key.zoneNumber", ""); 
		assertValue("key.number", "");
		assertValue("name", "");
		assertCollectionRowCount("buildings", 0); 
		
		// Read
		setValue("key.zoneNumber", "6"); 
		setValue("key.number", "66");
		execute("CRUD.refresh");
		assertValue("key.zoneNumber", "6"); 
		assertValue("key.number", "66");
		assertValue("name", "WAREHOUSE JUNIT");
		assertCollectionRowCount("buildings", 1);
		assertValueInCollection("buildings", 0, 0, "BUILDING JUNIT");
		
		
		// Modify
		setValue("name", "WAREHOUSE JUNIT MODIFIED");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("key.zoneNumber", ""); 
		assertValue("key.number", "");
		assertValue("name", "");
		assertCollectionRowCount("buildings", 0); 

		// Verify modified
		setValue("key.zoneNumber", "6"); 
		setValue("key.number", "66");
		execute("CRUD.refresh");
		assertValue("key.zoneNumber", "6"); 
		assertValue("key.number", "66");
		assertValue("name", "WAREHOUSE JUNIT MODIFIED");
		
		// Delete
		execute("CRUD.delete");
		assertNoErrors();
		execute("CRUD.new");
		setValue("key.zoneNumber", "6"); 
		setValue("key.number", "66");
		execute("CRUD.refresh");
		assertError("Object of type Warehouse 2 does not exists with key Number:66, Zone:6");
		
		// Delete from list mode
		execute("CRUD.new");
		setValue("key.zoneNumber", "999"); 
		setValue("key.number", "1");
		setValue("name", "WAREHOUSE JUNIT 999/1");
		execute("CRUD.save");		
		execute("Mode.list");
		execute("List.orderBy", "property=key.zoneNumber");
		execute("List.orderBy", "property=key.zoneNumber");
		assertValueInList(0, 0, "1000");
		assertValueInList(1, 0, "999");
		assertValueInList(2, 0, "10");
		execute("CRUD.deleteRow", "row=1");
		assertNoErrors();
		assertValueInList(0, 0, "1000");
		assertValueInList(1, 0, "10");
	}
	
}
