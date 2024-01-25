package org.openxava.test.actions;

import org.openxava.actions.*;

public class ChangeSubViewPropertyLabelAction extends ViewBaseAction {
	
	private String subView;
	private String property;
	private String label;

	public void execute() throws Exception {
		getView().getSubview(subView).setLabelId(property, label);
		getView().getSubview(subView).reloadMetaModel();
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getSubView() {
		return subView;
	}
	public void setSubView(String subView) {
		this.subView = subView;
	}
	
	
}
