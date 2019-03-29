package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class DriverTest extends ModuleTestBase {
	
	public DriverTest(String testName) {
		super(testName, "Driver");		
	}
		
	public void testSearchFromOverlappedReferenceWithStringKey() throws Exception {
		execute("CRUD.new");
		setValue("type", "C ");
		execute("Reference.search", "keyProperty=xava.Driver.drivingLicence.level");
		assertNoErrors();
		assertListRowCount(2);		
		assertValueInList(0, 0, "C"); 
		assertValueInList(1, 0, "C");
	}
	
	public void testNavigationBySelected() throws Exception {
		changeModule("DriverWithTabName");
		assertLabelInList(0, "Name"); 
		assertValueInList(0, 0, "ALONSO");
		assertValueInList(2, 0, "LORENZO");
		checkRow(0);
		checkRow(2);
		
		execute("List.viewDetail", "row=0");
		assertValue("name", "ALONSO");
		execute("Navigation.next");
		assertValue("name", "LORENZO");
		execute("Navigation.previous");
		assertValue("name", "ALONSO");
		// it fails when you go inside the list to choose reference
		execute("Reference.search", "keyProperty=drivingLicence.level");
		execute("ReferenceSearch.cancel");
		execute("Navigation.next");
		assertValue("name", "LORENZO");
	}
			
}
