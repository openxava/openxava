package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;

/**
 * Create on 21/06/2012 (11:22:16)
 * @author Ana Andres
 */
public class IngredientTest extends ModuleTestBase {
	private static Log log = LogFactory.getLog(IngredientTest.class);
	
	public IngredientTest(String testName) {
		super(testName, "IngredientWithSections");		
	}
	
	// This test fails with XML components 
	public void testNavigationWithHiddenKeyAndSections() throws Exception { 
		assertLabelInList(0, "Name");
		execute("List.orderBy", "property=name");
		assertValueInList(0, 0, "AZUCAR"); 
		assertValueInList(1, 0, "CAFE");
		assertValueInList(2, 0, "CAFE CON LECHE");
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("name", "AZUCAR");
		execute("Navigation.next");
		assertNoErrors();
		assertValue("name", "CAFE"); 
		execute("Navigation.next");
		assertNoErrors();
		assertValue("name", "CAFE CON LECHE");
		execute("Navigation.previous");
		assertNoErrors();
		assertValue("name", "CAFE");
		execute("Navigation.previous");
		assertNoErrors();
		assertValue("name", "AZUCAR");
	}

}
