package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * 
 * 
 * @author Javier Paniza
 */
public class ImportDataAction extends ViewBaseAction /* tmp implements ILoadFileAction */ {  
 
    public void execute() throws Exception {
    	showDialog();
    	getView().setModelName(WithFileItem.class.getSimpleName()); // tmp
    	setControllers("ConfigureImport"); // tmp
    	getView().setTitleId("import_data_title");
    }
 
    /* tmp
    public String[] getNextControllers() {                                      
        return new String [] { "ConfigureImport" };
    }
 
    
    public String getCustomView() {                                             
    	return "xava/editors/chooseFile.jsp?accept=.csv, .xlsx, .xls"; 
    }
 
    public boolean isLoadFile() {                                               
        return true;
    }
    */
 
}