package org.openxava.web.style;

import javax.servlet.http.*;

import org.openxava.web.*;


/**
 * @since 6.0 
 * @author Javier Paniza
 */

public class XavaStyle extends Style {
	
	private static XavaStyle instance = null;

	public XavaStyle() {
	}
	
	protected String getJQueryCss() { 
		return "/xava/style/smoothness/jquery-ui.css";
	}
	
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
	
	/* tmr
	public String getModuleSpacing() {
		return "";		
	}
	*/
		
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

	/* tmr
	public String getFrameHeaderStartDecoration(int width, boolean collection) {
	   // tmr Movido a padre
	}
	*/
		
	/* tmr Movido a padre
	public String getFrameTitleStartDecoration() {
		StringBuffer r = new StringBuffer();
		r.append("<span ");
		r.append("class='");
		r.append(getFrameTitleLabel());
		r.append("'>\n");
		return r.toString();
	}
	*/
	
	/* tmr Movido a padre
	public String getFrameActionsStartDecoration() {		
		return "<span class='" + getFrameActions() + "'>"; 
	}
	
	*/
	
	/* tmr Movido a padre
	public String getFrameHeaderEndDecoration() {
		return "</div>";			
	}
	*/
	
	/* tmr Movido a padre
	public String getFrameContentStartDecoration(String id, boolean closed) {
	}
	*/
		
	/* tmr Movido a padre
	public String getFrameContentEndDecoration() { 
		return "\n</div></div></div>"; 
	}
	*/
		
	public String getSectionBarStartDecoration() { 
		return "<td>";
	}
	
	public String getSectionBarEndDecoration() {
		return "</td>";
	}

	/* tmr
	public String getErrorStartDecoration () {  
		return "<div class='ox-message-box'>"; 
	}
	
	public String getErrorEndDecoration () { 
		return "</div>";
	}
	*/
	
	/* tmr
	public String getMessageStartDecoration () {  
		return "<div class='ox-message-box'>"; 
	}
		
	public String getMessageEndDecoration () { 
		return "</div>";
	}
	*/
	
	/**
	 * @since 5.5
	 */
	/* tmr
	public String getWarningStartDecoration() { 
		return "<div class='ox-message-box'>"; 
	}
	*/

	/**
	 * @since 5.5
	 */
	/* tmr
	public String getInfoStartDecoration() { 
		return "<div class='ox-message-box'>"; 
	}
	*/
	
}
