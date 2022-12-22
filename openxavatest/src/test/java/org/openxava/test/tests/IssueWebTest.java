package org.openxava.test.tests;

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
		
	/* tmr En changelog
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
	*/
	
	public void testUrlParametersChangeOfLocale() throws Exception {
		// let's get locale en - english for companyA
		// tmr newParameters="&schema=companya&locale=en";
		newParameters="&locale=en";
		resetModule();
		execute("Mode.list"); // tmr
		assertLabelInList(1, "Description");

		// let's get locale es - espa�ol for companyA
		// tmr newParameters="&schema=companya&locale=es";
		newParameters="&locale=es"; // tmr
		resetModule();
		execute("Mode.list"); // tmr
		assertLabelInList(1, "Descripci\u00f3n"); 
		
		// let's test language / country
		// tmr newParameters="&schema=companya&locale=es_DO";
		newParameters="&locale=es_DO"; // tmr
		resetModule();
		execute("Mode.list"); // tmr
		assertLabelInList(1, "Descripci\u00f3n"); // Should state the same
	}

	/* tmr En changelog
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
	*/
	
	@Override
	protected String getModuleURL() {
		String urlModule = super.getModuleURL();
		urlModule = urlModule + newParameters;	
		System.out.println("[IssueWebTest.getModuleURL] urlModule=" + urlModule); // tmr
		return urlModule;
	}
}
