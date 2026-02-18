package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class CreateNewLevelFromArtistAction extends CreateNewFromReferenceAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		getView().setValue("description", "NEW ACTING LEVEL");
	}

}
