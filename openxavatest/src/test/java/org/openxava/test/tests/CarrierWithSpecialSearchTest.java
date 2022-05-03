package org.openxava.test.tests;

/**
 * @author Javier Paniza
 */

public class CarrierWithSpecialSearchTest extends CarrierTestBase {
	
	public CarrierWithSpecialSearchTest(String testName) {
		super(testName, "CarrierWithSpecialSearch");		
	}
		
	public void testSearchCarrierWithDefaultValue() throws Exception { 
		execute("CRUD.new");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("name", "TRES");
		assertValue("number", "3");
		assertNoErrors();
	}

}
