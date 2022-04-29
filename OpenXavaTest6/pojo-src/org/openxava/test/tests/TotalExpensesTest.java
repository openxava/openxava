package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 

/**
 * 
 * @author Javier Paniza
 */
public class TotalExpensesTest extends ModuleTestBase {
	
	public TotalExpensesTest(String testName) {
		super(testName, "TotalExpenses");		
	}
	
	public void testDependentDescriptionsListInElementCollectionInsideDialog() throws Exception { 
		execute("Collection.new", "viewObject=xava_view_expenses");
		
		String [][] familyValidValues = {
			{ "", "" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" },
			{ "1", "SOFTWARE" }
		};		
		assertValidValuesInCollection("expenses", 0, "family.number", familyValidValues);
		String [][] noValidValues = {
			{ "", "" }	
		};
		assertValidValuesInCollection("expenses", 0, "subfamily.number", noValidValues);
		setValueInCollection("expenses", 0, "family.number", "1");
		String [][] subfamily1ValidValues = {
			{ "", "" },
			{ "1", "DESARROLLO" },
			{ "2", "GESTION" },
			{ "3", "SISTEMA" }
		};		
		assertValidValuesInCollection("expenses", 0, "subfamily.number", subfamily1ValidValues); 

	}
	
}
