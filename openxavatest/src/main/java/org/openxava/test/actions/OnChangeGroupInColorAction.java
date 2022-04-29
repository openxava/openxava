package org.openxava.test.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.IChangeControllersAction;
import org.openxava.actions.OnChangePropertyBaseAction;
import org.openxava.test.model.Color;

/**
 * Create on 05/03/2009 (9:28:28)
 * @autor Ana Andrés
 */
public class OnChangeGroupInColorAction extends OnChangePropertyBaseAction implements IChangeControllersAction{
	private static Log log = LogFactory.getLog(OnChangeGroupInColorAction.class);
	
	private Color.Group group;
	
	public void execute() throws Exception {
		group = (Color.Group) getNewValue();

		getView().setHidden("group1", group != Color.Group.GROUP1);
		getView().setHidden("group2", group != Color.Group.GROUP2);
		
		getView().setFocus("property1"); 
	}

	public String[] getNextControllers() throws Exception {
		return 
			group == Color.Group.GROUP1 ? new String [] {"ReturnPreviousModule"} :
			(group == Color.Group.GROUP2 ? new String [] {"ActionWithImage"} :
			new String [] {});
	}
	
}
