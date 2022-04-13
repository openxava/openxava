package org.openxava.actions;

/**
 * Allows to show a group of actions previously shown. <p>
 * 
 * You can just use the addActions() method of {@link BaseAction} instead of 
 * implementing this interface.
 * 
 * @author Javier Paniza
 */

public interface IShowActionsAction {

	/**
	 * The list of actions to show. <p>
	 * 
	 * @return If <code>null</code> no actions are shown.
	 */
	String [] getActionsToShow();
	
}
