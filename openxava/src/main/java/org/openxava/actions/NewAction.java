package org.openxava.actions;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.json.*;

import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * @author Javier Paniza
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {
	
	private String value = ""; 
	private CalendarEvent ce = new CalendarEvent();
	
	private String modelName; 
	private boolean restoreModel = false; 
	
	public void execute() throws Exception {
		if (restoreModel) getView().setModelName(modelName); 
		getView().setKeyEditable(true);
		getView().setEditable(true);
		getView().reset();
		if (value.length()>1) {
			System.out.println(ce.getStartLabel()+ ": " + ce.getStart());
			getView().setValue(ce.getStartLabel(), ce.getStart());
		}
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
		System.out.println("set");
		String v = value.replaceAll("_", ",");
		System.out.println(v);
		JsonReader jsonReader = Json.createReader(new StringReader(v));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
		
        JsonArray dates = jsonObject.getJsonArray("dates");
        for (int i = 0; i < dates.size(); i++) {
            JsonObject date = dates.getJsonObject(i);
            String label = date.getString("label");
            String dateStr = date.getString("date");
            ce.setStartLabel(label);
            
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date d = format.parse(dateStr);
            //ce.setStartAsDate(d);
            ce.setStart(dateStr);
            System.out.println(label + ": " + dateStr);
        }
		
		this.value = v;		
	}
	
	private String getFormat() throws ParseException {
		String dateFormat = Dates.dateFormatForJSCalendar();
		dateFormat = dateFormat.replace("n", "M")
							   .replace("m", "MM")
							   .replace("d", "dd")
							   .replace("j", "d")
							   .replace("Y", "yyyy");
        return dateFormat;

	}
	
}
