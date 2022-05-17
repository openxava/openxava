package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class LoginTest extends ModuleTestBase {
	
	public LoginTest(String testName) {
		super(testName, "Login");		
	}
	
	public void testPasswordStereotype_moduleForTransientClassIsDetailOnlyByDefault() throws Exception {
		assertFocusOn("user"); 
		
		assertNoAction("Mode.list");
	
		setValue("user", "JAVI");
		setValue("password", "x942JlmkK");
		execute("Login.login");
		assertErrorsCount(1); 
		
		setValue("user", "JAVI");
		setValue("password", "x8Hjk37mm");
		execute("Login.login");
		assertNoErrors(); 
		assertMessage("OK");
	}
	
}
