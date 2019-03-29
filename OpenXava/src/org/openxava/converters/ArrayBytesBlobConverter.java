package org.openxava.converters;

import java.sql.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;




/**
 * A bytes array (<tt>byte []</tt>) that in db it's saved as <tt>Blob</tt>. <p>
 * 
 * Util for save photos in as400 where any object that we 
 * store in a BLOD field is restore as <tt>java.sql.Blob</tt>.<br>
 * 
 * @author Javier Paniza
 */
public class ArrayBytesBlobConverter implements IConverter {

	private static Log log = LogFactory.getLog(ArrayBytesBlobConverter.class);
	
	public Object toDB(Object o) throws ConversionException {
		if (o == null) return null;
		if (!(o instanceof byte [])) {		
			throw new ConversionException("conversion_db_byte_array_expected");
		}		
		return o;
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return null;
		if (!(o instanceof Blob)) {		
			throw new ConversionException("conversion_java_blob_expected");
		}
		Blob b = (Blob) o;
		try {
			return b.getBytes(1l, (int) b.length());
		}
		catch (SQLException e) {
			log.error(XavaResources.getString("blob_to_array_warning"), e);
			return null;
		}
	}
		
}
