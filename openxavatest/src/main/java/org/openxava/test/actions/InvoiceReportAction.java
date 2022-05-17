package org.openxava.test.actions;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.IChainAction;
import org.openxava.actions.JasperReportBaseAction;
import org.openxava.model.MapFacade;
import org.openxava.test.model.Invoice;
import org.openxava.util.Messages;
import org.openxava.validators.ValidationException;

/**
 * For printing an Invoice using a JasperReport custom design. <p>
 * 
 * @author Javier Paniza
 */
public class InvoiceReportAction extends JasperReportBaseAction implements IChainAction {
	private static Log log = LogFactory.getLog(InvoiceReportAction.class);
	
	private Invoice invoice;
	private boolean newAfter = false;

	@Override
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
	
	private Invoice getInvoice() throws Exception {
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
