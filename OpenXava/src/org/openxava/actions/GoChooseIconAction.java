package org.openxava.actions;

import javax.inject.*;

/**
 * @author Javier Paniza
 */
public class GoChooseIconAction extends ViewBaseAction implements ICustomViewAction { 
	
	@Inject
	private String newIconProperty;

	public void execute() throws Exception {
		showDialog();
		setControllers("CloseFromCustomList");
	}

	public String getCustomView() throws Exception {
		return "xava/editors/chooseIcon.jsp";
	}

	public String getNewIconProperty() {
		return newIconProperty;
	}

	public void setNewIconProperty(String newIconProperty) {
		this.newIconProperty = newIconProperty;
	}

}
