package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author M� Carmen Gimeno
 */

public class StateHibernateTest extends ModuleTestBase {
	
	
	public StateHibernateTest(String testName) {
		super(testName, "StateHibernate");		
	}

	public void testCreateReadUpdateDeleteWithHandmadeHibernate() throws Exception { 		
		assertListNotEmpty();
		// Create				
		execute ("StateHibernate.new");
		setValue("id","66");
		setValue("name","State JUnit");
		execute("StateHibernate.save");
		assertNoErrors(); 
		assertValue("id", "");
		assertValue("name", "");
		
		// Read
		setValue("id", "66");
		execute("StateHibernate.search");
		assertNoErrors();
		assertValue("id","66");
		assertValue("name","STATE JUNIT");
		
		// Modify
		setValue("name","State JUnit Modified");
		execute("StateHibernate.save");
		assertNoErrors();
		setValue("id", "66");
		execute("StateHibernate.search");
		assertNoErrors();
		assertValue("id","66");
		assertValue("name","STATE JUNIT MODIFIED");
		
		// Delete
		
		execute("StateHibernate.delete");
		assertNoErrors();
	}
}
