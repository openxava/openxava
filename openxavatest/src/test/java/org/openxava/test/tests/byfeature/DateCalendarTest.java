package org.openxava.test.tests.byfeature;

import java.util.*;

import org.apache.commons.logging.*;
import org.openqa.selenium.*;

/**
 * To test pop up calendar with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class DateCalendarTest extends WebDriverTestBase {
	private static Log log = LogFactory.getLog(DateCalendarTest.class);
		/*
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
		setConditionValue("2015/5/26 AM8:15", 0);
		execute("List.filter");
		assertNoErrors(); 
	}

	public void testChineseDateTimeInJava8AndAmIssue_formatDateAndDateTimeUsingTwoDigits() throws Exception { 
		changeLanguage("zh-TW");
		appointment2();
		quarter();
		changeLanguage("zh-CN");
		appointment2();
		quarter();
		
		formatDateUsingTwoDigits("zh-CN");
		changeLanguage("es-ES");
		goModule("Quarter");
		formatDateUsingTwoDigits("es-ES");
		goModule("Shipment");
		formatDateTimeUsingTwoDigits(); 
	}
	
	public void testDate_DataChangedMessage_tabBlurWorkProperly_onChange() throws Exception {
		goModule("Order");
		WebElement date;
		WebElement dateLabel;
		WebElement modeList;
		execute("List.viewDetail", "row=0");
		date = getDriver().findElement(By.id("ox_openxavatest_Order__date"));
		date.sendKeys(Keys.TAB);
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		openCalendar(0);
		selectDate(0, "today");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		selectNextDay(0);
		modeList = getDriver().findElement(By.id("ox_openxavatest_Order__Mode___list"));
		modeList.click();
		Thread.sleep(500);
		Alert alert = getDriver().switchTo().alert();
		alert.accept();
		wait(getDriver());
		execute("List.viewDetail", "row=0");
		execute("Navigation.previous");
		assertMessage("We already are at the beginning of the list");
		date = getDriver().findElement(By.id("ox_openxavatest_Order__date"));
		date.sendKeys(Keys.TAB);
		modeList = getDriver().findElement(By.id("ox_openxavatest_Order__Mode___list"));
		modeList.click();
		wait(getDriver());
		
		execute("List.viewDetail", "row=0");
		selectNextDay(0);
		date = getDriver().findElement(By.id("ox_openxavatest_Order__date"));
		date.sendKeys(Keys.TAB);
		assertEquals("5/11/2017",date.getAttribute("value"));
		selectNextDay(0);
		dateLabel = getDriver().findElement(By.id("ox_openxavatest_Order__label_date"));
		dateLabel.click();
		assertValue("date", "5/12/2017");
		execute("Mode.List");
		
		goModule("OrderProductInDetailAsDescriptionsList");
		execute("List.viewDetail", "row=0");
		date = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__date"));
		date.sendKeys(Keys.TAB);
		assertNoMessage();
		modeList = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__Mode___list"));
		modeList.click();
		wait(getDriver());
		execute("List.viewDetail", "row=0");
		openCalendar(0);
		selectDate(0, "today");
		assertNoMessage();
		modeList = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__Mode___list"));
		modeList.click();
		wait(getDriver());
		execute("List.viewDetail", "row=0");
		selectNextDay(0);
		assertMessage("OnChangeVoidAction executed");
		selectNextDay(0);
		List<WebElement> messages = getDriver().findElements(By.className("ox-message-box"));
		assertTrue(messages.size() == 1);
		date = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__date"));
		date.sendKeys(Keys.TAB);
		assertValue("date", "5/12/2017");
		selectNextDay(0);
		dateLabel = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__label_date"));
		dateLabel.click();
		assertValue("date", "5/13/2017");
	}
	*/
	public void testDateTime_onChange_twoDigitYear() throws Exception {
		goModule("ShipmentWithOnChange");
		WebElement dateTime;
		WebElement timeLabel;
		/*
		execute("List.viewDetail", "row=2");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time"));
		openCalendarDateTime(0);
		selectDate(0, "today");
		timeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		timeLabel.click();
		assertNoMessage();
		Thread.sleep(5000);
		execute("Mode.list");
		*/
		execute("List.viewDetail", "row=2");
		openCalendarDateTime(0);
		WebElement upArrow = getDriver().findElements(By.className("arrowUp")).get(2);
		upArrow.click();
		Thread.sleep(100);
		assertNoMessage();
		upArrow.click();
		Thread.sleep(100);
		assertNoMessage();
		upArrow.click();
		Thread.sleep(100);
		timeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		timeLabel.click();
		Thread.sleep(500);
		assertMessage("OnChangeVoidAction executed");
		execute("Mode.list");
		
		changeLanguage("es-ES");
		goModule("ShipmentWithOnChange");
		execute("List.viewDetail", "row=2");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time"));
		dateTime.clear();
		dateTime.sendKeys(Keys.TAB);
		assertTrue(dateTime.getAttribute("value").equals(""));
		setValue("time","12/25/06 11:33 AM");
		timeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		timeLabel.click();
		
		goModule("Shipment");
		
	}
	
	private void formatDateUsingTwoDigits(String format) throws Exception {
		execute("CRUD.new");
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		setValue("initDate", String.valueOf(day));
		WebElement input = getDriver().findElement(By.id("ox_openxavatest_Quarter__initDate"));
		input.sendKeys(Keys.TAB);
		if (format.equals("zh-CN")) {
			assertEquals(getValue("initDate"), year + "/" + month + "/" + day);
		} else {
			String m = month < 10 ? "0"+ String.valueOf(month) : String.valueOf(month);
			assertEquals(getValue("initDate"), day + "/" + m + "/" + year);
		} 
	}
	
	private void formatDateTimeUsingTwoDigits() throws Exception {
		execute("CRUD.new"); 
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		setValue("time", String.valueOf(day));
		WebElement input = getDriver().findElement(By.id("ox_openxavatest_Shipment__time"));
		input.sendKeys(Keys.TAB);
		String m = month < 10 ? "0"+ String.valueOf(month) : String.valueOf(month);
		assertEquals(getValue("time"), day + "/" + m + "/" + year + " 00:00");
	}
	
	
	private void appointment2() throws Exception {
		goModule("Appointment2");
		execute("List.viewDetail", "row=2");
		assertValue("time", "2015/5/26 PM1:34");
		assertValue("dateTime", "2015/5/26 PM2:34");
		List<WebElement> calendarPopUp = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar-clock"));
		calendarPopUp.get(1).click();
		WebElement label = getDriver().findElement(By.id("ox_openxavatest_Appointment2__label_time"));
		label.click();
		calendarPopUp.get(1).click();
		label.click();
		assertValue("time", "2015/5/26 PM1:34");
		assertValue("dateTime", "2015/5/26 PM2:34");
		execute("Mode.list");
	}
	
	private void quarter() throws Exception {
		goModule("Quarter");
		execute("List.viewDetail", "row=0");
		assertValue("initDate", "2009/8/11");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
	}
	
	private void openCalendar(int i) throws InterruptedException {
		//i for more than one calendar in same view, 0 for unique calendar
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
		    WebElement calendarIcon = iconElements.get(i);
		    calendarIcon.click();
		}
		Thread.sleep(500);
	}
	
	private void openCalendarDateTime(int i) throws InterruptedException {
		//i for more than one calendar in same view, 0 for unique calendar
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar-clock"));
		if (!iconElements.isEmpty()) {
		    WebElement calendarIcon = iconElements.get(i);
		    calendarIcon.click();
		}
		Thread.sleep(500);
	}
	
	private void selectDate(int i, String day) throws InterruptedException {
		//day for choose today or another
		//i for more than one calendar in same view, 0 for unique calendar
		List<WebElement> spanElements;
		if (day.equals("today")) {
			spanElements = getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day selected']"));
		} else {
			int dayNumber = Integer.valueOf(day);
			spanElements = getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='" + dayNumber + "']"));
		}
		if (!spanElements.isEmpty()) {
		    WebElement spanElement = spanElements.get(i);
		    spanElement.click();
		}
		Thread.sleep(300);
	}
	
	private void selectNextDay(int i) throws InterruptedException {
		//i for more than one calendar in same view, 0 for unique calendar
		openCalendar(i);
		int daySelected = Integer.valueOf(getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day selected']")).get(i).getText());
		selectDate(i, String.valueOf(daySelected+1));
	}

}
