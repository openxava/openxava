package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Mª Carmen Gimeno
 */

public class StateJPATest extends ModuleTestBase {
	
	
	public StateJPATest(String testName) {
		super(testName, "StateJPA");		
	}

	public void testCreateReadUpdateDeleteWithHandmadeJPA() throws Exception {
		assertListNotEmpty();
		// Create		
		execute ("StateJPA.new");
		setValue("id","66");
		setValue("name","State JUnit");
		execute("StateJPA.save");
		assertNoErrors();
		assertValue("id", "");
		assertValue("name", "");
		
		// Read
		setValue("id", "66");
		execute("StateJPA.search");
		assertNoErrors();
		assertValue("id","66");
		assertValue("name","STATE JUNIT");
		
		// Modify
		setValue("name","State JUnit Modified");
		execute("StateJPA.save");
		assertNoErrors();
		setValue("id", "66");
		execute("StateJPA.search");
		assertNoErrors();
		assertValue("id","66");
		assertValue("name","STATE JUNIT MODIFIED");
		
		// Delete		
		execute("StateJPA.delete");
		assertNoErrors();
	}
}
