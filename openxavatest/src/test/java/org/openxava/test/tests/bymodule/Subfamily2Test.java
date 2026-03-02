package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Subfamily2Test extends ModuleTestBase {
	
	public Subfamily2Test(String testName) {
		super(testName, "Subfamily2");		
	}
	
	public void testDescriptionsListWithMultipleDescription_oidPropertyWithNotListFormatterNotShownByDefault() throws Exception {
		execute("CRUD.new");
		String [][] familyValues = {
				{ "", "" },
				{ "1", "1 SOFTWARE" },
				{ "2", "2 HARDWARE" },
				{ "3", "3 SERVICIOS" }	
		};
		assertValidValues("family.number", familyValues);

		// The above code is to verify that photos is not shown, you can adapt it to other changes in the collections if needed
		//assertCollectionColumnCount("productsValues", 14);
		assertLabelInCollection("productsValues", 0, "Number");
		assertLabelInCollection("productsValues", 1, "Description");
		assertLabelInCollection("productsValues", 2, "Unit price");
		assertLabelInCollection("productsValues", 3, "Unit price with tax");
		assertLabelInCollection("productsValues", 4, "Subfamily");
		assertLabelInCollection("productsValues", 5, "Extended description");
		assertLabelInCollection("productsValues", 6, "Unit price in pesetas");
		assertLabelInCollection("productsValues", 7, "Zone 1 1");
		assertLabelInCollection("productsValues", 8, "Zone 1 2");
		assertLabelInCollection("productsValues", 9, "Zone 1 3");
		assertLabelInCollection("productsValues", 10, "Zone 1 4");
		assertLabelInCollection("productsValues", 11, "Zone 1 5");
		assertLabelInCollection("productsValues", 12, "Zone 1 6");
		assertLabelInCollection("productsValues", 13, "Zone one");

		
	}
	
}
