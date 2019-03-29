package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 *  
 * @author Javier Paniza
 */

public class DocTest extends ModuleTestBase {
	
	public DocTest(String testName) {
		super(testName, "Doc");		
	}
			
	public void testHtmlTextInCharts() throws Exception {
		assertValueInList(0, 0, "DON QUIJOTE");
		assertTrue(getValueInList(0, 1).startsWith("En un lugar de la Mancha"));
		execute("ListFormat.select", "editor=Charts");
		setValue("xColumn", "content");
		assertFalse(getHtmlPage().asText().contains("/>"));		
		assertFalse(getHtmlPage().asText().contains("de cuyo nombre no quiero"));
	}
	
}
