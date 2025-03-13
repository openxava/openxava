package org.openxava.actions;

/**
 * An action that can be available for the user or hidden, depend on a programmatic condition. <p>
 * 
 * It can be used for any type of action, even for list action or row action to have action
 * that are only show in some rows, depending on the content.
 * 
 * @since 5.9
 * @author Javier Paniza
 */
public interface IAvailableAction extends IAction { 
	
	/**
	 * If true the action will be available for the user, otherwise it will be hidden. <br/>  
	 * 
	 * This method is executed before determine if the action has to be shown, so before execute().
	 * The action is configured completely (injecting all needed objects and properties) before calling
	 * isAvailable().
	 */
	boolean isAvailable();

}
