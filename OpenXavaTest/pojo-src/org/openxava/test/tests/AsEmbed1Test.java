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
		assertValueInCollection("xavaPropertiesList",  0, 0, "Value 3 of As embed 3 of As embed 2");
		assertNoAction("AddColumns.showMoreColumns");
	}
	
}
