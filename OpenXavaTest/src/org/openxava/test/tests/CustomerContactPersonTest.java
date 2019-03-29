package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class CustomerContactPersonTest extends ModuleTestBase {
	
	public CustomerContactPersonTest(String testName) {
		super(testName, "CustomerContactPerson");		
	}
	
	public void testSearchingObjectWithASingleReferenceAsKeyAndWhichRefencesHasOnlyAProperty() throws Exception {
		execute("CRUD.new");
		setValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("name", "Pepe");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
	}
			
}
