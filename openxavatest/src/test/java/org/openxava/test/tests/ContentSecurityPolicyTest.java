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
		
		long ini = System.currentTimeMillis(); // tmr
		waitImageLoaded(driver);
		long cuesta = System.currentTimeMillis() - ini; // tmr
		System.out.println("[ContentSecurityPolicyTest.testContentSecurityPolicyExceptions] cuesta="+ cuesta); // tmr
		WebElement img = driver.findElement(By.id("oximage"));
		assertEquals(new Dimension(500, 290), img.getSize()); // So, the original image is displayed
		
		WebElement greeting = driver.findElement(By.id("greeting"));
		assertEquals("Hello from openxava.org", greeting.getText());
		
		WebElement hidden = driver.findElement(By.id("thehidden"));
		assertFalse(hidden.isDisplayed());
		
		// TMR ME QUEDÉ POR AQUÍ, ACABO DE AÑADIR UN IFRAME, FALTA COMPROBARLO, HACER EL TEST E IMPLEMENTARLO. 
		// TMR   SOLO FALTAN LOS FRAMES PARA ACABAR
		
		// tmr driver.quit();
	}
	
	private void waitImageLoaded(WebDriver driver) throws Exception {
		Thread.sleep(500); 
		WebElement img = driver.findElement(By.id("oximage"));
		Dimension zero = new Dimension(0, 0);
		for (int c=0; c<10 && img.getSize().equals(zero); c++) {
			Thread.sleep(500);
		}
		
	}

}
