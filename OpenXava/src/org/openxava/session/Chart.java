package org.openxava.session;

import java.io.*;
import java.util.*;
import java.util.prefs.*;

import javax.persistence.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.annotations.*;
import org.openxava.model.meta.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;

/**
 * 
 * @author Federico Alcantara
 * @author Javier Paniza
 */

@View(members = 
	"chartType[chartType; columns], chartData;" + 
	"xColumn;"  
)	
public class Chart implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(Chart.class); 
	private static final String CHART_TYPE = "chartType"; 
	private static final String X_COLUMN = "xColumn"; 
		
	@LabelFormat(LabelFormatType.NO_LABEL)
	private String chartData;

	public enum ChartType {
		BAR("bar"),		
		LINE("line"),
		PIE("pie");
		String jsType;
		private ChartType(String jsType) {
			this.jsType = jsType;
		}
		public String jsType() {
			return jsType;
		}
	};
	@LabelFormat(LabelFormatType.NO_LABEL) 
	private ChartType chartType;
	
	@LabelFormat(LabelFormatType.NO_LABEL) 
	@OnChange(value = OnChangeChartXColumnAction.class)
	private String xColumn;  
	
	@RemoveSelectedAction("Chart.removeColumn")
	@ListProperties("name")
	@ElementCollection
	private List<ChartColumn> columns;
	
	private boolean rendered;
	
	private String nodeName;
	
	
	public static Chart create(org.openxava.tab.Tab tab) { 
		Chart chart = new Chart();
		chart.setNodeName(tab);
		chart.setxColumn(getAxisColumns(tab));
		chart.createColumns(tab, true, chart.getxColumn());
		chart.setChartType(ChartType.BAR);
		return chart;
	}
	
	public static Chart load(org.openxava.tab.Tab tab) { 
		try { 			
			Chart result = new Chart();
			result.setNodeName(tab);  
			result.load();
			if (result.getChartType() == null) return null;
			return result;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("warning_load_preferences_charts"),ex);
			return null; 
		}
	}
	
	private void load() throws BackingStoreException { 
		Preferences preferences = getPreferences();
		String schartType = preferences.get(CHART_TYPE, null);
		if (schartType == null) return;
		chartType  = ChartType.valueOf(schartType);
		xColumn = preferences.get(X_COLUMN, null);
		int i = 0;
		ChartColumn column = new ChartColumn();
		columns = new ArrayList<ChartColumn>();
		while (column.load(preferences, i++)) {
			columns.add(column);
			column = new ChartColumn();
		}
	}
	
	private Preferences getPreferences() throws BackingStoreException { 
		return Users.getCurrentPreferences().node(nodeName); 
	}
	
	private void setNodeName(Tab tab) { 
		nodeName = tab.getPreferencesNodeName("charts.");  
	}
		
	public void save(Tab tab) throws BackingStoreException { 
		if (nodeName == null) return; 
		if (!Is.emptyString(tab.getGroupBy())) return; 
		Preferences preferences = getPreferences();
		preferences.put(CHART_TYPE, chartType.name());		
		preferences.put(X_COLUMN, xColumn);
		
		int i = 0;
		for (ChartColumn column: columns) {
			column.save(preferences, i++); 
		}
		while (ChartColumn.remove(preferences, i)) i++; 		
		preferences.flush();
	}
	
	private static String getAxisColumns(org.openxava.tab.Tab tab) {
		for (Object metaPropertyObject : tab.getMetaProperties()) {
			MetaProperty metaProperty = (MetaProperty)metaPropertyObject;
			return metaProperty.getQualifiedName(); 			
		}
		return "";	
	}
	
	private void createColumns(Tab tab, boolean addNumeric, String ignore) {  
		columns = new ArrayList<ChartColumn>();
		boolean numericChosen = false;
		for (MetaProperty property: tab.getMetaProperties()) {
			if (!property.isNumber() || property.getQualifiedName().equals(getxColumn())) { 
				continue;
			}
			ChartColumn column = new ChartColumn();
			column.setChart(this);			
			column.setName(property.getQualifiedName());
			column.setNumber(property.isNumber());
			
			try {
				if (addNumeric 
						&& property.isNumber() 
						&& !numericChosen
						&& !property.getName().equals(ignore)) 
				{
					numericChosen = true;
				}
				columns.add(column);
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}		
	}	

	public String getChartData() {
		return chartData;
	}

	public void setChartData(String data) {
		this.chartData = data;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public String getxColumn() {
		return xColumn;
	}

	public void setxColumn(String xAxis) {
		this.xColumn = xAxis;
	}

	public List<ChartColumn> getColumns() {
		if (chartType == ChartType.PIE && columns != null && columns.size() > 1) {
			return Collections.singletonList(columns.get(0));
		}
		return columns;
	}

	public void setColumns(List<ChartColumn> columns) {
		this.columns = columns;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
		
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Chart [chartType=");
		builder.append(chartType);
		builder.append("]");
		return builder.toString();
	}

}
