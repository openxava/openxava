package org.openxava.types;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.time.*;

import org.apache.commons.logging.*;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.*;

/**
 * Allows LocalDate work with numeric date values in format yyyymmdd from database. <p>
 *
 * @since 7.4
 * @author Miguel Garbín
 */
public class LocalDateNumericType implements UserType<LocalDate> {

    private static Log log = LogFactory.getLog(LocalDateNumericType.class);

    @Override
    public int getSqlType() {
        return SqlTypes.NUMERIC;
    }

    @Override
    public Class<LocalDate> returnedClass() {
        return LocalDate.class;
    }

    @Override
    public boolean equals(LocalDate x, LocalDate y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(LocalDate x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public LocalDate nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        Object o = rs.getObject(position);
        if (o == null) {
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

    @Override
    public void nullSafeSet(PreparedStatement st, LocalDate value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setBigDecimal(index, null);
        }
        else {
            String string = value.toString();
            int year = Integer.parseInt(string.substring(0, 4));
            int month = Integer.parseInt(string.substring(5, 7));
            int day = Integer.parseInt(string.substring(8,10));
            int date = 10000*year + 100*month + day;
            st.setBigDecimal(index, BigDecimal.valueOf(date));
        }
    }

    @Override
    public LocalDate deepCopy(LocalDate value) {
        return value; // LocalDate is immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(LocalDate value) {
        return value;
    }

    @Override
    public LocalDate assemble(Serializable cached, Object owner) {
        return (LocalDate) cached;
    }

    @Override
    public LocalDate replace(LocalDate original, LocalDate target, Object owner) {
        return original;
    }

}
