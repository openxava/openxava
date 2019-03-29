package org.openxava.filters;



import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class FilterException extends XavaException {

	
	
	public FilterException() {
		super("filter_error");
	}

	public FilterException(String s) {
		super(s);
	}
	
	public FilterException(String s, Object argv0) {
		super(s, argv0);
	}
	
	public FilterException(String s, Object argv0, Object argv1) {
		super(s, argv0, argv1);
	}

	public FilterException(String s, Object argv0, Object argv1, Object argv2) {
		super(s, argv0, argv1, argv2);
	}

}
