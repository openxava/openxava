package org.openxava.actions;

import javax.inject.*;

import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoAddColumnsAction extends ViewBaseAction implements ICustomViewAction, IChangeModeAction {  
	
	private String collection;
	@Inject
	private Tab customizingTab;
	
	public void execute() throws Exception {
		String objectName =  Is.emptyString(collection)?"xava_tab":Tab.COLLECTION_PREFIX + collection.replace(".", "_"); 
		setCustomizingTab((Tab) getContext().get(getRequest(), objectName)); 
		getCustomizingTab().setColumnsToAddUntilSecondLevel(true);  
		setControllers("AddColumns"); 
		showDialog(); 	
		getView().setTitleId("choose_property_add_list_prompt"); 
	}

	public String getCustomView() throws Exception {
		return "xava/editors/addColumns";
	}

	public String getNextMode() {
		return DETAIL;		
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public Tab getCustomizingTab() {
		return customizingTab;
	}

	public void setCustomizingTab(Tab customizingTab) {
		this.customizingTab = customizingTab;
	}
	
}
