package org.openxava.web.style;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * This class and its subclasses is used from JSP code to give
 * style to the web applications. <p>
 * 
 * The nomenclature is:
 * <ul>
 * <li>By default: CSS class name.
 * <li>..Style: A CSS inline style.
 * <li>..Image: URI of image
 * <li>..Events: code for javascript events
 * <li>..StartDecoration/EndDecoration: HTML code to put before and after.
 * <li>..Spacing: Table spacing
 * </ul>
 * 
 * @author Javier Paniza
 */ 

public class PortalStyle extends Style {
		 	
	private static Log log = LogFactory.getLog(PortalStyle.class);
	private static PortalStyle instance = null;
	private static Collection styleClasses; 
	private static Map<String, PortalStyle> stylesByBrowser = new HashMap<String, PortalStyle>(); 
	private Collection<String> additionalCssFiles; 
	private String cssFile; 
	private boolean insidePortal; 
	private String browser; 
	
	
	protected Collection<String> createAdditionalCssFiles() {		
		Collection<String> result = new ArrayList(super.createAdditionalCssFiles());
		result.add("/xava/style/layout.css"); 
		result.add("/xava/style/portal.css");
		result.add("/xava/style/materialdesignicons.css");
		return result;
	}
		
	/**
	 * 
	 * @since 4.2
	 */
	public String getDefaultModeController() {
		return "Mode";
	}
	
	private static Collection getStyleClasses() throws Exception {
		if (styleClasses == null) {
			PropertiesReader reader = new PropertiesReader(PortalStyle.class, "styles.properties");
			styleClasses = reader.get().keySet();
		}
		return styleClasses;
	}
	
	/**
	 * 
	 * @since 5.4
	 */
	public boolean isUseIconsInsteadOfImages() {
		return false; 
	}
	
	public boolean isForBrowse(String browser) {
		return false;
	}
	
	public String [] getNoPortalModuleJsFiles() { 
		return null;
	}
	
	protected String getJQueryCss() { 
		return "/xava/style/ui-lightness/jquery-ui.css";
	}

	public String getModule() {		
		return "portlet-font";  
	}
	
	/** @since 5.5.1 */
	protected String getBrowserClass() { 
		return isIE()?" ie":""; 
	}
										
	public String getDetail() {
		return "";
	}
			
	public String getList() {  
		return "list";
	}
	
	public String getView() { 
		return "";
	}
				
	public String getListHeader() { 
		return "list";
	}
	
	public String getCollectionListActions() { 
		return getButtonBar(); 
	}
	
	public String getListHeaderCell() { 
		return getListHeader();
	}
	
	public String getListSubheader() {
		return "list-subheader";
	}	
	
	public String getListSubheaderCell() { 
		return getListSubheader();
	}
	
	public String getListOrderBy() {
		return "";
	}
	
	public String getListPair() { 
		return "list-pair";
	}
	
	public String getListPairEvents() {  
		return "";
	}	
	
	public String getListPairCell() { 
		return getListPair();
	}
	
	public String getListOdd() { 
		return "list-odd";
	}
	
	public String getListPairSelected() { 
		return "list-pair-selected";
	}
	
	public String getListOddSelected() { 
		return "list-odd-selected";
	}
		
	public String getListInfo() {
		return "list-info";
	}
	
	public String getListInfoDetail() { 
		return getListInfo() + " list-info-detail" + ((isIE7() || isIE6())?" ie7":""); 
		
	}
		
	public String getListTitle() {
		return "list-title";
	}
						
	public String getFrame() { 
		return "frame";
	}
		
	public String getEditor() { 
		return "editor";
	}
	
	public String getLabel() { 
		return "portlet-form-field-label ox-label"; 
	}
	
	protected String getFrameContent() { 
		return getFrame();
	}

	protected String getFrameTitle() {  
		return getFrame();
	}	
	
	protected String getFrameTitleLabel() { 
		return getFrameTitle();
	}
	
	public String getSmallLabel() {
		return "small-label";
	}
		
	public String getErrors() { 
		return "portlet-msg-error";
	}

	public String getMessages() { 
		return "portlet-msg-success"; 
	}
	
	/**
	 * @since 4.3
	 */
	public String getInfos() {  
		return "portlet-msg-info"; 
	}

	/**
	 * @since 4.3
	 */	
	public String getWarnings() {   
		return "portlet-msg-alert";
	}	
	
	public String getButton() {
		return "portlet-form-button";
	}
	
	
	public String getSection() {
		return "Jetspeed";
	}
		
	public String getActiveSection() {
		return "activeSection";
	}	
	

	
	protected String getActiveSectionTabStartDecoration() {
		return "<td class='TabLeft' nowrap='true'>&nbsp;</td><td class='TabMiddle' style='vertical-align: middle; text-align: center;' nowrap='true'>";
	}
	
	public String getActiveSectionTabEndDecoration() {
		return "</td><td class='TabRight' nowrap='true'>&nbsp;</td>";		
	}
	
	protected String getSectionTabStartDecoration() {
		return "<td class='TabLeftLow' nowrap='true'>&nbsp;</td><td class='TabMiddleLow' style='vertical-align: middle; text-align: center;' nowrap='true'>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</td><td class='TabRightLow' nowrap='true'>&nbsp;</td>";		
	}		
			
	
	/**
	 * If <code>true</code< the header in list is aligned as data displayed in its column. <p>
	 * 
	 * By default is <code>false</code> and it's used the portal default alignament for headers.
	 */
	public boolean isAlignHeaderAsData() {
		return false;
	}
		
	
	public String getSelectedRow(){
		return "selected-row";
	}
	
	public String getCurrentRow() {
		return "current-row"; 
	}
	
	
	public String getPageNavigationArrowDisable() { 
		return "page-navigation page-navigation-arrow page-navigation-arrow-disable"; 
	}
	
	public String getPageNavigationSelected() { 		
		return "page-navigation-selected";
	}
	
	public String getPageNavigation() { 		
		return "page-navigation";
	}
		
	public String getPageNavigationArrow() { 		
		return "page-navigation page-navigation-arrow";
	}
	
	public String getRowsPerPage() { 		
		return "rows-per-page";
	}
	

	
	/** @since 4m6 */
	public String getActiveSectionTabStartDecoration(boolean first, boolean last) {
		if (first) {
			return getActiveSectionFirstTabStartDecoration();
		}
		else if (last){
			return getActiveSectionLastTabStartDecoration();
		}
		return getActiveSectionTabStartDecoration();
	}
	
	/** @since 4m6 */
	public String getSectionTabStartDecoration(boolean first, boolean last) {
		if (first) {
			return getSectionFirstTabStartDecoration();
		}
		else if (last){
			return getSectionLastTabStartDecoration();
		}
		return getSectionTabStartDecoration();
	}
	
	/** @since 4m6 */
	protected String getActiveSectionFirstTabStartDecoration() {
		return getActiveSectionTabStartDecoration(); 
	} 
	
	/** @since 4m6 */
	protected String getActiveSectionLastTabStartDecoration() { 
		return getActiveSectionTabStartDecoration(); 
	} 
	
	/** @since 4m6 */
	protected String getSectionFirstTabStartDecoration() { 
		return getSectionTabStartDecoration(); 
	} 
	
	/** @since 4m6 */
	protected String getSectionLastTabStartDecoration() { 
		return getSectionTabStartDecoration(); 
	}
				
	public String getTotalCell() { 
		return "";
	}
	
	public String getTotalCapableCell() { 
		return "";
	}
	
	public String getTotalCellStyle() {
		return getTotalCellAlignStyle();
	}
	
	protected String getTotalCellAlignStyle() { 
		return "vertical-align: middle; padding-right: 0px;"; 
	}
	
	public String getTotalEmptyCellStyle() { 
		return ""; 
	}
	
	public String getTotalCapableCellStyle() { 
		return getTotalEmptyCellStyle() + "vertical-align: top; text-align: right;	padding: 0px;";
	}
	
	public String getSectionBarStartDecoration() {
		return "";
	}
	
	public String getSectionBarEndDecoration() {
		return "";
	}
	
	/**
	 * @since 5.9
	 */
	public String getChartsDataStyle() { 
		return "width: calc(90vw - 270px); height: calc(70vh - 270px);";
	}

		
}