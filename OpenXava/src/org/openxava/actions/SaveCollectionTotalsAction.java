package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */

public class SaveCollectionTotalsAction extends ViewBaseAction implements IForwardAction {  
	
	private String sumProperty;

	public void execute() throws Exception {
		if (getView().isKeyEditable() || getView().getKeyValuesWithValue().isEmpty()) return; 
		String collection = Strings.firstToken(sumProperty, ".");
		View collectionView = getView().getSubview(collection);
		Map<String, List<String>> totalProperties = collectionView.getTotalProperties();
		Map values = new HashMap();
		for (List<String> totalPropertiesByColumn: totalProperties.values()) {
			for (String totalProperty: totalPropertiesByColumn) {
				if (totalProperty.startsWith("__SUM__")) continue;
				String property = Strings.noFirstTokenWithoutFirstDelim(totalProperty, "."); 
				values.put(property, getView().getValue(property));
			}
		}
		MapFacade.setValues(getView().getModelName(), getView().getKeyValues(), values);
	}

	public String getSumProperty() {
		return sumProperty;
	}

	public void setSumProperty(String sumProperty) {
		this.sumProperty = sumProperty;
	}

	public String getForwardURI() { 
		return "javascript:void(0)"; // A dirty trick to show messages of previous action 
	}

	public boolean inNewWindow() { 
		return false;
	}

}
