package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ElClienteTest extends ModuleTestBase {
	
	public ElClienteTest(String testName) {
		super(testName, "ElCliente");		
	}
	
	public void testModuleDescriptionI18n() {
		// This test in only effective when the locale of the server is one other than English
		assertEquals("The customer - OpenXavaTest", getHtmlPage().getTitleText());
	}
			
}
