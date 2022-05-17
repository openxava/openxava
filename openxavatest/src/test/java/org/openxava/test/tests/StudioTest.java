package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class StudioTest extends ModuleTestBase {
	
	public StudioTest(String testName) {
		super(testName, "Studio");		
	}
		
	public void testEmbeddedCollectionElementNotShowParentReference_removingCollectionElementWhenParentNameNotMatchEntityName() throws Exception {
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("artists", 0);
		execute("Collection.new", "viewObject=xava_view_artists");
		assertNotExists("artistStudio.name");
		setValue("name", "ALFREDO LANDA");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("artists", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_artists");
		execute("Collection.remove");
		assertNoErrors();
		assertCollectionRowCount("artists", 0);
	}
	
}
