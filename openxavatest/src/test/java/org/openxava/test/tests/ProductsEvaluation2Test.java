package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProductsEvaluation2Test extends ModuleTestBase {
	
	public ProductsEvaluation2Test(String testName) {
		super(testName, "ProductsEvaluation2");		
	}
		
	public void testDescriptionsListInElementCollectionDependsOnMainEntityProperty() throws Exception { 
		// By now it has no records, so it enters in detail mode
		String [][] familyValues = { 
			{ "", "" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" },
			{ "1", "SOFTWARE" }
		};
		assertValidValues("family.number", familyValues);
		String [][] emptyValues = { 
			{ "", "" }
		};
		assertValidValuesInCollection("evaluations", 0, "product.number", emptyValues);
		
		setValue("family.number", "2"); // HARDWARE
		String [][] hardwareValues = {
			{ "", "" },
			{ "2", "IBM ESERVER ISERIES 270" }
		};		
		assertValidValuesInCollection("evaluations", 0, "product.number", hardwareValues);
		
		setValueInCollection("evaluations", 0, "product.number", "2");
		setValueInCollection("evaluations", 0, "evaluation", "BAD");
		assertValidValuesInCollection("evaluations", 1, "product.number", hardwareValues);
		
		setValue("family.number", "1"); // SOFTWARE
		String [][] softwareValues = {
			{ "", "" },
			{ "4", "CUATRE" },
			{ "1", "MULTAS DE TRAFICO" },
			{ "5", "PROVAX" },
			{ "6", "SEIS" },
			{ "7", "SIETE" },
			{ "3", "XAVA" }
		};		
		assertValidValuesInCollection("evaluations", 0, "product.number", softwareValues);
		assertValidValuesInCollection("evaluations", 1, "product.number", softwareValues);
		
		// TMR ¿AÑADIR TESTING ESPECÍFICO?
	}
	
}
