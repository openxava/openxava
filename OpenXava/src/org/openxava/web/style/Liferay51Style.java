package org.openxava.web.style;


/**
 * For Liferay 5.1. <p>
 * 
 * @author Javier Paniza
 */ 

public class Liferay51Style extends Liferay43Style {
	
	private static Liferay51Style instance = null;
	private static String [] noPortalModuleJsFiles = {  
		"liferay51/js/theme_display.js",	
		"liferay51/js/barebone_unpacked.js"
	};	

	protected Liferay51Style() {
	}
	
	public static Style getInstance() {
		if (instance == null) {
			instance = new Liferay51Style();
		}
		return instance;
	}
	
	public String [] getNoPortalModuleJsFiles() { 
		return noPortalModuleJsFiles;
	}	
		
	protected String getJQueryCss() { 
		return "/xava/style/liferay51/css/jquery-ui-patch.css";
	}	
			
	public String getModuleSpacing() { 
		return "";		
	}
	
	public String getDetail() {
		return ""; 		
	}
	
	public String getFrameActionsStartDecoration() {   
		return "<td align='right' width='40px'>";
	}
	
	public String getListHeader() { 
		return "portlet-section-header results-header";		
	}
	
	public String getListSubheader() {
		return "portlet-section-subheader results-header";		
	}
			
	public String getListPair() { 
		return "portlet-section-body results-row";		
	}
	
	public String getListPairEvents() { 
		return "onmouseover=\"$(this).removeClass('" + getSelectedRow() + " portlet-section-body').addClass('portlet-section-body-hover hover');\"" +  
			"onmouseout=\"$(this).removeClass('portlet-section-body-hover hover').addClass('portlet-section-body'); openxava.markRows()\""; 			
	}
	
	public String getListOdd() { 
		return "portlet-section-alternate results-row alt";		
	}
	
	public String getListOddEvents() { 
		return "onmouseover=\"$(this).removeClass('" + getSelectedRow() + " portlet-section-alternate').addClass('portlet-section-alternate-hover hover');\"" +  
			"onmouseout=\"$(this).removeClass('portlet-section-alternate-hover hover').addClass('portlet-section-alternate'); openxava.markRows()\"";				
	}	

	public String getSectionBarStartDecoration() {
		return "<td style='padding-top: 4px;'>\n" +
				"<ul class='tabs ui-tabs'>"; 
	}
	
	protected String getActiveSectionTabStartDecoration() {
		return "<li class='current' style='position: static;'><a href='javascript:void(0)' style='position: static;'>"; // position: static needed for ie7 
	}
	
	protected String getSectionTabStartDecoration() {
		return "<li style='position: static;'>"; // position: static needed for ie7 
	}
	
	protected String getLiferayImagesFolder() { 
		return isInsidePortal()?"/html/themes/classic/images/":"xava/style/liferay51/images/";
	}
	
	public String getSelectedRow(){
		return "selected-row";
	}
	
	public String getHelpImage() {
		return getLiferayImagesFolder() + "portlet/help.png";
	}
		
	public String getTotalRow() { 
		return super.getTotalRow() + " results-row"; 
	}
	
	public String getTotalCell() { 
		return "liferay-xava-cell-wrapper";
	}		
	
	public String getTotalCellStyle() {  
		return getTotalCellAlignStyle();
	}	
	
}