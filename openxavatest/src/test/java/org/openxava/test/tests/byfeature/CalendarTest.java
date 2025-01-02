package org.openxava.test.tests.byfeature;

import java.text.*;
import java.time.*;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;

/**
 * To test calendar view with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class CalendarTest extends WebDriverTestBase {
	
	public CalendarTest(String testName) {
		super(testName);
	}
	
    public void testCalendar() throws Exception {
    	assertErrorsHandlingCorrectly();
    	assertCreateEventPrevCurrentNextMonth_conditionsAndFilter_dragAndDropDate_dragAndDropLocalDate(); 
    	assertMultipleDatesPropertiesAndSelectDateToShow();
    	assertFilterPerformance();
    	assertCreateDateWithTimeInWeekAndDailyView_tooltip_dragAndDropDateTime_saveStateWhenClickDates();
    	assertAnyNameAsDateProperty();
    	assertNavigationInDateCalendarAndDateTimeCalendar_hiddenPref_prevYear();
    	assertDropDownVisible_DropDownOptionSavePrefDate();
    	assertTabWithBaseCondition();
    	
    }    

	private void nextOnCalendar() throws Exception {
		String s = getDriver().findElement(By.cssSelector(".fc-toolbar-title")).getText();
		WebElement next = getDriver().findElement(By.cssSelector(".fc-icon.fc-icon-chevron-right"));
		next.click();
		waitCalendarEvent(getDriver());
		if (s.equals(getDriver().findElement(By.cssSelector(".fc-toolbar-title")).getText())) {
			nextOnCalendar();
		}
	}

	private void prevOnCalendar() throws Exception {
		String s = getDriver().findElement(By.cssSelector(".fc-toolbar-title")).getText();
		WebElement prev = getDriver().findElement(By.cssSelector(".fc-icon.fc-icon-chevron-left"));
		prev.click();
		waitCalendarEvent(getDriver());
		if (s.equals(getDriver().findElement(By.cssSelector(".fc-toolbar-title")).getText())) {
			prevOnCalendar();
		}
	}
	
	private void prevYearOnCalendar() throws Exception {
		WebElement prevYearButton = getDriver().findElement(By.cssSelector("button.fc-prevYear-button.fc-button.fc-button-primary"));
		prevYearButton.click();
		waitCalendarEvent(getDriver());
	}
	
	private void assertErrorsHandlingCorrectly() throws Exception {
		//DWR errors
    	goModule("InvoiceNonExistentProperty");
		moveToCalendarView(getDriver());
		WebElement errorDiv = getDriver().findElement(By.className("ox-calendar-errors"));
		assertTrue(errorDiv.isDisplayed());
		
		//Calendar.java exception
		goModule("Appointment2");
		setConditionValue("***", 0);
		execute("List.filter");
		moveToCalendarView(getDriver());
		errorDiv = getDriver().findElement(By.className("ox-calendar-errors"));
		assertTrue(errorDiv.isDisplayed());
		moveToListView();
	}

	private void assertNavigationInDateCalendarAndDateTimeCalendar_hiddenPref_prevYear() throws Exception {
		goModule("Appointment");
		moveToCalendarView(getDriver());
		WebElement hiddenInputElement = getDriver().findElement(By.id("xava_calendar_date_preferences"));
		assertEquals("time", hiddenInputElement.getAttribute("value"));
		moveToTimeGridWeek(getDriver());
		goModule("Appointment2");
		moveToCalendarView(getDriver());
		moveToTimeGridWeek(getDriver());
		moveToListView();
		goModule("Invoice");
		moveToCalendarView(getDriver());
		boolean weekButton = getDriver().findElements(By.cssSelector("button.fc-timeGridWeek-button")).isEmpty();
		assertTrue(weekButton);
		boolean dayButton = getDriver().findElements(By.cssSelector("button.fc-timeGridDay-button")).isEmpty();
		assertTrue(dayButton);
		prevYearOnCalendar();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		Date prevYear = calendar.getTime();
		String date = new SimpleDateFormat("yyyy-MM-dd").format(prevYear);
		List<WebElement> datesElement = getDriver().findElements(By.cssSelector("td[data-date='" + date + "']"));
		assertFalse(datesElement.isEmpty());
		moveToListView();
	}
	
	private void assertDropDownVisible_DropDownOptionSavePrefDate() throws Exception {
		goModule("Quarter");
		execute("ListFormat.select", "editor=Calendar");
		
		WebElement option = getDriver().findElement(By.id("xava_calendar_date_preferences"));
		assertEquals("initDate", option.getAttribute("value"));
		WebElement selectElement = getDriver().findElement(By.className("xava_calendar_date_preferences"));
		Select select = new Select(selectElement);
		select.selectByIndex(1);
		
		execute("CRUD.new");
		execute("Mode.list");
		
		selectElement = getDriver().findElement(By.className("xava_calendar_date_preferences"));
		option = getDriver().findElement(By.id("xava_calendar_date_preferences"));
		assertEquals("endDate", option.getAttribute("value"));
		
		getDriver().quit();
		setUp();
		goModule("Quarter");

		option = getDriver().findElement(By.id("xava_calendar_date_preferences"));
		assertEquals("endDate", option.getAttribute("value"));
		selectElement = getDriver().findElement(By.className("xava_calendar_date_preferences"));
		select = new Select(selectElement);
		select.selectByIndex(1);
		execute("ListFormat.select", "editor=List");
		execute("ListFormat.select", "editor=Calendar");
		option = getDriver().findElement(By.id("xava_calendar_date_preferences"));
		assertEquals("initDate", option.getAttribute("value"));
		execute("ListFormat.select", "editor=List");
	}
	
	private void assertTabWithBaseCondition() throws Exception {
		goModule("OrderWithSeller");
		execute("ListFormat.select", "editor=Calendar");
		List<WebElement> elements = getDriver().findElements(By.cssSelector(".fc-event-today"));
		assertTrue(elements.isEmpty());
		execute("ListFormat.select", "editor=List");
		execute("CRUD.new");
		setValue("customer.number", "1");
		execute("CRUD.save");
		execute("Mode.list");
		execute("ListFormat.select", "editor=Calendar");
		elements = getDriver().findElements(By.cssSelector(".fc-event-today"));
		assertFalse(elements.isEmpty());
		execute("ListFormat.select", "editor=List");
		execute("CRUD.new");
		execute("CRUD.refresh");
		execute("CRUD.delete");
	}

	private void assertFilterPerformance() throws Exception {
		// testing default filter defined by user and month filter defined by calendar
		goModule("EventWithFilter");
		moveToListView();
		long ini = System.currentTimeMillis();
		moveToCalendarView(getDriver());
		long takes = System.currentTimeMillis() - ini;
		// with CompositeFilter it takes no more than 1500, without it takes more than
		// 4000
		assertTrue(takes < 3000);
		moveToListView();
	}
	
	private void assertCreateEventPrevCurrentNextMonth_conditionsAndFilter_dragAndDropDate_dragAndDropLocalDate() throws Exception {
		goModule("Invoice");
		moveToCalendarView(getDriver());
		prevOnCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = setDates();
		for (int i = 0; i < dates.size(); i++) {
			if (i > 0) {
				nextOnCalendar();
				wait(getDriver());
			}
			String dateString = dateFormat.format(dates.get(i));
			WebElement day = getDriver()
					.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='"
							+ dateString + "']]"));
			day.click();
			wait(getDriver());
			createInvoice(i);
		}
		prevOnCalendar();
		prevOnCalendar();
		verifyPrevInvoiceEvent();
		moveToListView();
		
		//conditions and filter
		goModule("InvoiceCalendar");
		setConditionValue("12", 1);
		setConditionComparator("=", 1);
		wait(getDriver());
		moveToCalendarView(getDriver());
		prevOnCalendar();
		verifyConditionEvents("past", false);
		nextOnCalendar();
		nextOnCalendar();
		verifyConditionEvents("future", true);

		dragAndDrop(dates.get(2), dateFormat);
		
		moveToListView();
		clearListCondition();
		for (int i = 0; i < 3; i++) {
			execute("CRUD.deleteRow", "row=0");
		}
		Thread.sleep(300);
		
		//drag and drop localdate
		goModule("UserWithBirthday");
		execute("Mode.list");
		moveToCalendarView(getDriver());
		WebElement firstDayElement = getDriver().findElement(By.cssSelector(".fc-daygrid-day"));
		firstDayElement.click();
		wait(getDriver());
		setValue("userName", "Pedro");
		execute("CRUD.save");
		execute("Mode.list");
		WebElement firstDayEvent = getDriver().findElement(By.cssSelector(".fc-event"));
		List<WebElement> dayElements = getDriver().findElements(By.cssSelector(".fc-daygrid-day"));
		WebElement secondDayElement = dayElements.get(1);
		Actions actions = new Actions(getDriver());
		actions.dragAndDrop(firstDayEvent, secondDayElement).build().perform();
		Thread.sleep(300);
		getDriver().navigate().refresh();
		wait(getDriver());
		waitCalendarEvent(getDriver());
		
		dayElements = getDriver().findElements(By.cssSelector(".fc-daygrid-day"));
		secondDayElement = dayElements.get(1);
		WebElement foundEvent = secondDayElement.findElement(By.cssSelector(".fc-event"));
		
		moveToListView();
		execute("List.viewDetail", "row=0");
		execute("CRUD.delete");
	}

	private void assertAnyNameAsDateProperty() throws Exception {
		goModule("UserWithBirthday");
		try {
			execute("Mode.list");
		} catch (NoSuchElementException e) {
		}
		moveToListView();
		moveToCalendarView(getDriver());
		moveToListView();
	}

	private void assertMultipleDatesPropertiesAndSelectDateToShow() throws Exception {
		goModule("Event");
		moveToCalendarView(getDriver());
		for (int i = 0; i < 6; i++) {
			createEvents(i);
		}
		moveToListView();
		setConditionValue("TEST", 5);
		setConditionComparator("=", 5);
		for (int i = 0; i < 5; i++) {
			Thread.sleep(3000);
			execute("CRUD.deleteRow", "row=0");
		}
		moveToCalendarView(getDriver());
		verifyShowDatesOfPreferDateProperty();
		moveToListView();
		setConditionValue("TEST", 5);
		setConditionComparator("=", 5);
		execute("CRUD.deleteRow", "row=0");
		clearListCondition();
	}

	private void assertCreateDateWithTimeInWeekAndDailyView_tooltip_dragAndDropDateTime_saveStateWhenClickDates() throws Exception {
		goModule("Appointment");
		moveToCalendarView(getDriver());
		moveToTimeGridWeek(getDriver());

	    while (true) {
	        String prevUrl = getDriver().getCurrentUrl();
	        WebElement dayTimeCell = getDriver().findElement(By.cssSelector("tr:nth-child(6) > .fc-timegrid-slot-lane"));
	        dayTimeCell.click();
	        wait(getDriver());

	        if (!prevUrl.equals(getDriver().getCurrentUrl())) {
	            break;
	        } else {
	            refreshCalendarView(getDriver());
	            moveToTimeGridWeek(getDriver());
	        }
	    }
		
		WebElement dateTime = getDriver().findElement(By.id("ox_openxavatest_Appointment__time"));
		String dateTimeInput = dateTime.getAttribute("value");
		assertTrue(dateTimeInput.contains("2:30"));
		setValue("description", "A");
		execute("CRUD.save");
		execute("Mode.list");
		waitCalendarEvent(getDriver());

		// tooltip
		refreshCalendarView(getDriver());
		WebElement event = getDriver().findElement(By.cssSelector(".fc-event-time"));
		verifyTooltipText(event, "A");
		WebElement nextYearButton = getDriver().findElement(By.className("fc-nextYear-button"));
		verifyTooltipText(nextYearButton, "Next year");

		moveToTimeGridWeek(getDriver());
		
		event = getDriver().findElement(By.cssSelector(".fc-event-time"));
		event.click();
		wait(getDriver());
		execute("Mode.list");
		
		//save calendar state when click dates
		moveToDayGridMonth(getDriver());
		
		//drag and drop
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dragAndDrop(calendar.getTime(), dateFormat);
		event = getDriver().findElement(By.cssSelector(".fc-event-time"));
		assertTrue(event.getText().contains("2:30"));
		event.click();
		wait(getDriver());
		
		execute("CRUD.delete");
		execute("Mode.list");
		waitCalendarEvent(getDriver());
		moveToListView();
		acceptInDialogJS(getDriver());
	}

	private List<Date> setDates() {
		List<Date> dates = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date currentMonth = calendar.getTime();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date previousMonth = calendar.getTime();
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = calendar.getTime();
		dates.add(previousMonth);
		dates.add(currentMonth);
		dates.add(nextMonth);

		return dates;
	}

	private void createInvoice(int invoiceNumber) throws Exception {
		setValue("number", String.valueOf(10 + invoiceNumber));
		setValue("customer.number", "1");
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "3");
		execute("CRUD.save");
		execute("Mode.list");
		waitCalendarEvent(getDriver());
	}

	private void verifyPrevInvoiceEvent() throws Exception {
		WebElement currentMonthEvent = getDriver().findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(getDriver());
		assertEquals("10", getValue("number"));
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		assertEquals(String.valueOf(year), getValue("year"));
		execute("Mode.list");
		waitCalendarEvent(getDriver());
	}

	private void verifyConditionEvents(String time, boolean isExist) {
		WebElement currentMonthEvent = null;
		try {
			currentMonthEvent = getDriver().findElement(By.cssSelector(
					"a.fc-event.fc-event-draggable.fc-event-start.fc-event-end.fc-event-" + time
							+ ".fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {
		}
		assert isExist ? currentMonthEvent != null : currentMonthEvent == null;

	}

	private void createEvents(int i) throws Exception {
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(dates.get(1));
		WebElement day;
		day = getDriver().findElement(By.xpath("//td[@data-date='" + dateString + "']//a[@class='fc-daygrid-day-number']"));
		day.click();
		wait(getDriver());
		Thread.sleep(500);
		List<WebElement> days = getDriver().findElements(By.xpath("//td[@data-date='" + dateString + "']//a[@class='fc-daygrid-day-number']"));
		if (days.size() > 0) {
			//sometimes test not work here
			refreshCalendarView(getDriver());
			day = getDriver().findElement(By.xpath("//td[@data-date='" + dateString + "']//a[@class='fc-daygrid-day-number']"));
			day.click();
			wait(getDriver());
		}
		
		WebElement startDate = getDriver().findElement(By.id("ox_openxavatest_Event__startDate"));
		blur(startDate);
		Thread.sleep(500); // needed after blur
		// selecting with pop up calendar in case that browser is not in english
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
			WebElement firstIconElement = iconElements.get(1);
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", firstIconElement);
		}
		List<WebElement> spanElements = getDriver().findElements(By.xpath("//div[@class='dayContainer']//span[contains(@class, 'flatpickr-day') and not(contains(@class, 'nextMonthDay')) and text()='2']"));
		if (!spanElements.isEmpty()) {
			WebElement spanElement = spanElements.get(1);
			spanElement.click();
		}
		wait(getDriver());
		setValue("name", "TEST");
		execute("CRUD.save");
		execute("Mode.list");
		waitCalendarEvent(getDriver());
		if (i == 0) {
			refreshCalendarView(getDriver());
			WebElement aElement = getDriver().findElement(By.xpath("//td[@data-date='" + dateString + "']//div[contains(@class,'fc-event-title')]"));
			assertEquals("TEST", aElement.getText());
		} else if (i == 5) {
			WebElement linkElement = getDriver().findElement(By.cssSelector("a.fc-daygrid-more-link.fc-more-link"));
			assertNotNull(linkElement);
		}
	}
	
	private void dragAndDrop(Date date, SimpleDateFormat format) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		} else {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		date = calendar.getTime();
		WebElement dragItem = getDriver().findElement(By.cssSelector(".fc-event.fc-event-draggable"));
		WebElement dropCell = getDriver().findElement(By.cssSelector("td[data-date='" + format.format(date) + "']"));
		Actions actions = new Actions(getDriver());
		actions.dragAndDrop(dragItem, dropCell).build().perform();
		Thread.sleep(300);
		getDriver().navigate().refresh();
		wait(getDriver());
		waitCalendarEvent(getDriver());
		
		dragItem = getDriver().findElement(By.cssSelector(".fc-event.fc-event-draggable"));
		dropCell = getDriver().findElement(By.cssSelector("td[data-date='" + format.format(date) + "']"));
		dropCell.findElement(By.xpath(".//descendant::*[contains(@class, '" + dragItem.getAttribute("class") + "')]"));
	}

	private void waitCalendarEvent(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000));
		try {
			wait.until(ExpectedConditions.jsReturnsValue("return calendarEditor.requesting === false;"));
			Thread.sleep(500);
		} catch (Exception ex) {
		}
	}

	private void moveToCalendarView(WebDriver driver) throws Exception {
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		WebElement tabCalendarParent = tabCalendar.findElement(By.xpath(".."));
		String title = tabCalendarParent.getAttribute("class");
		if (!(title != null && title.contains("ox-selected-list-format"))) {
			tabCalendar.click();
		}
		
		//sometimes errors occur in selenium when changing views, 
		//so we handle the alert and load the view again
		boolean b = false;
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			alert.dismiss();
			wait(driver);
			b = true;
		} catch(NoAlertPresentException e) {
		}
		if (b) {
			moveToListView();
			moveToCalendarView(driver);
		}
		waitCalendarEvent(driver);
		wait(driver);
	}

	private void moveToTimeGridWeek(WebDriver driver) throws Exception {
		WebElement weekButton = driver.findElement(By.cssSelector("button.fc-timeGridWeek-button"));
		weekButton.click();
		waitCalendarEvent(driver);
	}
	
	private void moveToDayGridMonth(WebDriver driver) throws Exception {
		WebElement monthButton = driver.findElement(By.cssSelector("button.fc-dayGridMonth-button"));
		monthButton.click();
		waitCalendarEvent(driver);
	}

	private void refreshCalendarView(WebDriver driver) throws Exception {
		//sometimes need when changing view so fast
		driver.navigate().refresh();
		acceptInDialogJS(driver);
		waitCalendarEvent(driver);
	}
	
	private void verifyShowDatesOfPreferDateProperty() throws Exception {
		WebElement selectElement = getDriver().findElement(By.className("xava_calendar_date_preferences"));
		String selectedOption = selectElement.getAttribute("value");
		assertEquals("startDate",selectedOption);
		List<String> dates = getFirstThreeDaysOfMonth();
		verifyDateIsDisplayed(dates.get(0), "TEST");
		Select select = new Select(selectElement);
		select.selectByIndex(1);
		waitCalendarEvent(getDriver());
		verifyDateIsDisplayed(dates.get(1), "TEST");
		resetModule(getDriver());
		waitCalendarEvent(getDriver());
		verifyDateIsDisplayed(dates.get(1), "TEST");
		selectElement = getDriver().findElement(By.className("xava_calendar_date_preferences"));
		select = new Select(selectElement);
		select.selectByIndex(1);
	}
	
	private List<String> getFirstThreeDaysOfMonth() {
		List<String> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date dayOne = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, 2);
		Date dayTwo = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		result.add(dateFormat.format(dayOne));
		result.add(dateFormat.format(dayTwo));
		return result;
	}
	
	private void verifyDateIsDisplayed(String date, String expectedText) {
	    WebElement dateElement = getDriver().findElement(By.cssSelector("td[data-date='" + date + "']"));
	    WebElement titleElement = dateElement.findElement(By.cssSelector(".fc-event-title-container"));
	    String text = titleElement.getText();
	    assertEquals(expectedText, text);
	}
	
	private void verifyTooltipText(WebElement element, String expectedText) throws InterruptedException {
	    Actions builder = new Actions(getDriver());
	    builder.moveToElement(element).perform();
	    Thread.sleep(500);
	    WebElement tooltip = getDriver().findElement(By.cssSelector(".fc-event-tooltip"));
	    assertEquals(expectedText, tooltip.getText());
	}
	
}
