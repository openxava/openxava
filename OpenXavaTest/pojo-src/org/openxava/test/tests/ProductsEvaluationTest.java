package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ProductsEvaluationTest extends ModuleTestBase {
	
	public ProductsEvaluationTest(String testName) {
		super(testName, "ProductsEvaluation");		
	}
	
	public ProductsEvaluationTest(String testName, String module) { 
		super(testName, module);		
	}
	
	public void testReadOnlyDescriptionsListInElementCollection() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("description", "THE EVALUATION");
		assertValueInCollection("evaluations", 0, 0, "XAVA");
		assertNoEditableInCollection("evaluations", 0, 0); 
	}
	
}
