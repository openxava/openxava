package org.openxava.actions;

import org.openxava.controller.*;

/**
 * Action that receives the ModuleContext. <p>
 * 
 * It is implemented by {@link BaseAction}, so if your 
 * extends from it you can access to the context 
 * just by calling to {@link BaseAction#getContext} (since 4m1). <p>
 * 
 * @author Javier Paniza
 */

public interface IModuleContextAction extends IAction {
	
	void setContext(ModuleContext context);

}
