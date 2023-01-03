package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class AsEmbed1Test extends ModuleTestBase {
	
	public AsEmbed1Test(String testName) {
		super(testName, "AsEmbed1");		
	}

	public void testCustomizeListAddColumnsWhen3LevelPropertiesAndNot2and1LevelProperties() throws Exception { 
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 1); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "As embed 2 as embed 3 value 3"); 
		assertNoAction("AddColumns.showMoreColumns");
	}
	
}
