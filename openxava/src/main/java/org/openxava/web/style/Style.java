package org.openxava.web.style;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * This class and its subclasses is used from JSP code to give
 * style to the web applications. <p>
 * 
 * Since v7.1 we started to use directly CSS classes in HTML and
 * removing methods from this class. This class was useful
 * when working with portals to get the portal style, given
 * that since v7.0 portals are no longer supported, now it's a
 * overload.<br>
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
 * @deprecated Since v7.1 should use directly the CSS classes in your HTML code.
 * @author Javier Paniza
 */ 

@Deprecated
public class Style {
		 	
	private static Log log = LogFactory.getLog(Style.class);
	private static Style instance = null;
	private Collection<String> additionalCssFiles; 
	private String cssFile; 
	private String browser; 
	
	public Style() { 		
	}

	public static Style getInstance() { 
		if (instance == null) {
			try {
				instance = (Style) Class.forName(XavaPreferences.getInstance().getStyleClass()).newInstance();
				instance.cssFile = XavaPreferences.getInstance().getStyleCSS();
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("default_style_warning", ex.getClass().getName()  + ": " + ex.getMessage()));
				instance = new Style();
				instance.cssFile = "default.css";
			}			
		}		
		return instance;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getDefaultModeController() {
		return "Mode";
	}
		
	/**
	 * Pixels to add/substract from list width to a correct adjustament.
	 * 
	 * @since 5.1.1
	 */
	public double getListAdjustment() { 
		return 0;		
	}
	
	/**
	 * Pixels to add/substract from list width to a correct adjustament.
	 * 
	 * @since 5.1.1
	 */
	public double getCollectionAdjustment() { 
		return -20;		
	}
	
	/**
	 * 
	 * @since 5.4
	 */
	public boolean isUseIconsInsteadOfImages() {
		return XavaPreferences.getInstance().isUseIconsInsteadOfImages();
	}
				
	/** 
	 * The JavaScript function that assign the HTML token to a specific a element. 
	 * 
	 * @since 4.2
	 */
	public String getSetHtmlFunction() {
		return "function (id, content) { $('#' + id).html(content); };";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public boolean allowsResizeColumns() { 
		return true;
	}
	
	public String getInitThemeScript() {
		return null;
	}
	
	/**
	 *
	 * @since 4.2
	 */
	public String getCoreStartDecoration() { 
		return "";
	}

	/**
	 *
	 * @since 4.2
	 */	
	public String getCoreEndDecoration() { 
		return "";
	}	
	
	public String getCssFile() {
		return cssFile;
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getMetaTags() { 
		return "";
	}

	public String getEditorWrapper() { 
		return "ox-editor-wrapper"; 
	}
	
	/** 
	 * The folder with images used for actions. <p>
	 *  
	 * If it starts with / is absolute, otherwise starts from the application context path. 
	 * 
	 * @since 4.2
	 */
	public String getImagesFolder() { 
		return "xava/images";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getPageNavigationSelectedImage() { 
		return "page_navigation_selected.gif";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getPageNavigationImage() { 
		return "page_navigation.gif";
	}
			
	/**
	 * @since 6.0
	 */
	public String getListMode() {
		return "ox-list-mode";
	}
			
	/**
	 * 
	 * @since 4.2
	 */		
	public String getView() { 
		return "ox-view";
	}
		
	public String getActionLink() {
		return "ox-action-link";
	}
	
	public String getActionImage() {
		return "ox-image-link";
	}
	
	/**
	 * @since 6.6
	 */
	public String getActionLabel() { 
		return "ox-action-label";
	}

	
	/**
	 * @since 5.5
	 */
	public String getCustomizeControls() { 
		return "ox-customize-controls";
	}
		
	/**
	 * @since 6.6
	 */
	public String getButtonBarContainer() {
		return "ox-button-bar-container"; 
	}
		
	public boolean isSeveralActionsPerRow() {
		return true;
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public boolean isUseLinkForNoButtonBarAction() {  
		return false;
	}

	/**
	 * If true an image is shown using this value as class,
	 * otherwise the image would be shown as the background of a span 
	 * with the getButtonBarButton() class.
	 * 
	 * @since 4.2
	 */	
	public boolean isUseStandardImageActionForOnlyImageActionOnButtonBar() {
		return false;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public boolean isSeparatorBeforeBottomButtons() {
		return true;
	}

	/**
	 * 
	 * @since 4.2
	 */	
	public String getButtonBarImage() { 
		return "ox-button-bar-image";
	}
			
	/**
	 * 
	 * @since 4.2
	 */		
	public String getActive() { 
		return "ox-active";
	}

	/**
	 * 
	 * @since 4.2
	 */		
	public String getFirst() { 
		return "ox-first";
	}
	
	/**
	 * 
	 * @since 4.2
	 */		
	public String getLast() { 
		return "ox-last";
	}
	
	public String getLabel() { 
		return "ox-label";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getModuleDescription() { 
		return "ox-module-description"; 
	}
	
	protected String getFrameContent() { 
		return "ox-frame-content";
	}
		
	/**
	 * 
	 * @since 5.4
	 */
	public String getSelectedListFormat() { 
		return "ox-selected-list-format";
	}

	/**
	 * 
	 * @since 5.4
	 */
	public String getCharts() { 
		return "ox-charts";
	}
	
	/**
	 * 
	 * @since 5.4
	 */
	public String getChartXColumn() {  
		return "ox-chart-x-column";
	}	
	
	/**
	 * 
	 * @since 5.4
	 */
	public String getChartType() {  
		return "ox-chart-type";
	}
		
	/**
	 * @since 5.7
	 */
	/* tmr
	public String getCards() {
		return "ox-cards";
	}
	*/

	/**
	 * @since 5.7
	 */
	/* tmr
	public String getCard() {
		return "ox-card";
	}
	*/
	
	/**
	 * @since 5.7
	 */
	/* tmr
	public String getCardHeader() {
		return "ox-card-header";
	}
	*/

	/**
	 * @since 5.7
	 */
	/* tmr
	public String getCardSubheader() {
		return "ox-card-subheader";
	}
	*/
	
	/**
	 * @since 5.7
	 */
	/* tmr
	public String getCardContent() {
		return "ox-card-content";
	}
	*/
		
	/**
	 * @since 5.7
	 */
	/* tmr
	public String getNoObjects() {
		return "ox-no-objects";
	}
	*/
			
	public String getListCellSpacing() {
		return ""; 
	}
		
	public String getListSubheader() {
		return "ox-list-subheader";
	}	

	public String getListOrderBy() {
		return "";
	}
	
	/** 
	 * @param  Since v4m5 it has no parameters
	 */
	public String getListPairEvents() {  
		return "";
	}		
	
	/** 
	 * @param  Since v4m5 it has no parameters
	 */
	public String getListOddEvents() {   
		return "";
	}		
		
	public String getListPairSelected() { 
		return "list-pair-selected";
	}
	
	public String getListOddSelected() { 
		return "list-odd-selected";
	}
			
	public String getListInfo() {
		return "ox-list-info";
	}	
	
	public String getListTitle() {
		return "ox-list-title";
	}
	
	/**
	 * 	
	 * @since 4.2
	 */	
	public String getHeaderListCount() { 
		return "ox-header-list-count";
	}

	
	public String getListTitleWrapper() {
		return "";
	}
	
	public String getFrameHeaderStartDecoration() {
		return getFrameHeaderStartDecoration(0); 
	}
	
	/**
	 * 
	 * @since 5.1.1
	 */
	public String getCollectionFrameHeaderStartDecoration(int width) { 
		return getFrameHeaderStartDecoration(width, true);  
	}
	
	public String getFrameHeaderStartDecoration(int width) {
		return getFrameHeaderStartDecoration(width, false);
	}

	public String getFrameHeaderStartDecoration(int width, boolean collection) {   
		StringBuffer r = new StringBuffer();
		r.append("<div ");
		r.append(" class='");
		r.append(getFrame());
		if (collection) {
			r.append(' ');
			r.append(getCollection());
		}
		if (width == 100) { 
			r.append(' ');			
			r.append(getFullFrame()); 
		}
		else if (width == 50) { // Two frames in a row
			r.append(' ');			
			r.append(getHalfFrame()); 		
		}		
		r.append("'"); 
		r.append(getFrameSpacing());
		r.append(">");
		r.append("<div class='");
		r.append(getFrameTitle());
		r.append("'>");		
		r.append("\n");						
		return r.toString();

	}
	
	public String getFrameHeaderEndDecoration() { 		
		return "</div>"; 
	}
	
	public String getFrameTitleStartDecoration() {
		StringBuffer r = new StringBuffer();
		r.append("<span ");
		r.append("class='");
		r.append(getFrameTitleLabel());
		r.append("'>\n");
		return r.toString();
	}	
	
	public String getFrameTitleEndDecoration() { 
		return "</span>";
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getFrameActions() {
		return "ox-frame-actions";
	}
	
	public String getFrameActionsStartDecoration() {		
		return "<span class='" + getFrameActions() + "'>";
	}	
	public String getFrameActionsEndDecoration() { 
		return "</span>";
	}		
	
	/**
	 * @since 4.4
	 */
	public String getFrameTotals() { 
		return "ox-frame-totals";
	}
	
	/**
	 * @since 4.4
	 */
	public String getFrameTotalsLabel() { 
		return "ox-frame-totals-label";
	}
	
	/**
	 * @since 4.4
	 */
	public String getFrameTotalsValue() { 
		return "ox-frame-totals-value";
	}
	
	public String getFrameContentStartDecoration() { 
		return getFrameContentStartDecoration(UUID.randomUUID().toString(), false);
	}
		
	public String getFrameContentStartDecoration(String id, boolean closed) {
		StringBuffer r = new StringBuffer();
		r.append("<div id='");
		r.append(id);
		r.append("' ");
		if (closed) r.append("class='ox-display-none'");
		r.append("><div class='");
		r.append(getFrameContent());	
		r.append("'>\n");
		return r.toString();
	}	
	
	public String getFrameContentEndDecoration() { 
		return "\n</div></div></div>"; 
	}
	
	public String getFrame() { 
		return "ox-frame"; 
	}
	
	/** @since 6.6.3 */
	protected String getHalfFrame() { 
		return "ox-half-frame"; 
	}

	/** @since 6.6.3 */
	protected String getFullFrame() { 
		return "ox-full-frame"; 
	}

	protected String getFrameTitle() {   
		return "ox-frame-title";
	}

	protected String getFrameTitleLabel() { 
		return "ox-frame-title-label";
	}

	protected String getFrameSpacing() { 
		return "";
	}
		
	public String getEditor() { 
		return "editor";
	}
	
	/**
	 * @since 6.5.3
	 */
	public String getEditorSuffix() { 
		return "ox-editor-suffix"; 
	}
		
	public String getSmallLabel() {
		return "small-label";
	}
	
	/**
	 * @since 5.5
	 */
	public String getRadioButtons() { 
		return "ox-radio-buttons";
	}

	
	/**
	 * For icon editor.
	 * 
	 * @since 5.5
	 */
	public String getIcon() { 
		return "ox-icon";
	}
	
	/**
	 * For icon in list as data.
	 * 
	 * @since 5.5
	 */
	public String getIconInList() {  
		return "ox-icon-in-list";
	}
	
	/**
	 * For icons list to choose one.
	 * 
	 * @since 5.5
	 */
	/* tmr
	public String getIconsList() {  
		return "ox-icons-list";
	}
	*/

	/**
	 * @since 5.5
	 */
	public String getDescriptionsList() {  
		return "ox-descriptions-list";
	}
	
	/**
	 * @since 6.4.1
	 */
	public String getReadOnlyHtmlText() { 
		return "ox-read-only-html-text";
	}

	
	/**
	 * @since 5.6.1
	 */
	public String getMoney() {    
		return "ox-money";
	}

	public String getMessages() { 
		return "ox-messages";
	}
	
	/**
	 * @since 4.3
	 */
	public String getInfos() { 
		return "ox-infos"; 
	}

	/**
	 * @since 4.3
	 */	
	public String getWarnings() {  
		return "ox-warnings"; 
	}	

	/**
	 * For messages
	 */
	public String getMessagesWrapper() { 
		return "ox-messages-wrapper";
	}

	public String getButton() { // tmr Probar quitarlo
		return ""; 
	}
		
	/**
	 * @since v5.5
	 */
	public String getSortIndicator() { 
		return "ox-sort-indicator";
	}
	
	/**
	 * @since v5.5
	 */
	public String getSortIndicator2() { 
		return "ox-sort-indicator2";
	}	

	/**
	 * @since v5.7
	 */
	public String getSections() { 
		return "ox-sections";
	}

	
	public String getSection() {
		return "ox-section";
	}

	/**
	 * 
	 * @since 4.2
	 */
	public String getSectionTab() {
		return "ox-section-tab";
	}
	
	public String getSectionTableAttributes() {
		return "border='0' cellpadding='0' cellspacing='0'";
	}

	
	/**
	 * 
	 * @since 4.2, renamed from getSectionActive()
	 */
	public String getActiveSection() { 
		return "ox-active-section";
	}
			
	public String getSectionBarStartDecoration() { 
		return "<td>";
	}

	public String getSectionBarEndDecoration() { 
		return "</td>";
	}
		
	public String getActiveSectionTabStartDecoration(boolean first, boolean last) { 
		return getSectionTabStartDecoration(first, last, true);
	}
	
	public String getSectionTabStartDecoration(boolean first, boolean last) { 
		return getSectionTabStartDecoration(first, last, false);
	}	
	
	protected String getSectionTabStartDecoration(boolean first, boolean last, boolean active) {
		StringBuffer r = new StringBuffer();		
		r.append("<span class='");
		if (active) {
			r.append(getActive());
			r.append(' ');
		}
		if (first) {
			r.append(getFirst());
			r.append(' ');
		}
		if (last) r.append(getLast());
		r.append("'>");		
		r.append("<span class='"); 
		r.append(getSectionTab());
		r.append("'>");
		return r.toString();
	}
	
	public String getActiveSectionTabEndDecoration() {		
		return "</span></span>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</span></span>";
	}
	
	/**
	 * @since 5.8 
	 */
	public String getCollection() {  
		return "ox-collection"; 
	}	
	
	public String getCollectionListActions() { 
		return "ox-collection-list-actions"; 
	}
	
	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */
	public String getRestoreImage() {
		return getImagesFolder() +  "/restore.gif";
	}
	
	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */
	public String getMinimizeImage() {  
		return getImagesFolder() +  "/minimize.gif";
	}	

	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */	
	public String getRemoveImage() {
		return getImagesFolder() +  "/remove.gif";
	}


	/**
	 * If <code>true</code< the header in list is aligned as data displayed in its column. <p>
	 * 
	 * By default is <code>false</code>.
	 */
	public boolean isAlignHeaderAsData() {
		return false;
	}
	
	public boolean isFixedPositionSupported() {
		return true;
	}
	
	/**
	 * If <code>true</code> the style for selected row (or special style) in a list
	 * is applied to the row (tr) and to <b>also the cell</b> (td). <p>
	 * 
	 * If <code>false</code> the style is applied only to the row (tr).<p> 
	 *
     * By default is <code>true</code>.
	 */
	public boolean isApplySelectedStyleToCellInList() {
		return true;
	}
	
	public boolean isShowImageInButtonBarButton() {
		return true;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public boolean isShowModuleDescription() {  
		return false;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public boolean isShowPageNumber() { 
		return true;
	}	
		
	public String getBottomButtons() {
		return "ox-bottom-buttons";
	}

	/**
	 * 
	 * @since 4.2
	 */
	public boolean isChangingPageRowCountAllowed() { 
		return true;
	}

	/**
	 * 
	 * @since 4.2
	 */	
	public boolean isHideRowsAllowed() { 
		return true;
	}
	
	/**
	 * 
	 * @since 4.2
	 */	
	public boolean isShowRowCountOnTop() { 
		return false;
	}
	
	public String getSelectedRow(){
		return "selected-row";
	}
	
	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowser() {
		return browser;
	}
	
	protected boolean isFirefox() { 		
		return browser == null?false:browser.contains("Firefox");
	}
	
	/** @since 4m5 */
	protected boolean isIE6() { 		
		return browser == null?false:browser.contains("MSIE 6");
	}
	
	/** @since 4m5 */
	protected boolean isIE7() { 		
		return browser == null?false:browser.contains("MSIE 7");
	}
	
	/** @since 5.5.1 */
	protected boolean isIE() {  		
		return browser == null?false:browser.contains("Trident") || browser.contains("MSIE");
	}
	
	/**
	 * @since 4m5
	 */
	public String getCurrentRow() {
		return "current-row"; 
	}
	
	/**
	 * @since 6.1
	 */
	public String getSubcontrollerSelected() {
		return "ox-subcontroller-select";
	}
	
	/**
	 * @since 4m5
	 */
	public String getCurrentRowCell() {
		return ""; 
	}
	
	
	/**
	 * @since 4m5
	 */
	public String getPageNavigationSelected() { 		
		return "ox-page-navigation-selected"; 
	}

	
	/**
	 * @since 4m5
	 */
	public String getPageNavigation() {
		return "ox-page-navigation"; 
	}

	/**
	 * 
	 * @since 4.2
	 */			
	public String getPageNavigationPages() {
		return "ox-page-navigation-pages";
	}
	
	/**
	 * @since 4m5
	 */
	public String getPageNavigationArrow() { 		
		return "ox-page-navigation-arrow";
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getNextPageNavigationEvents(String listId) {  		
		return "";
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getPreviousPageNavigationEvents(String listId) {  		
		return "";
	}
	
	/**
	 * @since 4m5
	 */		
	public String getPageNavigationArrowDisable() { 		
		return "ox-page-navigation-arrow-disable";
	}

	
	/**
	 * @since 4m5
	 */
	public String getRowsPerPage() { 		
		return "rows-per-page";
	}
	
	/**
	 * 
	 * @since 4.2
	 */		
	public boolean isHelpAvailable()  {
		return true;
	}

	/**
	 * @since 4m6
	 */
	public String getHelpImage() {
		return "images/help.png";
	}
			
	/**
	 * CSS class for the help icon, link or button. <p>
	 * 
	 * @since 4m6
	 */
	public String getHelp() {
		return "ox-help";
	}
	
	public String getTotalRow() { 
		return "ox-total-row";
	}
				
	/**
	 * 
	 * @since 4.2
	 */	
	public String getFilterCell() { 
		return "ox-filter-cell";
	}
	
	/**
	 * To allow the definition of different style for the label side of the view.
	 * @since 4.5
	 */
	public String getLayoutLabelCell() {
		return "ox-layout-label-cell";
	}

	/**
	 * To allow the definition of different style for the data side of the view.
	 * @since 4.5
	 */
	public String getLayoutDataCell() {
		return "ox-layout-data-cell";
	}

	/**
	 * To define the space between rows.
	 * @since 4.5
	 */
	public String getLayoutRowSpacer() {
		return "ox-layout-row-spacer";
	}

	/**
	 * Helps define additional styles for the content.
	 * @since 4.7
	 */
	public String getLayoutContent() {
		return "ox-layout-content";
	}

	/**
	 * @since 4.9.1
	 * @return Style for the second and next frames within the same row.
	 */
	public String getFrameSibling() {
		return "ox-frame-sibling";
	}
	
	/**
	 * @since 4.9.1
	 * @return Left margin for properties.
	 */
	public int getPropertyLeftMargin() {
		return 8;
	}
	
	/**
	 * @since v6.0
	 */
	public String getErrorEditor() { 
		return "ox-error-editor";
	}

	/**
	 * @since v6.0
	 */
	public String getRequiredEditor() { 
		return "ox-required-editor";
	}
	
	/**
	 * Determines if the html content has a class attribute with the getFrame() code and replace its content
	 * with getFrame() getFrameSibling combination.
	 * @param html Html text to be manipulated.
	 * @param delimiterChars Characters used as left most delimiter for the class attribute content.
	 * @return True if the html text contains the class attribute with the getFrame() code and was updated.
	 * @since 5.0
	 */
	private boolean replacedFrameHeaderClass(StringBuffer html, String delimiterChars) {
		return replacedFrameHeaderClass(html, delimiterChars, delimiterChars);
	}

	/**
	 * Determines if the html content has a class attribute with the getFrame() code and replace its content
	 * with getFrame() getFrameSibling combination.
	 * @param html Html text to be manipulated.
	 * @param starDelimiter Characters used as left most delimiter for the class attribute content.
	 * @param endDelimiter Characters used as right most delimiter for the class attribute content.
	 * @return True if the html text contains the class attribute with the getFrame() code and was updated.
	 * @since 5.0
	 */
	private boolean replacedFrameHeaderClass(StringBuffer html, String startDelimiter, String endDelimiter) {
		boolean returnValue = false;
		String toLookFor = "class=" + startDelimiter + getFrame() + endDelimiter;
		if (html.toString().contains(toLookFor)) {
			String replacedString = html.toString().replace("class=" + startDelimiter + getFrame() + endDelimiter, "class=" + startDelimiter + getFrame() + " " + getFrameSibling() + endDelimiter);
			html.delete(0, html.length());
			html.append(replacedString);
			returnValue = true;
		}
		return returnValue;
	}
	
	/**
	 * @since  5.1
	 * @return Style class for attached files.
	 */
	public String getAttachedFile() {
		return "ox-attached-file";
	}
	
	/**
	 * @since 5.7
	 */
	public String getResizeColumns() { 
		return "ox-resize-columns";
	}
	
	/**
	 * @since 5.9
	 */
	public String getSubscribed() { 
		return "ox-subscribed";
	}
	
	/**
	 * @since 5.9
	 */	
	public String getUnsubscribed() { 
		return "ox-unsubscribed";
	}
	
}