package org.openxava.web.style;

/**
 * 
 * @author Javier Paniza
 */

public class WebSpherePortal6Style extends WebSpherePortalStyle {
	
	private static WebSpherePortal6Style instance = null;
	
	protected WebSpherePortal6Style() {		
	}
	
	public static Style getInstance() {
		if (instance == null) {
			instance = new WebSpherePortal6Style();
		}
		return instance;
	}

	public String getSection() {
		return "";
	}	
	
	public String getSectionTableAttributes() {		
		return "border='0' cellpadding='0' cellspacing='0' width='100%'";
	}
	
	
	public String getActiveSection() {
		return "";
	}	
		
	public String getSectionLink() {
		return "";
	}
	
	public String getSectionBarStartDecoration() {
		return "<td><ul class='wpsPageBarFirstRow'>";
	}
	public String getSectionBarEndDecoration() {	
		return "</ul><div class='pageBarSeparator'><!--  --></div></td>";
	}
		
	protected String getActiveSectionTabStartDecoration() {
		return "<li class='wpsSelectedPage'>";
	}
	
	public String getActiveSectionTabEndDecoration() {
		return "</li>";		
	}
	
	protected String getSectionTabStartDecoration() {
		return "<li class='wpsUnSelectedPage'>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</li>";		
	}

	public String getFrame() { 
		return "wpsPortlet";
	}
	
	protected String getFrameTitle() { 
		return "wpsPortletTitleBar";
	}
		
	protected String getFrameTitleLabel() { 
		return "websphere-frame-title-label";
	}

	public String getFrameHeaderStartDecoration(int width) {		
		return super.getFrameHeaderStartDecoration(width) + 
			"<div style='width:100%; text-align: left'>";
	}
	
	public String getFrameHeaderEndDecoration() { 
		return 	"<img alt='' style='border:0; text-align: right;' width='1' height='22' src='/wps/skins/html/IBM/title_minheight.gif'></div>"
			+ super.getFrameHeaderEndDecoration();
	}
	
	protected String getFrameContent() { 
		return "wpsPortletBody";
	}
	
	protected String getFrameSpacing() { 
		return "border=0 cellspacing=0 cellpadding=2"; 		
	}
	
	public String getRestoreImage() { 
		return "xava/images/restore.gif";
	}
	
	public String getMinimizeImage() { 
		return "xava/images/minimize.gif";
	}	
	
	public String getInitThemeScript() {  
		return "javascriptEventController.enableAll();";
	}

	public String getMessageStartDecoration () {
		return "<div class='wpsStatusMsg'>&nbsp;<img src='" + getImageMessageBaseURI() + "info.gif' title='' alt='' />&nbsp;<span class='wpsFieldInfoText'>";
	}
	
	public String getMessageEndDecoration () {
		return "</span><div>";
	}	
	
	public String getErrorStartDecoration () {
		return "<div class='wpsStatusMsg'>&nbsp;<img src='" + getImageMessageBaseURI() + "error.gif' title='Error' alt='Error' />&nbsp;<span class='wpsFieldInfoText'>";
	}
	
	protected String getImageMessageBaseURI() {
		return "/wps/themes/html/IBM/"; 		
	}
	
	public String getErrorEndDecoration () {
		return "</span><div>";
	}	
	
	public String getListCellStyle() {
		return "border-left-width: 1px;";
	}
				
}
