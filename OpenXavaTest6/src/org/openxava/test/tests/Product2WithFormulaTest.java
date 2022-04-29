package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product2WithFormulaTest extends ModuleTestBase {
	
	public Product2WithFormulaTest(String testName) {
		super(testName, "Product2WithFormula");				
	}
	

	public void testReferenceToEntityAutoOid() throws Exception {
		// Creating product and its formula at once
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT 66");
		setValue("subfamily.number", "11");
		setValue("unitPrice", "66");
		execute("Reference.createNew", "model=Formula,keyProperty=xava.Product2.formula.name");
		setValue("Formula", "name", "JUNIT FORMULA FROM PRODUCT"); 
		execute("NewCreation.saveNew");
		assertNoErrors();
		assertValue("formula.name", "JUNIT FORMULA FROM PRODUCT");
		
		execute("CRUD.save");
		assertNoErrors(); 
		
		// Searching to verify
		assertValue("description", "");
		setValue("number", "66");
		execute("CRUD.refresh");
		
		assertValue("description", "JUNIT PRODUCT 66");		
		assertValue("formula.name", "JUNIT FORMULA FROM PRODUCT");
				
		// Removing
		execute("CRUD.delete");
		assertNoErrors();
		assertMessage("Product deleted successfully"); 
		
		// Asserting that formula is not removed
		assertProductNotExist(66);
		assertFormulaExist("JUNIT FORMULA FROM PRODUCT");
		deleteFormula("JUNIT FORMULA FROM PRODUCT");
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
