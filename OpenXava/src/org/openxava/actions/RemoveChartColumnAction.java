package org.openxava.actions;

import javax.inject.Inject;

import org.openxava.session.Chart;
import org.openxava.tab.Tab;
import org.openxava.web.Charts;

/**
 * @author Federico Alcantara
 * @author Javier Paniza
 */
public class RemoveChartColumnAction extends RemoveSelectedInCollectionAction {
	
	@Inject
	private Chart chart;
	
	@Inject
	private Tab tab; 
	
	/**
	 * @see org.openxava.actions.CollectionElementViewBaseAction#execute()
	 */
	@Override
	public void execute() throws Exception {
		if (getRow() > -1 && getRow() < chart.getColumns().size()) {
			chart.getColumns().remove(getRow());
			Tab tab = (Tab) getContext().get(getRequest(), "xava_tab");
			if (getView() != null && tab != null) {
				Charts.updateView(getRequest(), getView(), tab, chart); 
			}
			if (getView() != null) {
				getView().refreshCollections();
			}
			chart.save(tab); 
		}
	}

}
