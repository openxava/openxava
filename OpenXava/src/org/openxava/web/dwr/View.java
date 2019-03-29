package org.openxava.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * For accessing to the View from DWR. <p>
 * 
 * @author Javier Paniza
 */

public class View extends ViewBase {
	
	private static Log log = LogFactory.getLog(View.class);
	
	public void setFrameClosed(HttpServletRequest request, HttpServletResponse response, String frameId, boolean closed) { 
		try {
			String [] id = frameId.split("_");
			if (!"ox".equals(id[0])) {
				// Bad format. This method relies in the id format by Ids class
				log.warn(XavaResources.getString("impossible_store_frame_status")); 
				return;
			}
			String application = id[1];
			String module = id[2];
			initRequest(request, response, application, module);

			org.openxava.view.View view = (org.openxava.view.View) 
				getContext(request).get(application, module, "xava_view");
			view.setFrameClosed(frameId, closed);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_store_frame_status"), ex);
		}
		finally {
			cleanRequest();
		}
	}
	
	public void moveCollectionElement(HttpServletRequest request, HttpServletResponse response, String tableId, int from, int to) {
		TableId id = new TableId(tableId, 0);
		if (!id.isValid()) {
			log.error(XavaResources.getString("impossible_store_collection_element_movement"));
			throw new XavaException("impossible_store_collection_element_movement");
		}
		try {
			initRequest(request, response, id.getApplication(), id.getModule());
			org.openxava.view.View view = getView(request, id.getApplication(), id.getModule());
			try {
				view.getSubview(id.getCollection()).moveCollectionElement(from, to);
				XPersistence.commit();
			}
			catch (Exception ex) {
				XPersistence.rollback();
				log.error(XavaResources.getString("impossible_store_collection_element_movement"), ex);
				throw new XavaException("impossible_store_collection_element_movement");
			}
		}
		finally {
			cleanRequest();
		}
	}	
	
}
