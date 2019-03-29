/*
 * Created on 19-may-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openxava.converters;


import java.sql.*;

import org.apache.commons.logging.*;
import org.openxava.util.XavaResources;

/**
 * Supports Blob (and also other types for byte []) in as column type in DB. <p>
 * 
 * @author Luis Miguel
 */
public class StringArrayBytesConverter implements IConverter {
	
	private static Log log = LogFactory.getLog(StringArrayBytesConverter.class);
    	
	public Object toJava(Object o) throws ConversionException {	    
    	if (o == null) return "";        	    
    	try {
    		byte[] b = null;
	    	if (o instanceof Blob) {
	    		Blob blob = (Blob) o;
	    		b = blob.getBytes(1l, (int)blob.length());
	    	}
	    	else if (o instanceof byte[]) {
	    		b = (byte[]) o;			
			}
	    	else {
	    		throw new ConversionException("conversion_java_byte_array_expected");
	    	}		
			return new String(b);
		}
		catch (Exception e){
			log.error(XavaResources.getString("byte_array_to_string_warning"), e);
			return "";
		}
		
	}
	
	public Object toDB(Object o) throws ConversionException {
	    return o==null?null:o.toString().getBytes();
	}
	
}
