package org.openxava.actions;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.view.meta.*;

/**
 * 
 * @author José Luis Dorado
 */
public class OpenSearchDialogAction extends ViewBaseAction {
	
	private static Log log = LogFactory.getLog(OpenSearchDialogAction.class);
	
	private static final String VIEW_SEARCH_NAME = "Search";
	private static final String BACKUP_VIEW_SEARCH_NAME = "Simple";

	public void execute() throws Exception {
		MetaView mview = null;
		try {
			mview = getView().getMetaModel().getMetaView(VIEW_SEARCH_NAME);
		} 
		catch (ElementNotFoundException ex) {
			log.warn(XavaResources.getString("search_view_missing", getModelName()));
			try {
				mview = getView().getMetaModel().getMetaView(BACKUP_VIEW_SEARCH_NAME);
			} 
			catch (ElementNotFoundException ex2) {
				log.warn(XavaResources.getString("simple_view_missing", getModelName()));
				mview = getView().getMetaModel().getMetaViewByDefault();
			}
		} 
		finally {
			showDialog();
			getView().setModelName(mview.getModelName());
			getView().setViewName(mview.getName());
			setControllers("Search");
		}
	}
	
}