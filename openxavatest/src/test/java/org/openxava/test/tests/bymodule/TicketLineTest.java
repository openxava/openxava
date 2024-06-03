package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TicketLineTest extends ModuleTestBase {
	
	public TicketLineTest(String testName) {
		super(testName, "TicketLine");		
	}
	
	public void testSearchReferenceWithZeroId() throws Exception {
		setValue("article.number", "0");
		assertValue("article.description", "FIRST AT ALL");
	}
		
}
