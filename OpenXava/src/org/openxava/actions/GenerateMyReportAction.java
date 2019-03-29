package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.session.*;
import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class GenerateMyReportAction extends GenerateReportAction {
	
	private static Log log = LogFactory.getLog(GenerateMyReportAction.class);
	
	@Inject
	private MyReport myReport; 
	
	public void execute() throws Exception {
		super.execute();		
		getRequest().getSession().removeAttribute("xava_selectedRowsReportTab"); 
		getRequest().getSession().removeAttribute("xava_selectedKeysReportTab"); 
		
		myReport.setName(getView().getValueString("name")); 
		Tab tab = new Tab();
		tab.setModelName(getTab().getModelName());
		tab.setTabName(getTab().getTabName());
		tab.setTitle(getView().getValueString("name").replaceAll(MyReport.SHARED_REPORT, ""));
		tab.clearProperties();		
		Collection<String> comparators = new ArrayList<String>();
		Collection<String> values = new ArrayList<String>();
		StringBuffer order = new StringBuffer();
		int columnCountLimit = 0;
		for (MyReportColumn column: myReport.getColumns()) {
			if (column.isHidden()) continue;
			addColumn(tab, comparators, values, order, column);
			columnCountLimit++;
		}			
		for (MyReportColumn column: myReport.getColumns()) {
			if (column.isHidden()) {
				addColumn(tab, comparators, values, order, column);
			}
		}
		if (myReport.getColumns().size() > columnCountLimit) {
			getRequest().getSession().setAttribute("xava_columnCountLimitReportTab", columnCountLimit);
		}
		if (order.length() > 0) {
			tab.setDefaultOrder(order.toString());			
		}		
		tab.setConditionComparators(comparators);
		tab.setConditionValues(values);

		try {
			myReport.save();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("my_report_save_problems"), ex);
		}
		getRequest().getSession().setAttribute("xava_reportTab", tab);
		closeDialog();
	}

	private void addColumn(Tab tab, Collection<String> comparators,
			Collection<String> values, StringBuffer order,
			MyReportColumn column) 
	{
		tab.addProperty(column.getName());
		tab.setLabel(column.getName(), column.getLabel()); 
		if (column.isCalculated())
			return;
		if (column.getComparator() != null) {
			comparators.add(column.getComparator());
			values.add(column.getValueForCondition());				
		}
		else {
			comparators.add(null);
			values.add(null);				
		}
		if (column.getOrder() != null) {
			order.append(order.length() == 0?"":", ");
			order.append("${");
			order.append(column.getName());
			order.append("} ");
			order.append(column.getOrder() == MyReportColumn.Order.ASCENDING?"ASC":"DESC");				
		}
		if (column.isSum()) {
			tab.addSumProperty(column.getName());
		}
		else {
			tab.removeSumProperty(column.getName());
		}
	}

}
