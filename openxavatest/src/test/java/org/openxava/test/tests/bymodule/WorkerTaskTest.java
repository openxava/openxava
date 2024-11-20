package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class WorkerTaskTest extends ModuleTestBase {
	
	public WorkerTaskTest(String testName) {
		super(testName, "WorkerTask");		
	}
	
	public void testBaseCondition3levelNotKeyProperties() throws Exception { 
		// We also test it in Product6SoftwareTest because it failed sometimes depending on the name of the properties
		assertListRowCount(2);
		assertListColumnCount(1); // Just one column, to test that a baseCondition bug
		assertLabelInList(0, "Description"); // Not a property from baseCondition condition		
	}			
}
