package org.openxava.actions;

import java.text.*;
import java.util.*;

import net.sf.jasperreports.engine.*;

/**
 * To generate a custom report concatenating simple reports. <p>
 *	
 * You only need to overwrite the abstract methods.<br>
 * 
 * To send parameters to the simple reports you have 3 options. 
 * <ol>
 * <li>If all the reports use the same parameters just overwrite getParameters().</li>
 * <li>If each report has different parameter overwrite getParameters(int i), or well</li>
 * <li>overwrite execute() and before calling to super.execute() call to addParameters() method.</li>
 * </ol><br>
 * <h4>Option 1 for parameters</h4>
 * <pre>
 * <code>
 * 	{@literal @}Override
 *   public Map getParameters() throws Exception  {
 * 		Map parameters = new HashMap();				
 *		parameters.put("param1", value1);								
 *		parameters.put("param2", value2);
 *		return parameters;
 *	 }
 * </code>
 * </pre>
 * 
 * <h4>Option 2 for parameters</h4>
 * <pre>
 * <code>
 *  {@literal @}Override
 * 	 protected Map getParameters(int index) throws Exception  {
 *		Map parameters = new HashMap();
 *		switch (index) {
 *			case 0:								
 *				parameters.put("param1", value1);								
 *				parameters.put("param2", value2);
 *				return parameters;
 *			case 1:								
 *				parameters.put("param3", value3);								
 *				parameters.put("param4", value4);
 *				return parameters;
 *		}
 *		return null;
 *	 }
 * </code>
 * </pre>
 * 
 * <h4>Option 3 for parameters</h4>
 * <pre>
 * <code>
 *  {@literal @}Override 
 *	 public void execute() throws Exception {
 *		Map parameters1 = new HashMap();
 *		parameters1.put("param1", value1);								
 *		parameters1.put("param2", value2);
 *		addParameters(parameters1);
 *		Map parameters2 = new HashMap();
 *		parameters1.put("param3", value3);								
 *		parameters1.put("param4", value4);
 *		addParameters(parameters2);		
 *		super.execute();
 *	 }
 * </code>
 * </pre> 
 * 
 * @author Jeromy Altuna	
 */
abstract public class JasperConcatReportBaseAction extends JasperMultipleReportBaseAction {
	
	private String filename;
	
	@Override
	protected abstract JRDataSource[] getDataSources() throws Exception; 
		
	/**
	 * XML list JasperReports designed that will be concatenated 
	 * into a single report
	 * 
	 * @return String[] jrxml list
	 */
	@Override
	protected abstract String[] getJRXMLs() throws Exception;
	
	@Override
	public void execute() throws Exception {
		super.execute();
		getRequest().getSession().setAttribute("xava.report.filename", getFilename());
	}
	
	@Override
	public String[] getForwardURIs() {
		return new String[] {
			"/xava/concatReport.pdf?time="+System.currentTimeMillis() 
		};
	}
	
	public String getFilename() {
		if (filename == null) {
			String now = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
			return getModelName() + "-report_" + now;
		} 
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}	
}
