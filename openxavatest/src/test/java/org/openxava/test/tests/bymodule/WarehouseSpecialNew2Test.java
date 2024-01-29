package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
* Create on 11 ene. 2017 (9:10:40)
* @author Ana Andres
*/
public class WarehouseSpecialNew2Test extends ModuleTestBase {
		
	public WarehouseSpecialNew2Test(String testName) {
		super(testName, "WarehouseSpecialNew2");		
	}
	
	public void testOverwriteActionsWhenSameActionOverwriteInSeveralNivels() throws Exception {
		// test overwrite actions when a controllers extends another that extends another and both have the same action: before it showed WarehouseSpecialNew2.new and CRUD.new
		assertActions(new String[] { 
			"Print.generatePdf", 
			"List.filter", "List.goNextPage", "List.sumColumn", "List.orderBy", "List.hideRows", "List.viewDetail", "List.goPage", "List.changeColumnName",  
			"CRUD.deleteRow", "CRUD.deleteSelected",   
			"ListFormat.select",  "Print.generateExcel",  
			"ImportData.importData", 
			"WarehouseSpecialNew2.new"	
		});
	}
}