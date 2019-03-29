package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class DrivingLicenceTest extends ModuleTestBase {
		
	public DrivingLicenceTest(String testName) {
		super(testName, "DrivingLicence");		
	}
	
	public void testBeanValidation() throws Exception { 
		execute("CRUD.new");
		setValue("type", "X");
		setValue("level", "3"); // This breaks @Max(2) Bean Validation
		setValue("description", "JUNIT TEST");
		execute("CRUD.save");
		assertError("3 is not a valid value for Level of Driving licence: tiene que ser menor o igual que 2"); 
	}
	
	public void testEmptyPDFReport() throws Exception {
		assertListNotEmpty();
		setConditionValues("Z");
		execute("List.filter");
		assertListRowCount(0);
		execute("Print.generatePdf");
		assertTrue(getPopupText().contains("No data. Try other conditions in the filters"));
	}
	
}
