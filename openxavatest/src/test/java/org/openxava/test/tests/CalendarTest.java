package org.openxava.test.tests;

import java.text.*;
import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import junit.framework.*;

public class CalendarTest extends TestCase {

	private WebDriver driver;

	public void setUp() throws Exception {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
		driver = new ChromeDriver(options);
	}

	// We use Thread.sleep(8000) because in dwr.Calendar it was set to 5000
	// to simulate the cost of fetching many records,
	// for the stable version, the sleep in dwr.Calendar will be removed
	// but still need until calculated properties in tab was fixed
	public void testNavegacion() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Order");
		wait(driver);
		moveToCalendarView();
		wait(driver);
		Thread.sleep(8000);
		prevOnCalendar();
		wait(driver);
		Thread.sleep(8000);
		createOrderEventPrevCurrentNextMonth();
		prevOnCalendar();
		wait(driver);
		Thread.sleep(8000);
		verifyPrevOrderEvent();
		
		driver.get("http://localhost:8080/openxavatest/m/OrderCalendar");
		wait(driver);
		moveToListView();
		wait(driver);
		setOrderCondition("OrderCalendar");
		wait(driver);
		Thread.sleep(8000);
		moveToCalendarView();
		wait(driver);
		Thread.sleep(8000);
		prevOnCalendar();
		wait(driver);
		Thread.sleep(8000);
		verifyConditionEvents("past", false);
		nextOnCalendar();
		Thread.sleep(8000);
		verifyConditionEvents("future", true);
		moveToListView();
		wait(driver);
		deteleEvents();	
	}

	public void tearDown() throws Exception {

		driver.quit();
	}

	private void wait(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("xava_loading")));
		} catch (Exception ex) {
		}
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("xava_loading")));
	}

	private void moveToCalendarView() throws InterruptedException {
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		WebElement tabCalendarParent = tabCalendar.findElement(By.xpath(".."));
		String title = tabCalendarParent.getAttribute("class");
		if (!(title != null && title.equals("ox-selected-list-format"))) {
			tabCalendar.click();
		}
	}
	
	private void moveToListView() throws InterruptedException {
		WebElement tabList = driver.findElement(By.cssSelector(".mdi.mdi-table-large"));
		WebElement tabListParent = tabList.findElement(By.xpath(".."));
		String title = tabListParent.getAttribute("class");
		if (!(title != null && title.equals("ox-selected-list-format"))) {
			tabList.click();
		}	
	}

	private void nextOnCalendar() throws InterruptedException {
		WebElement next = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-right"));
		next.click();
	}

	private void prevOnCalendar() throws InterruptedException {
		WebElement prev = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-left"));
		prev.click();
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

	private void createOrderEventPrevCurrentNextMonth() throws Exception {
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat();
		for (int i = 0; i < dates.size(); i++) {
			if (i == 2) {
				nextOnCalendar();
				Thread.sleep(8000);
				wait(driver);
			}
			String dateString = dateFormat.format(dates.get(i));
			WebElement day = driver
					.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='"
							+ dateString + "']]"));
			day.click();
			wait(driver);
			createOrder();
		}
	}
	
	private void createOrder() throws Exception {
		WebElement inputCustomerNumber = driver.findElement(By.id("ox_openxavatest_Order__customer___number"));
		inputCustomerNumber.sendKeys("1");
		WebElement buttonSave = driver.findElement(By.id("ox_openxavatest_Order__CRUD___save"));
		buttonSave.click();
		wait(driver);
		WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Order__Mode___list"));
		buttonList.click();
		wait(driver);
		Thread.sleep(8000);
	}

	private void verifyPrevOrderEvent() throws Exception {
		WebElement currentMonthEvent = driver.findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(driver);
		WebElement orderNumber = driver.findElement(By.id("ox_openxavatest_Order__number"));
		assertEquals("1", orderNumber.getAttribute("value"));
		WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Order__Mode___list"));
		buttonList.click();
		wait(driver);
		Thread.sleep(8000);
	}
	
	private void setOrderCondition(String module) throws InterruptedException {
		WebElement inputOrderNumber = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___1"));
		inputOrderNumber.sendKeys("3");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].focus();", inputOrderNumber);
		WebElement selectOrderNumberCondition = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionComparator___1"));
		Select select = new Select(selectOrderNumberCondition);
		select.selectByVisibleText("=");
		WebElement filterAction = driver.findElement(By.id("ox_openxavatest_" + module + "__List___filter"));
		filterAction.click();
	}
	
	private void verifyConditionEvents(String time, boolean isExist) {
		WebElement currentMonthEvent = null;
		try {
		    currentMonthEvent = driver.findElement(By.cssSelector(
		            "a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-" + time + ".fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {}
		assert isExist ? currentMonthEvent != null : currentMonthEvent == null;

	}
	
	private void deteleEvents() throws Exception {
		WebElement clearFilterAction = driver.findElement(By.cssSelector("td.ox-list-subheader a:has(i.mdi.mdi-eraser)"));
		clearFilterAction.click();
		wait(driver);
		for (int i = 0; i < 3; i ++) {
			WebElement element = driver.findElement(By.xpath("//a[contains(@class, 'ox-image-link') and .//i[contains(@class, 'mdi-delete')]]"));
			element.click();
			Alert alert = driver.switchTo().alert();
			alert.accept();
			wait(driver);
		}
	}
	
}
