package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerRadioButtonTest extends ModuleTestBase {
	
	public CustomerRadioButtonTest(String testName) {
		super(testName, "CustomerRadioButton");		
	}
	
	public void testEditorByView_radioButton() throws Exception { 
		// Really editor by property-view must be tested visually
		// and about radioButton we only test that it's possible to use in junit test,
		// because behaves equals that a combo. Hence it's needed to test visually
		execute("CRUD.new");
		setValue("number", "66");		
		setValue("type", usesAnnotatedPOJO()?"1":"2"); // steady		
		setValue("name", "CUSTOMER JUNIT");
		setValue("address.street", "DOCTOR PESSET");
		setValue("address.zipCode", "46540");
		setValue("address.city", "EL PUIG");
		setValue("address.state.id", "CA");
		execute("CRUD.save");
		
		assertNoErrors();  
		assertValue("number", "");
		assertValue("name", "");
		assertValue("type", usesAnnotatedPOJO()?"2":"3"); // special, because a default calculator
		
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("name", "Customer Junit");
		assertValue("type", usesAnnotatedPOJO()?"1":"2"); // steady
		
		execute("CRUD.delete");
		assertMessage("Customer deleted successfully"); 
	}
		
}
