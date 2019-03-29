package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class OfficeOnlyWarehouseTest extends ModuleTestBase {
	
		
	public OfficeOnlyWarehouseTest(String testName) {
		super(testName, "OfficeOnlyWarehouse");		
	}
	
	public void testOveralappedReferenceThatExplicityHidesTheOverlapedKey() throws Exception {
		execute("CRUD.new");
		setValue("zoneNumber", "1");
		setValue("mainWarehouse.number", "1");
		assertNoErrors();
		assertValue("mainWarehouse.name", "CENTRAL VALENCIA"); 
		setValue("mainWarehouse.number", "2");
		assertNoErrors();
		assertValue("mainWarehouse.name", "VALENCIA SURETE");		
	}
		
}
