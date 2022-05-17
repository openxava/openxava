package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class SupervillainTest extends ModuleTestBase {
	
	public SupervillainTest(String testName) {
		super(testName, "Supervillain");		
	}
	
	public void testReferenceBetweenEntitiesMappedToTheSameTable() throws Exception {
		execute("List.orderBy", "property=name");
		assertValueInList(0, 0, "ESCARIANO AVIESO"); 
		assertValueInList(0, 1, "SUPERLOPEZ");
	}
		
	
}
