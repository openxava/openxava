/**
 * 
 */
package org.openxava.actions;

import org.apache.commons.lang3.StringUtils;
import org.openxava.util.Is;
import org.openxava.util.Strings;
import org.openxava.web.Ids;

/**
 * @author Federico Alcantara
 * Handles on change when occurs on elements in collection action
 *
 */
public abstract class OnChangeElementCollectionBaseAction extends CollectionElementViewBaseAction
		implements IOnChangePropertyAction {
	
	private String propertyName;
	private Object value;
	private Integer row = null;
	
	@Override
	public void setChangedProperty(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Gets the changed property.
	 * @return Returns the changed property name.
	 */
	public String getChangedProperty() {
		return propertyName;
	}
	/**
	 * @see org.openxava.actions.IOnChangePropertyAction#setNewValue(java.lang.Object)
	 */
	@Override
	public void setNewValue(Object value) {
		this.value = value;
	}

	public Object getNewValue() {
		return value;
	}
	
	public int getRow() {
		if (row == null) {
			row = -1;
			if (getCollectionElementView().isRepresentsElementCollection()) {
				String propertyId = getRequest().getParameter("xava_changed_property");
				if (!Is.emptyString(propertyId)) {
					propertyId = Ids.undecorate(propertyId);
					String rowString = "-1";
					while(propertyId.indexOf(".") > -1) {
						if (StringUtils.isNumeric(Strings.firstToken(propertyId, "."))) {
							rowString = Strings.firstToken(propertyId, ".");
							break;
						}
						propertyId = Strings.noFirstTokenWithoutFirstDelim(propertyId, ".");
					}
					row = Integer.parseInt(rowString);
				}
			}
		}
		return row;
	}

}
