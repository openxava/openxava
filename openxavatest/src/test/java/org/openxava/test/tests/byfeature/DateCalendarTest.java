package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

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
	
	public void testCalendarIconChangeInFilter() throws Exception {
		goModule("Order");
		WebElement dateComparator;
		WebElement dateCondition;
		Select select;

		dateComparator = getDriver().findElement(By.id("ox_openxavatest_Order__conditionComparator___2"));
		dateCondition = getDriver().findElement(By.id("ox_openxavatest_Order__conditionValue___2"));
		dateCondition.click();
		assertTrue(isCalendarIconPresent());
		select = new Select(dateComparator);
		select.selectByValue("year_comparator");
		assertFalse(isCalendarIconPresent());
		dateCondition.sendKeys("2020");
		dateCondition.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertEquals("2020", dateCondition.getAttribute("value"));
		select.selectByValue("eq_comparator");
		WebElement outside = getDriver().findElement(By.className("rows-per-page"));
		outside.click();
		openCalendar(1);
		selectDate(0, String.valueOf(1));
		Thread.sleep(100);
		select.selectByValue("year_comparator");
		assertEquals("", dateCondition.getAttribute("value"));
		dateCondition.sendKeys("2020");
		dateCondition.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertEquals("2020", dateCondition.getAttribute("value"));
		select.selectByValue("eq_comparator");
		assertEquals("", dateCondition.getAttribute("value"));
		assertTrue(isCalendarIconPresent());
		dateCondition.sendKeys("4112024");
		select.selectByValue("gt_comparator");
		wait(getDriver());
		dateCondition = getDriver().findElement(By.id("ox_openxavatest_Order__conditionValue___2"));
		assertEquals("4/11/2024", dateCondition.getAttribute("value"));
		assertTrue(isCalendarIconPresent());
		
		dateComparator = getDriver().findElement(By.id("ox_openxavatest_Order__conditionComparator___2"));
		select = new Select(dateComparator);
		select.selectByValue("year_comparator");
		dateCondition.clear();
		dateCondition.sendKeys("2004");
		dateCondition.sendKeys(Keys.TAB);
		assertFalse(isCalendarIconPresent());
		execute("List.filter");
		assertFalse(isCalendarIconPresent());
		dateCondition = getDriver().findElement(By.id("ox_openxavatest_Order__conditionValue___2"));
		dateCondition.clear();
		dateCondition.sendKeys("2002");
		dateCondition.sendKeys(Keys.TAB);
		assertEquals("2002", dateCondition.getAttribute("value"));
		dateComparator = getDriver().findElement(By.id("ox_openxavatest_Order__conditionComparator___2"));
		select = new Select(dateComparator);
		select.selectByValue("eq_comparator");
		assertTrue(getDriver().findElements(By.cssSelector(".ox-date-calendar.xava_date")).size() == 1); // only one left
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
		execute("Mode.list");
		
		goModule("OrderProductInDetailAsDescriptionsList");
		execute("List.viewDetail", "row=0");
		date = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__date"));
		date.sendKeys(Keys.TAB);
		assertNoMessages();
		modeList = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__Mode___list"));
		modeList.click();
		wait(getDriver());
		execute("List.viewDetail", "row=0");
		openCalendar(0);
		selectDate(0, "today");
		assertNoMessages();
		modeList = getDriver().findElement(By.id("ox_openxavatest_OrderProductInDetailAsDescriptionsList__Mode___list"));
		modeList.click();
		wait(getDriver());
		execute("List.viewDetail", "row=0");
		selectNextDay(0);
		Thread.sleep(150); 
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
		
	public void testDateTime_onChange_twoDigitYear_dateTimeSeparated_srDateTime() throws Exception {
		goModule("ShipmentWithOnChange");
		WebElement dateTime;
		WebElement timeLabel;
		execute("List.viewDetail", "row=2");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time"));
		openCalendarDateTime(0);
		selectDate(0, "today");
		timeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		timeLabel.click();
		assertNoMessages();
		execute("Mode.list");
		
		execute("List.viewDetail", "row=2");
		openCalendarDateTime(0);
		WebElement upArrow = getDriver().findElements(By.className("arrowUp")).get(2);
		upArrow.click();
		Thread.sleep(100);
		assertNoMessages();
		upArrow.click();
		Thread.sleep(100);
		assertNoMessages();
		upArrow.click();
		Thread.sleep(100);
		timeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		timeLabel.click();
		Thread.sleep(200); 
		assertMessage("OnChangeVoidAction executed");
		execute("Mode.list");
		
		execute("List.viewDetail", "row=2");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time"));
		dateTime.clear();
		dateTime.sendKeys("12/25/07 11:33 AM");
		WebElement dateTimeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		dateTimeLabel.click();
		Thread.sleep(100);
		assertValue("time", "12/25/2007 11:33 AM");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time")); // sometime has element not found error
		dateTime.clear();
		dateTime.sendKeys(" 12/25/08 12:33 AM");
		dateTimeLabel = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__label_time"));
		dateTimeLabel.click();
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time"));
		dateTimeLabel.click();
		Thread.sleep(200); 
		assertValue("time", "12/25/2008 12:33 AM");
		
		changeLanguage("es");
		goModule("ShipmentWithOnChange");
		execute("List.viewDetail", "row=2");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_ShipmentWithOnChange__time"));
		dateTime.clear();
		dateTime.sendKeys(Keys.TAB);
		assertValue("time", "");
		setValue("time","25/12/06 11:33");
		Thread.sleep(100);
		assertValue("time", "25/12/2006 11:33");
		
		List<WebElement> dateTimeInput;
		WebElement dateInput;
		WebElement timeInput;
		goModule("ShipmentSeparatedTime");
		execute("List.viewDetail", "row=0");
		dateTimeInput = getDriver().findElements(By.id("ox_openxavatest_ShipmentSeparatedTime__time"));
		dateInput = dateTimeInput.get(0);
		timeInput = dateTimeInput.get(1);
		selectToday(0);
		dateInput.sendKeys(Keys.TAB);
		assertValue("time", "");
		dateInput.sendKeys("04/10/24");
		dateInput.sendKeys(Keys.ENTER);
		assertValue("time","04/10/2024");
		timeInput.sendKeys("1212");
		timeInput.sendKeys(Keys.TAB);
		assertEquals("04/10/2024", dateInput.getAttribute("value"));
		assertEquals("12:12", timeInput.getAttribute("value"));
		execute("CRUD.save");

		changeLanguage("sr");
		goModule("ShipmentSeparatedTime");
		execute("List.viewDetail", "row=0");
		dateTimeInput = getDriver().findElements(By.id("ox_openxavatest_ShipmentSeparatedTime__time"));
		dateInput = dateTimeInput.get(0);
		timeInput = dateTimeInput.get(1);
		assertEquals("4.10.2024.", dateInput.getAttribute("value"));
		assertEquals("12:12", timeInput.getAttribute("value"));
		timeInput.clear();
		timeInput.sendKeys("1111");
		timeInput.sendKeys(Keys.TAB);
		assertEquals("11:11", timeInput.getAttribute("value"));
		dateInput.clear();
		timeInput.clear();
		execute("CRUD.save");

		changeLanguage("sr");
		goModule("Shipment");
		execute("List.viewDetail", "row=2");
		dateTime = getDriver().findElement(By.id("ox_openxavatest_Shipment__time"));
		setValue("time", "25.12.26. 11:33");
		dateTime.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertValue("time", "25.12.2026. 11:33");
		setValue("time", "25.12.18 05:05");
		dateTime.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertValue("time", "25.12.2018. 05:05");
	}
	
	public void testDate_addSeparators() throws Exception {
		goModule("Quarter");
		WebElement endDate;
		WebElement endDateLabel;
		execute("List.viewDetail", "row=0");
		endDate = getDriver().findElement(By.id("ox_openxavatest_Quarter__endDate"));
		endDateLabel = getDriver().findElement(By.id("ox_openxavatest_Quarter__label_endDate"));
		setValue("endDate", "32402");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "3/24/2002");
		assertValue("initDate", "8/11/2009");
		
		setValue("endDate", "3+402");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "3/+4/2002");
		setValue("endDate", "3442002");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "3/44/2002");
		endDate.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertValue("endDate", "3/44/2002");
		execute("CRUD.save");
		assertMessage("End date in Quarter is not of expected type");
		
		changeLanguage("es");
		goModule("Quarter");
		execute("List.viewDetail", "row=0");
		endDate = getDriver().findElement(By.id("ox_openxavatest_Quarter__endDate"));
		endDateLabel = getDriver().findElement(By.id("ox_openxavatest_Quarter__label_endDate"));
		setValue("endDate", "51106");
		endDate.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertValue("endDate", "05/11/2006");
		setValue("endDate", "051196");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "05/11/1996");
		setValue("endDate", "24022019");
		endDate.sendKeys(Keys.ENTER);
		Thread.sleep(100);
		assertValue("endDate", "24/02/2019");
		setValue("endDate", "7111999");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "07/11/1999");
		assertValue("initDate", "11/08/2009");
		
		if (isWindows7()) setHeadless(false); // In Windows 7 with headless the language is not changed to "en-ZA" maybe because a bug of Chrome version in Windows 7
		changeLanguage("en-ZA");
		goModule("Quarter");
		execute("List.viewDetail", "row=0");
		endDate = getDriver().findElement(By.id("ox_openxavatest_Quarter__endDate"));
		assertValue("endDate", "2010/06/03"); 
		setValue("endDate", "20170617");
		endDate.sendKeys(Keys.TAB);
		Thread.sleep(100);
		assertValue("endDate", "2017/06/17");
		if (isWindows7()) setHeadless(true); 
		
		changeLanguage("sr");
		goModule("Quarter");
		execute("List.viewDetail", "row=0");
		endDateLabel = getDriver().findElement(By.id("ox_openxavatest_Quarter__label_endDate"));
		assertValue("endDate", "3.6.2010.");
		setValue("endDate", "5.2.2017.");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "5.2.2017.");
		setValue("endDate", "5.2.2018");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "5.2.2018.");
		setValue("endDate", "5.2.19.");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "5.2.2019.");
		setValue("endDate", "5.2.17");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "5.2.2017.");
		setValue("endDate", "05022018");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "5.2.2018.");
		setValue("endDate", "050219");
		endDateLabel.click();
		Thread.sleep(100);
		assertValue("endDate", "5.2.2019.");
		
		changeLanguage("zh-CN");
		goModule("Quarter");
		execute("List.viewDetail", "row=0");
		assertValue("endDate","2010/6/3");
	}

	public void testTime_dateTimeSeparatedUseTimeEditor_separatedTimePartOnChange_autoFillColon() throws Exception {
		goModule("Event");
		execute("List.viewDetail", "row=0");
		assertValue("endTime", "1:00 PM");
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-clock-outline"));
		assertTrue(iconElements.size() == 2);
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(0);
		    firstIconElement.click();
		}
		List<WebElement> spanElements = getDriver().findElements(By.cssSelector("span.flatpickr-am-pm"));
		if (!spanElements.isEmpty()) {
		    WebElement firstSpanElement = spanElements.get(0);
		    firstSpanElement.click();
		}
		WebElement label = getDriver().findElement(By.id("ox_openxavatest_Event__label_name"));
		label.click();
		assertValue("endTime", "1:00 AM");
		List<WebElement> createDateTime = getDriver().findElements(By.id("ox_openxavatest_Event__createDate"));
		WebElement timePart = createDateTime.get(1);
		timePart.sendKeys(Keys.TAB);
		List<WebElement> messages = getDriver().findElements(By.cssSelector(".ox-messages .ox-message-box"));
		assertTrue(messages.isEmpty());
		setValue("endTime", "111 PM");
		WebElement endTime = getDriver().findElement(By.id("ox_openxavatest_Event__endTime"));
		endTime.sendKeys(Keys.TAB);
		assertValue("endTime","1:11 PM");
		setValue("endTime", "100 PM");
		endTime.sendKeys(Keys.TAB);
		messages = getDriver().findElements(By.cssSelector(".ox-messages .ox-message-box"));
		assertTrue(messages.isEmpty());
		execute("CRUD.save");
		
		changeLanguage("zh-CN");
		goModule("Event");
		execute("List.viewDetail", "row=0");
		endTime = getDriver().findElement(By.id("ox_openxavatest_Event__endTime"));
		endTime.sendKeys(Keys.TAB);
		assertValue("endTime", "AM1:00");
		openTimeCalendar(0);
		changeAmPm(0);
		assertValue("endTime", "PM1:00");
		execute("CRUD.save"); 
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
			String d = day < 10 ? "0"+ String.valueOf(day) : String.valueOf(day);
			assertEquals(getValue("initDate"), d + "/" + m + "/" + year);
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
		String d = day < 10 ? "0"+ String.valueOf(day) : String.valueOf(day);
		assertEquals(getValue("time"), d + "/" + m + "/" + year + " 00:00");
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
		Thread.sleep(100);
	}
	
	private void selectNextDay(int i) throws InterruptedException {
		//i for more than one calendar in same view, 0 for unique calendar
		openCalendar(i);
		int daySelected = Integer.valueOf(getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day selected']")).get(i).getText());
		selectDate(i, String.valueOf(daySelected+1));
	}
	
	private void selectToday(int i) throws InterruptedException {
		openCalendar(i);
		int daySelected = Integer.valueOf(getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day today']")).get(i).getText());
		selectDate(i, String.valueOf(daySelected));
	}
	
	private void openTimeCalendar(int i) throws InterruptedException {
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-clock-outline"));
		if (!iconElements.isEmpty()) {
		    WebElement timeIcon = iconElements.get(i);
		    timeIcon.click();
		}
		Thread.sleep(500);
	}
	
	private void changeAmPm(int i ) throws InterruptedException {
		List<WebElement> iconElements = getDriver().findElements(By.className("flatpickr-am-pm"));
		if (!iconElements.isEmpty()) {
		    WebElement timeIcon = iconElements.get(i);
		    timeIcon.click();
		}
		Thread.sleep(500);
	}
	
	private boolean isCalendarIconPresent() {
		return getDriver().findElements(By.cssSelector(".ox-date-calendar.xava_date")).size() == 2;
	}
	
	private boolean isWindows7() { 
		return System.getProperty("os.name").toLowerCase().contains("windows 7");
	}

}
