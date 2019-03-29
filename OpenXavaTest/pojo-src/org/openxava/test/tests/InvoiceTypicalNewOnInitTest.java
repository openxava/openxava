package org.openxava.test.tests;

import java.util.*;

import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class InvoiceTypicalNewOnInitTest extends ModuleTestBase {
	
	public InvoiceTypicalNewOnInitTest(String testName) {
		super(testName, "InvoiceTypicalNewOnInit");		
	}
	
	public void testTypicalNewOnInit() throws Exception {
		assertAction("CRUD.save");
		String currentYear = Integer.toString(Dates.getYear(new Date()));
		assertValue("year", currentYear);  
	}
		
}
