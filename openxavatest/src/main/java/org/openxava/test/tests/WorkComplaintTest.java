package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WorkComplaintTest extends ModuleTestBase {
	
	public WorkComplaintTest(String testName) {
		super(testName, "WorkComplaint");		
	}
	
	public void testDescriptionsListWithSameNameInDialog() throws Exception {
		String referenceName = "type.id"; // Same for both, to test a bug
		
		String [][] complaintTypes = {
			{ "", "" },
			{ "40288118682cb68a01682cb843a20001", "INCOMPLETE" },
			{ "40288118682cb68a01682cb7caf50000", "LOW QUALITY" },
			{ "40288118682cb68a01682cb86aae0002", "MISSING" }
		};		
		assertValidValues(referenceName, complaintTypes);
		
		execute("Reference.createNew", "model=WorkOrder,keyProperty=order__KEY__");
		
		String [][] orderTypes = {
			{ "", "" },
			{ "40288118682816ee0168281834d50001", "CONSULTING" },
			{ "40288118682816ee01682817e80b0000", "HANDWORK" },
			{ "40288118682816ee01682818b4080002", "TRAINING" }
		};
		assertValidValues(referenceName, orderTypes); 
	}
			
}
