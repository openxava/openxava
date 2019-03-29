package org.openxava.web.style;

/**
 * 
 * @author Javier Paniza 
 */
public class WebSpherePortal8Style extends PortalStyle {
	
	private static WebSpherePortal8Style instance = null;

	protected WebSpherePortal8Style() {
	}
	
	/**
	 * Pixels to add/substract from list width to a correct adjustament.
	 * 
	 * @since 5.1.1
	 */
	public double getCollectionAdjustment() {  
		return -60;		
	}

	
	public static Style getInstance() {
		if (instance == null) {
			instance = new WebSpherePortal8Style();
		}
		return instance;
	}
	
	protected String getJQueryCss() { 
		return "/xava/style/smoothness/jquery-ui.css";
	}
	
	public String getEditor() { 
		return "portlet-form-input-field";
	}
	
	public String getFrame() {
		return "wpthemeControl wpthemeStandard";
	}
	
	protected String getFrameTitle() {
		return "";
	}
		
	protected String getFrameContent() { 
		return "";
	}
	
	public String getMinimizeImage() {
		return "/wps/images/icons/MinimizeFrame.gif"; 
	}
	
	public String getRestoreImage() {
		return "/wps/images/icons/RestoreView_Task.gif";
	}
			
	public String getRemoveImage() {
		return "/wps/images/icons/DeleteFolder.gif";
	}
	
	public String getHelpImage() {
		return "/wps/images/icons/scope_search_help.gif";
	}

	public String getListCellStyle() {
		return "border-left-width: 1px;";
	}

	public String getList() { 
		return "wpsTable";
	}
	
	public String getListCellSpacing() {
		return "border=0 cellspacing=0 cellpadding=0"; 
	}
	
	public String getListStyle() {  
		return "border-collapse: collapse;";
	}			
	
	public String getListHeader() {  
		return "wpsPagingTableHeaderMiddle";
	}
	
	public String getListSubheader() {
		return "wpsPagingTableHeaderMiddle";
	}
	
	public String getListPair() { 
		return "wpsTableNrmRow";
	}	
	
	public String getListOdd() { 
		return "wpsTableNrmRow";
	}
	
	public String getListPairSelected() {  
		return "websphere-list-selected";
	}
	
	public String getListOddSelected() { 
		return "websphere-list-selected";
	}
				
	public String getListInfo() {	
		return "";
	}
	
	public String getListInfoDetail() {
		return "list-info-detail"; 
	}
	
	public String getListTitle() {
		return "websphere-list-title"; 
	}	
	
	public String getListTitleWrapper() {
		return "";
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
		return "<td><div class='wpthemeSecondaryBanner'><div class='wpthemeNavContainer1'><nav style='width: 100%' class='wpthemeSecondaryNav'><ul class='wpthemeNavList'>";
	}
	
	public String getSectionBarEndDecoration() {
		return "</ul></nav><div class='wpthemeClear'></div></div><div class='wpthemeClear'></div></div></td>";
	}
		
	protected String getActiveSectionTabStartDecoration() {
		return "<li class='wpthemeNavListItem wpthemeLeft wpthemeSelected'><a href='javascript:void(0)'>";
	}
	
	public String getActiveSectionTabEndDecoration() {
		return "</a></li>";		
	}
	
	protected String getSectionTabStartDecoration() {
		return "<li class='wpthemeNavListItem wpthemeLeft'>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</li>";		
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
		
	public String getErrorEndDecoration () {
		return "</span><div>";
	}		
	
	protected String getImageMessageBaseURI() {
		return "/wps/defaultTheme80/themes/html/";  		
	}
	
}
