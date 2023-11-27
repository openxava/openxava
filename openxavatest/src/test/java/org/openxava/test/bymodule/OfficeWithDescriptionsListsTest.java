package org.openxava.test.bymodule;

import org.openxava.model.meta.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class OfficeWithDescriptionsListsTest extends ModuleTestBase {
	
	public OfficeWithDescriptionsListsTest(String testName) {
		super(testName, "OfficeWithDescriptionsLists");		
	}
	
	public OfficeWithDescriptionsListsTest(String testName, String moduleName) {
		super(testName, moduleName);		
	}
	
	public void testDescriptionsListDependsOnCompositeKeyReference() throws Exception { 
		// execute("CRUD.new"); Not needed because there is no office
		String [][] noEntries = {
			{ "", "" }	
		};
		assertValidValues("defaultCarrier.number", noEntries);
		
		Warehouse warehouse1 = createWarehouse1();
		String key = MetaModel.getForPOJO(warehouse1).toString(warehouse1);		
		setValue("mainWarehouse.KEY", key);		
		
		String [][] carriersOfWarehouse1 = {
			{ "", "" },
			{ "4", "CUATRO" },
			{ "2", "DOS" },
			{ "3", "TRES" },
			{ "1", "UNO" }
		};
		assertValidValues("defaultCarrier.number", carriersOfWarehouse1);
		
	}

	private Warehouse createWarehouse1() {
		Warehouse w = new Warehouse();
		w.setZoneNumber(1);
		w.setNumber(1);
		return w;
	}
		
}
