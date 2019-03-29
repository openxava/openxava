package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CompanyTest extends ModuleTestBase {
	
	public CompanyTest(String testName) {
		super(testName, "Company");		
	}
	
	public void testReferenceAndCascadeRemoveCollectionToSameEntity() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("name", "MY COMPANY");
		assertValue("mainBuilding.name", "MY OFFICE");
		assertCollectionRowCount("buildings", 1);
		assertValueInCollection("buildings", 0, 0, "BUILDING A");
		execute("Collection.edit", "row=0,viewObject=xava_view_buildings");
		assertNoErrors();
		assertValue("name", "BUILDING A");
		execute("Collection.save");
		assertNoErrors();
	}
	
	public void testCollectionElementInsideAGroup() throws Exception {
		execute("CRUD.new");
		setValue("name", "SOMETHING"); 
		execute("Collection.new", "viewObject=xava_view_buildings");
		assertNoErrors(); // For verifying that really works
		assertMessagesCount(1);
		setValue("function", "Factory"); // For verifying that onchange is thrown only once
		assertMessagesCount(1);		
	}	
	
	public void testErrorOnCommitFromADialog() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertNoDialog();		
		execute("Collection.edit", "row=0,viewObject=xava_view_buildings");
		assertDialog();
		execute("Company.failTrasactionInBuilding");
		assertError("Impossible to execute Fail trasaction in building action: Transaction marked as rollbackOnly");  
		assertNoDialog(); // The dialog is closed because the exception is produced on commit when the
			// dialog has been already closed by the action. 
		execute("Reference.modify", "model=Building,keyProperty=mainBuilding.name");
		assertNoErrors();
		assertDialog();
		
		closeDialog();
		execute("Collection.edit", "row=0,viewObject=xava_view_buildings");
		assertDialog();
		execute("Company.saveBuildingFailing");
		assertError("Impossible to execute Save building failing action: Transaction marked as rollbackOnly");
		assertDialog();
	}
		
}
