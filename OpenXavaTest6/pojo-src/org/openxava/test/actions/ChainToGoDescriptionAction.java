package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ChainToGoDescriptionAction extends BaseAction implements IChainAction {

	public void execute() throws Exception {
		
	}

	public String getNextAction() throws Exception {
		return "GoDescription.goDescription";
	}
	
}
