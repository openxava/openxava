package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerCardListTest extends ModuleTestBase {
	
	public CustomerCardListTest(String testName) {
		super(testName, "CustomerCardList");				
	}

	public void testCustomEditorForTab() throws Exception {		
		assertTrue(getHtml().contains("Gonzalo Gonzalez(43)"));
		
		assertAction("ListFormat.select", "editor=Charts");
		assertAction("ListFormat.select", "editor=CustomerCardList");
		assertNoAction("ListFormat.select", "editor=List");
		
		execute("ListFormat.select", "editor=Charts");
		assertExists("xColumn");
		assertFalse(getHtml().contains("Gonzalo Gonzalez(43)"));
		execute("ListFormat.select", "editor=CustomerCardList");
		assertNotExists("xColumn");
		assertTrue(getHtml().contains("Gonzalo Gonzalez(43)"));	
	}	
									
}
