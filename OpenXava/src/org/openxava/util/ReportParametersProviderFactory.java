package org.openxava.util;

import javax.servlet.http.*;

import org.apache.commons.logging.*;

/**
 * For obtaining a instance of report parameters.
 * 
 * Create on 11/03/2011 (12:59:00)
 * @author Ana Andres
 */
public class ReportParametersProviderFactory {
	private static Log log = LogFactory.getLog(ReportParametersProviderFactory.class);
	private static IReportParametersProvider instance;
	
	public static IReportParametersProvider getInstance(HttpServletRequest request) { 
		if (instance == null) {
			try {
				Object provider = Class.forName(XavaPreferences.getInstance().getReportParametersProviderClass()).newInstance();
				if (provider instanceof IRequestReportParametersProvider) {
					IRequestReportParametersProvider requestProvider = (IRequestReportParametersProvider) provider;
					requestProvider.setRequest(request);
					return requestProvider;
				}
				instance = (IReportParametersProvider) Class.forName(XavaPreferences.getInstance().getReportParametersProviderClass()).newInstance();
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new XavaException(XavaResources.getString("report_parameters_provider_error"));
			}
		}		
		return instance;
	}
}
