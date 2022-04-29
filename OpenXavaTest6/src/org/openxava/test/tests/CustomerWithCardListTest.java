package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerWithCardListTest extends ModuleTestBase {
	
	public CustomerWithCardListTest(String testName) {
		super(testName, "CustomerWithCardList");				
	}

	public void testCustomEditorsForTab() throws Exception {
		assertAction("List.filter");
		assertAction("ListFormat.select", "editor=List"); 
		assertAction("ListFormat.select", "editor=Charts");
		assertAction("ListFormat.select", "editor=CustomerCardList");
		assertNoAction("ListFormat.select", "editor=NotExist");
		
		execute("ListFormat.select", "editor=Charts");
		assertExists("xColumn");
		assertFalse(getHtml().contains("Gonzalo Gonzalez(43)"));
		assertNoAction("List.filter");
		
		execute("ListFormat.select", "editor=CustomerCardList");
		assertNotExists("xColumn");
		assertTrue(getHtml().contains("Gonzalo Gonzalez(43)"));
		assertNoAction("List.filter");
		
		execute("ListFormat.select", "editor=List");
		assertNotExists("xColumn");
		assertFalse(getHtml().contains("Gonzalo Gonzalez(43)"));		
		assertAction("List.filter");
	}	
									
}
