package org.openxava.actions;

/**
 * 
 * @since 5.7
 * @author Javier Paniza
 */
public class RemoveListConfigurationAction extends TabBaseAction implements ICustomViewAction {

	public void execute() throws Exception {
		getTab().removeConfiguration();
		addMessage("list_configuration_removed"); 
		closeDialog();
	}
	
	public String getCustomView() throws Exception { 
		return PREVIOUS_VIEW;
	}

}
