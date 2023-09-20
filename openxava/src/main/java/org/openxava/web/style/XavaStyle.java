package org.openxava.web.style;

import javax.servlet.http.*;

import org.openxava.web.*;


/**
 * @since 6.0 
 * @deprecated  Since v7.1 as Style parent class.
 * @author Javier Paniza
 */

@Deprecated
public class XavaStyle extends Style {
	
	private static XavaStyle instance = null;

	public XavaStyle() {
	}
	
	/* tmr
	protected String getJQueryCss() { // tmr Quitar
		return "/xava/style/smoothness/jquery-ui.css";
	}
	*/
	
	public static String getBodyClass(HttpServletRequest request) {
		String browser = request.getHeader("user-agent");
		if (browser == null) return "";
		if (browser.contains("Edge") || browser.contains("Trident") || browser.contains("MSIE")) return "class='ie'"; 
		if (browser.contains("iPad")) return "class='ipad'";
		if (Browsers.isMobile(request)) return "class='mobile'"; 
		if (browser.contains("Firefox")) return "class='firefox'"; 
		return "";
	}

	
	public static Style getInstance() {
		if (instance == null) {
			instance = new XavaStyle();
		}
		return instance;
	}
	
	public String getCssFile() {
		return null; // Because CSS file is provided by the Themes class
	}

	public String getHelpImage() {
		return null; 
	}
	
	public String getMinimizeImage() {
		return null;
	}
	
	public String getRemoveImage() {
		return getImagesFolder() +  "/delete.gif"; 
	}

	@Override
	public String getRestoreImage() {
		return null; 
	}
	
	public String getFrame() { 
		return "ox-frame";
	}
	
		
	/**
	 * @since 5.1.1
	 */
	public double getListAdjustment() { 
		return 17;		
	}
	
	/**
	 * @since 5.1.1
	 */
	public double getCollectionAdjustment() { 
		return -40;		
	}

	public String getSectionBarStartDecoration() { 
		return "<td>";
	}
	
	public String getSectionBarEndDecoration() {
		return "</td>";
	}
	
}
