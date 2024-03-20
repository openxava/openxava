package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * 
 * @author Chungyen Tsai
 *
 */
public class MaskTest extends WebDriverTestBase {

	public void testMaskAsPropertyInEditorXml() throws Exception {
		goModule("CustomerWithSection");
		execute("CRUD.new");
		WebElement email = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__email"));
		assertTrue(email.getAttribute("data-inputmask") != null);
		email.sendKeys("pedro.matias.g.f@gmail.com.ar");
		String textInInput = email.getAttribute("value");
		assertEquals("pedro.matias.g.f@gmail.com.ar", textInInput);
		email.sendKeys("ab");
		assertEquals("pedro.matias.g.f@gmail.com.ar", textInInput);
		
		WebElement emailList = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__additionalEmails"));
		assertTrue(emailList.getAttribute("data-inputmask") != null);
		emailList.sendKeys("pedro@gmail.com, pedro2@gmail.com");
		textInInput = emailList.getAttribute("value");
		assertEquals("pedro@gmail.com, pedro2@gmail.com", textInInput);
		emailList.sendKeys("@");
		assertEquals("pedro@gmail.com, pedro2@gmail.com", textInInput);
		emailList.sendKeys(".ar");
		assertEquals("pedro@gmail.com, pedro2@gmail.com.ar", textInInput);
	}
	
}
