package com.yourcompany.yourapp.actions;

import org.openxava.actions.*;

public class CreateNewIssueInVersionAction extends CreateNewElementInCollectionAction {
	
	
	public void execute() throws Exception {
		String projectId = getView().getValueString("project.id"); 
		super.execute();
		getCollectionElementView().setValue("project.id", projectId);
		getCollectionElementView().setEditable("project", false);
	}

}
