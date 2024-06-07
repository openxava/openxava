package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomCarrierTest extends ModuleTestBase {
	
	public CustomCarrierTest(String testName) {
		super(testName, "CustomCarrier");
	}
	
	public void testEditorForCollectionAndReferenceUsingAnnotation() throws Exception{
		execute("List.orderBy", "property=number"); 
		execute("List.viewDetail", "row=0");
		assertValue("number", "1"); 
		assertValue("name", "UNO");
		assertTrue(getHtml().contains("The values for warehouse are"));
		assertTrue(getHtml().contains("The fellows of UNO are:"));
		
	}
	
}
