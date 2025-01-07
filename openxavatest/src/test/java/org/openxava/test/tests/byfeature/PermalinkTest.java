package org.openxava.test.tests.byfeature;

/**
 * To test any link issues with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class PermalinkTest extends WebDriverTestBase {
	
	public PermalinkTest(String testName) {
		super(testName);
	}
	
	public void testAccessingRecordByPermalinkWhenHasTabBaseCondition() throws Exception {
		goModule("CustomerLessThanFour");
		assertListRowCount(3);
		getDriver().get("http://localhost:8080/openxavatest/m/CustomerLessThanFour?detail=4");
		wait(getDriver());
		assertFalse(getValue("number").equals("4"));
	}
		
}
