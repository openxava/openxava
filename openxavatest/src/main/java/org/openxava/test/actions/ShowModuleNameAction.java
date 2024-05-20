package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.application.meta.*;

public class ShowModuleNameAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		MetaModule module = getManager().getMetaModule();
		System.out.println(getManager().getModuleName());
		addMessage("You are in " + module.getName() 
		+ " from " + module.getModelName());
	}

}
