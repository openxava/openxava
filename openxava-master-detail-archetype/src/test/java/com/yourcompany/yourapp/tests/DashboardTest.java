package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class DashboardTest extends ModuleTestBase {
	
	public DashboardTest(String testName) {
		super(testName, "Dashboard");
	}
	
	public void testDashboardIsDisplayed() throws Exception {
		login("admin", "admin");
		assertPositive("numberOfMasters");
		assertPositive("numberOfPeople");
		assertCollectionRowCount("topPeople", 5);
		assertCollectionRowCount("topYears", 5);
	}
	
	private void assertPositive(String property) throws Exception {
		String value = getValue(property).replaceAll("[^0-9]", "");
		assertFalse(property + " has no value", value.isEmpty());
		assertTrue(property + " must be positive", Long.parseLong(value) > 0);
	}
	
}
