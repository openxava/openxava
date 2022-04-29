package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class NetMachineWithoutNewTest extends ModuleTestBase {

	public NetMachineWithoutNewTest(String testName) {
		super(testName, "NetMachineWithoutNew");
	}
	
	public void testNoRecordsWithoutNewStartsInList() throws Exception {
		assertListRowCount(0);
		assertNotExists("name");
		assertNotExists("mac");		
	}
		
}
