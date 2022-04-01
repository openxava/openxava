package org.openxava.actions;

import java.util.*;

/**
 * Allows you execute an action on the file(s) uploaded
 * in the last form of type multipart/form-data. <p>
 * 
 * It can be used in a non-web context
 * because it has no references to servlets technologies. 
 * 
 * @author Javier Paniza
 */

public interface IProcessLoadedFileAction extends IAction {
	
	/**
	 * 
	 * @param fileItems List of <code>org.apache.commons.fileupload.FileItem</code> 
	 */
	void setFileItems(List fileItems);

}
