package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class FamilyTest extends ModuleTestBase {
	
	
	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"ImportData.importData", 
		"CRUD.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow",
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn",
		"List.changeConfiguration",
		"List.changeColumnName", 
		"ListFormat.select" 
	};

	public FamilyTest(String testName) {
		super(testName, "Family");		
	}
	
	// This test depends on a HTML generated UI
	public void testOverrideI18nMessage() throws Exception {		
		assertTrue("list_count is not override and it should", getHtml().indexOf("objets in list") < 0);
		assertTrue("list_count is not override and it should", getHtml().indexOf("records in list") >= 0);
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		assertListRowCount(3); 
		// Create
		execute ("CRUD.new");
		setValue("number","66");
		setValue("description","Family JUnit");
		execute("CRUD.save");
		assertNoErrors();
		
		execute("Mode.list");
		assertListRowCount(4);
		// Read
		execute("List.viewDetail", "row=3");
		assertNoErrors();
		assertValue("number","66");
		assertValue("description","FAMILY JUNIT");
		// Modify
		setValue("description","Family JUnit Modified");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(3,"description","FAMILY JUNIT MODIFIED");
		// Delete
		execute("List.viewDetail", "row=3");
		assertNoErrors();
		execute("CRUD.delete");		
		assertMessage("Family deleted successfully");		
		execute("Mode.list");
		assertListRowCount(3);
	}
	
	public void testTabDefaultOrder() throws Exception {
		assertActions(listActions); 
		assertValueInList(0, "number", "1");
		assertValueInList(1, "number", "2");
		assertValueInList(2, "number", "3");
	}	
	
	public void testDependOnHiddenKey() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoErrors(); 
		
		String [][] productsOfFamily1 = {
				{ "", "" },				
				{ "1", "XAVA" }
		};		
		assertValidValues("products", productsOfFamily1);
		execute("Navigation.next");
		
		String [][] productsOfFamily2 = {
				{ "", "" },				
		};		
		assertValidValues("products", productsOfFamily2);
		execute("Navigation.next");
		
		String [][] productsOfFamily3 = {
				{ "", "" },
				{ "77", "ANATHEMA" },
				{ "2", "XAVA COURSE" }
				
		};		
		assertValidValues("products", productsOfFamily3);								
	}
					
}
