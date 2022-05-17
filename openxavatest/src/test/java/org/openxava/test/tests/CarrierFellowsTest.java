package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class CarrierFellowsTest extends ModuleTestBase {
	
	public CarrierFellowsTest(String testName) {
		super(testName, "CarrierFellows");		
	}
	
	public void testAddRemoveListActionPreservesOrder() throws Exception {
		execute("CRUD.new");
		assertAction("Carrier.translateName");
		execute("CarrierFellows.removeTranslateName");
		assertNoAction("Carrier.translateName");
		assertMessage("Original fellows actions=[Carrier.translateName, Carrier.allToEnglish, CollectionCopyPaste.cut, CollectionCopyPaste.paste, Print.generatePdf, Print.generateExcel]"); 
		
		execute("CarrierFellows.addTranslateName");
		assertAction("Carrier.translateName");
		assertMessage("Final fellows actions=[Carrier.translateName, Carrier.allToEnglish, CollectionCopyPaste.cut, CollectionCopyPaste.paste, Print.generatePdf, Print.generateExcel]"); 
	}
	
}
