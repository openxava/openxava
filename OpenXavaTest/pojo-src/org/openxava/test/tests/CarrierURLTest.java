package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CarrierURLTest extends ModuleTestBase { 
	
	private String moduleURI = "/m/Carrier";
	
	public CarrierURLTest(String testName) {
		super(testName, "Carrier");		
	}
	
	// We also have a Selenium test for this
	public void testPermalink() throws Exception {
		execute("ListFormat.select", "editor=Charts"); 
		// tmp moduleURI = "/m/Carrier?detail=5";
		moduleURI = "m/Carrier?detail=5"; // tmp
		resetModule();
		assertNoErrors(); 
		assertValue("number", "5");
		assertValue("name", "Cinco");
		
		// tmp moduleURI = "/m/Carrier?action=CRUD.new";
		moduleURI = "m/Carrier?action=CRUD.new"; // tmp
		resetModule();
		assertNoErrors();
		assertValue("number", "");
		assertValue("name", "");
		
		// tmp moduleURI = "/m/Carrier?action=CRUD.create";
		moduleURI = "m/Carrier?action=CRUD.create"; // tmp
		resetModule();
		assertError("Action CRUD.create not available"); 
		execute("ListFormat.select", "editor=List");
	}
		
	protected String getModuleURL() { 
		// tmp return "http://" + getHost() + ":" + getPort() + "/OpenXavaTest" + moduleURI;
		return "http://" + getHost() + ":" + getPort() + getContext() + moduleURI; // tmp
	}
	
}
