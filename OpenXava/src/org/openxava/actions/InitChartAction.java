package org.openxava.actions;

import javax.inject.*;
import org.openxava.session.*;
import org.openxava.web.*;

/**
 * @since 5.7
 * @author Javier Paniza
 */
public class InitChartAction extends TabBaseAction { 
	
	@Inject
	private Chart chart;
	
	public void execute() throws Exception {
		getView().setModelName("Chart");
		getView().setEditable(true);
		chart = Chart.load(getTab());
		if (chart == null) chart = Chart.create(getTab());
		Charts.updateView(getRequest(), getView(), getTab(), chart);
	}

}
