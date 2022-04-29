package org.openxava.test.tests;

/**
 * @author Javier Paniza
 */

public class Product2DescriptionsListShowsReferenceViewTest extends EmailNotificationsTestBase { 
	
	public Product2DescriptionsListShowsReferenceViewTest(String testName) {
		super(testName, "Product2DescriptionsListShowsReferenceView");		
	}
	

	public void testDescriptionsListWithShowsReferenceViewDependents() throws Exception {  
		
		execute("CRUD.new");
	
		// Verifying initial state		
		String [][] familyValues = {
			{ "", "" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" }	
		};
		
		String [][] hardwareValues = {
			{ "", ""},
			{ "12", "PC"},
			{ "13", "PERIFERICOS"},			
			{ "11", "SERVIDORES"}						
		};

		
		assertValue("family.number", "2"); // 2 is the default value		
		assertValidValues("family.number", familyValues);
		assertValidValues("subfamily.number", hardwareValues);
		
		// Changing the value 
		setValue("family.number", "1");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};
		assertValidValues("subfamily.number", softwareValues);										
	}
	
						
}
