package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.model.*;
import org.openxava.session.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza 
 */

public class SaveMyReportColumnAction extends CollectionElementViewBaseAction {
	
	@Inject
	private MyReport myReport; 

	public void execute() throws Exception {
		Map values = getCollectionElementView().getValues();
		Messages errors = MapFacade.validate("MyReportColumn", values);
		if (!errors.isEmpty()) {
			addErrors(errors);
			return;
		}
		MyReportColumn column = new MyReportColumn();
		column.setReport(myReport);
		String columnName = (String) values.get("name"); 
		column.setName(columnName);
		String columnLabel = (String) values.get("label"); 
		column.setLabel(columnLabel);
		if (getCollectionElementView().getMembersNames().containsKey("value")) {
			String value = (String) values.get("value");
			column.setValue(value);
		}
		Date dateValue = (Date) values.get("dateValue");
		column.setDateValue(dateValue);		
		if (getCollectionElementView().getMembersNames().containsKey("comparator")) {
			String comparator = (String) values.get("comparator");
			if (!Is.emptyString(column.getValue())) {
				column.setComparator(comparator);
			}
		}
		Boolean booleanValue = (Boolean) values.get("booleanValue");
		column.setBooleanValue(booleanValue);
		Integer validValuesValue = (Integer) values.get("validValuesValue");
		column.setValidValuesValue(validValuesValue==null?0:validValuesValue);		
		String descriptionsListValue = (String) values.get("descriptionsListValue"); 		
		column.setDescriptionsListValue(descriptionsListValue);
		MyReportColumn.Order order = (MyReportColumn.Order) values.get("order"); 
		column.setOrder(order);	
		Boolean sum = (Boolean) values.get("sum"); 
		column.setSum(sum==null?false:sum);
		Boolean hidden = (Boolean) values.get("hidden"); 
		column.setHidden(hidden==null?false:hidden);		
		
		if (getCollectionElementView().getCollectionEditingRow() < 0) {
			if (alreadyExists(columnName)) {
				column.setHidden(true);
			}			
			myReport.getColumns().add(column);
			addMessage("column_added_to_report", "'" + columnName + "'");						
		}
		else {
			myReport.getColumns().set(getCollectionElementView().getCollectionEditingRow(), column);
			setOnlyOneNotHiddeColumn(columnName); 
			addMessage("report_column_modified", "'" + columnName + "'");			
		}
		closeDialog();
	}

	private void setOnlyOneNotHiddeColumn(String columnName) { 
		boolean found = false;
		for (MyReportColumn column: myReport.getColumns()) {
			if (column.getName().equals(columnName)) {
				if (found) column.setHidden(true);
				else if (!column.isHidden()) found = true;
			}
		}
	}

	private boolean alreadyExists(String columnName) {
		for (MyReportColumn column: myReport.getColumns()) {
			if (column.getName().equals(columnName)) return true;
		}
		return false;
	}

}
