package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.application.meta.*;

import com.openxava.naviox.impl.*;

public class ShowModuleAndModelNameAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		MetaModule module = getManager().getMetaModule();
		List<MetaModule> moduleList = ModulesHelper.getAll(getRequest());
		for (MetaModule m : moduleList) {
			if (m.getName().equals(module.getName())) {
				addMessage("You are in " + m.getName() 
				+ " from " + m.getModelName());
			}
		}
	}

}
