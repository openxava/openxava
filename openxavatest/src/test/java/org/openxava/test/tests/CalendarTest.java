package org.openxava.test.tests;

import java.text.*;
import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;

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
		//prevOnCalendar();
		WebElement prev = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-left"));
		prev.click();
		wait(driver);
		//createInvoice();
		
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat();
		for (int i = 0; i < dates.size(); i++) {
			if (i > 1) {
				nextOnCalendar();
				wait(driver);
			}
			String dateString = dateFormat.format(dates.get(i));
			//para clickear en el evento
			//WebElement day = driver.findElement(By.xpath("//div[@class='fc-daygrid-day-events' and ancestor::td[@data-date='2023-03-01']]"));
			WebElement day = driver.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='" + dateString + "']]"));
			day.click();
			wait(driver);
			
			
			WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
			int invoiceNumber = 10+i;
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
		}

		// String invoiceYear = setInvoice(driver);
//
//		driver.get("http://localhost:8080/openxavatest/m/Delivery");
//		wait(driver);
//
//		moveToCalendarView();
//		selectDayAndHour(driver);

		Thread.sleep(10000);

//        //verificar evento
//        WebElement currentMonthEvent = driver.findElement(By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-today.fc-daygrid-event.fc-daygrid-dot-event"));
//        currentMonthEvent.click();
//        wait(driver);
//        WebElement currentDateElement = driver.findElement(By.id("ox_openxavatest_Delivery__date"));
//        assertEquals(dateValue, currentDateElement.getAttribute("value"));
//        WebElement currentinputInvoiceNumber= driver.findElement(By.id("ox_openxavatest_Delivery__invoice___number"));
//        assertEquals("11", currentinputInvoiceNumber.getAttribute("value"));
//        //WebElement currentbuttonList= driver.findElement(By.id("ox_openxavatest_Delivery__Mode___list"));
//        buttonList.click();
//        wait(driver);
//        //crear evento en mes anterior y posterior, navegar sobre los meses
//        
//        WebElement prev = driver.findElement(By.cssSelector(".fc-prev-button.fc-button.fc-button-primary"));
//        prev.click();
//        wait(driver);

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

	private String setInvoice(WebDriver driver) throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Invoice");
		wait(driver);
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		tabCalendar.click();
		wait(driver);
		WebElement toolbarDay = driver
				.findElement(By.cssSelector(".fc-timeGridDay-button.fc-button.fc-button-primary"));
		toolbarDay.click();
		wait(driver);
		WebElement hourDay = driver
				.findElement(By.cssSelector("td.fc-timegrid-slot.fc-timegrid-slot-lane[data-time='00:00:00']"));
		hourDay.click();
		wait(driver);
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
		inputInvoiceNumber.sendKeys("11");
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
		return result;
	}

	private void checkDate(WebDriver driver) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();
		WebElement dateElement = driver.findElement(By.id("ox_openxavatest_Delivery__date"));
		String dateValue = dateElement.getAttribute("value");
		String dateFormat = Dates.dateFormatForJSCalendar();
		dateFormat = dateFormat.replace("n", "M").replace("m", "MM").replace("d", "dd").replace("j", "d").replace("Y",
				"yyyy");
		DateFormat format = new SimpleDateFormat(dateFormat);
		Date dateFormatted = format.parse(dateValue);
		assertEquals(today, dateFormatted);
	}

	private void createEvent(WebDriver driver) throws Exception {
		WebElement inputInvoiceYear = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___year"));
		inputInvoiceYear.sendKeys(invoiceYear);
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Delivery__invoice___number"));
		inputInvoiceNumber.sendKeys("11");
		wait(driver);
		WebElement inputNumber = driver.findElement(
				By.cssSelector("[id='ox_openxavatest_Delivery__number'][name='ox_openxavatest_Delivery__number']"));
		inputNumber.sendKeys("77");
		WebElement setDeliveryType = driver.findElement(By.id("ox_openxavatest_Delivery__Delivery___setDefaultType"));
		setDeliveryType.click();
		WebElement buttonSave = driver.findElement(By.id("ox_openxavatest_Delivery__CRUD___save"));
		buttonSave.click();
		wait(driver);
		WebElement buttonList = driver.findElement(By.id("ox_openxavatest_Delivery__Mode___list"));
		buttonList.click();
		wait(driver);
	}

	private void moveToCalendarView() throws InterruptedException {
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		WebElement tabCalendarParent = tabCalendar.findElement(By.xpath(".."));
		System.out.println("padre");
		String title = tabCalendarParent.getAttribute("class");
		System.out.println(title);
		if (title != null && title.equals("ox-selected-list-format")) {
			System.out.println("esta en la vista calendar");
			// esta en vista calendar
		} else {
			// no esta en vista calendar
			System.out.println("no esta en la vista calendar");
			tabCalendar.click();
		}
	}

	private void selectDayAndHour(WebDriver driver) {
		try {
			WebElement toolbarDay = driver
					.findElement(By.cssSelector(".fc-timeGridDay-button.fc-button.fc-button-primary"));
			toolbarDay.click();
			wait(driver);
			WebElement hourDay = driver
					.findElement(By.cssSelector("td.fc-timegrid-slot.fc-timegrid-slot-lane[data-time='00:00:00']"));
			hourDay.click();
			wait(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void nextOnCalendar() {
		WebElement next = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-right"));
		next.click();
	}

	private void prevOnCalendar() {
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
			if (i > 1) {
				nextOnCalendar();
				wait(driver);
			}
			String dateString = dateFormat.format(dates.get(i));
			WebElement day = driver
					.findElement(By.xpath("//div[@class='fc-daygrid-day-events' and ancestor::div[@date-date='"
							+ dateString + "']]/parent::div/parent::div"));
			day.click();
			wait(driver);
			
			
			WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
			int invoiceNumber = 10+i;
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
		}

		// EntityManager em = ;
		// Primero, creamos una instancia del objeto que queremos insertar
		/*
		 * for (int i = 0; i < dates.size(); i++) { int customerNumber = 1; BigDecimal
		 * vatPercentage = new BigDecimal(1); int year = dates.get(i).getYear(); int
		 * number = 10 + i; System.out.println("antes de crear query"); // Luego,
		 * creamos el query para hacer el insert // Query query =
		 * XPersistence.getManager().
		 * createQuery("INSERT INTO INVOICE (year, number, customer_number, vatpercentage, date) VALUES (:year, :number, :customerNumber, :vatPercentage, :date)"
		 * ); // query.setParameter("year", year); // query.setParameter("number",
		 * number); // query.setParameter("customerNumber", customerNumber); //
		 * query.setParameter("vatPercentage", vatPercentage); //
		 * query.setParameter("date", dates.get(i)); // int rowsInserted =
		 * query.executeUpdate(); // if (rowsInserted > 0) { //
		 * System.out.println("Se insertó correctamente la factura"); // }
		 * 
		 * 
		 * 
		 * // Obtener la entidad Customer correspondiente al número de cliente 1
		 * Customer customer = XPersistence.getManager().find(Customer.class, 1);
		 * 
		 * // Crear la entidad Invoice Invoice invoice = new Invoice();
		 * invoice.setYear(year); invoice.setNumber(number);
		 * invoice.setCustomer(customer); // Asignar la entidad Customer encontrada
		 * invoice.setVatPercentage(new BigDecimal(i)); invoice.setDate(dates.get(i));
		 * // Persistir la entidad Invoice XPersistence.getManager().persist(invoice);
		 * 
		 * 
		 * 
		 * }
		 */

	}

}
