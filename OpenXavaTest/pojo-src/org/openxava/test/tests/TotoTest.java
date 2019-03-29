package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class TotoTest extends ModuleTestBase {
	
	public TotoTest(String testName) {
		super(testName, "Toto");		
	}
	
	public void testCollectionOfGenericType() throws Exception { 
		assertNoErrors();
		execute("CRUD.new");
		assertNoErrors();
	}
	
}
