package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ChangeProductsPriceTest extends ModuleTestBase {
	
	private String [] detailActions = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",		
		"Mode.list",									
		"ChangeProductsPrice.save",
		"ChangeProductsPrice.editDescription"
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

	public ChangeProductsPriceTest(String testName) {
		super(testName, "ChangeProductsPrice");		
	}

	

	public void testActionOnInitAndViewSetEditable() throws Exception {
		assertActions(listActions); 
		
		execute("List.viewDetail", "row=0");
		assertActions(detailActions);

		assertNoEditable("description");
		assertEditable("unitPrice");		
	}	
					
}
