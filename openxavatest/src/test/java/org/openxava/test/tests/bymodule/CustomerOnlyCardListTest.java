package org.openxava.test.tests.bymodule;

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
		assertTrue(getHtml().contains("Gonzalo Gonzalez(43)")); // TMR FALLA
		
		assertNoAction("ListFormat.select", "editor=Charts"); 
		assertNoAction("ListFormat.select", "editor=CustomerCardList");
		assertNoAction("ListFormat.select", "editor=List");
	}	
									
}
