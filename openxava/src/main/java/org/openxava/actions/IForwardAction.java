package org.openxava.actions;

/**
 * It allows to do a <i>forward</i> to any URI.
 * 
 * It can be a resource inside the same application, such as a jsp or servlet, 
 * or an absolute URL in the internet. The absolute URL is supported since v4m1. 
 * 
 * @author Javier Paniza
 */

public interface IForwardAction extends IAction {
	
	/**
	 * The URI to go. <p>
	 * 
	 * If it starts with "http://" or "https://" the action will forward to the
	 * absolute URL in internet (since v4m1).
	 * 
	 * Since 4.0.1 if it starts with "javascript:" the corresponding code will executed 
	 * by the browser. Since 5.9 you should use IJavaScriptPostAction to execute JavaScript
	 * because IForwardAction with javascript: does not update the page before
	 * executing the JavaScript, but executes the JavaScript instead.  
	 * 
	 * If it returns null the forwarding is not done.
	 */
	String getForwardURI();
	boolean inNewWindow();

}
