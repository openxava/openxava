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
		moduleURI = "/m/Carrier?detail=5";
		resetModule();
		assertNoErrors(); 
		assertValue("number", "5");
		assertValue("name", "Cinco");
		
		moduleURI = "/m/Carrier?action=CRUD.new";
		resetModule();
		assertNoErrors();
		assertValue("number", "");
		assertValue("name", "");
		
		moduleURI = "/m/Carrier?action=CRUD.create";
		resetModule();
		assertError("Action CRUD.create not available"); 
		execute("ListFormat.select", "editor=List");
	}
		
	protected String getModuleURL() { 
		return "http://" + getHost() + ":" + getPort() + "/OpenXavaTest" + moduleURI;
	}
	
}
