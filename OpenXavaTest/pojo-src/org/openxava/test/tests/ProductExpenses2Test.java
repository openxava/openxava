package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 *  
 * @author Javier Paniza
 */

public class ProductExpenses2Test extends ModuleTestBase {
	
	public ProductExpenses2Test(String testName) {
		super(testName, "ProductExpenses2");		
	}
		
	public void testDescriptionsListWithDefaultValueInElementCollection() throws Exception {  
		execute("CRUD.new");
		setValue("description", "JUNIT EXPENSES");
		
		assertValueInCollection("expenses", 0, "invoice.KEY", "");
		assertValueInCollection("expenses", 0, "product.number", ""); 				
		setValueInCollection("expenses", 0, "carrier.number", "3");  
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]"); 
		assertValueInCollection("expenses", 0, "product.number", "2");	
		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("description", "JUNIT EXPENSES");
		assertCollectionRowCount("expenses", 1);
		assertValueInCollection("expenses", 0, "carrier.number", "3");  
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]"); 
		assertValueInCollection("expenses", 0, "product.number", "2");	
		
		execute("CRUD.delete");
		assertNoErrors();
	}
			
}
