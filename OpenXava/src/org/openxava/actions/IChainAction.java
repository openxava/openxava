package org.openxava.actions;

/**
 * Indicates an action that must be executed after the execution of this one. <p>
 * 
 * @author Javier Paniza
 */

public interface IChainAction {
	
	/**
	 * The qualified name of the next action to execute. <p>
	 * 
	 * Qualified name is name of the controller + name of the 
	 * action as in controllers.xml file. For example,
	 * <code>CRUD.new</code>.	
	 */
	String getNextAction() throws Exception;

}
