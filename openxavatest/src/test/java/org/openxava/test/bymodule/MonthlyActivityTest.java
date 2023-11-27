package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class MonthlyActivityTest extends ModuleTestBase {
	
	public MonthlyActivityTest(String testName) {
		super(testName, "MonthlyActivity");		
	}
			
	public void testValidationExceptionFromValidatorUsingJPA() throws Exception {
		setValue("name", "SOL");
		execute("MonthlyActivity.saveWithJPA");
		assertErrorsCount(1); 
		assertError("SOL is not a valid month name");
	}	
			
}
