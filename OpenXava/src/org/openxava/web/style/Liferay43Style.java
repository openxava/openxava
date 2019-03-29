package org.openxava.web.style;

import org.openxava.util.*;

/**
 * For Liferay 4.3, 4.4 and 5.0. <p>
 * 
 * @author Javier Paniza
 */ 

public class Liferay43Style extends PortalStyle {
	
	private static Liferay43Style instance = null;

	
	
	protected Liferay43Style() {
	}
	
	public static Style getInstance() {
		if (instance == null) {
			instance = new Liferay43Style();
		}
		return instance;
	}
	
	public String [] getNoPortalModuleJsFiles() {  
		// The JS for Liferay 5.1 works fine for Liferay 4.3
		return Liferay51Style.getInstance().getNoPortalModuleJsFiles(); 
	}
	
	protected String getJQueryCss() { 
		return "/xava/style/cupertino/jquery-ui.css";
	}
			
	public String getInitThemeScript() { 
		return "jQuery( function() { Liferay.Util.addInputType(); Liferay.Util.addInputFocus(); } );";
	}
	
	public String getNoPortalModuleStartDecoration(String title) {   
		return "<div class='portlet' style='margin: 4px'><div class='portlet-topper'><span class='portlet-title'>"
			+ title + "</span></div><div class='portlet-content'>"; 
	}
	
	public String getNoPortalModuleEndDecoration() { 
		return "</div></div>";
	}					
	
	public String getModule() {
		return getBrowserClass(); 
	}
	
	public String getModuleSpacing() {
		return "style='padding: 2px;'";		
	}
	
	public String getLabel() { 	
		return super.getLabel() + " liferay-xava-label";
	} 
	
	public String getEditorWrapper() { 
		return "liferay-xava-editor-wrapper";
	}
									
	public String getDetail() {
		return "liferay-table"; 		
	}
			
	public String getList() { 
		return ""; 
	}
	
	public String getListCellSpacing() {
		return "border=0 cellspacing=0 cellpadding=0"; 
	}
	
	public String getListStyle() {  
		if (isIE6() || isIE7()) return "border-collapse: collapse; border-bottom-style: hidden;"; 
		return "border-collapse: collapse; border-bottom: 1px solid #CCCCCC;"; 
	}	
		
	public String getListHeader() { 
		return "portlet-section-header";		
	}
		
	public String getListHeaderCell() { 		
		return getListCell();
	}
	
	
	public String getListSubheader() {
		return "portlet-section-subheader";		
	}
	
	
	public String getListSubheaderCell() { 
		return getListCell();		
	}
		
	public String getListPair() { 
		return "portlet-section-body";		
	}

	public String getListPairEvents() { 		
		return "onmouseover=\"$(this).removeClass('" + getSelectedRow() + " portlet-section-body').addClass('portlet-section-body-hover');\"" +  
			"onmouseout=\"$(this).removeClass('portlet-section-body-hover').addClass('portlet-section-body'); openxava.markRows()\""; 
	}
		
	public String getListPairCell() {
		return getListCell();
	}
	
	public String getListOdd() { 
		return "portlet-section-alternate";		
	}
	
	public String getListOddEvents() {  
		return "onmouseover=\"$(this).removeClass('" + getSelectedRow() + " portlet-section-alternate').addClass('portlet-section-alternate-hover');\"" +  
			"onmouseout=\"$(this).removeClass('portlet-section-alternate-hover').addClass('portlet-section-alternate'); openxava.markRows()\""; 
	}
		
	public String getListOddCell() { 
		return getListCell();		
	}
			
	public String getListInfo() {
		return "list-info";
	}
	
		
	public String getListTitle() {
		return "list-title";
	}
	
	private String getListCell() { 
		return "liferay-xava-cell-wrapper";		
	}

	
	public String getFrameContentStartDecoration(String id, boolean closed) {
		String closedStyle = closed?"style='display: none;'":"";
		return "<div id='" + id + "' " + closedStyle + " class='portlet-content'><div class='portlet-content-container'>\n";
	}	
		
	public String getFrameContentEndDecoration() { 		
		return "\n</div></div></div>"; 
	}
		
	public String getListPairSelected() { 
		return "liferay-list-selected"; 
	}
	
	public String getListOddSelected() { 
		return "liferay-list-selected"; 
	}	

	public String getFrameHeaderStartDecoration(int width) {
		String rightMargin = width == 100?"":"style='margin-right: 7px;'";
		String frameClass = null; 
		switch (width) {
			case 0:
				frameClass = "ox-frame-sibling";
				break;
			case 50:
				frameClass = "ox-frame-sibling ox-two-collections-in-a-row";
				break;
			default: // 100	
				frameClass = "ox-frame";				
		}
		
		return "<div class='portlet " + frameClass + "' " + rightMargin +"><div class='portlet-topper' style='position: static; padding-right: 8px;'><table width='100%'><tr>"; // position: static needed for ie7 + liferay 4.3
	}	
	
	public String getFrameHeaderEndDecoration() { 
		return "</tr></table></div>"; 
	}
	public String getFrameTitleStartDecoration() { 
		return "<td><span class='portlet-title'>";
	}	
	public String getFrameTitleEndDecoration() { 
		return "</span></td>";
	}

	public String getFrameActionsStartDecoration() {  				
		return "<td align='right' valign='bottom' width='40px'>";
	}	


	public String getFrameActionsEndDecoration() { 
		return "</td>";
	}	
			
	public String getEditor() { 
		return "form-text";		
	}
	
	public String getSmallLabel() {
		return "''";
	}
		
	public String getButton() {
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
		return "<td style='padding-top: 4px;'><ul class='tabs'>"; 
	}
	
	public String getSectionBarEndDecoration() {	
		return "</ul></td>";
	}
		
	protected String getActiveSectionTabStartDecoration() {
		return "<li class='current' style='position: static;'><a href='javascript:void(0)'>"; // position: static needed for ie7 + liferay 4.3 
	}
	
	public String getActiveSectionTabEndDecoration() {
		return "</a></li>";	  	
	}
	
	protected String getSectionTabStartDecoration() {
		return "<li style='position: static;'>"; // position: static needed for ie7 + liferay 4.3
	}
	
	public String getSectionTabEndDecoration() {
		return "</li>";		
	}	
		
	public String getRestoreImage() { 
		return getLiferayImagesFolder() + "portlet/restore.png";
	}
	
	public String getMinimizeImage() {
		return getLiferayImagesFolder() + "portlet/minimize.png";
	}	
	
	
	public String getRemoveImage() {
		return getLiferayImagesFolder() + "portlet/close.png";
	}
		
	public boolean isApplySelectedStyleToCellInList() {
		return false;
	}
	
	public String getSectionLinkStyle() { 
		return "position: static;";
	}
	
	protected String getLiferayImagesFolder() {  
		return isInsidePortal()?"/html/themes/classic/images/":"xava/style/liferay43/images/"; 
	}
	
	public String getSelectedRow(){
		return "liferay4-selected-row";
	}
	
	public String getSelectedRowStyle(){
		return "";
	}
	
	public String getHelpImage() {
		return getLiferayImagesFolder() + "common/help.png";
	}
			
	public String getTotalCellStyle() {  
		return getTotalCellAlignStyle() + ";border-bottom-style: hidden;";
	}	
	
	public String getTotalEmptyCellStyle() {  
		return "background-color: white; border-bottom-style: hidden;";		
	}

				
}

