package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TaxpayerTariffTest extends ModuleTestBase {
	
	public TaxpayerTariffTest(String testName) {
		super(testName, "TaxpayerTariff");		
	}
			
	public void testReferenceViewWithInheritanceAndGenericType() throws Exception {
		assertExists("taxpayer.name");
		assertNotExists("taxpayer.address");
		assertExists("tariff");
	}
				
}
