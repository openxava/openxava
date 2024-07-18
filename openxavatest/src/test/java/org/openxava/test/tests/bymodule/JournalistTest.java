package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class JournalistTest extends ModuleTestBase {
	
	public JournalistTest(String testName) {
		super(testName, "Journalist");		
	}
			
	public void testManyToManyWithCascadeRemove() throws Exception {
		assertNoErrors();
		execute("List.viewDetail", "row=0");
		assertValue("name", "MANOLO");
		assertCollectionRowCount("articles", 0);		
		execute("ManyToMany.new", "viewObject=xava_view_articles");
		setValue("title", "JUNIT JOURNALIST ARTICLE"); 
		execute("ManyToManyNewElement.save");
		assertCollectionRowCount("articles", 1);		
		assertValueInCollection("articles", 0, 0, "JUNIT JOURNALIST ARTICLE");
		execute("Collection.deleteSelected", "row=0,viewObject=xava_view_articles");
		assertCollectionRowCount("articles", 0);
	}
				
}
