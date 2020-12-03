package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * tmp
 * @since 6.5
 * @author Javier Paniza
 */
public class GoSaveListConfigurationAction extends TabBaseAction implements ICustomViewAction { 
	
	public void execute() throws Exception {
		showDialog();
		getView().setTitleId("List.saveConfiguration"); // tmp i18n También para changeConfiguration 
		WithRequiredLongName dialog = new WithRequiredLongName();
		getView().setModel(dialog);
		getView().setValue("name", getTab().getConfigurationName());
		setControllers("TmpSaveListConfiguration"); 
	}

	public String getCustomView() throws Exception {
		return DEFAULT_VIEW;
	}
	
	

}
