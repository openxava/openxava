package org.openxava.test.tests;

import org.openqa.selenium.*;

/**
 * tmr
 * To test CSP related issues with Selenium.
 *
 * @author Javier Paniza
 */

public class ContentSecurityPolicyTest extends WebDriverTestBase {
	
	public void testContentSecurityPolicyExceptions() throws Exception {
		WebDriver driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/SellerJSP?detail=1"); 
		wait(driver);
		Thread.sleep(500); // Because painting image requires some time
		WebElement img = driver.findElement(By.id("oximage"));
		assertTrue(img.isDisplayed());
		// TMR ME QUEDÉ POR AQUÍ: INTENTANDO HACER EL TEST. EL isDisplayed() NO FUNCIONA PERO PARECE QUE EL getSize() SÍ
		System.out.println("[ContentSecurityPolicyTest.testContentSecurityPolicyExceptions] img.getSize()=" + img.getSize()); // tmr

		// tmr driver.quit();
	}

}
