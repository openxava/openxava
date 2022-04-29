package org.openxava.test.actions;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.*;
import org.openxava.model.MapFacade;
import org.openxava.test.model.Invoice;
import org.openxava.util.Messages;
import org.openxava.validators.ValidationException;

/**
 * For printing a report for a Invoice and another for a Customer at once. <p>
 * 
 * @author Javier Paniza
 */
public class InvoiceAndCustomerReports2Action extends JasperMultipleReportBaseAction {
	private static Log log = LogFactory.getLog(InvoiceAndCustomerReports2Action.class);
	
	private Invoice invoice;
	
	
	public void execute() throws Exception {
		Messages errors = MapFacade.validate("Invoice", getView().getValues());
		if (errors.contains()) throw new ValidationException(errors);

		Map parameters1 = new HashMap();
		parameters1.put("number", getInvoice().getYear() + "/" + getInvoice().getNumber());								
		parameters1.put("customer", getInvoice().getCustomer().getName());
		parameters1.put("address", getInvoice().getCustomer().getAddress().getAsString());
		parameters1.put("date", DateFormat.getDateInstance(DateFormat.LONG).format(getInvoice().getDate()));
		parameters1.put("sum", getInvoice().getAmountsSum());
		parameters1.put("vat", getInvoice().getVat());
		parameters1.put("total", getInvoice().getTotal());		
		addParameters(parameters1);

		Map parameters2 = new HashMap();
		parameters2.put("number", getInvoice().getCustomer().getNumber());								
		parameters2.put("name", getInvoice().getCustomer().getName());		
		addParameters(parameters2);
		
		super.execute();
	}
	
	protected JRDataSource [] getDataSources() throws Exception {
		JRDataSource ds = new JRBeanCollectionDataSource(getInvoice().getDetails());
		return new JRDataSource [] { ds, new JREmptyDataSource() }; // In this case the same data source for both reports
	}
	
	protected String [] getJRXMLs() {
		return new String [] { "Invoice.jrxml", "Customer.jrxml" }; 
	}
	
	private Invoice getInvoice() throws Exception {
		if (invoice == null) {
			int year = getView().getValueInt("year");
			int number = getView().getValueInt("number");
			invoice = Invoice.findByYearNumber(year, number);
		}
		return invoice;
	}

}
