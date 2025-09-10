package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class SuperheroeTest extends ModuleTestBase {
	
	public SuperheroeTest(String testName) {
		super(testName, "Superheroe");		
	}
	
	public void testHTMLFilterListFormatter() throws Exception {
		execute("CRUD.new");
		setValue("name", "<b>Super</b>man");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(0, 0, "<b>Super</b>man"); 
		execute("CRUD.deleteRow", "row=0");
	}
	
	public void testListConfigurationWithOrderWithOnlyOnePropertyAfterResetModule_notDuplicatedListConfigurations() throws Exception { 
		assertValueInList(0, 0, "ESCARIANO AVIESO");
		assertValueInList(1, 0, "SUPERLOPEZ");
		execute("List.orderBy", "property=name");
		assertNoAction("List.saveConfiguration"); // Because the order matches with the default one
		assertNoAction("List.changeConfiguration");
		execute("List.orderBy", "property=name");
		execute("List.saveConfiguration");
		execute("SaveListConfiguration.save");
		assertValueInList(0, 0, "SUPERLOPEZ");
		assertValueInList(1, 0, "ESCARIANO AVIESO");
		selectListConfiguration("All");
		assertValueInList(0, 0, "ESCARIANO AVIESO");
		assertValueInList(1, 0, "SUPERLOPEZ");		

		resetModule();
		
		assertValueInList(0, 0, "ESCARIANO AVIESO");
		assertValueInList(1, 0, "SUPERLOPEZ");
		selectListConfiguration("Ordered by name descending"); 
		assertValueInList(0, 0, "SUPERLOPEZ");
		assertValueInList(1, 0, "ESCARIANO AVIESO");
		
		assertListAllConfigurations("Ordered by name descending", "All");
		execute("List.orderBy", "property=name");
		execute("List.orderBy", "property=name");
		selectListConfiguration("All");
		assertListAllConfigurations("All", "Ordered by name descending");	
		
		selectListConfiguration("Ordered by name descending");
		execute("Print.generatePdf");
		assertPopupPDFLine(1, "Superheroe report - Ordered by name descending");
		
		selectListConfiguration("All");
		execute("Print.generatePdf");
		assertPopupPDFLine(1, "Superheroe report");
		
		setConditionValues("es");
		execute("List.filter");
		assertListSelectedConfiguration("Name contains es");
		execute("Print.generatePdf");
		assertPopupPDFLine(1, "Superheroe report");
		
		execute("List.saveConfiguration");
		setValue("name", "Ordered by name descending"); // An already existing configuration
		execute("SaveListConfiguration.save");
		assertWarning("Warning! The already existing query named Ordered by name descending has been overwritten");
		selectListConfiguration("All");
		assertListAllConfigurations("All", "Ordered by name descending");
		resetModule();
		assertListAllConfigurations("All", "Ordered by name descending");
	}
	
}
