package org.openxava.actions;

import org.openxava.view.meta.*;

/**
 * @author Javier Paniza
 */

public class CreateNewFromReferenceAction extends NavigationFromReferenceBaseAction {
	
	public String getCustomController() {	
		return getModel() + "Creation";
	}
	
	public String getDefaultController() {
		return "NewCreation";
	}
		
	public String getNextAction() throws Exception {
		return getController() + ".new";
	}
	
	@Override
	protected String getNewViewName() {
		try {
			MetaReferenceView metaReferenceView = getViewInfo().getParent().getMetaView().getMetaReferenceViewFor(getViewInfo().getMemberName());
			if (metaReferenceView != null) {
				return metaReferenceView.getNewViewName();
			}
		}
		catch (Exception ex) {			
		}
		return null;
	}
	
}
