package org.openxava.web.servlets;

import java.io.*;
import java.lang.annotation.Annotation;
import java.math.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;
import org.openxava.util.jxls.*;
import org.openxava.web.*;
import org.openxava.web.editors.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

/**
 * To generate automatically reports from list mode. <p>
 * 
 * Uses JasperReports.
 * 
 * @author Javier Paniza
 */

@WebServlet({ "/xava/list.pdf", "/xava/list.csv", "/xava/list.xls" })
public class GenerateReportServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(GenerateReportServlet.class);
	
	public static class TableModelDecorator implements TableModel {
							 
		private TableModel original;		
		private List metaProperties;
		private boolean withValidValues = false;
		private Locale locale;
		private boolean labelAsHeader = false;
		private transient HttpServletRequest request;  
		private boolean format = false;	// format or no the values. If format = true, all values to the report are String
		private Integer columnCountLimit;
		private boolean formatBigDecimal = true; 
		private boolean isImage = false;
		private String type;

		public TableModelDecorator(HttpServletRequest request, TableModel original, List metaProperties, Locale locale, boolean labelAsHeader, boolean format, Integer columnCountLimit, boolean formatBigDecimal, String type) throws Exception { 
			this.request = request;
			this.original = original;
			this.metaProperties = metaProperties;
			this.locale = locale;
			this.withValidValues = calculateWithValidValues();
			this.labelAsHeader = labelAsHeader;			
			this.format = format;
			this.columnCountLimit = columnCountLimit;
			this.formatBigDecimal = formatBigDecimal; 
			this.type = type;
		}


		private boolean calculateWithValidValues() {
			Iterator it = metaProperties.iterator();
			while (it.hasNext()) {
				MetaProperty m = (MetaProperty) it.next();
				if (m.hasValidValues()) return true;
			}
			return false;
		}
		
		private MetaProperty getMetaProperty(int i) {
			return (MetaProperty) metaProperties.get(i);
		}

		public int getRowCount() {			
			return original.getRowCount();
		}

		public int getColumnCount() {			
			return columnCountLimit == null?original.getColumnCount():columnCountLimit;
		}

		public String getColumnName(int c) {
			return labelAsHeader?getMetaProperty(c).getQualifiedLabel(locale):Strings.change(getMetaProperty(c).getQualifiedName(), ".", "_"); 
		}

		public Class getColumnClass(int c) {						
			return original.getColumnClass(c);
		}

		public boolean isCellEditable(int row, int column) {			
			return original.isCellEditable(row, column);
		}

		public Object getValueAt(int row, int column) {
			if (isFormat()) return getValueWithWebEditorsFormat(row, column);
			else return getValueWithoutWebEditorsFormat(row, column);
		}

	    private Object getValueWithoutWebEditorsFormat(int row, int column) {
	        Object r = original.getValueAt(row, column);
	        MetaProperty p = getMetaProperty(column); // In order to use the type declared by the developer 
	        // and not the one returned by JDBC or the JPA engine

	        if (r instanceof Boolean) {
	            System.out.println("1 " + p.getName());
	            if (((Boolean) r).booleanValue()) return XavaResources.getString(locale, "yes");
	            return XavaResources.getString(locale, "no");
	        }

	        if (withValidValues) {
	            System.out.println("2 " + p.getName());
	            if (p.hasValidValues()) {
	                return p.getValidValueLabel(locale, original.getValueAt(row, column));
	            }
	        }

	        if (r instanceof java.util.Date || r instanceof java.time.LocalDate || r instanceof java.sql.Timestamp) {
	            System.out.println("3 " + p.getName());
	            return getValueWithWebEditorsFormat(row, column);
	        }

	        if (formatBigDecimal && r instanceof BigDecimal) {
	            System.out.println("4 " + p.getName());
	            return formatBigDecimal(r, locale);
	        }

	        //excel pasa por aca primero, definirlo aca directamente
	        if (p.isImage(r)) {
	        	// en caso que la propiedad sea un string
	        	// ya se comprobo que es un oid, si no es un oid, retorno falso en el isImage, por lo que no pasa
	            if (p.isCompatibleWith(String.class)) {
	                AttachedFile file = new AttachedFile();
	                file = (AttachedFile) FilePersistorFactory.getInstance().find(r.toString());
	                if (file.getName() != null) return file.getNameWithoutExtension();
	            } else if (p.isCompatibleWith(byte[].class)) {
	                //se hace un toString del objeto array 
	            	//hay que ver como obtener el nombre de la imagen apartir del byte
	            	return r.toString();
	            }
	            return getValueWithWebEditorsFormat(row, column);
	        }
	        //en caso que no sea imagen y solo archivo
	        Annotation[] annotations = p.getAnnotations();
	        boolean isFile = false;
	        for (Annotation an : annotations) {
				if (an.annotationType().getSimpleName().equals("File")) {
					isFile = true;
					break;
					}
			}
	        if ((p.getStereotype()!=null && p.getStereotype().contains("FILE"))|| isFile ) {
                AttachedFile file = new AttachedFile();
                file = (AttachedFile) FilePersistorFactory.getInstance().find(r.toString());
                if (file.getName() != null) return file.getNameWithoutExtension();
	        }

	        return r;
	    }
		
		private Object getValueWithWebEditorsFormat(int row, int column){
			Object r = original.getValueAt(row, column);
			MetaProperty metaProperty = getMetaProperty(column);
			//System.out.println("getValueWithWebEditorsFormat " + metaProperty.getName());
			isImage = checkIsImage(metaProperty);
			//excele suele o solia pasar por aca si no encuentra solucion arriba
			
				if (metaProperty.isImage(r)) {
					if (metaProperty.isCompatibleWith(byte[].class)) {
						return r==null?null:new ByteArrayInputStream((byte [])r); 
					} else if (metaProperty.isCompatibleWith(String.class)) {
						AttachedFile file = new AttachedFile();
						file = (AttachedFile) FilePersistorFactory.getInstance().find(r.toString());
						byte[] fileData = file.getData();
						return fileData==null ? null: new ByteArrayInputStream(fileData);
					}
				}
			
			String result = WebEditors.format(this.request, metaProperty, r, null, "", true);
			if (isHtml(result)){	// this avoids that the report shows html content
				result = result.contains("ox-attached-file") ? extractFileName(result) : WebEditors.format(this.request, metaProperty, r, null, "", false);
			}
			else {
				result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
			}
			return result;
		}
		
		public void setValueAt(Object value, int row, int column) {
			original.setValueAt(value, row, column);			
		}

		public void addTableModelListener(TableModelListener l) {
			original.addTableModelListener(l);			
		}

		public void removeTableModelListener(TableModelListener l) {
			original.removeTableModelListener(l);			
		}

		private boolean isHtml(String value){
			return value.matches("<.*>");
		}

		public boolean isFormat() {
			return format;
		}

		public void setFormat(boolean format) {
			this.format = format;
		}
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {				
			Tab tab = (Tab) request.getSession().getAttribute("xava_reportTab");
			if (tab == null) { // If you change this pass the ZAP test again
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			int [] selectedRowsNumber = tab.getSelected();
			Map [] selectedKeys = tab.getSelectedKeys();
			int [] selectedRows = getSelectedRows(selectedRowsNumber, selectedKeys, tab);			
			Integer columnCountLimit = (Integer) request.getSession().getAttribute("xava_columnCountLimitReportTab");
			
			setDefaultSchema(request);
			String uri = request.getRequestURI();				
			if (uri.endsWith(".pdf")) {
				InputStream is;
				JRDataSource ds;
				Map parameters = new HashMap();
				synchronized (tab) {
					tab.setRequest(request);
					String title = tab.isSaveConfigurationAllowed()?tab.getTitle():tab.getTitle() + " - " + tab.getConfigurationName();
					parameters.put("Title", title);
					parameters.put("Organization", getOrganization(request)); 
					parameters.put("Date", getCurrentDate());
					for (String totalProperty: tab.getTotalPropertiesNames()) {
						parameters.put(totalProperty + "__TOTAL__", getTotal(request, tab, totalProperty));
					}
					TableModel tableModel = getTableModel(request, tab, selectedRows, false, true, null, "pdf");
					tableModel.getValueAt(0, 0);
					if (tableModel.getRowCount() == 0) {
						generateNoRowsPage(response);
						return;
					}
					is  = getReport(request, response, tab, tableModel, columnCountLimit);
					ds = new JRTableModelDataSource(tableModel);
				}	
				JasperPrint jprint = JasperFillManager.fillReport(is, parameters, ds);
				response.setContentType("application/pdf");	
				response.setHeader("Content-Disposition", "inline; filename=\"" + getFileName(tab) + ".pdf\"");
				JasperExportManager.exportReportToPdfStream(jprint, response.getOutputStream());
			}
			else if (uri.endsWith(".csv")) {	
				String csvEncoding = XavaPreferences.getInstance().getCSVEncoding(); 
				if (!Is.emptyString(csvEncoding)) {
					response.setCharacterEncoding(csvEncoding);
				}
				response.setContentType("text/x-csv");
				response.setHeader("Content-Disposition", "inline; filename=\"" + getFileName(tab) + ".csv\""); 
				synchronized (tab) {
					tab.setRequest(request);
					response.getWriter().print(TableModels.toCSV(getTableModel(request, tab, selectedRows, true, false, columnCountLimit, "csv")));
				}
			}
			else if (uri.endsWith(".xls")) {    
                synchronized (tab) {
                	tab.setRequest(request);
                    JxlsWorkbook wb = new JxlsWorkbook(getTableModel(request, tab, selectedRows, true, false, columnCountLimit, false, "xls"), 
                            getFileName(tab));                	
                    JxlsSheet sheet = wb.getSheet(0);
                    int lastRow = sheet.getLastRowNumber();
                    JxlsStyle sumStyle = wb.addStyle(JxlsConstants.FLOAT)
                            .setBold()
                            .setBorder(JxlsConstants.TOP, JxlsConstants.BORDER_THIN);
                    for (int column=0; column<tab.getTableModel().getColumnCount(); column++) {
                        MetaProperty property = tab.getMetaProperty(column);
                        if (!property.isNumber() || !tab.hasTotal(column)) continue;
                        sheet.setFormula(column+1, lastRow+1, "=SUM(R2C" + (column+1) + ":R" + lastRow + "C"  + (column+1) + ")", sumStyle);
                    }
                    wb.write(response);
                }
			}
			else {
				throw new ServletException(XavaResources.getString("report_type_not_supported", "", ".pdf .csv"));
			}			
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("report_error"));
		}		
		finally {
			// request.getSession().removeAttribute("xava_reportTab"); // We don't do because in some browser configuration the PDF is asked twice and fails in the second one.
			SessionData.clean();
		}
	}

	private void generateNoRowsPage(HttpServletResponse response) throws Exception { 
		response.setContentType("text/html");
		
		response.setCharacterEncoding(XSystem.getEncoding());  
		response.getWriter().println("<html><head><title>");
		response.getWriter().println(XavaResources.getString("no_rows_report_message_title")); 
		response.getWriter().println("</title></head><body><font face='Tahoma,Arial,sans-serif'>");
		response.getWriter().println("<h1>");
		response.getWriter().println(XavaResources.getString("no_rows_report_message_title"));
		response.getWriter().println("</h1>");
		response.getWriter().println("<font size='+1'><p>");
		response.getWriter().println(XavaResources.getString("no_rows_report_message_detail"));
		response.getWriter().println("</font></p></font></body></html>");
	}

	private String getCurrentDate() {
		return java.text.DateFormat.getDateInstance(DateFormat.MEDIUM, Locales.getCurrent()).format(new java.util.Date());
	}

	private String getFileName(Tab tab) { 
		String now = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
		String fileName = tab.getTitle() + " " + now;
		byte[] bytes = fileName.getBytes();
		String encoding = XSystem.getEncoding();
		try {
			fileName = new String(bytes, encoding); 
		} catch (UnsupportedEncodingException e) {
			log.warn(XavaResources.getString("filename_not_encoded", encoding)); 
		} 
		return fileName;
	}

	private Object getTotal(HttpServletRequest request, Tab tab, String totalProperty) {
		Object total = tab.getTotal(totalProperty);
		return WebEditors.format(request, tab.getMetaProperty(totalProperty), total, new Messages(), null, true);
	}

	private void setDefaultSchema(HttpServletRequest request) {
		String jpaDefaultSchemaTab = (String) request.getSession().getAttribute("xava_jpaDefaultSchemaTab");
		if (jpaDefaultSchemaTab != null) {
			// request.getSession().removeAttribute("xava_jpaDefaultSchemaTab"); // We don't do because in some browser configuration the PDF is asked twice so second one we'll not use the correct schema
			XPersistence.setDefaultSchema(jpaDefaultSchemaTab);			
		}
	}

	protected String getOrganization(HttpServletRequest request) throws MissingResourceException, XavaException { 
		try {
			return ReportParametersProviderFactory.getInstance(request).getOrganization(); 
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("organization_name_error"), ex); 
			return "";
		}
	}
	
	private InputStream getReport(HttpServletRequest request, HttpServletResponse response, Tab tab, TableModel tableModel, Integer columnCountLimit) throws ServletException, IOException {
		StringBuffer suri = new StringBuffer();
		suri.append("/xava/jasperReport");
		suri.append("?language="); 
		suri.append(Locales.getCurrent().getLanguage());
		suri.append("&widths=");
		suri.append(Arrays.toString(getWidths(tableModel))); 
		if (columnCountLimit != null) {
			suri.append("&columnCountLimit="); 
			suri.append(columnCountLimit);			
		}
		response.setCharacterEncoding(XSystem.getEncoding());
		return Servlets.getURIAsStream(request, response, suri.toString());
	}
	
	private int [] getWidths(TableModel tableModel) { 
		int [] widths = new int[tableModel.getColumnCount()];
		for (int r=0; r<Math.min(tableModel.getRowCount(), 500); r++) { // 500 is not for performance, but for using only a sample of data with huge table			
			for (int c=0; c<tableModel.getColumnCount(); c++) {
				Object o = tableModel.getValueAt(r, c);				
				if (o instanceof String) {
					String s = ((String) o).trim();
					if (s.length() > widths[c]) widths[c] = s.length();
				}
			}
		}
		return widths;
	}

	private TableModel getTableModel(HttpServletRequest request, Tab tab, int [] selectedRows, boolean labelAsHeader, boolean format, Integer columnCountLimit, boolean formatBigDecimal, String type) throws Exception {
		TableModel data = null;
		if (selectedRows != null && selectedRows.length > 0) {
			data = new SelectedRowsXTableModel(tab.getTableModel(), selectedRows);
		}
		else {
			data = tab.getAllDataTableModel();
		}
		return new TableModelDecorator(request, data, tab.getMetaProperties(), Locales.getCurrent(), labelAsHeader, format, columnCountLimit, formatBigDecimal, type);
	}	
	
	private TableModel getTableModel(HttpServletRequest request, Tab tab, int [] selectedRows, boolean labelAsHeader, boolean format, Integer columnCountLimit, String type) throws Exception {
		return getTableModel(request, tab, selectedRows, labelAsHeader, format, columnCountLimit, true, type);
	}
	
	private static Object formatBigDecimal(Object number, Locale locale) { 
		NumberFormat nf = NumberFormat.getNumberInstance(locale);
		nf.setMinimumFractionDigits(2);
		nf.setGroupingUsed(false); 
		return nf.format(number);
	}

	private int[] getSelectedRows(int[] selectedRowsNumber, Map[] selectedRowsKeys, Tab tab){
		if (selectedRowsKeys == null || selectedRowsKeys.length == 0) return new int[0];
		// selectedRowsNumber is the most performant so we use it when possible
		else if (selectedRowsNumber.length == selectedRowsKeys.length) return selectedRowsNumber;
		else{			
			// find the rows from the selectedKeys
			
			// This has a poor performance, but it covers the case when the selected
			// rows are not loaded for the tab, something that can occurs if the user
			// select rows and afterwards reorder the list.
			try{
				int[] s = new int[selectedRowsKeys.length];
				List selectedKeys = Arrays.asList(selectedRowsKeys);
				int end = tab.getTableModel().getTotalSize();
				int x = 0;
				for (int i = 0; i < end; i++){
					Map key = (Map)tab.getTableModel().getObjectAt(i);
					if (selectedKeys.contains(key)){
						s[x] = i; 
						x++;
					}
				}	
				return s;
			}
			catch(Exception ex){
				log.warn(XavaResources.getString("fails_selected"), ex); 
				throw new XavaException("fails_selected");
			}
		}
	}
	
	private static String extractFileName(String htmlContent) {
			String result = "";
	        String startTag = "<span class=\"ox-attached-file\">";
	        String endTag = "</span>";
	        int startIndex = htmlContent.indexOf(startTag);
	        int endIndex = htmlContent.indexOf(endTag, startIndex);
	        result = htmlContent.substring(startIndex + startTag.length(), endIndex);
	        int lastDotIndex = result.lastIndexOf(".");
	        return (lastDotIndex != -1) ? result.substring(0, lastDotIndex) : result;
	}
	
	private static boolean isPossibleOID(String input) {
	    if (input.length() != 32) {
	        return false; 
	    }
	    for (char c : input.toCharArray()) {
	        if (!Character.isDigit(c) && (c < 'A' || c > 'F')) {
	            return false;
	        }
	    }
	    return true;
	}
	
	private static boolean checkIsImage(MetaProperty metaProperty) {
		if (metaProperty.getStereotype() != null && (metaProperty.getStereotype().contains("FILE") || metaProperty.getStereotype().contains("IMAGE") || metaProperty.getStereotype().contains("PHOTO"))) {
			return true;
		}
		Annotation[] annotation = (Annotation[]) metaProperty.getAnnotations();
		for (Annotation an : annotation) {
			if (an.toString().contains(".File")) {
				return true;
			}
		}
		return false;
	}
	
}
