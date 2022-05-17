package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Invoice20020001Test extends ModuleTestBase {
	
	
	public Invoice20020001Test(String testName) {
		super(testName, "Invoice20020001");		
	}

	public void testFilterProduceMoreValuesThanPersistentPropertiesInList() throws Exception {
		assertNoErrors();
		String [] comparators = { "=", "="};
		String [] condition = { "2002", "2"	};
		setConditionComparators(comparators);
		setConditionValues(condition);
		execute("List.filter");
		assertNoErrors();
	}	
								
}
