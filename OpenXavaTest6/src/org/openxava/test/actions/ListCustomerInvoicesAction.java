package org.openxava.test.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.controller.*;
import org.openxava.tab.*;


/**
 * @author Javier Paniza
 */
public class ListCustomerInvoicesAction extends BaseAction implements IChangeModuleAction, IModuleContextAction {
	
	private int row;
	@Inject
	private Tab tab;
	private ModuleContext context;

	public void execute() throws Exception {
		Map customerKey = (Map) tab.getTableModel().getObjectAt(row);
		int customerNumber = ((Integer) customerKey.get("number")).intValue();
		Tab invoiceTab = (Tab) context.get("OpenXavaTest", getNextModule(), "xava_tab");
		invoiceTab.setBaseCondition("${customer.number} = " + customerNumber);
	}

	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	
	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public String getNextModule() {
		return "CustomerInvoices";
	}

	public void setContext(ModuleContext context) {
		this.context = context;		
	}

	public boolean hasReinitNextModule() {
		return true;
	}

}
