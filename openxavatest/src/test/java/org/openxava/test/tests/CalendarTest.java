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
	private String invoiceYear;

	public void setUp() throws Exception {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
		driver = new ChromeDriver(options);
	}

	public void testNavegacion() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Invoice");
		wait(driver);
		moveToCalendarView();
		wait(driver);
		Thread.sleep(8000);
		prevOnCalendar();
		wait(driver);
		Thread.sleep(8000);
		createInvoice();

		driver.get("http://localhost:8080/openxavatest/m/Delivery");
		wait(driver);
		moveToCalendarView();
 		wait(driver);
		Thread.sleep(8000);
		prevOnCalendar();
		wait(driver);
		createDelivery();

		prevOnCalendar();
		wait(driver);
		Thread.sleep(8000);
		verifyDeliveryEvent();
		createAndVerifyDeliveryOnCurrentMonth_weekView_dayView();
		
		moveToListView();
		wait(driver);
		//Thread.sleep(5000);
		setCondition();
		wait(driver);
		Thread.sleep(3000);
		moveToCalendarView();
		wait(driver);
		Thread.sleep(8000);
		verifyConditionEvents();

		
		
	}

	public void tearDown() throws Exception {
		// Cerrar el navegador
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
		// System.out.println("padre");
		String title = tabCalendarParent.getAttribute("class");
		// System.out.println(title);
		if (title != null && title.equals("ox-selected-list-format")) {
			System.out.println("esta en la vista calendar");
			// esta en vista calendar
		} else {
			// no esta en vista calendar
			System.out.println("no esta en la vista calendar");
			tabCalendar.click();
		}
	}
	
	private void moveToListView() throws InterruptedException {
		WebElement tabList = driver.findElement(By.cssSelector(".mdi.mdi-table-large"));
		WebElement tabListParent = tabList.findElement(By.xpath(".."));
		String title = tabListParent.getAttribute("class");
		if (title != null && title.equals("ox-selected-list-format")) {
			System.out.println("esta en la vista lista");
			// esta en vista calendar
		} else {
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
		// Primer día del mes actual
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date currentMonth = calendar.getTime();
		// Primer día del mes anterior
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date previousMonth = calendar.getTime();
		// Primer día del mes siguiente
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = calendar.getTime();
		dates.add(previousMonth);
		dates.add(currentMonth);
		dates.add(nextMonth);
		System.out.println(dates);
		return dates;
	}

	private void createInvoice() throws Exception {
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat();
		for (int i = 0; i < dates.size(); i++) {
			System.out.println(i);
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

			WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
			int invoiceNumber = (10 + i);
			System.out.println(invoiceNumber);
			inputInvoiceNumber.sendKeys(String.valueOf(invoiceNumber));
			WebElement inputInvoiceYear = driver.findElement(By.id("ox_openxavatest_Invoice__year"));
			String result = inputInvoiceYear.getAttribute("value");
			WebElement inputCustomerNumber = driver.findElement(By.id("ox_openxavatest_Invoice__customer___number"));
			inputCustomerNumber.sendKeys("1");
			WebElement section2Child = driver
					.findElement(By.id("ox_openxavatest_Invoice__label_xava_view_section2_sectionName"));
			WebElement section2Parent = section2Child.findElement(By.xpath(".."));
			section2Parent.click();
			wait(driver);
			WebElement inputVAT = driver.findElement(By.id("ox_openxavatest_Invoice__vatPercentage"));
			inputVAT.sendKeys("3");
			WebElement buttonSave = driver.findElement(By.id("ox_openxavatest_Invoice__CRUD___save"));
			buttonSave.click();
			wait(driver);
			WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Invoice__Mode___list"));
			buttonList.click();
			wait(driver);
			Thread.sleep(8000);
		}
	}

	private void createDelivery() throws Exception {
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat();
		for (int i = 0; i < dates.size(); i++) {
			System.out.println(i);
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

			WebElement inputInvoiceYear = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___year"));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dates.get(i));
			inputInvoiceYear.sendKeys(String.valueOf(calendar.get(Calendar.YEAR)));
			WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___number"));
			int invoiceNumber = (10 + i);
			inputInvoiceNumber.sendKeys(String.valueOf(invoiceNumber));
			wait(driver);
			WebElement inputNumber = driver.findElement(
					By.cssSelector("[id='ox_openxavatest_Delivery__number'][name='ox_openxavatest_Delivery__number']"));
			int deliveryNumber = (70 + i);
			inputNumber.sendKeys(String.valueOf(deliveryNumber));
			WebElement setDeliveryType = driver
					.findElement(By.id("ox_openxavatest_Delivery__Delivery___setDefaultType"));
			setDeliveryType.click();
			WebElement buttonSave = driver.findElement(By.id("ox_openxavatest_Delivery__CRUD___save"));
			buttonSave.click();
			wait(driver);
			WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Delivery__Mode___list"));
			buttonList.click();
			wait(driver);
			Thread.sleep(8000);
		}
	}

	private void verifyDeliveryEvent() throws Exception {
		WebElement currentMonthEvent = driver.findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(driver);
		WebElement deliveryNumber = driver.findElement(By.id("ox_openxavatest_Delivery__number"));
		assertEquals("70", deliveryNumber.getAttribute("value"));
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___number"));
		assertEquals("10", inputInvoiceNumber.getAttribute("value"));
		WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Delivery__Mode___list"));
		buttonList.click();
		wait(driver);
		Thread.sleep(8000);
	}
	
	private void createAndVerifyDeliveryOnCurrentMonth_weekView_dayView() throws Exception {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(calendar.getTime());
		
		WebElement toolbarDay = driver
				.findElement(By.cssSelector(".fc-timeGridDay-button.fc-button.fc-button-primary"));
		toolbarDay.click();
		wait(driver);
		WebElement hourDay = driver
				.findElement(By.cssSelector("td.fc-timegrid-slot.fc-timegrid-slot-lane[data-time='00:00:00']"));
		hourDay.click();
		wait(driver);

		WebElement inputInvoiceYear = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___year"));
		inputInvoiceYear.sendKeys(String.valueOf(calendar.get(Calendar.YEAR)));
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___number"));
		int invoiceNumber = (11);
		inputInvoiceNumber.sendKeys(String.valueOf(invoiceNumber));
		wait(driver);
		WebElement inputNumber = driver.findElement(By.id("ox_openxavatest_Delivery__number"));
		int deliveryNumber = (75);
		inputNumber.sendKeys(String.valueOf(deliveryNumber));
		WebElement setDeliveryType = driver
				.findElement(By.id("ox_openxavatest_Delivery__Delivery___setDefaultType"));
		setDeliveryType.click();
		WebElement buttonSave = driver.findElement(By.id("ox_openxavatest_Delivery__CRUD___save"));
		buttonSave.click();
		wait(driver);
		WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Delivery__Mode___list"));
		buttonList.click();
		wait(driver);
		Thread.sleep(8000);
		
		WebElement currentMonthEvent = driver.findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-today.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(driver);
		inputNumber = driver.findElement(By.id("ox_openxavatest_Delivery__number"));
		inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___number"));
		assertEquals("75", inputNumber.getAttribute("value"));
		assertEquals("11", inputInvoiceNumber.getAttribute("value"));
		buttonList = driver.findElement(By.id("ox_openxavatest_Delivery__Mode___list"));
		buttonList.click();
		wait(driver);
		Thread.sleep(8000);
		
	}
	
	private void setCondition() throws InterruptedException {
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Delivery__conditionValue___1"));
		inputInvoiceNumber.sendKeys("12");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].focus();", inputInvoiceNumber);
		WebElement selectInvoiceNumberCondition = driver.findElement(By.id("ox_openxavatest_Delivery__conditionComparator___1"));
		Select select = new Select(selectInvoiceNumberCondition);
		select.selectByVisibleText("=");
		WebElement filterAction = driver.findElement(By.id("ox_openxavatest_Delivery__List___filter"));
		filterAction.click();
	}
	
	private void verifyConditionEvents() {
		WebElement currentMonthEvent = null;
		try {
		    currentMonthEvent = driver.findElement(By.cssSelector(
		            "a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {}

		assertNull(currentMonthEvent);
	}
	
}
