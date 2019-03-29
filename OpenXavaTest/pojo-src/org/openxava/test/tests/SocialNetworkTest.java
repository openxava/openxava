package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class SocialNetworkTest extends ModuleTestBase {
	
	public SocialNetworkTest(String testName) {
		super(testName, "SocialNetwork");		
	}
	
	public void testManyToManyNewFromACollection_restoringControllersWhenSetControllersInDialog() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("name", "LA RED");
		execute("Collection.edit", "row=0,viewObject=xava_view_communities"); 
		assertValue("name", "PROGRAMMERS");
		execute("ManyToMany.new", "viewObject=xava_view_members");
		assertAction("ManyToManyNewElement.save"); // Testing a bug here
		
		execute("Dialog.cancel");
		assertAction("ManyToMany.new");
		assertNoAction("ManyToManyNewElement.save");
		assertAction("Collection.hideDetail"); // Testing a bug here
		execute("Collection.hideDetail");
		assertNoAction("Collection.hideDetail");
		assertAction("CRUD.new");
		assertAction("CRUD.save");
	}
	
}
