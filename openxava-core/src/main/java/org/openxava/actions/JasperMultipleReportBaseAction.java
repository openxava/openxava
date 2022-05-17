package org.openxava.actions;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.XPersistence;
import org.openxava.util.*;

import net.sf.jasperreports.engine.*;

/**
 * To generate several custom Jasper Reports with the same action.
 * <p>
 * 
 * You only need to overwrite the abstract methods.<br>
 * 
 * To send parameters to the reports you have 3 options. 
 * <ol>
 * <li>If all the repors use the same parameters just overwrite getParameters().</li>
 * <li>If each report has different parameter overwrite getParameters(int i), or well</li>
 * <li>overwrite execute() and before calling to super.execute() call to addParameters() method.</li>
 * </ol>
 * <h4>Option 1 for parameters</h4>
 * <code>
 * 	public Map getParameters() throws Exception  {
 *		Map parameters = new HashMap();				
 *		parameters.put("param1", value1);								
 *		parameters.put("param2", value2);
 *		return parameters;
 *	}
 * </code>
 * 
 * <h4>Option 2 for parameters</h4>
 * <code>
 * 	protected Map getParameters(int index) throws Exception  {
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
 *	}
 * </code>
 * 
 * <h4>Option 3 for parameters</h4>
 * <code>
 *	public void execute() throws Exception {
 *		Map parameters1 = new HashMap();
 *		parameters1.put("param1", value1);								
 *		parameters1.put("param2", value2);
 *		addParameters(parameters1);
 *		Map parameters2 = new HashMap();
 *		parameters1.put("param3", value3);								
 *		parameters1.put("param4", value4);
 *		addParameters(parameters2);		
 *		super.execute();
 *	}
 * </code> 
 * 
 * @author Oscar Caro
 * @author Franklin Alier : Added a parameters list to allow separate parameters per report 
 */
abstract public class JasperMultipleReportBaseAction extends ViewBaseAction
		implements IMultipleForwardAction, IModelAction {
	
	private static Log log = LogFactory.getLog(JasperMultipleReportBaseAction.class);

	public static String PDF = "pdf";
	public static String EXCEL = "excel";
	public static String RTF = "rtf";
	public static String ODT = "odt";

	private String modelName;
	private String format = PDF;
	
	// To Allow parameter sets per report
	private List<Map> parametersList = new ArrayList<Map>();

	/** 
	 * Data to print.
	 * <p>
	 * 
	 * If return null then a JDBC connection is sent to JasperReport, this is
	 * for the case of a SQL inside JasperReport design.
	 */
	abstract protected JRDataSource[] getDataSources() throws Exception;

	/**
	 * The name of the XML with the JasperReports design.
	 * <p>
	 * 
	 * If it is a relative path (as <code>reports/myreport.jrxml</code> has to
	 * be in classpath. If it is a absolute path (as
	 * <code>/home/java/reports/myreport.xml</code> or
	 * <code>C:\\JAVA\\REPORTS\MYREPORT.JRXML</code> then it look at the file
	 * system.
	 */
	abstract protected String[] getJRXMLs() throws Exception;

	/**
	 * Parameters to send to report.
	 */
	protected Map getParameters() throws Exception {
		return null;
	}

	/**
	 * Retrieve parameters for the specified report index.
	 * <p>
	 * Maintains backward compatibility by calling getParameters()
	 * if parameters list is empty or not enough parameter sets defined.
	 * @author Franklin Alier
	 * @since 4.5
	 */
	protected Map getParameters(int i) throws Exception { 
		
		if ( parametersList.size() == 0 || i > parametersList.size()-1 ) {
			return getParameters();
		}
		else
		{
			return parametersList.get(i);
		}
	}

	/**
	 * Output report format, it can be 'pdf' or 'excel'.
	 * <p>
	 */
	public String getFormat() throws Exception {
		return format;
	}

	/**
	 * Output report format, it can be 'pdf', 'excel' or 'rtf'.
	 * <p>
	 */
	public void setFormat(String format) throws Exception {
		if (!EXCEL.equalsIgnoreCase(format) && !PDF.equalsIgnoreCase(format)
				&& !RTF.equalsIgnoreCase(format)
				&& !ODT.equalsIgnoreCase(format)) {
			throw new XavaException("invalid_report_format",
					"'excel', 'pdf', 'rtf','odt'");
		}
		this.format = format;
	}

	public void execute() throws Exception {

		ServletContext application = getRequest().getSession()
				.getServletContext();
		System.setProperty("jasper.reports.compile.class.path", application
				.getRealPath("/WEB-INF/lib/jasperreports.jar")
				+ System.getProperty("path.separator")
				+ application.getRealPath("/WEB-INF/classes/"));

		InputStream xmlDesign = null;
		String[] jrxml = getJRXMLs();
		JasperPrint[] jprintArray=new JasperPrint[jrxml.length];
		for (int i = 0; i < jrxml.length; i++) {
			if (isAbsolutePath(jrxml[i])) {
				xmlDesign = new FileInputStream(jrxml[i]);
			} else {
				xmlDesign = JasperMultipleReportBaseAction.class
						.getResourceAsStream("/" + jrxml[i]);
			}
			if (xmlDesign == null)
				throw new XavaException("design_not_found");
			JasperReport report = JasperCompileManager.compileReport(xmlDesign);
			
			// Retrieve the parameter set for the report at this index.
			//
			Map parameters = getParameters(i); // getParameters() before
												// getDatasource()

			JRDataSource[] ds = getDataSources();
			JasperPrint jprint = null;
			if (ds == null) {
				Connection con = DataSourceConnectionProvider.getByComponent(
						modelName).getConnection();
				// If the schema is changed through URL or
				// XPersistence.setDefaultSchema, the connection
				// contains the original catalog (schema) instead of the new
				// one, thus rendering the
				// wrong data on the report. This is a fix for such behavior.
				if (!Is.emptyString(XPersistence.getDefaultSchema())) {
					con.setCatalog(XPersistence.getDefaultSchema());
				}
				jprint = JasperFillManager.fillReport(report, parameters, con);
				con.close();
			} else {
				jprint = JasperFillManager.fillReport(report, parameters, ds[i]);
			}
			jprintArray[i]=jprint;
		}
		getRequest().getSession().setAttribute("xava.report.jprints", jprintArray);
		getRequest().getSession().setAttribute("xava.report.format", getFormat());
	}

	private boolean isAbsolutePath(String design) {
		return design.startsWith("/")
				|| (design.length() > 2 && design.charAt(1) == ':' && Character
						.isLetter(design.charAt(0)));
	}

	public String[] getForwardURIs() {
		try {	
			String[] jrxml = getJRXMLs();
			String[] arrayURIs = new String[jrxml.length];			
			for (int i=0; i<jrxml.length; i++) {
				arrayURIs[i] = "/xava/report.pdf?time="+System.currentTimeMillis() + "&index=" + i;
			}
			return arrayURIs;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("forward_uris_error"); 
		}
	}

	public boolean inNewWindow() {
		return true;
	}

	public void setModel(String modelName) { // to obtain a JDCB connection, if
												// required
		this.modelName = modelName;
	}

	/**
	 * Set parameter list.
	 * <p>
	 * Allows setting the parameter list outside this code.
	 * @author Franklin Alier
	 * @since 4.5
	 */
	public void setParametersList(List<Map> parametersList) {
		this.parametersList = parametersList;
	}

	/**
	 * Retrieve the current parameter list.
	 * @author Franklin Alier
	 * @since 4.5
	 */
	public List<Map> getParametersList() {
		return parametersList;
	}
	
	/**
	 * Add a parameter set to the current list.
	 * <p>
	 * Appends the parameter set to the parameter list.
	 * @author Franklin Alier
	 * @since 4.5
	 */	
	public void addParameters(Map parameters) {
		parametersList.add(parameters);
	}

	/**
	 * Add a parameter set at a specified index.
	 * <p>
	 * Inserts the parameter set into the parameter list at the specified index.
	 * @param index - The index to insert into.
	 * @param parameter - The parameter set to insert.
	 * @author Franklin Alier
	 * @since 4.5
	 */
	public void addParameters(int index, Map parameters) {
		parametersList.add(index, parameters);
	}
	
}