package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {

	private String startDate = "";
	private String modelName;
	private boolean restoreModel = false;

	public void execute() throws Exception {
		if (restoreModel) getView().setModelName(modelName);
		getView().setKeyEditable(true);
		getView().setEditable(true);
		getView().reset();
		if (!startDate.isEmpty()) setValueFromCalendar();
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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	private void setValueFromCalendar() {
		//startDate=date_07/04/2023,endDate=date2_08/04/2023
		//private String endDate = "";
		String[] start = startDate.split("_");
		String name = start[0];
		String dateStr = start[1];
		getView().setValue(name, dateStr);
	}

}
