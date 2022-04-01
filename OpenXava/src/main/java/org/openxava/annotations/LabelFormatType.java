package org.openxava.annotations;

/**
 * For using in <code>@LabelFormat</code> as value. <p>
 * 
 * @author Javier Paniza
 */

public enum LabelFormatType { 

	/**
	 * The label is displayed at the left of the editor. 
	 */
	NORMAL, 
	
	/**
	 * The label is displayed with a small size and above the editor.
	 */
	SMALL, 
	
	/**
	 * The label is not displayed.
	 */
	NO_LABEL	
}