package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;

/**
 * @since 7.4
 * @author Chungyen Tsai
 */
public class OpenCollectionElementInNewTabAction extends CollectionElementViewBaseAction implements IForwardAction, IAvailableAction {

	private String nextURI = "";
	private int row;

	@Override
	public void execute() throws Exception {
		Collection elements;
		Map keys = null;
		if (getCollectionElementView().isCollectionFromModel()) {
			elements = getCollectionElementView().getCollectionValues();
			if (elements == null) return;
			int rowValue = getCollectionElementView().getCollectionEditingRow();
			row = (rowValue > 0) ? rowValue : getRow();
			if (elements instanceof List) {
				keys = (Map) ((List) elements).get(row);
			}
		} else { 
			keys = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
		}
		
		Object o = MapFacade.findEntity(getCollectionElementView().getModelName(), keys);
		keys = MapFacade.getKeyValues(getCollectionElementView().getModelName(), o);
		
		if (keys != null) {
			nextURI = getOrganizationPrefix() + "/m/" + getReferencedModel() + "?detail=" + getReferencedId(keys);
		}

	}
	
	@Override
	public boolean isAvailable() {
		return !(getCollectionElementView().getMetaModel().getAllKeyPropertiesNames().size() > 1);
	}

	@Override
	public String getForwardURI() {
		return nextURI;
	}

	private String getReferencedModel() {
		return getCollectionElementView().getMetaModel().getName();
	}

	private String getReferencedId(Map map) {
		String firstValue = map.values().iterator().next().toString();
		return firstValue;
	}

	private String getOrganizationPrefix() {
		String organization = OrganizationsCurrent.get(getRequest());
		return Is.emptyString(organization)?"":"/o/" + organization;
	}
	
	@Override
	public boolean inNewWindow() {
		return true;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		row = i;
	}

}
