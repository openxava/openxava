package org.openxava.actions;

/**
 * It allows to do several <i>forwards</i> to several URIs.
 * 
 * The URIs can be a resources inside the same application, such as a jsp or servlet, 
 * or an absolute URL in the internet.  
 * 
 * @since 4.3
 * @author Oscar Caro
 */

public interface IMultipleForwardAction extends IAction {

	/**
	 * The URIs to go. <p>
	 * 
	 * If it starts with "http://" or "http://" the action will forward to the
	 * absolute URL in internet, if it starts with "javascript:" the
	 * corresponding code will executed by the browser, 
	 * otherwise it will forward to a resource inside this application.
	 * 
	 * @since 4.3 
	 */	
	String [] getForwardURIs();
	
}
