package org.openxava.actions;

import java.io.*;
import java.text.*;

import javax.json.*;

/**
 * @author Javier Paniza
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {

	private String value = "";
	//private CalendarEvent ce = new CalendarEvent();

	private String modelName;
	private boolean restoreModel = false;

	public void execute() throws Exception {
		if (restoreModel) getView().setModelName(modelName);
		getView().setKeyEditable(true);
		getView().setEditable(true);
		getView().reset();
		if (!value.isEmpty()) {
			String v = value.replaceAll("_", ",");
			JsonReader jsonReader = Json.createReader(new StringReader(v));
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			JsonObject date = jsonObject.getJsonObject("dates");
			String name = date.getString("name");
			String dateStr = date.getString("date");
			getView().setValue(name, dateStr);
		}
		if (getView().hasSections())
			getView().setActiveSection(0);
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) throws ParseException {
		this.value = value;
	}

}
