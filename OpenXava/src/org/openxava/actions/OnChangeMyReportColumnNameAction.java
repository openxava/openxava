package org.openxava.actions;

import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza 
 */

public class OnChangeMyReportColumnNameAction extends OnChangeMyReportColumnBaseAction { 
	

	public final static String SHOW_MORE="__MORE__";
	
	public void execute() throws Exception {
		String propertyName = (String) getNewValue();
		if (SHOW_MORE.equals(propertyName)) {
			getView().putObject("xava.myReportColumnShowAllColumns", true);
			propertyName = null;
		}
		if (Is.emptyString(propertyName)) {
			getView().setValue("label", ""); 
			getView().setValue("comparator", EMPTY_COMPARATOR);
			getView().setHidden("sum", true); // We hide it by default, because there are more non-summable properties than summable ones
			showStandardMembers();
			return;
		}		
		MetaProperty property = getTab().getMetaTab().getMetaModel().getMetaProperty(propertyName);
		MyReportColumn column = (MyReportColumn) getView().getModel();
		if (column != null && propertyName.equals(column.getName())) {
			getView().setValue("label", column.getLabel());
		}
		else {
			property = property.cloneMetaProperty();
			property.setQualifiedName(propertyName);
			getView().setValue("label", property.getQualifiedLabel(Locales.getCurrent()));
		}
		getView().setHidden("sum", !getTab().isTotalCapable(property));
		if (property.isCalculated()) {
			hideMembers();
			return;
		}
		
		if (java.util.Date.class.equals(property.getType())) {
			setComparator(property);
			showDateValue();
			return;
		}
		
		if (boolean.class.equals(property.getType()) || java.lang.Boolean.class.equals(property.getType())) {		
			showBooleanValue();
			return;
		}
		
		if (property.hasValidValues()) {
			showValidValuesValue();
			return;
		}
		
		String descriptionsListEditorURL = WebEditors.getEditorURLDescriptionsList(
				getTab().getTabName(), getTab().getModelName(), "${propertyKey}", 
				-1, "", propertyName, property.getName());
		
		if (!Is.emptyString(descriptionsListEditorURL)) {
			getView().putObject("xava.myReportColumnDescriptionsListEditorURL", descriptionsListEditorURL); 
			showDescriptionsListValue();
			return;
		}
				
		setComparator(property);
		showStandardMembers();
	}

	
	private void setComparator(MetaProperty property) {
		String value = getView().getValueString("value"); 
		String comparatorValue = Is.emptyString(value)?"":getView().getValueString("comparator"); 
		if (String.class.equals(property.getType())) { 
			getView().setValue("comparator", STRING_COMPARATOR + ":" + comparatorValue); 			
		}
		else if (java.util.Date.class.isAssignableFrom(property.getType()) && 
			!property.getType().equals(java.sql.Time.class)) 
		{ 			
			getView().setValue("comparator", DATE_COMPARATOR + ":" + comparatorValue); 			
		}
		else {			
			getView().setValue("comparator", OTHER_COMPARATOR + ":" + comparatorValue); 			
		}
	}
	
}
