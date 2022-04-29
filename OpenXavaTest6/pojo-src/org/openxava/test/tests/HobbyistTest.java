package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 


/**
 * 
 * @author Javier Paniza
 */
public class HobbyistTest extends ModuleTestBase {
	
	public HobbyistTest(String testName) {
		super(testName, "Hobbyist");		
	}
	
	public void testAddingToNotOwningManyToManyCollection() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("hobbies", 0);
		execute("Collection.add", "viewObject=xava_view_hobbies");
		String name = getValueInList(0, 0);
		execute("AddToCollection.add", "row=0");
		assertCollectionRowCount("hobbies", 1);
		assertValueInCollection("hobbies", 0, 0, name);
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_hobbies");
		assertCollectionRowCount("hobbies", 0);
	}
	

}
