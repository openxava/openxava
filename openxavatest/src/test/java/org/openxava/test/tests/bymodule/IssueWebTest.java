package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Federico Alcantara
 */

public class IssueWebTest extends ModuleTestBase { 
	
	private String newParameters="";
	
	public IssueWebTest(String testName) {
		super(testName, "IssueWeb");		
	}
		
	public void testUrlParametersChangeOfLocale() throws Exception {
		// let's get locale en - english for companyA
		newParameters="&locale=en";
		resetModule();
		execute("Mode.list"); 
		assertLabelInList(1, "Description");

		// let's get locale es - espa�ol for companyA
		newParameters="&locale=es"; 
		resetModule();
		execute("Mode.list"); 
		assertLabelInList(1, "Descripci\u00f3n"); 
		
		// let's test language / country
		newParameters="&locale=es_DO"; 
		resetModule();
		execute("Mode.list"); 
		assertLabelInList(1, "Descripci\u00f3n"); // Should state the same
	}
	
	@Override
	protected String getModuleURL() {
		String urlModule = super.getModuleURL();
		urlModule = urlModule + newParameters;	
		return urlModule;
	}
}
