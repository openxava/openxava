package org.openxava.view.meta;

/**
 * tmr
 * @author Javier Paniza
 */
public class MetaChart implements java.io.Serializable {
	
	private String dataPropertiesNames;
	private String labelPropertiesNames;	
	private boolean showList;
	
	public String getDataPropertiesNames() {
		return dataPropertiesNames;
	}
	public void setDataPropertiesNames(String dataPropertiesNames) {
		this.dataPropertiesNames = dataPropertiesNames;
	}
	public String getLabelPropertiesNames() {
		return labelPropertiesNames;
	}
	public void setLabelPropertiesNames(String labelPropertiesNames) {
		this.labelPropertiesNames = labelPropertiesNames;
	}
	public boolean isShowList() {
		return showList;
	}
	public void setShowList(boolean showList) {
		this.showList = showList;
	} 
	
}
