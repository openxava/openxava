package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class WallTest extends ModuleTestBase {
	
	public WallTest(String testName) {
		super(testName, "Wall");		
	}
	
	public void testCollectionsOfGenericType() throws Exception {
		execute("List.viewDetail", "row=0");		
		assertCollectionRowCount("entries", 2);
	}
	
}
