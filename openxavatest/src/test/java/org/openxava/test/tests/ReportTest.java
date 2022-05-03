package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ReportTest extends ModuleTestBase {
	
		
	public ReportTest(String testName) {
		super(testName, "Report");		
	}
	
	public void testChangeViewNameToASubview() throws Exception {
		// Changing view name to a subview works, but it's not
		// efficient in AJAX terms, beacuse all root view is reloaded
		// This can be optimized
		execute("CRUD.new");
		assertExists("ranges.numberFrom");
		assertExists("ranges.numberTo");
		assertExists("ranges.dateFrom");
		assertExists("ranges.dateTo");
		
		setValue("ranges.type", "1"); // NUMBERS
		assertExists("ranges.numberFrom");
		assertExists("ranges.numberTo");
		assertNotExists("ranges.dateFrom");
		assertNotExists("ranges.dateTo");

		setValue("ranges.type", "2"); // DATES
		assertNotExists("ranges.numberFrom");
		assertNotExists("ranges.numberTo");
		assertExists("ranges.dateFrom");
		assertExists("ranges.dateTo");		
	}
	
		
}
