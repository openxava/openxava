package org.openxava.invoicedemo.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import org.openxava.invoicedemo.model.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

public class PrintInvoiceAction extends JasperReportBaseAction {

	private Invoice invoice;
	
	@Override
	protected JRDataSource getDataSource() throws Exception {
		return new JRBeanCollectionDataSource(getInvoice().getDetails());
	}

	@Override
	protected String getJRXML() throws Exception {
		return "Invoice.jrxml";
	}

	@Override
	protected Map<String, Object> getParameters() throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("year", getInvoice().getYear());
		parameters.put("number", getInvoice().getNumber());
		parameters.put("date", getInvoice().getDate());
		parameters.put("customerNumber", getInvoice().getCustomer().getNumber());
		parameters.put("customerName", getInvoice().getCustomer().getName());
		parameters.put("customerAddress", getInvoice().getCustomer().getAddress());
		parameters.put("customerCity", getInvoice().getCustomer().getCity());
		parameters.put("customerCountry", getInvoice().getCustomer().getCountry());
		parameters.put("vatPercentage", getInvoice().getVatPercentage());
		parameters.put("vat", getInvoice().getVat());
		parameters.put("total", getInvoice().getTotal());
		parameters.put("remarks", getInvoice().getRemarks());
		
		return parameters;
	}

	private Invoice getInvoice() throws Exception {
		if (invoice == null) {
			String id = getView().getValueString("id");
			invoice = XPersistence.getManager().find(Invoice.class, id);
		}
		return invoice;
	}
	
}
