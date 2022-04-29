package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TicketTest extends ModuleTestBase {
	
	public TicketTest(String testName) {
		super(testName, "Ticket");		
	}
	
	public void testCalculatedPropertiesWithDependsInTheSameViewOfAPolimorphicReference() throws Exception {
		execute("CRUD.new");
		setValue("number", "66"); 
		execute("Collection.new", "viewObject=xava_view_lines");
		setValue("article.number", "1");
		assertValue("article.description", "MORCILLA DE BURGOS");
		setValue("quantity", "2");
		setValue("price", "3");
		assertValue("amount", "6.00");
	}
		
}
