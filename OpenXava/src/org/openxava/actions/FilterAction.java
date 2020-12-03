package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class FilterAction extends FilterTabBaseAction {   
	
	private int configurationId;
	
	public void execute() throws Exception {
		getTab().filter(); 
		/* tmp
		if (configurationId == 0) getTab().saveConfiguration();
		else getTab().setConfigurationId(getConfigurationId());
		*/
		// tmp ini
		if (configurationId == 0) {
			getTab().allowSaveConfiguration();
			getTab().createConfiguration();
		}
		else getTab().setConfigurationId(getConfigurationId());
		// tmp fin
	}

	public int getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(int configurationId) {
		this.configurationId = configurationId;
	}

}
