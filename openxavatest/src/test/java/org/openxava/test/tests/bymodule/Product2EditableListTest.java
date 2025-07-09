package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Product2EditableListTest extends ModuleTestBase {
	
	public Product2EditableListTest(String testName) {
		super(testName, "Product2EditableList");		
	}
	
	public void testEditablePropertiesInList() throws Exception {
		// TMR ME QUEDÉ POR AQUÍ: ADAPTAR TEST A Product2
		assertValueInList(0, "peopleCount", "0");
		assertValueInList(1, "peopleCount", "0");
		assertValueInList(2, "peopleCount", "0");
		assertValueInList(3, "peopleCount", "0");
		assertEditableInList(0, "peopleCount");
		assertEditableInList(1, "peopleCount");
		assertEditableInList(2, "peopleCount");
		assertEditableInList(3, "peopleCount");
		assertNoEditableInList(0, "extendedDescription"); // Included in editableProperties but it's @Formula so not should be editable
		assertNoEditableInList(1, "extendedDescription");
		assertNoEditableInList(2, "extendedDescription");
		assertNoEditableInList(3, "extendedDescription");
		
		setValueInList(0, "peopleCount", "5");
		assertNoErrors();
		assertMessage("Saved new value for people count in row 1");
		
		setValueInList(1, "peopleCount", "7");
		assertNoErrors();
		assertMessage("Saved new value for people count in row 2");
		
		setValueInList(1, "peopleCount", "15");
		assertError("15 is not a valid value for People count of Appointment 2: must be less than or equal to 13");
		
		execute("List.viewDetail", "row=0");
		assertValue("peopleCount", "5");
		
		execute("Mode.list");
		
		assertValueInList(0, "peopleCount", "5");
		assertValueInList(1, "peopleCount", "7");
		assertValueInList(2, "peopleCount", "0");
		assertValueInList(3, "peopleCount", "0");
		
		// Restoring
		setValueInList(0, "peopleCount", "0");
		setValueInList(1, "peopleCount", "0");
	}
	
}
