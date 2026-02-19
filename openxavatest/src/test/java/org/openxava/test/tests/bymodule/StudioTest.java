package org.openxava.test.tests.bymodule;

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
		assertCollectionRowCount("artists", 1);
		assertValueInCollection("artists", 0, 0,"AARON");
		execute("Collection.new", "viewObject=xava_view_artists");
		assertNotExists("artistStudio.name");
		setValue("name", "ALFREDO LANDA");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("artists", 2);
		assertValueInCollection("artists", 0, 0,"AARON");
		assertValueInCollection("artists", 1, 0,"ALFREDO LANDA");
		execute("Collection.edit", "row=1,viewObject=xava_view_artists");
		execute("Collection.remove");
		assertNoErrors();
		assertCollectionRowCount("artists", 1);
		assertValueInCollection("artists", 0, 0,"AARON");
	}
	
}
