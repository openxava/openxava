package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
 * To test pop up calendar with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class DateCalendarTest extends WebDriverTestBase {
		
	public void testGreek() throws Exception { 
		changeLanguage("el");
		goModule("Event");
		WebElement calendarIcon = getDriver().findElement(By.cssSelector(".ox_openxavatest_Event__list_col0 .mdi-calendar"));
		calendarIcon.click();
		Thread.sleep(100); 
		WebElement days = getDriver().findElement(By.className("flatpickr-weekdaycontainer"));
		String daysText = days.getText();
		assertEquals(916, daysText.charAt(0)); 
		assertEquals(949, daysText.charAt(1));
	}
	
	public void testMultipleDateWithOnChange_localeTranslationWellLoaded() throws Exception {
		changeLanguage("ja");
		goModule("Event");
		execute("List.viewDetail", "row=0"); 
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(0);
		    firstIconElement.click();
		}
		Thread.sleep(500);
		List<WebElement> spanElements = getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='8']"));
		if (!spanElements.isEmpty()) {
		    WebElement firstSpanElement = spanElements.get(0);
		    firstSpanElement.click();
		}
		wait(getDriver());
		WebElement divElement = getDriver().findElement(By.cssSelector("div.ox-message-box"));
		String message = divElement.getText();
		String expectedMessage = "Calendar changed";
		assertEquals(expectedMessage, message);
		
		getDriver().navigate().refresh();
		wait(getDriver());
		iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(0);
		    firstIconElement.click();
		}
		List<WebElement> weekdaycontainerDivElement = getDriver().findElements(By.className("flatpickr-weekdaycontainer"));
        WebElement thirdSpanElement = weekdaycontainerDivElement.get(0).findElements(By.tagName("span")).get(2);
		assertTrue("The translation of dateCalendar is not loaded correctly", !thirdSpanElement.getText().equals("Tue"));
		execute("Mode.list");
		acceptInDialogJS(getDriver());
	}
	
	public void testDutch_zhCN() throws Exception { 
		changeLanguage("nl");
		goModule("Appointment");
		setConditionValue("26-5-2015 8:15", 0);
		execute("List.filter");
		assertNoErrors(); 
		
		changeLanguage("zh-CN");
		goModule("Appointment");
		setConditionValue("2015-5-26 AM8:15", 0);
		execute("List.filter");
		assertNoErrors(); 
	}

	public void testChineseDateTime() throws Exception {
		changeLanguage("zh-TW");
		goModule("Appointment2");
		execute("List.viewDetail", "row=2");
		assertValue("time", "2015/5/26 PM 1:34");
		assertValue("dateTime", "2015/5/26 PM 2:34");
		List<WebElement> calendarPopUp = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar-clock"));
		calendarPopUp.get(1).click();
		WebElement label = getDriver().findElement(By.id("ox_openxavatest_Appointment2__label_time"));
		label.click();
		calendarPopUp.get(1).click();
		label.click();
		assertValue("time", "2015/5/26 PM 1:34");
		assertValue("dateTime", "2015/5/26 PM 2:34");
	}

}
