package org.openxava.actions;

/**
 * tmp Era ChangeListConfigurationNameAction
 * @since 5.6
 * @author Javier Paniza
 */
public class SaveListConfigurationNameAction extends TabBaseAction implements ICustomViewAction {
	
	public void execute() throws Exception {
		validateViewValues();
		getTab().setConfigurationName(getView().getValueString("name"));
		closeDialog();
	}

	public String getCustomView() throws Exception { 
		return PREVIOUS_VIEW;
	}

}
