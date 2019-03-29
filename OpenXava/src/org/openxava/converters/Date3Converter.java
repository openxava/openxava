package org.openxava.converters;

import java.util.*;



import org.openxava.util.*;


/**
 * In java a <tt>java.util.Date</tt> and in database 3 columns of 
 * integer type. <p>
 * 
 * @author Javier Paniza
 */
public class Date3Converter implements IMultipleConverter {
	
	private int day;
	private int month;
	private int year;
	
	public Object toJava() throws ConversionException {		
		return Dates.create(day, month, year);
	}

	public void toDB(Object objetoJava) throws ConversionException {
		if (objetoJava == null) {
			setDay(0);
			setMonth(0);
			setYear(0);
			return; 					
		}
		if (!(objetoJava instanceof java.util.Date)) {
			throw new ConversionException("conversion_db_utildate_expected");
		}
		java.util.Date fecha = (java.util.Date) objetoJava;
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		setDay(cal.get(Calendar.DAY_OF_MONTH));
		setMonth(cal.get(Calendar.MONTH) + 1);
		setYear(cal.get(Calendar.YEAR)); 		
	}

	public int getYear() {
		return year;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public void setYear(int i) {
		year = i;
	}

	public void setDay(int i) {
		day = i;
	}

	public void setMonth(int i) {
		month = i;
	}

}
