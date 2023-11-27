package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProductsEvaluation2ExtendedDetailsTest extends ModuleTestBase {
	
	public ProductsEvaluation2ExtendedDetailsTest(String testName) {
		super(testName, "ProductsEvaluation2ExtendedDetails");		
	}
		
	public void test3LevelPropertiesInElementCollectionFromADescriptionsListReference() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("description", "THE EVALUATION");
		assertValueInCollection("evaluations", 0, "product.number", "1");
		assertValueInCollection("evaluations", 0, "product.color.name", "COLOR B2");
		assertValueInCollection("evaluations", 1, "product.number", "");
		assertValueInCollection("evaluations", 1, "product.color.name", "");
		
		setValueInCollection("evaluations", 1, "product.number", "5");
		assertValueInCollection("evaluations", 1, "product.color.name", "NEGRO"); 
	}
	
}
