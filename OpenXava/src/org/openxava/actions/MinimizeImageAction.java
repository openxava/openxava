package org.openxava.actions;



import javax.inject.*;

import org.openxava.session.*;

/**
 * 
 * @author Javier Paniza
 */

public class MinimizeImageAction extends BaseAction {
	
	@Inject
	private Gallery gallery;
	

	public void execute() throws Exception {
		gallery.setMaximized(false);
	}

}
