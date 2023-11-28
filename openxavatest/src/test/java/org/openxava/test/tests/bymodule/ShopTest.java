package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShopTest extends ModuleTestBase{

	public ShopTest(String testName) {
		super(testName, "Shop");
	}
	
	public void testJoinedInheritanceStrategy() throws Exception {
		assertListRowCount(3);
		
		execute("List.orderBy", "property=name");
		
		assertValueInList(0, 0, "AMAZON"); 
		assertValueInList(1, 0, "EL CORTE INGLES");
		assertValueInList(2, 0, "FNAC");
		
		execute("List.viewDetail", "row=0");
		assertValue("name", "AMAZON");
		assertValue("url", "http://www.amazon.com");
		assertNotExists("city");
		
		execute("Navigation.next");
		assertValue("name", "EL CORTE INGLES");
		assertNotExists("url");
		assertValue("city", "VALENCIA");

		execute("Navigation.next");
		assertValue("name", "FNAC");
		assertNotExists("url");
		assertNotExists("city");
		
		changeModule("InternetShop");
		assertListRowCount(1);
		assertValueInList(0, 0, "AMAZON");
		assertValueInList(0, 1, "http://www.amazon.com");
		
		changeModule("StreetShop");
		assertListRowCount(1);
		assertValueInList(0, 0, "EL CORTE INGLES");
		assertValueInList(0, 1, "VALENCIA");
	}
	
}
