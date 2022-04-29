package org.openxava.test.actions;

import java.text.*;
import java.util.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * For print an Invoice using a JasperReport custom design. <p>
 * 
 * @author Javier Paniza
 */
public class InvoiceReportAction extends JasperReportBaseAction implements IChainAction {
	
	private IInvoice invoice;
	private boolean newAfter = false;
	
	public void execute() throws Exception {
		super.execute();
		addMessage("InvoiceReportAction.printOK");
	}

	public Map getParameters() throws Exception  {
		Messages errors = MapFacade.validate("Invoice", getView().getValues());
		if (errors.contains()) throw new ValidationException(errors);
		Map parameters = new HashMap();				
		parameters.put("number", getInvoice().getYear() + "/" + getInvoice().getNumber());								
		parameters.put("customer", getInvoice().getCustomer().getName());
		parameters.put("address", getInvoice().getCustomer().getAddress().getAsString());
		parameters.put("date", DateFormat.getDateInstance(DateFormat.LONG).format(getInvoice().getDate()));
		parameters.put("sum", getInvoice().getAmountsSum());
		parameters.put("vat", getInvoice().getVat());
		parameters.put("total", getInvoice().getTotal());
		return parameters;
	}

	protected JRDataSource getDataSource() throws Exception {
		return new JRBeanCollectionDataSource(getInvoice().getDetails());		
	}
	
	protected String getJRXML() {
		return "Invoice.jrxml"; // In this way it's readed from classpath
		// return "/home/javi/temporal/Invoice.jrxml"; // In this way it's readed from file system
	}
	
	private IInvoice getInvoice() throws Exception {
		if (invoice == null) {
			int year = getView().getValueInt("year");
			int number = getView().getValueInt("number");
			invoice = Invoice.findByYearNumber(year, number);
		}
		return invoice;
	}
	
	public String getNextAction() throws Exception {
		return newAfter ? "CRUD.new" : null;
	}

	public boolean isNewAfter() {
		return newAfter;
	}

	public void setNewAfter(boolean newAfter) {
		this.newAfter = newAfter;
	}
	

}
