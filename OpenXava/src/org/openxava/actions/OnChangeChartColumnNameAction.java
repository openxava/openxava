package org.openxava.actions;

import org.openxava.session.*;
import org.openxava.web.*;


/**
 * 
 * @author Federico Alcantara
 * @see org.openxava.actions.OnChangeMyReportColumnNameAction
 */

public class OnChangeChartColumnNameAction extends OnChangeChartColumnBaseAction {
	
	public final static String SHOW_MORE="__MORE__"; 
	public final static String SHOW_LESS="__LESS__"; 
	
	/* tmr
	@Inject
	private Tab chartTab; // tmr
	*/


	@Override
	public void executeOnValidValues() throws Exception {
		String propertyName = (String)getNewValue();
		if (propertyName == null) {
			if (getChartColumn() != null) {
				getChart().getColumns().remove(getRow());
			}
		} else if (propertyName.equals(SHOW_MORE)) { 	
			getView().getRoot().putObject("xava.myReportColumnShowAllColumns", true);
		} else if (propertyName.equals(SHOW_LESS)) { 	
			getView().getRoot().putObject("xava.myReportColumnShowAllColumns", false);
		} else {
			ChartColumn column = getChartColumn();
			if (column == null) {
				System.out.println("[OnChangeChartColumnNameAction.executeOnValidValues] Entro"); // tmp
				column = new ChartColumn();
				column.setChart(getChart());
				column.setName(propertyName);
				column.setNumber(true); // Only number types are added here.
				getChart().getColumns().add(column);
				// tmr ini
				/*
				List<ChartColumn> chartColumns = getChart().getColumns();
				if (!chartColumns.contains(column)) {
					System.out.println("[OnChangeChartColumnNameAction.executeOnValidValues] Adding " + column.getName()); // tmp
					chartColumns.add(column);
					updateView();
				}
				else {
					System.out.println("[OnChangeChartColumnNameAction.executeOnValidValues] Not added " + column.getName()); // tmp
				}
				*/
				// tmr fin
			}
			System.out.println("[OnChangeChartColumnNameAction.executeOnValidValues] propertyName=" + propertyName); // tmp
			System.out.println("[OnChangeChartColumnNameAction.executeOnValidValues] column.getName()=" + column.getName()); // tmp
			column.setName(propertyName);
			updateView(propertyName);
		}
	}
	
	protected void updateView(String propertyName) throws Exception { // tmr
		System.out.println("[OnChangeChartColumnNameAction.updateView] Trying to update view"); // tmp
		// TMR ME QUEDÉ POR AQUÍ: AL INYECTAR chartTab DEJA DE FUNCIONAR. ¿BUSCAR OTRA FORMA DE OPTIMIZAR?
		// tmr if (chartTab != null && chartTab.containsProperty(propertyName)) return;
		if (getView() != null && getTab() != null) {
			System.out.println("[OnChangeChartColumnNameAction.updateView] UPDATING VIEW"); // tmp
			long ini = System.currentTimeMillis(); // tmr
			Charts.updateView(getRequest(), getView(), getTab(), getChart());
			long cuesta = System.currentTimeMillis() - ini; // tmr
			System.out.println("[OnChangeChartColumnNameAction.updateView] cuesta=" + cuesta); // tmp
		}
	}

	
}
