package org.openxava.actions;



import javax.inject.*;

import org.openxava.session.*;

/**
 * tmp Quitar
 * @author Javier Paniza
 */

public class MaximizeImageAction extends BaseAction {
	
	@Inject
	private Gallery gallery;
	private String oid;
	
	
	public void execute() throws Exception {
		/* tmp
		gallery.setMaximized(true);
		gallery.setMaximizedOid(oid);
		*/
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {		
		this.oid = oid;
	}

}
