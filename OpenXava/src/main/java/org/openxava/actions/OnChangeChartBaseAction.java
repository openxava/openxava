package org.openxava.actions;

import javax.inject.Inject;

import org.openxava.session.Chart;
import org.openxava.tab.Tab;
import org.openxava.web.Charts;

/**
 * Used for changes of the chart properties.
 * @author Federico Alcantara
 * @author Javier Paniza
 */
public abstract class OnChangeChartBaseAction extends OnChangePropertyBaseAction {
	
	@Inject
	private Chart chart;
	
	@Inject
	private Tab tab;
	
	protected Chart getChart() {
		return chart;
	}
	
	protected Tab getTab() {
		return tab;
	}
	
	/**
	 * @see org.openxava.actions.IAction#execute()
	 */
	@Override
	public void execute() throws Exception {
		if (getNewValue() != null) {
			executeOnValidValues();
			Charts.updateView(getRequest(), getView(), getTab(), chart);
			chart.save(getTab()); 
		}
	}
	
	/**
	 * Calls upon finding valid new values.
	 * @throws Exception
	 */
	protected abstract void executeOnValidValues() throws Exception;

}
