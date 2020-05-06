package org.openxava.test.actions;

import org.openxava.actions.ViewBaseAction;

/**
 * @author Ana Andres
 * Created on 15 abr. 2020
 */
public class ChangeNameGroupAndSection extends ViewBaseAction{

	@Override
	public void execute() throws Exception {
		
		// group
		getView().setLabelId("deliveryData", "My group name");
		// property
		getView().setLabelId("number", "My property name");
		// section
		getView().setLabelId("incidents", "My section name");
	}

}
