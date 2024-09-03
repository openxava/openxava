package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Chungyen Tsai
 */

public class CarrierNoDefaultActionsTest extends ModuleTestBase {
	
	public CarrierNoDefaultActionsTest(String testName) {
		super(testName, "CarrierNoDefaultActions");
	}
	
	public void testNoDefaultActions() throws Exception{
		execute("List.viewDetail", "row=0");
		assertNoAction("CollectionOpenInNewTab.openInNewTab", "row=0,viewObject=xava_view_fellowCarriers");
		assertNoAction("CollectionCopyPaste.cut");
		assertNoAction("CollectionOpenInNewTab.openInNewTab", "row=0,viewObject=xava_view_fellowCarriersCalculated");
	}
	
}
