package org.openxava.test.actions;

import org.openxava.actions.ViewBaseAction;

/**
 * Sets the title of the incidents section from code, to test 
 * that section titles changed programmatically are refreshed 
 * when navigating between records.
 * 
 * @author Javier Paniza
 */
public class UpdateIncidentsSectionTitleAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		if (getView().isKeyEditable()) return;
		Object number = getView().getValue("number");
		getView().getSectionView(1).setTitle("Incidents of " + number);
	}

}
