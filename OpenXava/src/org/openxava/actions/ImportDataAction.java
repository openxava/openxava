package org.openxava.actions;

/**
 * 
 * 
 * @author Javier Paniza
 */
public class ImportDataAction extends ViewBaseAction implements ILoadFileAction {  
 
    public void execute() throws Exception {
    	showDialog();
    }
 
    public String[] getNextControllers() {                                      
        return new String [] { "ConfigureImport" };
    }
 
    public String getCustomView() {                                             
    	return "xava/editors/chooseFile.jsp?accept=.csv, .xlsx, .xls"; 
    }
 
    public boolean isLoadFile() {                                               
        return true;
    }
 
}