package org.openxava.actions;

/**
 * As {@link IChangeAction} but you can indicate values for filling
 * properties of the chained action too. <p>
 * 
 * @see IChangeAction
 * @author Javier Paniza
 */

public interface IChainActionWithArgv extends IChainAction {
	
	/**
	 * Values for filling the properties of the chained action. <p>
	 * 
	 * The chained action is the one returned in {@link IChainAction#getNextAction()}.<br>
	 * The values arguments returned by this method will fill the properties of the action.<br>
	 * The format is like this one:
	 * <pre>
	 * propertyA=valueA,propertyB=valueB
	 * </pre>
	 * In this case, valueA will be injected in propertyA of the chained action,
	 * and valueB will be injected in the propertyB of chained action before execute it.
	 *  
	 * @return
	 * @throws Exception
	 */
	String getNextActionArgv() throws Exception;

}
