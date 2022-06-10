package org.openxava.web.dwr;

import javax.servlet.http.*;

/**
 * Base class for DWR class that uses View. <p>
 * 
 * @author Javier Paniza
 * @since 5.6.1
 */

public class ViewBase extends DWRBase {
	
	protected org.openxava.view.View getView(HttpServletRequest request, String application, String module) { 
		org.openxava.view.View view = (org.openxava.view.View)		
			getContext(request).get(application, module, "xava_view"); 
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		view.setRequest(request);
		return view;
	}

}
