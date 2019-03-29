package org.openxava.actions;



import javax.inject.*;

import org.openxava.session.*;

/**
 * 
 * @author Javier Paniza
 */

public class MaximizeImageAction extends BaseAction {
	
	@Inject
	private Gallery gallery;
	private String oid;
	
	
	public void execute() throws Exception {
		gallery.setMaximized(true);
		gallery.setMaximizedOid(oid);
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {		
		this.oid = oid;
	}

}
