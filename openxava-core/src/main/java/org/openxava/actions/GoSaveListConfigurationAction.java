package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * 
 * @since 6.5
 * @author Javier Paniza
 */
public class GoSaveListConfigurationAction extends TabBaseAction implements ICustomViewAction { 
	
	public void execute() throws Exception {
		showDialog();
		getView().setTitleId("List.saveConfiguration");  
		WithRequiredLongName dialog = new WithRequiredLongName();
		getView().setModel(dialog);
		getView().setValue("name", getTab().getConfigurationName());
		setControllers("SaveListConfiguration"); 
	}

	public String getCustomView() throws Exception {
		return DEFAULT_VIEW;
	}
	
}
