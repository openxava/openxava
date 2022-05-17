package org.openxava.actions;

/**
 * It allows to navigate to a view with possibility of load a file. <p>  
 * 
 * This is for web application, where forms for load files are
 * different (ENCTYPE="multipart/form-data") from the normal ones.
 *
 * Since it does not do direct reference to HTTP, it can be used
 * in non web context; simply ignoring this interface.   
 * 
 * Possibly you need an IProcessLoadedFileAction action in the next controller.<br>
 *    
 * @author Javier Paniza
 */

public interface ILoadFileAction extends INavigationAction {
	
	boolean isLoadFile();

}
