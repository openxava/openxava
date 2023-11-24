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

	private WebDriver driver;

	public void setUp() throws Exception {
		setHeadless(true);
		driver = createWebDriver();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

	private void nextOnCalendar() throws Exception {
		String s = driver.findElement(By.cssSelector(".fc-toolbar-title")).getText();
		WebElement next = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-right"));
		next.click();
		waitCalendarEvent(driver);
		if (s.equals(driver.findElement(By.cssSelector(".fc-toolbar-title")).getText())) {
			nextOnCalendar();
		}
	}

	private void prevOnCalendar() throws Exception {
		String s = driver.findElement(By.cssSelector(".fc-toolbar-title")).getText();
		WebElement prev = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-left"));
		prev.click();
		waitCalendarEvent(driver);
		if (s.equals(driver.findElement(By.cssSelector(".fc-toolbar-title")).getText())) {
			prevOnCalendar();
		}
	}

	public void testNavigationInDateCalendarAndDateTimeCalendar() throws Exception {
		goModule(driver, "Appointment");
		moveToCalendarView(driver);
		moveToTimeGridWeek(driver);
		moveToListView();
		goModule(driver, "Event");
		moveToCalendarView(driver);
		boolean weekButton = driver.findElements(By.cssSelector("button.fc-timeGridWeek-button")).isEmpty();
		assertTrue(weekButton);
		boolean dayButton = driver.findElements(By.cssSelector("button.fc-timeGridDay-button")).isEmpty();
		assertTrue(dayButton);
		moveToListView();
	}

	public void testFilterPerformance() throws Exception {
		// testing default filter defined by user and month filter defined by calendar
		goModule(driver, "EventWithFilter");
		moveToListView();
		long ini = System.currentTimeMillis();
		moveToCalendarView(driver);
		long takes = System.currentTimeMillis() - ini;
		// with CompositeFilter it takes no more than 1500, without it takes more than
		// 4000
		assertTrue(takes < 3000);
		moveToListView();
	}

	public void testCreateEventPrevCurrentNextMonth_conditionsAndFilter() throws Exception {
		goModule(driver, "Invoice");
		moveToCalendarView(driver);
		prevOnCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = setDates();
		for (int i = 0; i < dates.size(); i++) {
			if (i == 2) {
				nextOnCalendar();
				wait(driver);
			}
			String dateString = dateFormat.format(dates.get(i));
			WebElement day = driver
					.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='"
							+ dateString + "']]"));
			day.click();
			wait(driver);
			createInvoice(i);
		}
		prevOnCalendar();
		verifyPrevInvoiceEvent();
		moveToListView();
		
		//conditions and filter
		goModule(driver, "InvoiceCalendar");
		setConditionValue("12", 1);
		setConditionComparator("=", 1);
		wait(driver);
		moveToCalendarView(driver);
		prevOnCalendar();
		verifyConditionEvents("past", false);
		nextOnCalendar();
		nextOnCalendar();
		verifyConditionEvents("future", true);
		moveToListView();
		clearListCondition();
		for (int i = 0; i < 3; i++) {
			execute("CRUD.deleteRow", "row=0");
		}
	}

	public void testAnyNameAsDateProperty() throws Exception {
		goModule(driver, "UserWithBirthday");
		try {
			execute("Mode.list");
		} catch (NoSuchElementException e) {
		}
		moveToListView();
		moveToCalendarView(driver);
		moveToListView();
	}

	public void testMultipleDatesPropertiesAndFirstDateAsEventStart() throws Exception {
		goModule(driver, "Event");
		moveToCalendarView(driver);
		for (int i = 0; i < 6; i++) {
			createEvents(driver, i);
		}
		moveToListView();
		setConditionValue("TEST", 3);
		setConditionComparator("=", 3);
		for (int i = 0; i < 6; i++) {
			execute("CRUD.deleteRow", "row=0");
		}
		clearListCondition();
	}

	public void testCreateDateWithTimeInWeekAndDailyView_tooltip() throws Exception {
		goModule(driver, "Appointment");
		moveToCalendarView(driver);
		moveToTimeGridWeek(driver);

		WebElement dayTimeCell = driver.findElement(By.cssSelector("tr:nth-child(6) > .fc-timegrid-slot-lane"));
		dayTimeCell.click();
		wait(driver);

		assertTrue(getValue("time").contains("2:30"));
		setValue("description", "A");
		execute("CRUD.save");
		execute("Mode.list");
		waitCalendarEvent(driver);

		// tooltip
		WebElement monthEvent = driver.findElement(
				By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end"));
		Actions builder = new Actions(driver);
		builder.moveToElement(monthEvent).perform();
		Thread.sleep(500);
		WebElement tooltip = driver.findElement(By.cssSelector(".fc-event-tooltip"));
		assertEquals("A", tooltip.getText());

		moveToTimeGridWeek(driver);
		WebElement event = driver.findElement(
				By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end"));
		event.click();
		wait(driver);
		assertTrue(getValue("time").contains("2:30"));
		execute("CRUD.delete");
		execute("Mode.list");
		waitCalendarEvent(driver);
		moveToListView();
		acceptInDialogJS(driver);
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
		waitCalendarEvent(driver);
	}

	private void verifyPrevInvoiceEvent() throws Exception {
		WebElement currentMonthEvent = driver.findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(driver);
		assertEquals("10", getValue("number"));
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		assertEquals(String.valueOf(year), getValue("year"));
		execute("Mode.list");
		waitCalendarEvent(driver);
	}

	private void verifyConditionEvents(String time, boolean isExist) {
		WebElement currentMonthEvent = null;
		try {
			currentMonthEvent = driver.findElement(By.cssSelector(
					"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-" + time
							+ ".fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {
		}
		assert isExist ? currentMonthEvent != null : currentMonthEvent == null;

	}

	private void createEvents(WebDriver driver, int i) throws Exception {
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(dates.get(1));
		WebElement day = driver.findElement(By.xpath("//td[@data-date='" + dateString + "']//a[@class='fc-daygrid-day-number']"));
		day.click();
		wait(driver);
		
		List<WebElement> days = driver.findElements(By.xpath("//td[@data-date='" + dateString + "']//a[@class='fc-daygrid-day-number']"));
		if (days.size() > 0) day.click(); wait(driver);
		//sometimes test not work here
		WebElement startDate = driver.findElement(By.id("ox_openxavatest_Event__startDate"));
		blur(startDate);
		Thread.sleep(500); // sleep needed after blur
		// selecting with pop up calendar in case that browser is not in english
		List<WebElement> iconElements = driver.findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
			WebElement firstIconElement = iconElements.get(1);
			firstIconElement.click();
		}
		List<WebElement> spanElements = driver.findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='2']"));
		if (!spanElements.isEmpty()) {
			WebElement spanElement = spanElements.get(1);
			spanElement.click();
		}
		wait(driver);
		setValue("name", "TEST");
		execute("CRUD.save");
		execute("Mode.list");
		waitCalendarEvent(driver);
		if (i == 0) {
			List<WebElement> events = driver.findElements(
					By.xpath("//div[contains(@class,'fc-daygrid-event-harness') and ancestor::td[@data-date='"
							+ dateString + "']]"));
			assertTrue(!events.isEmpty());
		} else if (i == 5) {
			WebElement linkElement = driver.findElement(By.cssSelector("a.fc-daygrid-more-link.fc-more-link"));
			assertNotNull(linkElement);
		}
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

}
