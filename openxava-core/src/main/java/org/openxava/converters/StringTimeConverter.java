package org.openxava.converters;

import org.openxava.converters.IConverter;
import org.openxava.converters.ConversionException;

import java.sql.Time;

/**
 * In java a <tt>String</tt> and in database a
 * <tt>java.sql.Time</tt>. <p>
 *
 * @author Janesh Kodikara
 */
public class StringTimeConverter implements IConverter {

    private Time time;
    private String timeStamp;

    public Object toDB(Object o) throws ConversionException {


        if (o == null) return null;
        if (!(o instanceof String)) {
            throw new ConversionException("conversion_db_string_expected");
        }

        timeStamp = o.toString() + ":00";
        time = Time.valueOf(timeStamp);
        return time;
    }

    public Object toJava(Object o) throws ConversionException {
        if (o == null) return null;
        if (!(o instanceof java.sql.Time)) {
            throw new ConversionException("conversion_java_sqltime_expected");
        }

        time = (Time) o;
        timeStamp = time.toString();
        timeStamp = timeStamp.substring(0, 5);

        return timeStamp;

    }

}
