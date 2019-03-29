package org.openxava.actions;

import javax.inject.*;

import org.openxava.session.*;
import org.openxava.tab.*;
import org.openxava.web.*;

/**
 * @author Javier Paniza
 */
public class SelectChartTypeAction extends ViewBaseAction {
	
	@Inject
	private Chart chart;
	
	@Inject
	private Tab tab;
		
	private String chartType;
	
	public void execute() throws Exception {
		chart.setChartType(Chart.ChartType.valueOf(chartType));
		getView().setValue("chartType", chart.getChartType());
		Charts.updateView(getRequest(), getView(), tab, chart); 
		chart.save(tab); 
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

}
