package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ColorinColoradoTest extends ModuleTestBase {
	
	public ColorinColoradoTest(String testName) {
		super(testName, "ColorinColorado");		
	}
	
	public void testAfterEachRequestAction() throws Exception { 
		assertValue("name", "NULLCOLORADO"); 
		setValue("name", "");
		execute("ColorinColorado.fillName");		
		assertValue("name", "COLORIN COLORADO");
	}
	
}
