package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public interface IRemoteAction extends IAction, java.io.Serializable {
	
	void executeBefore() throws Exception; 
	
	void executeAfter() throws Exception; 	
	
	boolean isExecuteOnServer();
	
	/**
	 * Package, usually the project name in lowercase. 
	 */
	String getPackageName();

}
