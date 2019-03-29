package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
* Create on 6 oct. 2017 (13:54:54)
* @author Ana Andres
*/
public class OnChangeStateConditionInCity extends OnChangePropertyBaseAction {

	@Override
	public void execute() throws Exception {
		String value = (String)getNewValue();
		String condition = "";
		if (Is.empty(value)) condition = "1=1";
		else condition = "upper(name) like '%" + value + "%'";
		getView().setDescriptionsListCondition("state", condition);
	}

}
