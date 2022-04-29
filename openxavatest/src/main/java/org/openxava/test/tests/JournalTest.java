package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class JournalTest extends ModuleTestBase {
	
	public JournalTest(String testName) {
		super(testName, "Journal"); 		
	}
	
	public void testEntityValidatorInACascadeAllCollectionElementWithAReferenceToParentThatNotMatchWithEntityName() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("Collection.edit", "row=0,viewObject=xava_view_entries");
		execute("Collection.save");
		assertNoErrors();
	}
		
}
