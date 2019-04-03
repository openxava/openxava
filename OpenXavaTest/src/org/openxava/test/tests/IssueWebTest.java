package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Federico Alcantara
 */

public class IssueWebTest extends ModuleTestBase {
	
	private String newParameters="";
	
	public IssueWebTest(String testName) {
		super(testName, "IssueWeb");		
	}
		
	public void testUrlParametersChangeOfDefaultSchema() throws Exception {
		// let's add schema parameter for companyA
		newParameters="&schema=companya"; 
		resetModule();
		assertListRowCount(2); 
		// let's add schema parameter for companyB
		newParameters="&schema=companyb";
		resetModule();
		assertListRowCount(3);

	}
	
	public void testUrlParametersChangeOfLocale() throws Exception {
		// let's get locale en - english for companyA
		newParameters="&schema=companya&locale=en";
		resetModule();
		assertLabelInList(1, "Description");

		// let's get locale es - espa�ol for companyA
		newParameters="&schema=companya&locale=es";
		resetModule();
		assertLabelInList(1, "Descripci\u00f3n"); 
		
		// let's test language / country
		newParameters="&schema=companya&locale=es_DO";
		resetModule();
		assertLabelInList(1, "Descripci\u00f3n"); // Should state the same
	}
	
	public void testUrlParametersChangeOfUser() throws Exception {
		// let's set user to THE_USER in companyA
		newParameters="&schema=companya&user=THE_USER&locale=en";
		resetModule();
		execute("List.viewDetail", "row=0"); 
		assertValueIgnoringCase("description", "THE_USER"); 
		newParameters="&schema=companya&user=OTHER_USER&locale=en";
		resetModule();
		execute("List.viewDetail", "row=0");
		assertValueIgnoringCase("description", "OTHER_USER");
	}
	
	@Override
	protected String getModuleURL() {
		String urlModule = super.getModuleURL();
		urlModule = urlModule + newParameters;		
		return urlModule;
	}
}
