package org.openxava.actions;

import org.openxava.view.*;

public class RemoveFromReferenceAction extends ReferenceBaseAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		View reference = getReferenceSubview();
		reference.clear();
	}

}
