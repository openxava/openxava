package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class ModifyStudioFromArtistAction extends ModifyFromReferenceAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		String name = getView().getValueString("name").trim();
		getView().setValue("name", name + " (MODIFIED)");
	}
	
}
