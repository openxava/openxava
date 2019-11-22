package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * Only for OX3 test.
 * 
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
		// tmp ini
		execute("Collection.new", "viewObject=xava_view_buildings");
		setValue("name", "BUILDING JUNIT");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("building", 1);
		assertValueInCollection("building", 0, 0, "BUILDING JUNIT");
		// tmp fin
		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("key.zoneNumber", ""); 
		assertValue("key.number", "");
		assertValue("name", "");
		assertCollectionRowCount("building", 0); // tmp
		
		// Read
		setValue("key.zoneNumber", "6"); 
		setValue("key.number", "66");
		execute("CRUD.refresh");
		assertValue("key.zoneNumber", "6"); 
		assertValue("key.number", "66");
		assertValue("name", "WAREHOUSE JUNIT");
		// tmp ini
		assertCollectionRowCount("building", 1);
		assertValueInCollection("building", 0, 0, "BUILDING JUNIT");
		// tmp fin
		
		
		// Modify
		setValue("name", "WAREHOUSE JUNIT MODIFIED");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("key.zoneNumber", ""); 
		assertValue("key.number", "");
		assertValue("name", "");
		assertCollectionRowCount("building", 0); // tmp

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
	}
	
}
