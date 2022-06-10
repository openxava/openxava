package org.openxava.converters;



import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class ConversionException extends XavaException {

	
	
	public ConversionException() {		
		super("conversion_error");
	}

	public ConversionException(String idOrMessage) {
		super(idOrMessage);
	}
	
	public ConversionException(String id, Object argv0) {
		super(id, argv0);
	}
	
	public ConversionException(String id, Object argv0, Object argv1) {
		super(id, argv0, argv1);
	}

}
