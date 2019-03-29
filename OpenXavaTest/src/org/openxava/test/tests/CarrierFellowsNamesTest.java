package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Javier Paniza
 */

public class CarrierFellowsNamesTest extends ModuleTestBase {
	
	public CarrierFellowsNamesTest(String testName) {
		super(testName, "CarrierFellowsNames");
	}
	
	public void testEditorForCollectionByView() throws Exception{
		execute("List.orderBy", "property=number"); 
		execute("List.viewDetail", "row=0");
		assertValue("number", "1"); 
		assertValue("name", "UNO");
		assertTrue(getHtml().indexOf("The fellows of UNO are:") >= 0);
		
		execute("Navigation.next");
		assertValue("number", "2");
		assertValue("name", "DOS");
		assertTrue(getHtml().indexOf("The fellows of DOS are:") >= 0);		
	}
	
}
