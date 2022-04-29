package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product4Test extends ModuleTestBase {
	
		
	public Product4Test(String testName) {
		super(testName, "Product4");		
	}
		
	public void testCalculatedPropertyWhenAnnotatedGetters_genericI18nForTabs() throws Exception { 
		assertLabelInList(2, "Family");
		assertLabelInList(3, "Subfamily");
		
		execute("CRUD.new");
		assertEditable("unitPrice");
		assertNoEditable("unitPriceInPesetas");
		assertValue("unitPrice", "");
		assertValue("unitPriceInPesetas", "");
		setValue("unitPrice", "10");
		assertValue("unitPriceInPesetas", "1,664");		
	}
	
	public void testValidationFromPrePersist() throws Exception {
		execute("CRUD.new");
		setValue("number", "666");
		setValue("description", "OPENXAVA");
		setValue("subfamily.number", "12");
		setValue("unitPrice", "300");
		execute("CRUD.save");
		assertError("You cannot sell OpenXava");
		assertErrorsCount(1);
		setValue("description", "ECLIPSE");
		execute("CRUD.save");
		assertError("You cannot sell Eclipse");
		assertErrorsCount(1);		
		setValue("description", "WEBSPHERE");
		execute("CRUD.save");
		assertError("666 is not a valid value for Number of Product 4: It's number of man"); 
		assertErrorsCount(1);
	}
	
	public void testValidationFromPreRemove() throws Exception { 
		execute("CRUD.deleteRow", "row=0");
		assertError("Impossible to remove Product4 because: The number one is not deletable");
		assertErrorsCount(2);
		
		execute("CRUD.deleteRow", "row=1");
		assertError("Impossible to remove Product4 because: The number two is not deletable");
		assertErrorsCount(2);
		
		execute("CRUD.deleteRow", "row=2");
		assertError("Impossible to remove Product4 because: It has family");
		assertErrorsCount(2);
		
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");
		execute("CRUD.delete");
		assertError("Impossible to remove Product4 because: The number one is not deletable");
		assertErrorsCount(1);
		
		execute("Navigation.next");
		assertValue("number", "2");
		execute("CRUD.delete");
		assertError("Impossible to remove Product4 because: The number two is not deletable");
		assertErrorsCount(1);
		
		execute("Navigation.next");
		assertValue("number", "3");
		execute("CRUD.delete");
		assertError("Impossible to remove Product4 because: It has family");
		assertErrorsCount(1);
	}
	
		
}
