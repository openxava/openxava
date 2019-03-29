package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */
public class RemoveIconAction extends ViewBaseAction {
	
	private String newIconProperty;

	public void execute() throws Exception {
		getView().setValue(newIconProperty, null);
	}

	public String getNewIconProperty() {
		return newIconProperty;
	}

	public void setNewIconProperty(String newIconProperty) {
		this.newIconProperty = newIconProperty;
	}

}
