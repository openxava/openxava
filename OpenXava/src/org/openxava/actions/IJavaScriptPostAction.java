package org.openxava.actions;

/**
 * Executes some JavaScript code after the regular action execution. <br/>
 * 
 * When the JavaScript code is executed the view is already update with
 * the changes from the regular action executed.  
 * 
 * @since 5.9
 * @author Javier Paniza
 */
public interface IJavaScriptPostAction extends IAction {
	
	/**
	 * The JavaScript code to execute.<p>
	 */
	String getPostJavaScript();

}
