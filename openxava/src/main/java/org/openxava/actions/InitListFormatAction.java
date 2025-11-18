package org.openxava.actions;

/**
 * Initializes list format for list tabs.
 * 
 * @since 7.6.3
 * @author Javier Paniza
 */
public class InitListFormatAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().setOptimizeChunkSize(true);
		getView().setModelName(getManager().getModelName());
		getView().setViewName(getManager().getXavaViewName());
	}
}
