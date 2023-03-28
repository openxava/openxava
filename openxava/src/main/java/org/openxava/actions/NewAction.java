package org.openxava.actions;

import java.text.*;

/**
 * @author Javier Paniza
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {
	
	private String value = ""; 
	
	private String modelName; 
	private boolean restoreModel = false; 
	
	public void execute() throws Exception {
		if (restoreModel) getView().setModelName(modelName); 
		getView().setKeyEditable(true);
		getView().setEditable(true);
		getView().reset();
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) throws ParseException { 
		System.out.println(value.replace("*", ","));
		/*
		JsonReader jsonReader = Json.createReader(new StringReader(value));
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		for (String key : jsonObject.keySet()) {
		    String v = jsonObject.get(key).toString();
		    System.out.println(key + ": " + v);
		}*/
		this.value = value;		
	}
	
}
