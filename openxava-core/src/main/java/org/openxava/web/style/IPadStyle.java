package org.openxava.web.style;

/**
 * For iPads. <p>
 * 
 * @author Javier Paniza
 */ 

public class IPadStyle extends Style {
	
	private static String [] jsFiles = {   
		"ipad/ipad.js",			
	};
	
	private static IPadStyle instance = null;	
	
	protected IPadStyle() {
	}
	
	public static Style getInstance() { 
		if (instance == null) {
			instance = new IPadStyle();
		}
		return instance;
	}
	
	public boolean isForBrowse(String browser) {		
		return browser != null && browser.contains("iPad");		
	}
	
	public boolean isSeparatorBeforeBottomButtons() {
		return false;
	}


	/* This was for Flip effect, we currently do not use it because of performance issue 
	public String getSetHtmlFunction() {
		return "ipad.setHtml";
	}
	*/
	
	public String getCssFile() {
		return "ipad/ipad.css";
	}
	
	public String [] getNoPortalModuleJsFiles() { 
		return jsFiles; 
	}

	
	public String getInitThemeScript() { 		
		return "ipad.onLoad()"; 	
	}
	
	public String getNextPageNavigationEvents(String listId) { 
		return "onclick=ipad.onClickNextPage('" + listId + "')";
	}
	
	public String getPreviousPageNavigationEvents(String listId) { 
		return "onclick=ipad.onClickPreviousPage('" + listId + "')";
	}
	
	/* This was for Flip effect, we currently do not use it because of performance issue
	public String getCoreStartDecoration() { 
		return "<div id='container'><div id='sheet'><div id='front'>";
	}

	public String getCoreEndDecoration() { 
		return "</div><div id='back'><div id='core_BACK' style='display: inline;' class='"
			+ getModule() + "'></div></div></div></div>";
	}
	*/	

	public String getDefaultModeController() {
		return "DetailList";		
	}
	
	public boolean allowsResizeColumns() { 
		return false;
	}
	
	public boolean isRowLinkable() { 
		return false;
	}
	
	public boolean isShowPageNumber() { 
		return false;
	}
	
	public boolean isShowModuleDescription() { 
		return true;
	}
	
	public boolean isSeveralActionsPerRow() {
		return false;
	}
	
	public boolean isChangingPageRowCountAllowed() {
		return false;
	}
	
	public boolean isHideRowsAllowed() {
		return false;
	}
	
	public boolean isShowRowCountOnTop() {
		return true;
	}

	public boolean isUseLinkForNoButtonBarAction() { 
		return true;
	}
	
	public boolean isHelpAvailable()  { 
		return false;
	}
	
	public boolean isShowImageInButtonBarButton() {
		return false;
	}

	public boolean isUseStandardImageActionForOnlyImageActionOnButtonBar() {
		return true;
	}
	
	public boolean isFixedPositionSupported() {
		return false;
	}

	
	public String getMetaTags() { 
		return "<meta name='apple-mobile-web-app-capable' content='yes'/>";
	}


	public String getModuleSpacing() {
		return "style='padding: 0px;'";		
	}
			
	public String getListCellSpacing() {
		return "cellspacing=0 cellpadding=0";
	}
				
	public String getImagesFolder() { 
		return "xava/style/ipad/images";
	}
	
}

