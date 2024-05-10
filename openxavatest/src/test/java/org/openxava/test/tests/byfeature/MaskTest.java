package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * 
 * @author Chungyen Tsai
 *
 */
public class MaskTest extends WebDriverTestBase {

	public void testMask_MaskAsPropertyInEditorXml() throws Exception {
		goModule("CustomerWithSection");
		execute("CRUD.new");
		WebElement email = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__email"));
		assertTrue(email.getAttribute("data-inputmask") != null);
		email.sendKeys("ingenierocivilpedromatias.guerrero@gmail.com.ar");
		assertValue("email","ingenierocivilpedromatias.guerrero@gmail.com.ar");
		email.sendKeys("ab");
		assertValue("email","ingenierocivilpedromatias.guerrero@gmail.com.ar");
		
		WebElement emailList = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__additionalEmails"));
		assertTrue(emailList.getAttribute("data-inputmask") != null);
		emailList.sendKeys("pedro@gmail.com, pedro2@gmail.com");
		assertValue("additionalEmails","pedro@gmail.com, pedro2@gmail.com");
		emailList.sendKeys("@");
		assertValue("additionalEmails","pedro@gmail.com, pedro2@gmail.com");
		emailList.sendKeys(".ar");
		assertValue("additionalEmails","pedro@gmail.com, pedro2@gmail.com.ar");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		WebElement passport = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__passport"));
		passport.sendKeys("E123456");
		WebElement creditCard = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__creditCard"));
		creditCard.sendKeys("1234000056780000");
		assertValue("passport", "E-123456");
		assertValue("creditCard", "1234 0000 5678 0000");
		execute("Customer.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("passport", "E-123456");
		assertValue("creditCard", "1234 0000 5678 0000");
		passport = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__passport"));
		passport.clear();
		creditCard = getDriver().findElement(By.id("ox_openxavatest_CustomerWithSection__creditCard"));
		creditCard.clear();
		execute("Customer.save");
	}
	
}
