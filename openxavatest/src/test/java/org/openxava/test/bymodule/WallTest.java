package org.openxava.test.bymodule;

import javax.persistence.*;

import org.openxava.jpa.*;
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
		execute("Collection.new", "viewObject=xava_view_entries");
		setValue("message", "JUNIT WALL MESSAGE");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("entries", 3);
		assertValueInCollection("entries", 2, 0, "JUNIT WALL MESSAGE");
		Query query = XPersistence.getManager().createQuery("delete from WallEntry we where we.message = 'JUNIT WALL MESSAGE'");
		assertEquals(1, query.executeUpdate());
	}
	
}
