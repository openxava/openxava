package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class ModifyLevelFromArtistAction extends ModifyFromReferenceAction {
	
	@Override
	public void execute() throws Exception {
		System.out.println("ModifyLevelFromArtistAction.execute()"); // tmr
		super.execute();
		String description = getView().getValueString("description");
		getView().setValue("description", description + "(MODIFIED)");
	}
	
}
