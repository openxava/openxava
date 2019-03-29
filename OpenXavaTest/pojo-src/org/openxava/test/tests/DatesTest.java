package org.openxava.test.tests;

import java.util.*;
import org.openxava.util.*;
import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class DatesTest extends TestCase {
	
	private Date date;
		
	public DatesTest(String name) {
		super(name);
	}
	
	
	protected void setUp() {
		date = Dates.create(8, 10, 2013);
	}
		
	public void testCreate() {		
		assertTrue(date.toString().startsWith("Tue Oct 08 00:00:00"));
		assertTrue(date.toString().endsWith("2013"));
	}
	
	public void testSetGetDay() {
		Dates.setDay(date, 10);
		assertTrue(date.toString().startsWith("Thu Oct 10"));
		assertTrue(date.toString().endsWith("2013"));
		assertEquals(10, Dates.getDay(date));
	}
	
	public void testSetGetMonth() {
		Dates.setMonth(date, 11);
		assertTrue(date.toString().startsWith("Fri Nov 08"));
		assertTrue(date.toString().endsWith("2013"));
		assertEquals(11, Dates.getMonth(date));
	}
	
	public void testSetGetYear() {
		Dates.setYear(date, 2014);		
		assertTrue(date.toString().startsWith("Wed Oct 08"));
		assertTrue(date.toString().endsWith("2014"));
		assertEquals(2014, Dates.getYear(date));
	}
	
	public void testAddDays() {
		Dates.addDays(date, 3);		
		assertTrue(date.toString().startsWith("Fri Oct 11"));
		assertTrue(date.toString().endsWith("2013"));
	}
	
	public void testLastDayOfYear() {
		date = Dates.lastOfYear(date);
		assertTrue(date.toString().startsWith("Tue Dec 31"));
		assertTrue(date.toString().endsWith("2013"));
	}
	
	public void testLastOfMonth() {
		date = Dates.lastOfMonth(date);
		assertTrue(date.toString().startsWith("Thu Oct 31"));
		assertTrue(date.toString().endsWith("2013"));
	}

	public void testFirstOfMonth() {
		date = Dates.firstOfMonth(date);
		assertTrue(date.toString().startsWith("Tue Oct 01"));
		assertTrue(date.toString().endsWith("2013"));
	}
	
	public void testDateTimeFormatForJSCalendar() throws Exception { 
		assertEquals("%m/%e/%y %l:%M %p", Dates.dateTimeFormatForJSCalendar(Locale.US)); // The Java 8 format for all Java versions		
	}

}
