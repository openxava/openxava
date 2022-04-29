package org.openxava.test.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.IChangeControllersAction;
import org.openxava.actions.OnChangePropertyBaseAction;
import org.openxava.test.model.Color;

/**
 * 
 * @autor Javier Paniza
 */
public class OnChangeGroupInColorAction extends OnChangePropertyBaseAction implements IChangeControllersAction{
	
	private int group;
	
	public void execute() throws Exception {
		Number n = (Number) getNewValue(); 
		group = n==null?0:n.intValue();

		getView().setHidden("group1", group != 1);
		getView().setHidden("group2", group != 2);
	}

	public String[] getNextControllers() throws Exception {
		return 
			group == 1 ? new String [] {"ReturnPreviousModule"} :
			(group == 2 ? new String [] {"ActionWithImage"} :
			new String [] {});
	}
	
}
