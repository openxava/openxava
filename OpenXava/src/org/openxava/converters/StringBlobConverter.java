package org.openxava.converters;

import org.apache.commons.logging.*;

/**
 * In java a String a a Blog in database. <p>
 * 
 * @author Javier Paniza
 */
public class StringBlobConverter implements IConverter {
	
	private static Log log = LogFactory.getLog(StringBlobConverter.class);
	
	static public class StringValue implements java.io.Serializable {
    	
		public String data;
    	
		StringValue(String data) {
			this.data = data;
		}
    	
	}
	
	public Object toJava(Object o) throws ConversionException {	    
		try{
			return o==null?"":((StringValue)o).data;
		}
		catch (Exception ex){
			log.error(ex.getMessage(), ex);
			return "";
		}
	}

	public Object toDB(Object o) throws ConversionException {
	    StringValue value = new StringValue(o==null?"":o.toString());
	    return value;
	}
		
}
