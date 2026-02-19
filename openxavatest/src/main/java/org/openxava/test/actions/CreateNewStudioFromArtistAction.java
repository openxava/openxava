package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class CreateNewStudioFromArtistAction extends CreateNewFromReferenceAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		getView().setValue("name", "NEW STUDIO");
	}

}
