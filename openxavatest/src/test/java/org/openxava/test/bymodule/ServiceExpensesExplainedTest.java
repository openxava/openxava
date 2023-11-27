package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 *  
 * @author Javier Paniza
 */

public class ServiceExpensesExplainedTest extends ModuleTestBase {
	
	public ServiceExpensesExplainedTest(String testName) {
		super(testName, "ServiceExpensesExplained");		
	}
		
	public void testTotalPropertiesInElementCollectionOfBaseEntity() throws Exception {
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		setValueInCollection("expenses", 0, "invoice.number", "2");
		assertValueInCollection("expenses", 0, "invoice.amount", "1,730.00");
		assertTotalInCollection("expenses", "invoice.amount", "1,730.00");  
	}
	
			
}
