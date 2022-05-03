package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product2ColorWithFrameTest extends ModuleTestBase {
	
	public Product2ColorWithFrameTest(String testName) {
		super(testName, "Product2ColorWithFrame");		
	}
	
	public void testEditorForReferenceByView() throws Exception {
		execute("List.viewDetail", "row=0");
		setValue("color.number", "1");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("description", "");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("color.number", "1");
		
		setValue("color.number", "28");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("description", "");		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("color.number", "28");
				
		assertTrue(
				getHtml().indexOf(
					"<input name=\"ox_OpenXavaTest_Product2ColorWithFrame__color___number\" value=\"0\" type=\"radio\"") 
					>= 0
		);
		
		assertTrue(getHtml().indexOf("Color Frame Editor:") >= 0);
	}
						
}
