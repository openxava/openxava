package org.openxava.types;

import java.io.*;
import org.hibernate.*;
import org.hibernate.metamodel.spi.*;
import org.hibernate.usertype.*;
import org.openxava.util.*;
import jakarta.persistence.Embeddable;

/**
 * In java a <tt>java.util.Date</tt> and in database 3 columns of 
 * integer type. <p>
 * 
 * @author Javier Paniza
 */

public class Date3Type implements CompositeUserType<java.util.Date> {

	@Embeddable
	public static class Date3Component implements Serializable {
		private int day;
		private int month;
		private int year;
		public int getDay() { return day; }
		public void setDay(int day) { this.day = day; }
		public int getMonth() { return month; }
		public void setMonth(int month) { this.month = month; }
		public int getYear() { return year; }
		public void setYear(int year) { this.year = year; }
	}

	@Override
	public Object getPropertyValue(java.util.Date component, int property) throws HibernateException {
		if (component == null) return null;
		switch (property) {
			case 0:
				return Dates.getDay(component);
			case 1:
				return Dates.getMonth(component);
			case 2:
				return Dates.getYear(component);
		}
		throw new HibernateException(XavaResources.getString("date3_type_only_3_properties"));
	}

	@Override
	public java.util.Date instantiate(ValueAccess values) {
		Integer day = values.getValue(0, Integer.class);
		Integer month = values.getValue(1, Integer.class);
		Integer year = values.getValue(2, Integer.class);
		if (day == null || month == null || year == null) return null;
		return Dates.create(day, month, year);
	}

	@Override
	public Class<?> embeddable() {
		return Date3Component.class;
	}

	@Override
	public Class<java.util.Date> returnedClass() {
		return java.util.Date.class;
	}

	@Override
	public boolean equals(java.util.Date x, java.util.Date y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return !Dates.isDifferentDay(x, y);
	}

	@Override
	public int hashCode(java.util.Date x) {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public java.util.Date deepCopy(java.util.Date value) {
		if (value == null) return null;
		return (java.util.Date) value.clone();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(java.util.Date value) {
		return (Serializable) deepCopy(value);
	}

	@Override
	public java.util.Date assemble(Serializable cached, Object owner) {
		return deepCopy((java.util.Date) cached);
	}

	@Override
	public java.util.Date replace(java.util.Date detached, java.util.Date managed, Object owner) {
		return deepCopy(detached);
	}

}

