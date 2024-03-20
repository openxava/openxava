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
		assertValue("email","pedro.matias.g.f@gmail.com.ar");
		email.sendKeys("ab");
		assertValue("email","pedro.matias.g.f@gmail.com.ar");
		
		WebElement emailList = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__additionalEmails"));
		assertTrue(emailList.getAttribute("data-inputmask") != null);
		emailList.sendKeys("pedro@gmail.com, pedro2@gmail.com");
		assertValue("additionalEmails","pedro@gmail.com, pedro2@gmail.com");
		emailList.sendKeys("@");
		assertValue("additionalEmails","pedro@gmail.com, pedro2@gmail.com");
		emailList.sendKeys(".ar");
		assertValue("additionalEmails","pedro@gmail.com, pedro2@gmail.com.ar");
	}
	
}
