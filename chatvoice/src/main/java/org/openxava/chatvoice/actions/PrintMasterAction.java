package org.openxava.chatvoice.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import org.openxava.chatvoice.model.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

public class PrintMasterAction extends JasperReportBaseAction {

	private Master master;
	
	@Override
	protected JRDataSource getDataSource() throws Exception {
		return new JRBeanCollectionDataSource(getMaster().getDetails());
	}

	@Override
	protected String getJRXML() throws Exception {
		return "Master.jrxml";
	}

	@Override
	protected Map<String, Object> getParameters() throws Exception {
		// We don't need to validate the data because we use available-on-new="false" for print action
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.REPORT_LOCALE, Locales.getCurrent()); // To use browser locale to format number, instead of server locale
		parameters.put("year", getMaster().getYear());
		parameters.put("number", getMaster().getNumber());
		parameters.put("date", getMaster().getDate());
		parameters.put("personNumber", getMaster().getPerson().getNumber());
		parameters.put("personName", getMaster().getPerson().getName());
		parameters.put("personAddress", getMaster().getPerson().getAddress());
		parameters.put("personCity", getMaster().getPerson().getCity());
		parameters.put("personCountry", getMaster().getPerson().getCountry());
		parameters.put("taxPercentage", getMaster().getTaxPercentage());
		parameters.put("tax", getMaster().getTax());
		parameters.put("total", getMaster().getTotal());
		parameters.put("remarks", getMaster().getRemarks());
		
		return parameters;
	}

	private Master getMaster() throws Exception {
		if (master == null) {
			String id = getView().getValueString("id");
			master = XPersistence.getManager().find(Master.class, id);
		}
		return master;
	}
	
}
