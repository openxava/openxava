package org.openxava.actions;

import java.text.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * @author Javier Paniza
 * @author Chungyen Tsai
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {

	private String defaultValues = "";
	private String modelName;
	private boolean restoreModel = false;

	public void execute() throws Exception {
		System.out.println("crud new " + defaultValues); 
		
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

	/**
	 * A string with default values in string format. <br>
	 * 
	 * We use : to separate key from value and ; to separate entries, thus:
	 * <pre>
	 * name:John;date:2/2/2034
	 * </pre>
	 *  
	 * @since 7.1 
	 */
	public String getDefaultValues() {
		return defaultValues;
	}

	/**
	 * A string with default values in string format. <br>
	 * 
	 * We use : to separate key from value and ; to separate entries, thus:
	 * <pre>
	 * name:John;date:2/2/2034
	 * </pre>
	 *  
	 * @since 7.1 
	 */
	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}
	
	private void setValueFromDefaultValues() throws XavaException, ParseException {
		//defaultvalues=name:dateStr;name2:dateStr
		//String[] dates = defaultValues.split(";");
		int firstColonIndex = defaultValues.indexOf(":");
		String name = defaultValues.substring(0, firstColonIndex);
		//String date = dates[1];
		MetaProperty mp = getView().getMetaProperty(name); 
		Object value = null;
		System.out.println(defaultValues.substring(firstColonIndex + 1).trim());
		int firstSpaceIndex = defaultValues.indexOf(" ");
		
		//System.out.println(defaultValues.substring(firstColonIndex +1, firstSpaceIndex));
		try {//
			if (firstSpaceIndex != 1) {
				value = WebEditors.parse(getRequest(), mp, defaultValues.substring(firstColonIndex + 1).trim(), getErrors(), getView().getViewName());
			} else {
				value = WebEditors.parse(getRequest(), mp, defaultValues.substring(firstColonIndex +1, firstSpaceIndex), getErrors(), getView().getViewName());
			}
			//normal
			//value = mp.parse(defaultValues.substring(firstColonIndex + 1).trim());
			
			//value = WebEditors.parse(getRequest(), mp, defaultValues.substring(firstColonIndex +1, firstSpaceIndex), getErrors(), getView().getViewName());
		} catch(Exception e) {
			//con hora
			//firstSpaceIndex != 1
			//value = mp.parse(defaultValues.substring(firstColonIndex +1, firstSpaceIndex));
			//value = WebEditors.parse(getRequest(), mp, defaultValues.substring(firstColonIndex +1, firstSpaceIndex), getErrors(), getView().getViewName());
		}
		getView().setValue(name, value);
	}

}
