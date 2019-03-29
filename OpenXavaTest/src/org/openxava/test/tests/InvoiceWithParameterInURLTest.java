package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * @author Javier Paniza
 */

public class InvoiceWithParameterInURLTest extends ModuleTestBase {
	
	public InvoiceWithParameterInURLTest(String testName) {
		super(testName, "InvoiceWithParameterInURL");		
	}
	
	public void testParametersInURL() throws Exception {
		assertListRowCount(1); // Only one Invoice from 2002 
		execute("List.filter");
		assertListRowCount(1);
	}
	
	protected String getModuleURL() {
		String parameter = isPortalEnabled()?"?year=2002":"&year=2002";
		return super.getModuleURL() + parameter;
	}
	
}
