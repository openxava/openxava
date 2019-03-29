package org.openxava.actions;

import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;


/**
 * 
 * @author Javier Paniza 
 */

public class OnChangeMyReportColumnComparatorAction extends OnChangeMyReportColumnBaseAction {
	
		
	public void execute() throws Exception {
		String propertyName = getView().getValueString("name");
		if (Is.emptyString(propertyName)) return; 
		MetaProperty property = getTab().getMetaTab().getMetaModel().getMetaProperty(propertyName);
		if (!java.util.Date.class.equals(property.getType())) return;
		String comparator = (String) getNewValue(); 
		if (
			Tab.YEAR_COMPARATOR.equals(comparator) ||
			Tab.MONTH_COMPARATOR.equals(comparator) ||
			Tab.YEAR_MONTH_COMPARATOR.equals(comparator) ||
			Tab.IN_COMPARATOR.equals(comparator) ||
			Tab.NOT_IN_COMPARATOR.equals(comparator)
		) {
			showStandardMembers();
		}
		else {
			showDateValue();
		}
		getView().setValue("comparator", DATE_COMPARATOR + ":" + comparator);
	}

}
