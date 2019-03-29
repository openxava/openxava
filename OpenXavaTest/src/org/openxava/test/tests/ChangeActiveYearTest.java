package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class ChangeActiveYearTest extends ModuleTestBase {
	
	public ChangeActiveYearTest(String testName) {
		super(testName, "ChangeActiveYear");		
	}
	
	public void testCalculatedCollectionInATransientClass() throws Exception {
		assertCollectionRowCount("months", 12);
		assertValueInCollection("months", 0, 0, "ENERO");
		assertValueInCollection("months", 0, 1, "31");
		assertValueInCollection("months", 1, 0, "FEBRERO");
		assertValueInCollection("months", 1, 1, "28");		
	}
	
}
