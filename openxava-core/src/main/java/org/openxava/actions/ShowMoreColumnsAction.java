package org.openxava.actions;

/**
 * 
 * @author Javier Paniza 
 */
public class ShowMoreColumnsAction extends TabBaseAction {

	@Override
	public void execute() throws Exception {
		getTab().setColumnsToAddUntilSecondLevel(false);
	}

}
