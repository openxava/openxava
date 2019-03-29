package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class SearchAction extends BaseAction implements IChainAction {

	
	
	public void execute() throws Exception {
	}
	
	public String getNextAction() throws Exception {
		return getEnvironment().getValue("XAVA_SEARCH_ACTION");		
	} 		

}
