package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProjectReadOnlyCollectionsTest extends ModuleTestBase {
	
	public ProjectReadOnlyCollectionsTest(String testName) {
		super(testName, "ProjectReadOnlyCollections");		
	}
		
	public void testReadOnlyListNotSortable() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("members", 3);
		assertCollectionRowCount("tasks", 3);
		assertCollectionRowCount("notes", 3);
		assertFalse(getHtml().contains("class=\"xava_handle ")); 
	}
			
}
