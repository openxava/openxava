package org.openxava.actions;

/**
 * To go to another module. <p>
 * 
 * @author Javier Paniza
 */

public interface IChangeModuleAction extends IAction {
	
	final static String PREVIOUS_MODULE = "__PREVIOUS_MODULE__";
	
	/**
	 * The name of the module to go. <p>
	 * 
	 * @return if null no navigation is produces, and if PREVIOUS_MODULE 
	 * 	then returns to the previous module. 
	 */
	String getNextModule();
	
	boolean hasReinitNextModule();

}
