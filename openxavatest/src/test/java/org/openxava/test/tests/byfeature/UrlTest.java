package org.openxava.test.tests.byfeature;

/**
 * To test any URL issues with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class UrlTest extends WebDriverTestBase {
	
	public void testAccessingRecordByURLWhenHasTabBaseCondition() throws Exception {
		goModule("CustomerLessThanFour");
		assertListRowCount(3);
		getDriver().get("http://localhost:8080/openxavatest/m/CustomerLessThanFour?detail=4");
		wait(getDriver());
		assertFalse(getValue("number").equals("4"));
	}
		
}
