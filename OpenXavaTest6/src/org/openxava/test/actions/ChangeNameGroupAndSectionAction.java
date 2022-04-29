package org.openxava.test.actions;

import org.openxava.actions.ViewBaseAction;

/**
 * @author Ana Andres
 * Created on 15 abr. 2020
 */
public class ChangeNameGroupAndSectionAction extends ViewBaseAction{

	@Override
	public void execute() throws Exception {
		// group
		getView().setLabelId("deliveryData", "my_group_name");
		// property
		getView().setLabelId("number", "my_property_name");
		// section
		getView().setLabelId("incidents", "my_section_name");
	}

}
