package org.openxava.test.actions;

import javax.inject.*;
import org.openxava.actions.*;
import org.openxava.session.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ShowCutRowsAction extends BaseAction {
	
	@Inject
	private CutCollectionElements cutCollectionElements;
	
	public void execute() throws Exception {
		addMessage("cut_rows", cutCollectionElements.getElements());
	}
	
}
