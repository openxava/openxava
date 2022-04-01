package org.openxava.util;


/**
 * A OpenXava element does no exists. <p> 
 * 
 * @author Javier Paniza
 */
public class ElementNotFoundException extends XavaException {

	public ElementNotFoundException() {
		super("element_not_found");
	}

	public ElementNotFoundException(String s) {
		super(s);
	}
	
	public ElementNotFoundException(String s, Object argv0) {
		super(s, argv0);
	}
	
	public ElementNotFoundException(String s, Object argv0, Object argv1) {
		super(s, argv0, argv1);
	}

	public ElementNotFoundException(String s, Object argv0, Object argv1, Object argv2) {
		super(s, argv0, argv1, argv2);
	}
	
}


