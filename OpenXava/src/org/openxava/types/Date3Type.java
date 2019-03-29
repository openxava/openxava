package org.openxava.types;

import java.io.*;
import java.sql.*;

import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.type.*;
import org.hibernate.usertype.*;
import org.openxava.util.*;

/**
 * In java a <tt>java.util.Date</tt> and in database 3 columns of 
 * integer type. <p>

 * @author Javier Paniza
 */

public class Date3Type implements CompositeUserType {

	public String[] getPropertyNames() {
		return new String[] { "year", "month", "day" }; 
	}

	public Type[] getPropertyTypes() {
		return new Type[] { IntegerType.INSTANCE, IntegerType.INSTANCE, IntegerType.INSTANCE }; 
		
	}

	public Object getPropertyValue(Object component, int property) throws HibernateException {
		java.util.Date date = (java.util.Date) component;
		switch (property) {
			case 0:
				return Dates.getYear(date);
			case 1:	
				return Dates.getMonth(date);
			case 2:
				return Dates.getYear(date);				
		}
		throw new HibernateException(XavaResources.getString("date3_type_only_3_properties"));
	}

	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		java.util.Date date = (java.util.Date) component;
		int intValue = value == null?0:((Number) value).intValue();
		switch (property) {
			case 0:
				Dates.setYear(date, intValue);
				break;
			case 1:	
				Dates.setMonth(date, intValue);
				break;
			case 2:
				Dates.setYear(date, intValue); 
				break;
		}
		throw new HibernateException(XavaResources.getString("date3_type_only_3_properties"));	
	}

	public Class returnedClass() {
		return java.util.Date.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x==y) return true;
		if (x==null || y==null) return false;
		return !Dates.isDifferentDay((java.util.Date) x, (java.util.Date) y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException { 		
		Number year = (Number) IntegerType.INSTANCE.nullSafeGet( rs, names[0], session, owner);
		Number month = (Number) IntegerType.INSTANCE.nullSafeGet( rs, names[1], session, owner );
		Number day = (Number) IntegerType.INSTANCE.nullSafeGet( rs, names[2], session, owner );		
		
		int iyear = year == null?0:year.intValue();
		int imonth = month == null?0:month.intValue();
		int iday = day == null?0:day.intValue();
		
		return Dates.create(iday, imonth, iyear);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException { 
		java.util.Date d = (java.util.Date) value;
		IntegerType.INSTANCE.nullSafeSet(st, Dates.getYear(d), index, session);
		IntegerType.INSTANCE.nullSafeSet(st, Dates.getMonth(d), index + 1, session);
		IntegerType.INSTANCE.nullSafeSet(st, Dates.getDay(d), index + 2, session);		
	}

	public Object deepCopy(Object value) throws HibernateException {
		java.util.Date d = (java.util.Date) value;
		if (value == null) return null;
		return (java.util.Date) d.clone();
	}

	public boolean isMutable() {
		return true;
	}

	public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException { 
		return (Serializable) deepCopy(value);
	}

	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {  
		return deepCopy(cached);
	}

	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {  		
		return deepCopy(original); 
	}

}

