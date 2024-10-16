package org.openxava.test.tests.bymodule;

/**
 * 
 * @author Javier Paniza
 */

public class Product2SectionByDescriptionsListTest extends EmailNotificationsTestBase { 
	
	public Product2SectionByDescriptionsListTest(String testName) {
		super(testName, "Product2SectionByDescriptionsList");		
	}	

	public void testDescriptionsListDependentsWitThisInDifferentSections() throws Exception {		
		execute("CRUD.new");
	
		// Verifying initial state		
		String [][] familyValues = {
			{ "", "" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" }	
		};
		
		assertValue("family.number", "2"); // 2 is the default value		
		assertValidValues("family.number", familyValues); 
		
		// Changing value 
		setValue("family.number", "1");
		execute("Sections.change", "activeSection=1");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", softwareValues);										
	}

}
