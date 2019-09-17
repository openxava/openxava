package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * 
 * 
 * @author Javier Paniza
 */
public class ImportDataAction extends ViewBaseAction {  
 
    public void execute() throws Exception {
    	showDialog();
    	getView().setModelName(WithFileItem.class.getSimpleName()); 
    	setControllers("ConfigureImport"); 
    	getView().setTitleId("import_data_title");
    }
 
}