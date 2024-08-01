package org.openxava.types;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.time.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.*;

/**
 * Allows LocalDate work with numeric date values in format yyyymmdd from database. <p>
 * 
 * @author Miguel Garbín
 */
public class DateNumericType implements UserType {
	
    private static Log log = LogFactory.getLog(DateNumericType.class);

    public int[] sqlTypes() {       
        return new int[] { Types.NUMERIC };
    }

    public Class returnedClass() {
        return LocalDate.class;
    }

    public boolean equals(Object obj1, Object obj2) throws HibernateException {
        if (obj1 == null) return obj2 == null;
        return obj1.equals(obj2);
    }

    public int hashCode(Object obj) throws HibernateException {
        return obj.hashCode();
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor sessionImplementor, Object owner) throws HibernateException, SQLException { 
        Object o = resultSet.getObject(names[0]);
        if (o==null) {
            return null;
        }
        else {
            String string = o.toString();
            string = "00000000".substring(string.length()) + string;
            int year = Integer.parseInt(string.substring(0, 4));
            int month = Integer.parseInt(string.substring(4, 6));
            int day = Integer.parseInt(string.substring(6,8));
            LocalDate date = LocalDate.of(year, month, day);
            return date;
        }
    }

    public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException {
        if (value == null) {
            ps.setBigDecimal(index, null);
        }
        else {
            String string = value.toString();
            int year = Integer.parseInt(string.substring(0, 4));
            int month = Integer.parseInt(string.substring(5, 7));
            int day = Integer.parseInt(string.substring(8,10)); 
            int date = 10000*year + 100*month + day;
            ps.setBigDecimal(index, BigDecimal.valueOf(date));
        }
    }

    public Object deepCopy(Object obj) throws HibernateException {      
        return obj;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object obj) throws HibernateException {
        return (Serializable) obj;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
