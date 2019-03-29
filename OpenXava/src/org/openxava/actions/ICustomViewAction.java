package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public interface ICustomViewAction extends IAction {		
			
	final static String DEFAULT_VIEW = "__DEFAULT_VIEW__";
	final static String PREVIOUS_VIEW = "__PREVIOUS_VIEW__"; 
	final static String SAME_VIEW = null;
		
	/**
	 * The id of a view made directly by developer (not OpenXava view). <p>
	 * 
	 * In web version is the name of jsp page (without .jsp extension).
	 * This is for inserting jsp (o swing) hand made view in our OpenXava application.
	 */
	String getCustomView() throws Exception;	

}
