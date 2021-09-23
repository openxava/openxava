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
    	// tmp getView().setModelName(WithFileItem.class.getSimpleName());
    	getView().setModelName(WithExcelCSVFileItem.class.getSimpleName()); // tmp
    	setControllers("ConfigureImport"); 
    	getView().setTitleId("import_data_title");
    }
 
}