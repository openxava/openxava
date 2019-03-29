package org.openxava.actions;


/**
 * @author Federico Alcantara
 *
 */
public class OnChangeChartXColumnAction extends OnChangeChartBaseAction {
		
	/**
	 * @see org.openxava.actions.OnChangeChartBaseAction#executeOnValidValues()
	 */
	@Override
	public void executeOnValidValues() throws Exception {
		if (getNewValue() != null) {
			String propertyName = (String)getNewValue();
			if (propertyName.equals(OnChangeChartColumnNameAction.SHOW_MORE)) {
				getView().getRoot().putObject("xava.myReportColumnShowAllColumns", true);
			} else if (propertyName.equals(OnChangeChartColumnNameAction.SHOW_LESS)) {
				getView().getRoot().putObject("xava.myReportColumnShowAllColumns", false);
			} else {
				getChart().setxColumn(propertyName);	
			}
		}
	}

}
