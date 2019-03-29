package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Product3WithDescriptionsListTest extends ModuleTestBase {
	
	public Product3WithDescriptionsListTest(String testName) {
		super(testName, "Product3WithDescriptionsList");		
	}
	 
	public void testDefaultFormatterDoesNotApplyToKeyOfDescriptionsList() throws Exception { 
		// Creating new one
		execute("CRUD.new");		
		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		execute("Reference.createNew", "model=Family,keyProperty=xava.Product3.family.oid"); 
		setValue("Family", "number", "66");
		setValue("Family", "description", "Family 66");
		execute("NewCreation.saveNew");
		assertDescriptionValue("family.oid", "FAMILY 66"); // We have UpperCaseFormatter assigned to String in editors.xml 		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("description", "");
		assertDescriptionValue("family.oid", "");
		
		// Search it
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");		
		assertDescriptionValue("family.oid", "FAMILY 66");
		
		// Deleting
		execute("CRUD.delete");			
		assertMessage("Product deleted successfully");		
		deleteFamily66(); 
	}

	public void testSetToNullADescriptionsListWithHiddenKey() throws Exception {
		// Creating new one
		execute("CRUD.new");		
		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		setValue("family.oid", "1037101896763");		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("description", "");
				
		// Search it
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		assertValue("family.oid", "1037101896763");
		assertDescriptionValue("family.oid", "HARDWARE");
		
		// Reset the family reference
		setValue("family.oid", "");
		execute("CRUD.save");
		assertNoErrors();
		
		// Verifying that change is done
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		assertValue("family.oid", "");
		assertDescriptionValue("family.oid", "");
		
		// Deleting
		execute("CRUD.delete");			
		assertMessage("Product deleted successfully");		
	}
	
	private void deleteFamily66() {
		Query query = XPersistence.getManager().createQuery("from Family f where f.number = 66");
		XPersistence.getManager().remove(query.getSingleResult());		
	}	
						
}
