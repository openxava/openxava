package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

abstract public class IssueTestBase extends ModuleTestBase {
	
	private String controllerName;
	private String descriptionSuffix;
	
	public IssueTestBase(String testName, String moduleName, String descriptionSuffix) {
		super(testName, moduleName);
		controllerName = moduleName;
		this.descriptionSuffix = descriptionSuffix;
	}
		
	public void testDynamicChangeOfCompany() throws Exception {  
		// We start on schema 'COMPANYA'
		assertListRowCount(2); 
		assertValueInList(0, "id", "A0001");
		assertValueInList(1, "id", "A0002");
		execute("List.viewDetail", "row=0");
		assertValue("id", "A0001");
		assertValue("description", "COMPANY A ISSUE 1" + descriptionSuffix);
		assertValueInCollection("comments", 0, "comment", "Comment on A0001" + descriptionSuffix);
		execute("Mode.list");
		assertListRowCount(2);
		assertValueInList(0, "id", "A0001");
		assertValueInList(1, "id", "A0002");

		// We change to 'COMPANYB' schema
		execute(controllerName + ".changeToCompanyB"); 
		assertListRowCount(3);
		assertValueInList(0, "id", "B0001");
		assertValueInList(1, "id", "B0002");
		assertValueInList(2, "id", "B0003");
		execute("List.viewDetail", "row=0");
		assertValue("id", "B0001");
		assertValue("description", "COMPANY B ISSUE 1" + descriptionSuffix);
		assertValueInCollection("comments", 0, "comment", "Comment on B0001" + descriptionSuffix);
		execute("Mode.list");
		assertListRowCount(3);
		assertValueInList(0, "id", "B0001");
		assertValueInList(1, "id", "B0002");
		assertValueInList(2, "id", "B0003");
		
		// At the end we return to 'COMPANYA' schema
		execute(controllerName + ".changeToCompanyA"); 
		assertListRowCount(2);
		assertValueInList(0, "id", "A0001");
		assertValueInList(1, "id", "A0002");
		execute("List.viewDetail", "row=0");
		assertValue("id", "A0001");
		assertValue("description", "COMPANY A ISSUE 1" + descriptionSuffix);
		assertValueInCollection("comments", 0, "comment", "Comment on A0001" + descriptionSuffix);
		execute("Mode.list");
		assertListRowCount(2);
		assertValueInList(0, "id", "A0001");
		assertValueInList(1, "id", "A0002");
	}	
			
}
