package org.openxava.test.tests.bymodule;

import java.time.*;

import org.junit.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class LedgerTest extends ModuleTestBase {
		
	public LedgerTest() {
		super("Ledger");
	}
	
	@Test
	public void testDefaultValueDependentOfReferenceWithDefaultValueThatReturnsEntity() throws Exception { 
		// No records, so CRUD.new on entering module
		assertDescriptionValue("period.id", String.valueOf(Year.now().getValue())); // It will fail in 2038, then you should add more records to LedgerPeriod 
		assertValue("number", "1");
	}
	
}
