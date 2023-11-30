package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class NetMachineWithNewTest extends ModuleTestBase {

	public NetMachineWithNewTest(String testName) {
		super(testName, "NetMachineWithNew");
	}
	
	public void testWithoutRecordsExecutesNonCRUDNewOnInit() throws Exception {  
		assertExists("name");
		assertExists("mac");
	}
		
}
