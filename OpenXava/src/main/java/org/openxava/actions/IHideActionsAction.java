package org.openxava.actions;

/**
 * Allows to hide a group of actions. <p>
 * 
 * You can just use the removeActions() and clearActions() methods of {@link BaseAction} 
 * instead of implementing this interface.
 * 
 * @author Javier Paniza
 */

public interface IHideActionsAction {
	
	/**
	 * The list of actions to hide. <p>
	 * 
	 * @return If <code>null</code> no actions are hidden.
	 */
	String [] getActionsToHide();

}
