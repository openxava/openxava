package org.openxava.actions;

import java.text.*;
import java.util.*;

import org.openxava.util.*;

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
		this.value = formatDate(value);		
	}
	
	private String formatDate(String date) throws ParseException {
		String dateFormat = Dates.dateFormatForJSCalendar();
		dateFormat = dateFormat.replace("n", "M")
							   .replace("j", "d")
							   .replace("m", "MM")
							   .replace("d", "dd")
							   .replace("Y", "yyyy");
		
		SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
		Date d = in.parse(date);
		SimpleDateFormat out = new SimpleDateFormat(dateFormat);
        String outputDateStr = out.format(d);
        return outputDateStr;
		
	}
	
	
}
