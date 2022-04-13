package org.openxava.web.style;

/**
 * 
 * @author Javier Paniza
 */

public class WebSpherePortalStyle extends PortalStyle {
	
	private static WebSpherePortalStyle instance = null;

	
	
	protected WebSpherePortalStyle() {		
	}
	
	public static Style getInstance() {
		if (instance == null) {
			instance = new WebSpherePortalStyle();
		}
		return instance;
	}
		
	protected String getJQueryCss() { 
		return "/xava/style/cupertino/jquery-ui.css";
	}
	
	public String getModuleSpacing() {
		return ""; 
	}	
	
	public String getDetail() {
		return "wpsTable";
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
		return "wpsTableHead";
	}
	
	public String getListSubheader() {
		return "wpsTableHead";
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
		return "wpsTable";
	}
	
	public String getListInfoDetail() {
		return "list-info-detail"; 
	}
	
	public String getListTitle() {
		return "websphere-list-title"; 
	}	
	
	public String getListTitleWrapper() {
		return "wpsTable";
	}	
	
	public String getFrame() { 
		return "";
	}
	
	protected String getFrameTitle() { 
		return "wpsPortletTitle";
	}
	
	protected String getFrameTitleLabel() { 
		return "websphere-frame-title-label";
	}
	
	protected String getFrameContent() { 
		return "wpsPortletBorder";
	}
	
	protected String getFrameSpacing() { 
		return "border=0 cellspacing=0 cellpadding=2";
	}
	
		
	public String getEditor() { 
		return "portlet-form-input-field";
	}
	
	public String getSmallLabel() {
		return "small-label";
	}
	
	public String getMessagesWrapper() { 
		return "wpsTable";
	}
	
	/**
	 * @since 5.8
	 */
	public String getErrorsWrapper() {
		return getMessagesWrapper();
	}
	
	public String getSection() {
		return "wpsPageBar";
	}
	
	public String getActiveSection() {
		return "";
	}	
	
	public String getSectionLink() {
		return "wpsUnSelectedPageLink";
	}
	
	public String getSectionBarStartDecoration() {
		return "<td class='wpsPageBar3dShadow'><img width='10' height='16' border='0' src='/wps/images/dot.gif' alt=''></td>";
	}
	
	
	protected String getActiveSectionTabStartDecoration() {
		return "<td class='wpsSelectedPage' style='vertical-align: middle; text-align: center;' nowrap='true'>";
	}
	
	public String getActiveSectionTabEndDecoration() {
		return "</td>";		
	}
	
	protected String getSectionTabStartDecoration() {
		return "<td class='wpsUnSelectedPage' style='vertical-align: middle; text-align: center;' nowrap='true'>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</td>";		
	}		
			
	public String getRestoreImage() {
		return "/wps/themes/html/title_alt_restore.gif";
	}
			
	public String getMinimizeImage() {
		return "/wps/themes/html/title_alt_minimize.gif";
	}	
	
	public String getRemoveImage() {
		return "/wps/images/icons/Delete.gif";
	}
	
	public boolean isAlignHeaderAsData() {
		return false; 
	}
	
	public String getBottomButtonsStyle() {
		return "padding-top: 4px;";
	}
	
	public boolean isApplySelectedStyleToCellInList() {
		return false;
	}

	public String getSelectedRow(){
		return "";
	}

	public String getSelectedRowStyle(){
		return "font-weight: bold;";
	}
	
	public String getHelpImage(){
		return "xava/images/help.png";
	}
}
