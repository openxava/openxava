package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProcessedWeighingTest extends ModuleTestBase {
	
	public ProcessedWeighingTest(String testName) {
		super(testName, "ProcessedWeighing");
	}
	
	public void testSearchActionForHiddenReferenceIdInElementCollection() throws Exception {
		assertAction("Reference.search", "keyProperty=details.0.weighing.id");
	}
	
}
