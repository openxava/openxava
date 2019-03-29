package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerOnlyCardListTest extends ModuleTestBase {
	
	public CustomerOnlyCardListTest(String testName) {
		super(testName, "CustomerOnlyCardList");				
	}

	public void testCustomEditorsForTabWithOneEditorNotShowFormatButton() throws Exception { 	
		assertTrue(getHtml().contains("Gonzalo Gonzalez(43)"));
		
		assertNoAction("ListFormat.select", "editor=Charts"); 
		assertNoAction("ListFormat.select", "editor=CustomerCardList");
		assertNoAction("ListFormat.select", "editor=List");
	}	
									
}
