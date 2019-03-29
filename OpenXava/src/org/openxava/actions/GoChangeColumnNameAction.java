package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * @since 5.8
 * @author Javier Paniza
 */
public class GoChangeColumnNameAction extends TabBaseAction {
	
	private String property;
	
	public void execute() throws Exception {
		showDialog();
		getView().setTitleId("changeColumnName"); 
		WithRequiredLongName dialog = new WithRequiredLongName();
		getView().setModel(dialog);
		String label = getTab().getMetaProperty(property).getQualifiedLabel(getRequest());
		getView().setValue("name", label);
		getView().putObject("xava.property", property);
		getView().putObject("xava.collection", getCollection());
		setControllers("ChangeColumnName"); 
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
