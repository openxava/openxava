package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class SearchPersonFromTeamMemberAction extends ReferenceSearchAction {
	

	public void execute() throws Exception {
		super.execute();
		getTab().setBaseCondition("${name} like 'J%'");
		getTab().setDefaultOrder("${name} asc");
	}
}
