package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Product2WithFormulaAsAggregateTest extends ModuleTestBase {
	
	public Product2WithFormulaAsAggregateTest(String testName) {
		super(testName, "Product2WithFormulaAsAggregate");				
	}
	

	public void testReferenceToEntityAutoOidAsAggregate() throws Exception {
		// Creating product and its formula at once
		execute("CRUD.new");
		setValue("number", "66");  
		setValue("description", "JUNIT PRODUCT 66");
		setValue("subfamily.number", "11");
		setValue("unitPrice", "66");
		setValue("formula.name", "JUNIT FORMULA FROM PRODUCT");
		
		execute("CRUD.save");
		assertNoErrors(); 
		
		// Searching to verify
		assertValue("description", "");
		setValue("number", "66");
		execute("CRUD.refresh");
		
		assertValue("description", "JUNIT PRODUCT 66");		
		assertValue("formula.name", "JUNIT FORMULA FROM PRODUCT");
		
		// Modifiying
		setValue("formula.name", "FORMULA MODIFIED FROM PRODUCT");
		
		execute("CRUD.save");
		assertNoErrors();
		
		// Searching to verify
		assertValue("description", "");
		setValue("number", "66");
		execute("CRUD.refresh");
		
		assertValue("description", "JUNIT PRODUCT 66");		
		assertValue("formula.name", "FORMULA MODIFIED FROM PRODUCT");
		
		// Removing
		execute("CRUD.delete");
		assertNoErrors();
		assertMessage("Product deleted successfully"); 
		
		// Asserting that formula is not removed
		// Although at IU level 'formula' behaves as aggreagate, actually it's a reference
		// to entity, therefore it cannot be removed automatically, because maybe reference
		// from other place.
		assertProductNotExist(66); 
		assertFormulaExist("FORMULA MODIFIED FROM PRODUCT");
		deleteFormula("FORMULA MODIFIED FROM PRODUCT");
	}


	private void deleteFormula(String name) throws Exception {
		Query query = XPersistence.getManager().createQuery("from Formula as o where o.name = :name"); 
		query.setParameter("name", name); 
		Formula f = (Formula) query.getSingleResult();
		XPersistence.getManager().remove(f);		
	}

	private void assertFormulaExist(String name) {
		Query query = XPersistence.getManager().createQuery("from Formula as o where o.name = :name");
		query.setParameter("name", name);
		if (query.getResultList().isEmpty()) {
			fail("Formula '" + name + "' does not exist, and it should");
		}
	}

	private void assertProductNotExist(long number) {		
		if (XPersistence.getManager().find(Product2.class, number) != null) {
			fail("Product " + number + " exists, and it shouldn't");
		}
				
	}
		
}
