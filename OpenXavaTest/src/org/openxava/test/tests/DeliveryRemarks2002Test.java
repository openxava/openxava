package org.openxava.test.tests;

import java.util.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class DeliveryRemarks2002Test extends ModuleTestBase {
	
		
	public DeliveryRemarks2002Test(String testName) {
		super(testName, "DeliveryRemarks2002");		
	}
	
	public void testGenerateExcelWithMultilineText() throws Exception { 
		assertListRowCount(1); // We assume that there are only a delivery from 2002 
		assertValueInList(0, 0, "2002");
		assertValueInList(0, 1, "1");
		String expectedLine = "2002;1;\"FOUR LINES CUATRO LINEAS\"";
		
		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");
				
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", "Year;Number;Remarks", header);		
		String line1 = excel.nextToken();
		assertEquals("line1", expectedLine, line1); 
		assertTrue("Only one line must have generated", !excel.hasMoreTokens());
	}


}
