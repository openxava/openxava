package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class StaffDashboardTest extends ModuleTestBase {
	
	public StaffDashboardTest(String testName) {
		super(testName, "StaffDashboard");		
	}
			
	public void testInitModelWithNewInstance() throws Exception {
		assertValue("staffCount", "223");
	}
					
}
