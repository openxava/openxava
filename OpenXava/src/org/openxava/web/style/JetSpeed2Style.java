package org.openxava.web.style;

/**
 * @author Javier Paniza
 */


public class JetSpeed2Style extends PortalStyle {
	
	private static JetSpeed2Style instance = null;

	
	
	protected JetSpeed2Style() {		
	}
	
	public static Style getInstance() {
		if (instance == null) {
			instance = new JetSpeed2Style();
		}
		return instance;
	}
		
	
	public String getList() { 
		return "";
	}
	
	public String getListHeader() { 
		return "portlet-section-header";
	}
	
	public String getListOrderBy() {
		return "jetspeed2-list-order-by";
	}	
	
	public String getListSubheader() {
		return "portlet-section-subheader";
	}
	
	public String getListPair() { 
		return "portlet-section-body";
	}
	
	public String getListOdd() { 
		return "portlet-section-alternate";
	}
	
	public String getListPairSelected() { 
		return "portlet-section-selected";
	}
	
	public String getListOddSelected() { 
		return "portlet-section-selected";
	}
				
	public String getListInfo() {	
		return "jetspeed2-list-info";
	}
	
	public String getListTitle() {
		return "PTitleContent";
	}	
	
	public String getFrame() {
		return "frame";
	}
	
	protected String getFrameTitle() { 
		return "PTitle";
	}
	
	protected String getFrameTitleLabel() { 
		return "PTitleContent";
	}
	
	protected String getFrameContent() { 
		return "PContent";
	}
		
	public String getEditor() { 
		return "portlet-form-input-field";
	}
	
	public String getSmallLabel() {
		return "small-label";
	}
	
	public String getSection() {
		return "tabs";
	}
	
	public String getActiveSection() {
		return "";
	}	
		
	protected String getActiveSectionTabStartDecoration() {
		return "<td class='LTabLeft' nowrap='true'>&nbsp;</td><td class='LTab' style='vertical-align: middle; text-align: center;' nowrap='true'>";
	}
	
	public String getActiveSectionTabEndDecoration() {
		return "</td><td class='LTabRight' nowrap='true'>&nbsp;</td>";		
	}
	
	protected String getSectionTabStartDecoration() {
		return "<td class='LTabLeftLow' nowrap='true'>&nbsp;</td><td class='LTabLow' style='vertical-align: middle; text-align: center;' nowrap='true'>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</td><td class='LTabRightLow' nowrap='true'>&nbsp;</td>";		
	}		
		
	public String getRestoreImage() {
		return "decorations/images/restore.gif";
	}
		
	public String getMinimizeImage() {
		return "decorations/images/minimized.gif";
	}	
	
	public String getRemoveImage() {
		return "decorations/layout/images/close.gif";
	}
	
	public String getSelectedRow(){
		return "";
	}
	
	public String getSelectedRowStyle(){
		return "font-weight: bold;";
	}			
}
