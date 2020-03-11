package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class SetExtendedCityAction extends ViewBaseAction {

	public void execute() throws Exception {
		String city = getView().getValueString("city");
		String state = getView().getValueString("address.state.id");
		getView().setValue("extendedCity", city + " (" + state + ")");
	}

}
