package org.openxava.test.tests;

import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class Product3Test extends ModuleTestBase {
	
	public Product3Test(String testName) {
		super(testName, "Product3");		
	}
	
	public void testSearchingByAnyReference_genericI18nForTabNotAffectDetail() throws Exception {	 
		execute("CRUD.new");
		assertLabel("family.description", "Description"); // Generic i18n for tab not affect detail
		setValue("number", "");
		String description = getValue("description");
		assertTrue("description must be empty", Is.emptyString(description));
		execute("SearchForCRUD.search"); 
		setValue("family.number", "1");
		execute("Search.search"); 
		// We assume that exists products of family 1
		assertNoErrors();
		description = getValue("description");		
		assertTrue("description must have value", !Is.emptyString(description));
	}
	
	public void testSearchingReferenceWithHiddenKeyTypingValue() throws Exception { 
		execute("CRUD.new");
		assertEditable("family.number");
		assertValue("family.description", "");
		assertValue("comments", "");
		setValue("family.number", "1");
		assertValue("family.description", "SOFTWARE");
		assertValue("comments", "Family changed"); 
		setValue("family.number", "2");
		assertValue("family.description", "HARDWARE");
		execute("CRUD.new");
		assertValue("comments", "");
		execute("Reference.search", "keyProperty=xava.Product3.family.number");
		execute("ReferenceSearch.choose", "row=0"); 
		assertValue("comments", "Family changed"); 		
	}
	
	public void testReferenceWithHiddenKey_defaultValueCalculatorWithJDBC() throws Exception { 
		execute("CRUD.new");		
		assertValue("number", "78"); // to test default-value-calculator
		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		
		execute("Reference.search", "keyProperty=xava.Product3.family.number");
		String familyNumber = getValueInList(0, "number"); 		
		String familyDescription = getValueInList(0, "description");		
		execute("ReferenceSearch.choose", "row=0");
		assertValue("family.number", familyNumber); 
		assertValue("family.description", familyDescription);
		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("family.description", "");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		assertValue("family.number", familyNumber);
		assertValue("family.description", familyDescription);
		
		execute("CRUD.delete");			
		assertMessage("Product deleted successfully");
	}
	
	public void testSameAggregateTwiceWithDependentReferences() throws Exception {	
		String [][] familyValues = {
			{ "", "" },			
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" },
			{ "1", "SOFTWARE" }
		};
		String [][] hardwareValues = {
			{ "", ""},
			{ "12", "PC"},
			{ "13", "PERIFERICOS"},			
			{ "11", "SERVIDORES"}						
		};
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};		
		String [][] voidValues = {
			{ "", "" },
		};
		
		execute("CRUD.new");		
		assertValidValues("subfamily1.family.number", familyValues);
		assertValidValues("subfamily1.subfamily.number", voidValues);
		assertValidValues("subfamily2.family.number", familyValues);
		assertValidValues("subfamily2.subfamily.number", voidValues);
		

		setValue("subfamily1.family.number", "2");
		assertValidValues("subfamily1.family.number", familyValues);
		assertValidValues("subfamily1.subfamily.number", hardwareValues);
		assertValidValues("subfamily2.family.number", familyValues);
		assertValidValues("subfamily2.subfamily.number", voidValues);
		
		setValue("subfamily2.family.number", "1");
		assertValidValues("subfamily1.family.number", familyValues);
		assertValidValues("subfamily1.subfamily.number", hardwareValues);
		assertValidValues("subfamily2.family.number", familyValues);
		assertValidValues("subfamily2.subfamily.number", softwareValues);		
	}
						
}
