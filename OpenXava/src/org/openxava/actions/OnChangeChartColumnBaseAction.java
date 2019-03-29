package org.openxava.actions;

import javax.inject.Inject;

import org.openxava.session.Chart;
import org.openxava.session.ChartColumn;
import org.openxava.tab.Tab;


/**
 * @author Federico Alcantara
 * @author Javier Paniza
 */
public abstract class OnChangeChartColumnBaseAction extends OnChangeElementCollectionBaseAction {
	@Inject
	private Chart chart;
	
	@Inject
	private Tab tab; 

	private ChartColumn column;
	/**
	 * @see org.openxava.actions.IAction#execute()
	 */
	@Override
	public void execute() throws Exception {
		executeOnValidValues();
		chart.save(tab);
	}
	
	public Chart getChart() {
		return chart;
	}
	
	/**
	 * Gets the chart column object associated with the changed property.
	 * @return Chart column object. Null if not found.
	 * @throws Exception
	 */
	public ChartColumn getChartColumn() throws Exception {
		if (column == null &&
				getRow() > -1 &&
				getChart() != null &&
				!getChart().getColumns().isEmpty() &&
				getRow() < getChart().getColumns().size()) {
			column = getChart().getColumns().get(getRow());
		}
		return column;
	}
		
	public abstract void executeOnValidValues() throws Exception;
	
}
