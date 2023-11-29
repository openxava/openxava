package org.openxava.test.tests;

import java.util.*;

import org.openqa.selenium.*;

public class DateCalendarTest extends WebDriverTestBase {
	
	private WebDriver driver;

	public void testNavigation() throws Exception {
		driver = createWebDriver("ja"); 
		forTestMultipleDateWithOnChange_localeTranslationWellLoaded();  
	}
	
	public void testGreek() throws Exception { 
		driver = createWebDriver("el"); 
		driver.get("http://localhost:8080/openxavatest/m/Event");
		wait(driver);
		WebElement calendarIcon = driver.findElement(By.cssSelector(".ox_openxavatest_Event__list_col0 .mdi-calendar"));
		calendarIcon.click();
		WebElement days = driver.findElement(By.className("flatpickr-weekdaycontainer"));
		String daysText = days.getText();
		assertEquals(916, daysText.charAt(0));
		assertEquals(949, daysText.charAt(1));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	private void forTestMultipleDateWithOnChange_localeTranslationWellLoaded() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Event");
		wait(driver);
		acceptInDialogJS(driver);
		moveToListView(driver);
		execute(driver, "Event", "List.viewDetail", "row=0"); 
		List<WebElement> iconElements = driver.findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(0);
		    firstIconElement.click();
		}
		Thread.sleep(500);
		List<WebElement> spanElements = driver.findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='8']"));
		if (!spanElements.isEmpty()) {
		    WebElement firstSpanElement = spanElements.get(0);
		    firstSpanElement.click();
		}
		wait(driver);
		WebElement divElement = driver.findElement(By.cssSelector("div.ox-message-box"));
		String message = divElement.getText();
		String expectedMessage = "Calendar changed";
		assertEquals(expectedMessage, message);
		
		driver.navigate().refresh();
		wait(driver);
		iconElements = driver.findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(0);
		    firstIconElement.click();
		}
		List<WebElement> weekdaycontainerDivElement = driver.findElements(By.className("flatpickr-weekdaycontainer"));
        WebElement thirdSpanElement = weekdaycontainerDivElement.get(0).findElements(By.tagName("span")).get(2);
		assertTrue("The translation of dateCalendar is not loaded correctly", !thirdSpanElement.getText().equals("Tue"));
		execute(driver, "Event", "Mode.list");
		acceptInDialogJS(driver);
	}
	

	
}
