package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ChangeProductsPrice2Test extends ModuleTestBase {
	
	private String [] detailActions = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",		
		"Mode.list",
		"ChangeProductsPrice2.save",
		"ChangeProductsPrice2.verifyEditables"
	};
	
	private String [] listActions = {
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn",
		"List.changeConfiguration", 
		"List.changeColumnName", 
		"ListFormat.select"
	};

	public ChangeProductsPrice2Test(String testName) {
		super(testName, "ChangeProductsPrice2");		
	}

	

	public void testViewSetEditable_isEditable_readOnly() throws Exception {
		assertActions(listActions); 
		
		execute("List.viewDetail", "row=0");
		assertActions(detailActions);

		assertNoEditable("description");
		assertEditable("unitPrice");
		
		execute("ChangeProductsPrice2.verifyEditables"); // Verifying View.isEditable
		assertNoErrors();				
	}	
					
}
