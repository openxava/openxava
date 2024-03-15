package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

public class MaskTest extends WebDriverTestBase {

	public void testMask_maskAsPropertyInEditorXml() throws Exception {
		goModule("Task");
		WebElement element = getDriver().findElement(By.id("ox_openxavatest_Task__userEMail"));
		assertTrue(element.getAttribute("data-inputmask") != null);
		element.sendKeys("pedro.matias.g.f@gmail.com.ar");
		String textInInput = element.getAttribute("value");
		assertEquals("pedro.matias.g.f@gmail.com.ar", textInInput);
		element.sendKeys("ab");
		assertEquals("pedro.matias.g.f@gmail.com.ar", textInInput);
	}
	
}
