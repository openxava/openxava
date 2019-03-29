package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product2ReferenceAndStereotypeTest extends ModuleTestBase {
	
	public Product2ReferenceAndStereotypeTest(String testName) {
		super(testName, "Product2ReferenceAndStereotype");		
	}
	
	public void testStereotypeInViewDepedensOnReference() throws Exception {
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
		setValue("family.number", "");
			
		String [][] voidValues = {
			{ "", "" }
		};
		
		assertValue("subfamilyNumber", "");		
		assertValidValues("subfamilyNumber", voidValues);
		
		// Change value
		setValue("family.number", "2");
		String [][] hardwareValues = {
			{ "", ""},			
			{ "12", "PC"},
			{ "13", "PERIFERICOS"},
			{ "11", "SERVIDORES"}
		};
		assertValue("subfamilyNumber", "");
		assertValidValues("subfamilyNumber", hardwareValues);
		
		// Changing the value again
		setValue("family.number", "1");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};
		assertValue("subfamilyNumber", "");
		assertValidValues("subfamilyNumber", softwareValues);		
	}
							
}
