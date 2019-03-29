package org.openxava.test.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.tests.ModuleTestBase;

/**
 * @author Javier Paniza
 */

public class SubfamilyTest extends ModuleTestBase {
	private static Log log = LogFactory.getLog(SubfamilyTest.class);
	
	public SubfamilyTest(String testName) {
		super(testName, "Subfamily");		
	}
		
	public void testSaveHiddenKeyWithSections() throws Exception {
		assertTrue("For this test is required al least 2 families", getListColumnCount() >= 2);
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		execute("CRUD.save");
		assertNoErrors(); 
	}	
	
	public void testFilledWithZeros() throws Exception {
		String formattedNumber = "002";		
		String[] condition = {"2"};
		setConditionValues(condition);
		execute("List.filter");
		assertListRowCount(1);
		assertNoErrors();
		execute("List.viewDetail", "row=0");
		assertValue("number", formattedNumber);
		assertNoErrors();
	}
	
	public void testNavigateHiddenKeyWithSections() throws Exception {
		assertTrue("For this test is required al least 2 families", getListColumnCount() >= 2);
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		execute("Navigation.next");
		assertNoErrors();
	}
	
	public void testPropertiesTabByDefault() throws Exception {
		assertLabelInList(0, "Number");
		assertLabelInList(1, "Family");
		assertLabelInList(2, "Description");		
	}
	
							
}
