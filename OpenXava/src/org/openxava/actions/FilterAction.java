package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class FilterAction extends FilterTabBaseAction {   
	
	private int configurationId;
	
	public void execute() throws Exception {
		getTab().filter(); 
		if (configurationId == 0) getTab().saveConfiguration();
		else getTab().setConfigurationId(getConfigurationId());
	}

	public int getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(int configurationId) {
		this.configurationId = configurationId;
	}

}
