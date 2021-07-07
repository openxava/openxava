package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class SoftwareProjectVersionTest extends ModuleTestBase {
	
	public SoftwareProjectVersionTest(String testName) {
		super(testName, "SoftwareProjectVersion");		
	}
			
	public void testElementCollectionInheritance() throws Exception {
		assertExists("name"); // In list mode. If in the future we need to add records for other tests we can add a CRUD.new here
		assertCollectionRowCount("features", 0); // The collection is displayed
		assertNoAction("Collection.add");
		
		setValueInCollection("features", 0, "estimatedDays", "100");
		assertTotalInCollection("features", 0, "estimatedDays", "100");
		assertTotalInCollection("features", 1, "estimatedDays", "100");
		setValueInCollection("features", 1, "estimatedDays", "200");
		assertTotalInCollection("features", 0, "estimatedDays", "300");
		assertTotalInCollection("features", 1, "estimatedDays", "300");
	}
				
}
