package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoChangeListConfigurationAction extends TabBaseAction implements ICustomViewAction { 
	
	public void execute() throws Exception {
		showDialog();
		getView().setTitleId("List.changeConfiguration"); 
		WithRequiredLongName dialog = new WithRequiredLongName();
		getView().setModel(dialog);
		getView().setValue("name", getTab().getConfigurationName());
		setControllers("ChangeListConfiguration"); 
	}

	public String getCustomView() throws Exception {
		return DEFAULT_VIEW;
	}
	
	

}
