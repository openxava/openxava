package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Product2OnlySoftwareTest extends ModuleTestBase {
	
	public Product2OnlySoftwareTest(String testName) {
		super(testName, "Product2OnlySoftware");		
	}
	
	public void testQualifiedPropertiesInDescriptionsListCondition_percentSymbolInDescriptionsListCondition() throws Exception {
		String [][] softwareValuesInListFilter = {
			{ "", ""},
			{ "1:_:DESARROLLO", "DESARROLLO"},
			{ "2:_:GESTION", "GESTION"},						  
			{ "3:_:SISTEMA", "SISTEMA"}						
		};		
		assertValidValues("conditionValue___3", softwareValuesInListFilter);
		execute("CRUD.new");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};		
		assertValidValues("subfamily.number", softwareValues);										
	}	
					
}
