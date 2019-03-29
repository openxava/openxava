package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CommentTest extends ModuleTestBase {
	
	public CommentTest(String testName) {
		super(testName, "Comment");		
	}
	
	public void testRequiredReference() throws Exception { 
		execute("CRUD.new");			
		execute("CRUD.save");
		assertError("Value for Issue in Comment is required");
	}
			
}
