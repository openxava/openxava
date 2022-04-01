package org.openxava.util.jxls;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.openxava.util.*;

/**
 * JxlsSheet: a class to wrap and simplify the use of Apache POI Sheet in the context of OpenXava
 * 
 * Principle:
 * 		As per Excel, the rows and colums start at 1
 * 
 * Types of cells:
 * 		Date, Number, Formulas and Text
 * 		Formulas can be encoded as
 * 			Excel: 	A1, $A1, A$1, $A$1, R1C1 (R[-1]C1 is not implemented)
 * 			Jxls:	R1C1, $R1C1, R1$C1, $R1$C1	
 * 			and they can start with =
 * 
 * Usage:
 * <code>
 * 		JxlsWorkbook wb = new JxlsWorkbook("Test");
 *		JxlsSheet sheet = wb.addSheet("Test");
 *		sheet.setValue(3, 4, "Pi", wb.addStyle(TEXT).setAlign(CENTER).setBold());
 *		sheet.setValue(4, 4, 3.141592654, wb.addStyle(FLOAT).setAllBorders(THIN_BORDER));
 *		sheet.setFormula(4, 5, "=2*$R4$C4", wb.addStyle("##0.0000"));
 *		sheet.setFormula(4, 6, "=2*R4C4", wb.addStyle("##0.000"));
 * </code>
 * 
 * @author Laurent Wibaux
 *
 */
public class JxlsSheet implements JxlsConstants {
	
	private String name = null;
	private int index = 0;
	private Map<Integer, JxlsCell> cells = new HashMap<Integer, JxlsCell>();
	private Map<Integer, JxlsStyle> columnStyles = new HashMap<Integer, JxlsStyle>();
	private Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>();
	private int columnSplit = -1;
	private int rowSplit = -1;
	private int firstRow = 0;
	private int lastRow = 0;
	private int firstColumn = 0;
	private int lastColumn = 0;
	private int zoom = 100;
	private JxlsWorkbook workbook;

	protected JxlsSheet(JxlsWorkbook workbook, String name) {
		this(workbook, name, -1);
	}
	
	protected JxlsSheet(JxlsWorkbook workbook, String name, int index) {
		if (workbook == null) return;
		this.workbook = workbook;
		this.name = correctName(Is.empty(name) ? "Sheet" + (index == -1 ? workbook.sheets.size()+1 : index) : name);
		if (index <= workbook.sheets.size()) workbook.sheets.add(this);
		else workbook.sheets.insertElementAt(this, 0);
		workbook.sheetNames.put(this.name, this);
	}
	
	/**
	 * Sets the name of the sheet, correcting patterns not allowed 
	 * 
	 * @param name		the name of the sheet to set
	 * @return the properly formed name
	 */
	private String correctName(String name) {
		 // limit the name to 31 characters
		 if (name.length() > 31) name = name.substring(0, 31);
		 // replace characters not allowed by space
		 name = name.replace('[', ' ').replace(']', ' ').replace('*', ' ').replace('/', ' ').replace('\\', ' ')
				 .replace('?', ' ').replace(':', ' ');
		 int index = workbook.sheets.size();
		 while (workbook.sheetNames.containsKey(name)) name = "Sheet" + (index ++);
		 return name;
	}
	
	/**
	 * Sets the width of one or more columns
	 * 
	 * @param column	the index of the first column width to set (1 based)
	 * @param widths	the widths of the columns to set
	 * @return the JxlsSheet to chain other settings
	 */
	public JxlsSheet setColumnWidths(int column, int... widths) {
		for (int width : widths) columnWidths.put(column++, width);
		return this;
	}

	/**
	 * Sets the width of one or more columns
	 * 
	 * @param column	the index of the first column style to set (1 based)
	 * @param styles	the styles of the columns to set
	 * @return 			the JxlsSheet to chain other settings
	 */
	public JxlsSheet setColumnStyles(int column, JxlsStyle... styles) {
		for (JxlsStyle style : styles) columnStyles.put(column++, style);
		return this;
	}

	/**
	 * Sets the freeze pane of the sheet
	 * 
	 * @param columnSplit	the column at which right the pane will freeze
	 * @param rowSplit		the row under which the pane will freeze
	 * @return 				the JxlsSheet to chain other settings
	 */
	public JxlsSheet setFreezePane(int columnSplit, int rowSplit) {
		this.columnSplit = columnSplit;
		this.rowSplit = rowSplit;
		return this;
	}
	
	/**
	 * Sets the zoom percentage used to display the sheet
	 * 
	 * @param zoom	the percentage (1 to 100)
	 * @return 		the JxlsSheet to chain other settings
	 */
	public JxlsSheet setZoom(int zoom) {
		this.zoom = (zoom < 10 ? 10 : (zoom > 100 ? 100 : zoom));
		return this;
	}
	
	/**
	 * Sets column headers
	 * 
	 * Usage:
	 * <code>
	 * 	// set the first column header as "Invoice" with style
	 * 	// do not set any header for the second column
	 * 	// set the third column as "Customer" with style and a width of 20
	 * 	// set the fourth column as "Customer" with style, right alignment and a width of 10
	 * 	setHeaders(1, 1, style, "Invoice", null, "Customer|20", "Cost|r", "Margin|r|10");
	 * </code>
	 * 
	 * @param startColumn	the column at which to start setting the headers
	 * @param row			the row containing the headers
	 * @param style			the style to use for the headers
	 * @param headers		a list of headers. The format for the headers is name[|align][|columnWidth]
	 */
	public void setHeaders(int startColumn, int row, JxlsStyle style, String... headers) {
		if (headers == null) return;
		JxlsStyle styleR = null;
		JxlsStyle styleC = null;
		JxlsStyle styleL = null;
		int column = startColumn;
		for (String header : headers) {
			if (Is.empty(header)) {
				column ++;
				continue;
			}
			String headerLC = header.toLowerCase();
			if (styleR == null && headerLC.contains("|r")) styleR = workbook.addClonedStyle(style).setAlign(RIGHT);
			else if (styleC == null && headerLC.contains("|c")) styleC = workbook.addClonedStyle(style).setAlign(CENTER);
			else if (styleL == null && headerLC.contains("|l")) styleL = workbook.addClonedStyle(style).setAlign(LEFT);
			JxlsStyle cStyle = style;
			int indOf = header.indexOf('|');
			short align = -1;
			int columnWidth = AUTO_SIZE;
			if (indOf != -1) {
				String format = header.substring(indOf+1);
				header = header.substring(0, indOf);
				if (format.charAt(0) == 'r') align = RIGHT;
				else if (format.charAt(0) == 'l') align = LEFT;
				else if (format.charAt(0) == 'c') align = CENTER;
				indOf = header.indexOf('|');
				if (indOf != -1) format = format.substring(indOf+1);
				try { columnWidth = Integer.parseInt(format); } catch (Exception e) { columnWidth = AUTO_SIZE; }; 
			}
			if (align == RIGHT) cStyle = styleR;
			else if (align == LEFT) cStyle = styleL;
			else if (align == CENTER) cStyle = styleC;
			setValue(column, row, header, cStyle);
			setColumnWidths(column, columnWidth);
			column ++;
		}
	}

	protected JxlsCell setCell(JxlsCell cell) {
		cells.put((cell.column << 16) + cell.row, cell);
		if (cell.row < firstRow) firstRow = cell.row;
		else if (cell.row > lastRow) lastRow = cell.row;
		if (cell.column < firstColumn) firstColumn = cell.column;
		else if (cell.column > lastColumn) lastColumn = cell.column;
		return cell;
	}

	/**
	 * Sets multiple values in one row
	 * 
	 * @param startColumn	the column at which to start setting the values
	 * @param row			the row at which the values are set
	 * @param values		the values
	 */
	public void setValues(int startColumn, int row, Object... values) {
		int column = startColumn;
		for (Object value : values) setValue(column++, row, value);
	}
	
	/**
	 * Sets the value at column,row
	 * 
	 * @param column	the column of the cell to set
	 * @param row		the column of the cell to set
	 * @param value		the value of the cell to set
	 * @return			the cell which as been set
	 */
	public JxlsCell setValue(int column, int row, Object value) {
		return setValue(column, row, value, null);
	}

	
	/**
	 * Sets the value at column,row
	 * 
	 * @param column	the column of the cell to set
	 * @param row		the column of the cell to set
	 * @param value		the value of the cell to set
	 * @param style		the style of the cell to set
	 * @return			the cell which as been set
	 */
	public JxlsCell setValue(int column, int row, Object value, JxlsStyle style) {
		if (value == null) return null;
		if (value instanceof Date) return new JxlsCell(this, column, row, (Date)value, style);
		else if ((value instanceof Number) || int.class.equals(value.getClass()) || 
				short.class.equals(value.getClass()) || long.class.equals(value.getClass()) || 
				float.class.equals(value.getClass()) || double.class.equals(value.getClass())) 
			return new JxlsCell(this, column, row, ((Number)value).doubleValue(), style);
		else return new JxlsCell(this, column, row, value.toString(), style, false);
	}

	/**
	 * Sets the formula at column,row
	 * 
	 * <code>
	 * 	sheet.setFormula(1, 1, "=A1+$A2+$R1C3+R1$C4");
	 * </code>
	 * 
	 * @param column	the column of the cell to set
	 * @param row		the column of the cell to set
	 * @param formula	the formula of the cell to set
	 * @return			the cell which as been set
	 */
	public JxlsCell setFormula(int column, int row, String formula) {
		return new JxlsCell(this, column, row, formula, null, true);
	}

	/**
	 * Sets the formula at column,row
	 * 
	 * <code>
	 * 	sheet.setFormula(1, 1, "=A1+$A2+$R1C3+R1$C4");
	 * </code>
	 * 
	 * @param column	the column of the cell to set
	 * @param row		the column of the cell to set
	 * @param formula	the formula of the cell to set
	 * @param style		the style of the cell to set
	 * @return			the cell which as been set
	 */
	public JxlsCell setFormula(int column, int row, String formula, JxlsStyle style) {
		return new JxlsCell(this, column, row, formula, style, true);
	}

	/**
	 * Gets the object value contained in the cell at position column,row
	 * 
	 * @param column	the column of the cell to retrieve
	 * @param row		the row of the cell to retrieve
	 * @return			the value of the cell
	 */
	public Object getValue(int column, int row) {
		JxlsCell cell = getCell(column, row);
		if (cell == null) return null;
		return cell.getValue();
	}

	/**
	 * Gets the numeric value contained in the cell at position column,row
	 * 
	 * @param column	the column of the cell to retrieve
	 * @param row		the row of the cell to retrieve
	 * @return			the value of the cell if a number, the time in ms for a date, 0 in other cases
	 */
	public double getNumericValue(int column, int row) {
		JxlsCell cell = getCell(column, row);
		if (cell == null) return 0;
		return cell.getNumericValue();
	}

	/**
	 * Gets the string value contained in the cell at position column,row
	 * 
	 * @param column	the column of the cell to retrieve
	 * @param row		the row of the cell to retrieve
	 * @return			the value of the cell converted as a text
	 */
	public String getStringValue(int column, int row) {
		JxlsCell cell = getCell(column, row);
		if (cell == null) return "";
		return cell.getStringValue();
	}

	/**
	 * Gets the JxlsCell contained in the cell at position column,row
	 * 
	 * @param column	the column of the cell to retrieve
	 * @param row		the row of the cell to retrieve
	 * @return			the cell
	 */
	public JxlsCell getCell(int column, int row) {
		return cells.get((column << 16) + row);
	}
	
	/**
	 * Gets the row number of the bottom most cell
	 * 
	 * @return	the row number
	 */
	public int getLastRowNumber() {
		return lastRow;
	}
	
	protected void createPOISheet(JxlsWorkbook workbook, Workbook wb) {
		Sheet sheet = wb.createSheet(name == null ? "Sheet"+(index+1) : name);
		for (int cellPos : cells.keySet()) cells.get(cellPos).createPOICell(workbook, sheet, columnStyles);
		for (int column : columnWidths.keySet()) {
			if (columnWidths.get(column) == AUTO_SIZE) {
				sheet.autoSizeColumn(column-1);
				if (sheet.getColumnWidth(column-1) == 0) sheet.setColumnWidth(column-1, 10*256);
			} else sheet.setColumnWidth(column-1, columnWidths.get(column)*256);
		}
		if (columnSplit != -1 || rowSplit != -1) sheet.createFreezePane(columnSplit, rowSplit);
		if (zoom != 100) sheet.setZoom(zoom, 100);
	}
}

