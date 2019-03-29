package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class AverageSpeedTest extends ModuleTestBase {

	
	public AverageSpeedTest(String testName) {
		super(testName, "AverageSpeed");		
	}
	
	
	public void testDefaultPropertiesForListWithoutTab_searchingInAReferenceByANonIdDoesNotUseLike() throws Exception {
		assertListColumnCount(4);
		assertLabelInList(0, "Number of Driver");
		assertLabelInList(1, "Driver");
		assertLabelInList(2, "Code of Vehicle");
		assertLabelInList(3, "Speed");
		
		execute("CRUD.new"); 
		assertEditable("vehicle.code"); 
		setValue("vehicle.code", "VLV40");
		assertValue("vehicle.model", "S40 T5");
		assertEditable("vehicle.code"); 
		setValue("vehicle.code", "");
		assertValue("vehicle.model", "");
		setValue("vehicle.code", "VLV");
		assertDialog();
		assertAction("ReferenceSearch.choose");
	}
		
}
