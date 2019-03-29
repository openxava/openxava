package org.openxava.actions;

/**
 * To change the module mode. <p>
 * 
 * It is implemented by {@link BaseAction}, so if your 
 * extends from it you can change the mode 
 * just by calling to {@link BaseAction#setNextMode} (since 4m1). <p> 
 * 
 * @author Javier Paniza
 */

public interface IChangeModeAction extends IAction {
	
	public final static String LIST = "list";
	public final static String DETAIL = "detail";
	/** @since 4m5 */
	@Deprecated
	public final static String SPLIT = "split"; 
	public final static String PREVIOUS_MODE = "__PREVIOUS_MODE__";
	
	/**
	 * LIST, DETAIL or PREVIOUS_MODE.
	 */
	String getNextMode();

}
