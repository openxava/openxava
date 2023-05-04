package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {

	private String defaultValues = "";
	private String modelName;
	private boolean restoreModel = false;

	public void execute() throws Exception {
		if (restoreModel) getView().setModelName(modelName);
		getView().setKeyEditable(true);
		getView().setEditable(true);
		getView().reset();
		if (!defaultValues.isEmpty()) setValueFromDefaultValues();
		if (getView().hasSections()) getView().setActiveSection(0);
	}

	public String getNextMode() {
		return IChangeModeAction.DETAIL;
	}

	public void setModel(String modelName) {
		this.modelName = modelName;
	}

	public boolean isRestoreModel() {
		return restoreModel;
	}

	public void setRestoreModel(boolean restoreModel) {
		this.restoreModel = restoreModel;
	}

	public String getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}
	
	private void setValueFromDefaultValues() {
		//defaultvalues=name:dateStr;name2:dateStr
		String[] dates = defaultValues.split(";");
		String[] start = dates[0].split(":");
		String name = start[0];
		String dateStr = start[1];
		getView().setValue(name, dateStr);
	}

}
