package org.openxava.actions;

/**
 * 
 * @since 5.8
 * @author Javier Paniza
 */
public class ChangeColumnNameAction extends TabBaseAction {
	
	public void execute() throws Exception {
		validateViewValues();
		setCollection((String) getView().getObject("xava.collection"));		
		getTab().setLabel((String) getView().getObject("xava.property"), getView().getValueString("name"));
		closeDialog();
	}

}
