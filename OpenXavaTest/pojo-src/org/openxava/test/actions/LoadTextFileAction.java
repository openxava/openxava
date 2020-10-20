package org.openxava.test.actions;

import java.util.*;

import javax.validation.*;

import org.apache.commons.fileupload.*;
import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class LoadTextFileAction extends LoadFileItemAction {
	
	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();
			String fileName = fi.getName();			
			if (!Is.emptyString(fileName)) {
				if (!fileName.endsWith(".txt")) {
					addError("only_txt_files_allowed"); 
					break;
				}
				getView().setValue(getProperty(), fi); 
				break;
			}			
		}		
	}	

}