package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class QuarterTest extends ModuleTestBase {
	
	public QuarterTest(String testName) {
		super(testName, "Quarter");		
	}
	
	public void testNestedCollectionsWithCompositeKeyWithReference() throws Exception {		
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("subjects", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_subjects");
		assertCollectionRowCount("groups", 1);
		assertValueInCollection("groups", 0, 0, "CODE"); 
		assertValueInCollection("groups", 0, 1, "TYPE");
	}
	
}
