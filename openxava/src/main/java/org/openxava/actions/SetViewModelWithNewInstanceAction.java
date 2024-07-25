package org.openxava.actions;

/**
 * tmr 
 * 
 * @since 7.4 
 * @author Javier Paniza
 */

public class SetViewModelWithNewInstanceAction extends ViewBaseAction {

	public void execute() throws Exception {
		Object model = getView().getMetaModel().getPOJOClass().newInstance();
		getView().setModel(model);
	}

}
