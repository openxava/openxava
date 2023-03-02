package org.openxava.actions;

import org.openxava.view.*;

public class RemoveFromReferenceAction extends ReferenceBaseAction {
	
	private String model;	
	
	@Override
	public void execute() throws Exception {
		super.execute();
		View reference = getReferenceSubview();
		reference.clear();
	}
	
	public String getModel() {
		return model;
	}

	public void setModel(String string) {
		model = string;
	}

}
