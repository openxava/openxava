package org.openxava.util;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Javier Paniza
 * @author Trifon Trifonov
 */
public class TableModels {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(TableModels.class);
	
	public static String toCSV(TableModel table) {
		if (table == null) return "";
		String separator = XavaPreferences.getInstance().getCSVSeparator();
		StringBuffer cvs = new StringBuffer();
		int columns = table.getColumnCount(); 
		for (int i=0; i<columns; i++) {
			cvs.append(table.getColumnName(i));
			if (i < columns - 1) cvs.append(separator);
		}
		cvs.append('\n');
		for (int row=0; row < table.getRowCount(); row++) {
			for (int i=0; i<columns; i++) {
				cvs.append(convert(table.getValueAt(row, i)));
				if (i < columns - 1) cvs.append(separator);
			}
			cvs.append('\n');			
		}
		cvs.append('\n');
		return cvs.toString();
	}

	private static Object convert(Object valueAt) {
		if (valueAt == null) {
			return "";
		}
		if (!(valueAt instanceof String)) return valueAt;
		return "\"" + ((String) valueAt).
			replaceAll("\n\r", " ").
			replaceAll("\r\n", " ").
			replace('\n', ' ').
			replace('\r', ' ').
			replace("\"", "\"\"") +
			"\"";
	}

	public static void saveCSV(TableModel table, String file) throws IOException {
		FileOutputStream ostream = new FileOutputStream(file);				
		ostream.write(toCSV(table).getBytes());				
		ostream.close();		
	}

}