package org.openxava.util.jxls;

import java.util.*;
import java.util.regex.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;

/**
 * JxlsCell: a class to wrap and simplify the use of Apache POI Cell in the context of OpenXava
 * 
 * Usage:
 * <code>
 * 		JxlsWorkbook wb = new JxlsWorkbook("Test");
 *		JxlsSheet sheet = wb.addSheet("Test");
 *		JxlsCell cell = sheet.setValue(4, 4, 2);
 *		cell.setSpan(2, 1).setHyperLink("http://www.openxava.org");
 *		sheet.setValue(4, 5, "Test").setSpan(3, 1);
 * </code>
 * 
 * @author Laurent Wibaux
 *
 */
public class JxlsCell {

	private static final char TEXT = 't';
	private static final char NUMBER = 'n';
	private static final char FORMULA = 'f';
	private static final char DATE = 'd';

	public static String getXlsReference(int column, int row) {
		return CellReference.convertNumToColString(column-1) + row; 	
	}
	
	private JxlsStyle style;	
	private char type = TEXT;
	private double number;
	private Date date;
	private String text;
	private int columnSpan = 1;
	private int rowSpan = 1;
	private String hyperlink = null;

	protected int row;
	protected int column;

	protected JxlsCell(JxlsSheet sheet, int column, int row, JxlsStyle style) {
		if (sheet == null) return;
		this.style = style;
		this.column = column;
		this.row = row;
		sheet.setCell(this);
	}
	
	protected JxlsCell(JxlsSheet sheet, int column, int row, Date date, JxlsStyle style) {
		this(sheet, column, row, style);
		type = DATE;
		this.date = date;
	}

	protected JxlsCell(JxlsSheet sheet, int column, int row, double f, JxlsStyle style) {
		this(sheet, column, row, style);
		type = NUMBER;
		this.number = f;
	}
	
	protected JxlsCell(JxlsSheet sheet, int column, int row, String text, JxlsStyle style, boolean isFormula) {
		this(sheet, column, row, style);
		this.text = text;
		type = TEXT;
		if (isFormula) {
			type = FORMULA;
			// check if we are using R1C1 and convert to A1
			if (text.startsWith("=")) text = text.substring(1);
			Matcher matcher = Pattern.compile("\\$?R\\d*\\$?C\\d*").matcher(text);
			if (matcher.find()) {
				String changed = "";
				int end = 0;
				do {
					String r1c1 = text.substring(matcher.start(), matcher.end());
					boolean rowIsAbsolute = r1c1.startsWith("$");
					if (rowIsAbsolute) r1c1 = r1c1.substring(1);
					int indOfC = r1c1.indexOf('C');
					String r = r1c1.substring(1, indOfC);
					boolean columnIsAbsolute = r.endsWith("$");
					if (columnIsAbsolute) r = r.substring(0, r.length()-1);
					int c = Integer.parseInt(r1c1.substring(indOfC+1, r1c1.length()));
					changed += text.substring(end, matcher.start()) + 
							(columnIsAbsolute ? "$" : "") + CellReference.convertNumToColString(c-1) + 
							(rowIsAbsolute ? "$" : "") + r;
					end = matcher.end();	
				} while (matcher.find());
				if (end != text.length()) changed += text.substring(end, text.length());
				this.text = changed;
			}
		}
	}
	
	/**
	 * Get the object value contained in the cell
	 * 
	 * @return the value
	 */
	public Object getValue() {
		if (type == DATE) return date;
		if (type == NUMBER) return number;
		return text;
	}
	
	/**
	 * Get the numeric value of the cell
	 * 
	 * @return the value whenever possible and 0 if the value is some text
	 */
	public double getNumericValue() {
		if (type == DATE) return date.getTime();
		if (type == NUMBER) return number;
		return 0;
	}

	/**
	 * Get the value of the cell as text
	 * 
	 * @return the value as a text
	 */
	public String getStringValue() {
		if (type == DATE) return date.toString();
		if (type == NUMBER) return number + "";
		return text;
	}

	/**
	 * Sets the span of the cell
	 * 
	 * @param columnSpan	the number of columns to occupy
	 * @param rowSpan		the number of rows to occupy
	 * @return the cell to chain other settings
	 */
	public JxlsCell setSpan(int columnSpan, int rowSpan) {
		this.columnSpan = (columnSpan <= 1 ? 1 : columnSpan);
		this.rowSpan = (rowSpan <= 1 ? 1 : rowSpan);;		
		return this;
	}

	/**
	 * Sets the hyperlink associated with this cell
	 * 
	 * @param hyperlink	a url associated with the cell content
	 * @return the cell to chain other settings
	 */
	public JxlsCell setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
		return this;
	}

	/**
	 * Gets the hyperlink associated with this cell
	 * 
	 * @return the url
	 */
	public String getHyperlink() {
		return hyperlink;
	}

	/**
	 * Sets the style of the cell
	 * 
	 * @param style the style to render the cell content
	 * @return the cell to chain other settings
	 */
	public JxlsCell setStyle(JxlsStyle style) {
		this.style = style;
		return this;
	}
	
	public JxlsStyle getStyle() {
		return style;
	}
		
	protected void createPOICell(JxlsWorkbook workbook, Sheet sheet, Map<Integer, JxlsStyle> columnStyles) {
		if (style == null) {
			if (columnStyles.containsKey(column)) style =  columnStyles.get(column);
			else if (type == DATE) style = workbook.getDefaultDateStyle();
			else if (type == NUMBER) style = workbook.getDefaultFloatStyle();
			else style = workbook.getDefaultStyle();
		}
		if (column < 1 || row < 1) return;
		Row cellRow = sheet.getRow(row-1);
		if (cellRow == null) cellRow = sheet.createRow(row-1);
		Cell cell = cellRow.getCell(column-1); 
		if (cell == null) cell = cellRow.createCell(column-1);
		if (style != null) {
			if (style.cellStyle == null) style.createPOICellStyle(workbook, sheet.getWorkbook());
			cell.setCellStyle(style.cellStyle);
		}
		if (rowSpan > 1 || columnSpan > 1) {
			for (int rSpan=1; rSpan<rowSpan; rSpan++) {
				for (int cSpan=1; cSpan<columnSpan; cSpan++) {
					Row rangeCellRow = sheet.getRow(row-1+rSpan-1);
					if (rangeCellRow == null) rangeCellRow = sheet.createRow(row-1+rSpan-1);
					Cell rangeCell = rangeCellRow.getCell(column-1+cSpan-1); 
					if (rangeCell == null) rangeCell = rangeCellRow.createCell(column-1+cSpan-1);
					if (style != null) rangeCell.setCellStyle(style.cellStyle);
				}
			}
			sheet.addMergedRegion(new CellRangeAddress(row-1, row-1+rowSpan-1, column-1, column-1+columnSpan-1));
		} 		
		if (type == FORMULA) {
			cell.setCellType(Cell.CELL_TYPE_FORMULA);
			cell.setCellFormula(text);
		} else if (type == TEXT) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(text);
		} else {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			if (type == DATE) cell.setCellValue(date);
			else cell.setCellValue(number);
		}
		if (hyperlink != null) {
			Hyperlink url = sheet.getWorkbook().getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
			url.setAddress(hyperlink);
			cell.setHyperlink(url);
		} 
	}
}