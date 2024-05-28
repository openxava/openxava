package org.openxava.test.tests.bymodule;

/**
 * 
 * @author Chungyen Tsai
 */
public class ArticleTest extends CustomizeListTestBase {
	
	public ArticleTest(String testName) {
		super(testName, "Article");		
	}
	
	public void testTrigger() throws Exception {
		execute("CRUD.new");
		setValue("number", "77");
		setValue("description", "abc");
		execute("CRUD.save");
		assertError("Description length must be at least 5");
	}
	
}
