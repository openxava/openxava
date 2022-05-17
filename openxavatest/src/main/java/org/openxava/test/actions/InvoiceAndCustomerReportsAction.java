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
public class InvoiceAndCustomerReportsAction extends JasperMultipleReportBaseAction {
	private static Log log = LogFactory.getLog(InvoiceAndCustomerReportsAction.class);
	
	private Invoice invoice;
	
	protected Map getParameters(int index) throws Exception  {
		Messages errors = MapFacade.validate("Invoice", getView().getValues());
		if (errors.contains()) throw new ValidationException(errors);
		Map parameters = new HashMap();
		switch (index) {
			case 0:								
				parameters.put("number", getInvoice().getYear() + "/" + getInvoice().getNumber());								
				parameters.put("customer", getInvoice().getCustomer().getName());
				parameters.put("address", getInvoice().getCustomer().getAddress().getAsString());
				parameters.put("date", DateFormat.getDateInstance(DateFormat.LONG).format(getInvoice().getDate()));
				parameters.put("sum", getInvoice().getAmountsSum());
				parameters.put("vat", getInvoice().getVat());
				parameters.put("total", getInvoice().getTotal());
				return parameters;
			case 1:								
				parameters.put("number", getInvoice().getCustomer().getNumber());								
				parameters.put("name", getInvoice().getCustomer().getName());
				return parameters;
		}
		return null;
	}

	protected JRDataSource [] getDataSources() throws Exception {
		JRDataSource ds = new JRBeanCollectionDataSource(getInvoice().getDetails());
		return new JRDataSource [] { ds, new JREmptyDataSource() }; 
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
