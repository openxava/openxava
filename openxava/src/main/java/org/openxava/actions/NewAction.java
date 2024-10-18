package org.openxava.actions;

import org.openxava.model.meta.*;
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
	
	private void setValueFromDefaultValues() {
		//defaultvalues=name:dateStr;name2:dateStr
		int firstColonIndex = defaultValues.indexOf(":");
		String name = defaultValues.substring(0, firstColonIndex);
		MetaProperty mp = getView().getMetaProperty(name);
		Object value = null;
		int firstSpaceIndex = defaultValues.indexOf(" ");
		if (mp.isDateType()) {
			value = WebEditors.parse(getRequest(), mp, defaultValues.substring(firstColonIndex +1, firstSpaceIndex), getErrors(), getView().getViewName());
		} else if (mp.isDateTimeType()) {
			value = WebEditors.parse(getRequest(), mp, defaultValues.substring(firstColonIndex + 1).trim(), getErrors(), getView().getViewName());
		} else {
			value = defaultValues.substring(firstColonIndex + 1).trim();
		}
		getView().setValue(name, value);
	}

}
