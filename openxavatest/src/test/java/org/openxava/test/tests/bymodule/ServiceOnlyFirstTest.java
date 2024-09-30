package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;


/**
 * @author Chungyen Tsai
 */

public class ServiceOnlyFirstTest extends ModuleTestBase {
			
	public ServiceOnlyFirstTest(String nombreTest) {
		super(nombreTest, "ServiceOnlyFirst");		
	}
	
	public void testOnChangeSearchActionNotAddingNonExistentRecordOfTabUsingSearchKey() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=invoice.number");
		assertListRowCount(1);
		assertValueInList(0, 0, "2007");
		execute("ReferenceSearch.cancel");
		setValue("invoice.year", "2007");
		setValue("invoice.number", "2");
		assertListRowCount(1);
		execute("ReferenceSearch.choose","row=0");
		assertValue("invoice.year", "2007");
		assertValue("invoice.number", "1");
	}
			
}
