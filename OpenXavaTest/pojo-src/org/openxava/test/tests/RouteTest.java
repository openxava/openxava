package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class RouteTest extends ModuleTestBase {
	
	public RouteTest(String testName) {
		super(testName, "Route");		
	}
	
	public void testOnChangeInElementCollection_NoSearchInElementCollection() throws Exception {
		execute("CRUD.new");
		
		assertNoAction("Reference.search", "keyProperty=visits.0.carrier.number"); 
		
		setValueInCollection("visits", 0, "km", "11");
		assertValueInCollection("visits", 0, "description", "VISIT KM 11");
		assertValue("description", "ROUTE KM 11");
		setValueInCollection("visits", 1, "km", "12");
		assertValueInCollection("visits", 0, "description", "VISIT KM 11");
		assertValueInCollection("visits", 1, "description", "VISIT KM 12");
		assertValue("description", "ROUTE KM 12");
		setValueInCollection("visits", 0, "km", "13");
		assertValueInCollection("visits", 0, "description", "VISIT KM 13");
		assertValueInCollection("visits", 1, "description", "VISIT KM 12");
		assertValue("description", "ROUTE KM 13");
		
		setValueInCollection("visits", 1, "customer.number", "1");
		assertValueInCollection("visits", 1, "customer.name", "Javi");
		assertValueInCollection("visits", 1, "description", "KM: 12, CUSTOMER: 1 JAVI");
		execute("Reference.search", "keyProperty=visits.0.customer.number");
		execute("ReferenceSearch.choose", "row=1"); 
		assertValueInCollection("visits", 0, "customer.name", "Juanillo"); 
		assertValueInCollection("visits", 0, "description", "KM: 13, CUSTOMER: 2 JUANILLO");		
		
		String [][] productValidValues = {
			{ "", "" },
			{ "4", "CUATRE" },
			{ "2", "IBM ESERVER ISERIES 270" },
			{ "1", "MULTAS DE TRAFICO" },
			{ "5", "PROVAX" },
			{ "6", "SEIS" },
			{ "7", "SIETE" },
			{ "3", "XAVA" }
		};		
		assertValidValuesInCollection("visits", 1, "product.number", productValidValues); 
		setValueInCollection("visits", 1, "product.number", "3");
		assertValueInCollection("visits", 1, "description", "PRODUCT: 3");
		
		setValueInCollection("visits", 1, "carrier.number", "11");
		assertValueInCollection("visits", 1, "carrier.number", "1");
		assertValueInCollection("visits", 1, "carrier.name", "UNO");
		setValueInCollection("visits", 0, "carrier.number", "13");
		assertValueInCollection("visits", 0, "carrier.number", "3");
		assertValueInCollection("visits", 0, "carrier.name", "TRES");	
	}
	
	public void testElementCollectionWithOnChangesThatChangesRootViewRefreshesOnResetView() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("CRUD.save");
		execute("CRUD.new"); // It failed at the second action call
		assertExists("description"); // In the bug the view goes away, so this detects the bug
	}
		
}
