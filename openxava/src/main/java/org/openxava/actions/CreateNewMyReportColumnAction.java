package org.openxava.actions;

/**
 * 
 * @author Javier Paniza 
 */
public class CreateNewMyReportColumnAction extends CreateNewElementInCollectionAction {

	public void execute() throws Exception {
		super.execute();
		getCollectionElementView().removeObject("xava.myReportColumnShowAllColumns");
	}
	
}
