package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.tests.ModuleTestBase;

/**
 * Create on 04/09/2009 (10:15:51)
 * @autor Ana Andres
 */
public class SelectSchemaTest extends ModuleTestBase {
	
	public SelectSchemaTest(String testName) {
		super(testName, "SelectSchema");		
	}
	
	public void testInitModules() throws Exception {
		// select first schema: COMPANYA
		// setValue("schema", "1"); // For XML components  
		setValue("schema", String.valueOf(SelectSchema.Schema.COMPANYA.ordinal())); // For annotated POJOs
		execute("SelectSchema.set"); 
		assertNoErrors(); 
		
		// Color: access to detail mode
		changeModule("Color");
		execute("List.viewDetail", "row=0");
		assertAction("Mode.list");
		// IssueList: COMPANYA schema
		changeModule("IssueList");
		String [][] issueCOMPANYA = { 
			{ "", "" }, 
			{ "A0001", "COMPANY A ISSUE 1" }, 
			{ "A0002", "COMPANY A ISSUE 2"} 
		};		
		assertValidValues("issue.id", issueCOMPANYA);
		
		// select second schema: COMPANYB
		changeModule("SelectSchema");
		// setValue("schema", "2"); // For XML components  
		setValue("schema", String.valueOf(SelectSchema.Schema.COMPANYB.ordinal())); // For annotated POJOs
		execute("SelectSchema.set");
		assertNoErrors();
		
		// Color: restart list mode
		changeModule("Color");
		assertNoAction("Mode.list");
		// IssueList: COMPANYB schema
		changeModule("IssueList"); 
		String [][] issueCOMPANYB = { 
			{ "", "" }, 
			{ "B0001", "COMPANY B ISSUE 1" }, 
			{ "B0002", "COMPANY B ISSUE 2"}, 
			{ "B0003", "COMPANY B ISSUE 3"} 
		};		
		assertValidValues("issue.id", issueCOMPANYB);

	}	
}
