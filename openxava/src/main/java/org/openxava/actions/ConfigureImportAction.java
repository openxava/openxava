package org.openxava.actions;

import java.text.*;
import java.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.lang.*;
import org.apache.commons.logging.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxava.model.meta.*;
import org.openxava.model.transients.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.meta.*;

/**
 * 
 * @author Javier Paniza
 */

public class ConfigureImportAction extends TabBaseAction
	implements INavigationAction {
	
	private static Log log = LogFactory.getLog(ConfigureImportAction.class);  
		 
	private String[] nextControllers = new String [] { "Import" }; 
	private Collection<MetaProperty> metaProperties; 
	
	public void execute() throws Exception {
		String fileName = "UNKNOWN";
		try {
	        FileItem fi = (FileItem) getView().getValue("file");                         
	        if (fi != null && !Is.emptyString(fi.getName())) {
	        	fileName = fi.getName().toLowerCase();
	        	if (fileName.endsWith(".xlsx") ) {
	        		if (!configureImport(excelToCSV(new XSSFWorkbook(fi.getInputStream())))) cancel();
		        	return;
	        	}
	        	else if (fileName.endsWith(".csv")) {
		        	if (!configureImport(fi.getString().trim())) cancel(); 
		        	return;
	        	} 	        	
	        	else if (fileName.endsWith(".xls")) {
		        	if (!configureImport(excelToCSV(new HSSFWorkbook(fi.getInputStream())))) cancel(); 
		        	return;
	        	}
	        	else {
	        		addError("file_type_not_supported", "CSV, XLSX, XLS");
	        		cancel();
	        		return;
	        	}
	        }
		    addError("file_required");
		    cancel();
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("import_error", fileName, ex.getMessage()), ex);
			addError("import_error", fileName, ex.getMessage()); 
			cancel();
		}
	}
	
	private boolean configureImport(String data) { 
		Import imp = new Import();
		Scanner scanner = new Scanner(data);
		if (!scanner.hasNextLine()) {
			addError("empty_file"); 
			return false;
		}
		List<ImportColumn> columns = new ArrayList<ImportColumn>();
		fillHeaders(scanner, columns);
		fillDataSamples(scanner, columns);
		setProperties(columns);
		imp.setData(data);
		imp.setModelName(getTab().getModelName());
		imp.setColumns(columns);
		getView().setModel(imp);
		fillNamesInAppValidValues();
		return true;
	}
	
	private String excelToCSV(Workbook document) throws Exception { 
		StringBuffer text = new StringBuffer();
		document.getCellStyleAt((short)0).setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
		Sheet sheet = document.getSheetAt(0);
		CellStyle style = document.createCellStyle();
		style.setDataFormat((short)49);
		int rowCount = sheet.getLastRowNum();
		for (int i=0; i <= rowCount; i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			int cellCount = row.getLastCellNum();
			for (int j=0; j < cellCount; j++) {
				Cell cell = row.getCell(j);
				if (cell == null) {
					text.append("\"\"");
				}
				else {
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						text.append(formatDate(cell.getDateCellValue()));
					}
					else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) { 
						text.append(formatNumber(cell.getNumericCellValue()));
					}
					else {
						text.append(Import.encodeSeparators(cell.toString())); 
					}
				}
				
				text.append(XavaPreferences.getInstance().getCSVSeparator()); 
			}
			text.append('\n');
		}			
		return text.toString();
	}

	private String formatNumber(double number) { 
		if (number % 1 == 0) return Long.toString(new Double(number).longValue()); 
		return Import.encodeSeparators(NumberFormat.getNumberInstance(Locales.getCurrent()).format(number)); 
	}

	private String formatDate(Date date) throws Exception { 
		boolean hasTime = Dates.hasTime(date);
		MetaEditor editor = MetaWebEditors.getMetaEditorForType(hasTime?"java.sql.Timestamp":"java.util.Date"); 
		if (editor == null) {
			DateFormat df = null;
			if (hasTime) df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locales.getCurrent());
			else df = DateFormat.getDateInstance(DateFormat.SHORT, Locales.getCurrent());
			return df.format(date);
		}
		return editor.getFormatter().format(getRequest(), date);
	}

	private void fillNamesInAppValidValues() {
		View columnsView = getView().getSubview("columns");
		for (MetaProperty property: getMetaProperties()) {  
			columnsView.addValidValue("nameInApp", property.getName(), property.getLabel()); 
		}
	}
	
	private Collection<MetaProperty> getMetaProperties() { 
		if (metaProperties == null) {
			MetaModel metaModel = getTab().getMetaTab().getMetaModel();
			metaProperties = new ArrayList<>(metaModel.getMetaPropertiesPersistents());
			for (MetaReference ref: metaModel.getMetaReferences()) {	
				for (MetaProperty key: ref.getMetaModelReferenced().getAllMetaPropertiesKey()) {
					MetaProperty qualifiedKey = key.cloneMetaProperty();
					qualifiedKey.setName(ref.getName() + "." + qualifiedKey.getName());
					qualifiedKey.setLabel(Strings.firstUpper( (Labels.get(key.getName()) + " " + XavaResources.getString("of") + " "  + ref.getLabel()).toLowerCase()));
					metaProperties.add(qualifiedKey);
				}
			}		
		}
		return metaProperties;
	}


	private void fillHeaders(Scanner scanner, Collection<ImportColumn> columns) {
		String header = scanner.nextLine(); 
		StringTokenizer st = new StringTokenizer(header, XavaPreferences.getInstance().getCSVSeparator());
		while (st.hasMoreTokens()) {
			String fieldHeader = Strings.unquote(st.nextToken());
			ImportColumn column = new ImportColumn();
			column.setHeaderInFile(fieldHeader);
			columns.add(column);
		}
	}
	
	private void fillDataSamples(Scanner scanner, List<ImportColumn> columns) {
		fillDataSampleLine(scanner, columns, 1);
		fillDataSampleLine(scanner, columns, 2);
	}
	
	private void setProperties(Collection<ImportColumn> columns) {
		MetaModel metaModel = getTab().getMetaTab().getMetaModel();
		Collection<MetaProperty> properties = new ArrayList<MetaProperty>(getMetaProperties()); 
		int distance = 0;
		Collection<ImportColumn> remainingColumns = columns;
		while (!remainingColumns.isEmpty() && distance < 8) {
			remainingColumns = setProperties(properties, remainingColumns, distance++); 
		}
	}
	
	private Collection<ImportColumn> setProperties(Collection<MetaProperty> properties, Collection<ImportColumn> columns, int distance) {
		Collection<ImportColumn> remainingColumns = new ArrayList<ImportColumn>();
		for (ImportColumn column: columns) {
			MetaProperty property = getPropertyWithDistance(properties, column.getHeaderInFile(), distance);
			if (property != null) {
				column.setNameInApp(property.getName());
				properties.remove(property);
			}
			else remainingColumns.add(column);
		}		
		return remainingColumns;
	}
	
	private MetaProperty getPropertyWithDistance(Collection<MetaProperty> properties, String nameInFile, int distance) {
		for (MetaProperty property: properties) {
			if (StringUtils.getLevenshteinDistance(property.getLabel(), nameInFile) == distance) return property;
		}
		return null;
	}

	private void fillDataSampleLine(Scanner scanner, List<ImportColumn> columns, int lineNumber) {
		if (!scanner.hasNextLine()) return; 
		String line = scanner.nextLine(); 
		String[] fields = line.split(XavaPreferences.getInstance().getCSVSeparator());
		int count = Math.min(fields.length, columns.size());
		for (int i=0; i<count; i++) {
			String field = Strings.unquote(fields[i]);
			field = Import.decodeSeparators(field); 
			if (lineNumber == 1) columns.get(i).setSampleContent1(field);
			else columns.get(i).setSampleContent2(field); // line 2
		}
	}
	
	private void cancel() { 
		nextControllers = DEFAULT_CONTROLLERS;
		closeDialog();
	}

	public String[] getNextControllers() {
		return nextControllers;  
	}
	
	public String getCustomView() {
	    return DEFAULT_VIEW;
	}


}