package org.openxava.util.jxls;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

/**
 *	JxlsWorkbook: a class to wrap and simplify the use of Apache POI Workbook in the context of OpenXava
 *	
 *	JxlsWorkbook's can be created:
 *		- empty					JxlsWorkbook wb = new JxlsWorkbook("Test");
 *		- from a TableModel		JxlsWorkbook wb = new JxlsWorkbook(tableModel, "Test");
 *		- from an xls file		JxlsWorkbook wb = new JxlsWorkbook(xlsFile);
 *	
 *	Usage:
 * <code>
 *		JxlsWorkbook wb = new JxlsWorkbook("Test");
 *		JxlsSheet sheet = wb.addSheet("Test");
 *		sheet.setValue(3, 4, "Pi", wb.addStyle(TEXT).setAlign(CENTER).setBold());
 *		sheet.setValue(4, 4, 3.141592654, wb.addStyle(FLOAT).setAllBorders(THIN_BORDER));
 *		sheet.setFormula(4, 5, "=2*$R4$C4", wb.addStyle("##0.0000"));
 *		sheet.setFormula(4, 6, "=2*R4C4", wb.addStyle("##0.000"));
 *		wb.write(new FileOutputStream("c:/Test.xls"));
 * </code>
 * 
 *	Use of POI more advanced functionalities
 * <code>
 *		JxlsWorkbook wb = new JxlsWorkbook("Test");
 *		JxlsSheet sheet = wb.addSheet("Test");
 *		sheet.setValue(3, 4, "Pi", wb.addStyle(TEXT).setAlign(CENTER).setBold());
 *		Workbook poiWorkbook = wb.createPOIWorkbook();
 *		// do an advanced function
 *		poiWorkbook.write(new FileOutputStream("c:/Test.xls"));
 * </code>
 *
 * @author Laurent Wibaux
 * 
 **/
public class JxlsWorkbook implements JxlsConstants {

	private String fontName = "Calibri"; 
	private short fontSize = 11; 
	private String floatFormat = "### ### ##0.00";
	private String integerFormat = "### ### ##0";
	private String dateFormat = "yyyy-MM-dd";
	
	private JxlsStyle defaultStyle;
	private JxlsStyle defaultDateStyle;
	private JxlsStyle defaultFloatStyle;
	
	private String name = null;
	
	protected Map<String, JxlsStyle> styles = new HashMap<String, JxlsStyle>();
	protected Map<String, Font> fonts = new HashMap<String, Font>();
	protected Vector<JxlsSheet> sheets = new Vector<JxlsSheet>();
	protected Map<String, JxlsSheet> sheetNames = new HashMap<String, JxlsSheet>();
	
	protected Workbook poiWorkbook = null;
	
	/**
	 * Constructs an empty JxlsWorkbook
	 *  
	 * @param name: the name of the JxlsWorkbook
	 */
	public JxlsWorkbook(String name) {
		this.name = name;
	}

	/**
	 * Constructs a JxlsWorkbook containing the data of the table
	 *  
	 * @param table: a Swing table model
	 * @param name: the name of the Workbook
	 */
	public JxlsWorkbook(TableModel table, String name) {
		if (table == null) return;
		this.name = name;
		JxlsStyle boldS = addStyle(TEXT).setBold();
		JxlsStyle intS = addStyle(INTEGER).setAlign(RIGHT);
		JxlsStyle floatS = addStyle(FLOAT).setAlign(RIGHT);
		JxlsStyle dateS = addStyle(DATE);
		JxlsStyle stringS = addStyle(TEXT);
		JxlsSheet sheet = addSheet(name);
		sheet.setFreezePane(0, 1);
		int columns = table.getColumnCount(); 
		for (int column=0; column<columns; column++) {
			sheet.setValue(column+1, 1, table.getColumnName(column), boldS);
			sheet.setColumnWidths(column+1, AUTO_SIZE);
		}
		for (int row=0; row<table.getRowCount(); row++) {
			for (int column=0; column<columns; column++) {
				Object value = table.getValueAt(row, column);
				JxlsStyle style = stringS;
				if (value instanceof Integer) style = intS;
				else if (value instanceof Date) style = dateS;
				else if (value instanceof Number) style = floatS;
				sheet.setValue(column+1, row+2, value, style);
			}
		}
	}
	
	/**
	 * Constructs a JxlsWorkbook and fills it with the raw data of the input file
	 * styles are not copied
	 * 
	 * @param xlsFile: an xls file
	 */
	public JxlsWorkbook(File xlsFile) {
		try {
			name = xlsFile.getName();
			if (name.indexOf('.') != -1) name = name.substring(0, name.lastIndexOf('.'));
			FileInputStream fis = new FileInputStream(xlsFile);
			Workbook poiWorkbook = new HSSFWorkbook(fis);
			FormulaEvaluator poiEvaluator = poiWorkbook.getCreationHelper().createFormulaEvaluator();
			for (int poiSheetNb=0; poiSheetNb<poiWorkbook.getNumberOfSheets(); poiSheetNb++) {
				Sheet poiSheet = poiWorkbook.getSheetAt(poiSheetNb);
				JxlsSheet sheet = addSheet(poiSheet.getSheetName());
				for (Row poiRow : poiSheet) {
		        	int row = poiRow.getRowNum() + 1;
			        for (Cell poiCell : poiRow) {
			        	int column = poiCell.getColumnIndex() + 1;
			            switch (poiCell.getCellType()) {
			            	case Cell.CELL_TYPE_BOOLEAN:
			                case Cell.CELL_TYPE_STRING:
			                	sheet.setValue(column, row, poiCell.getRichStringCellValue().getString());
			                    break;
			                case Cell.CELL_TYPE_NUMERIC:
			                    if (DateUtil.isCellDateFormatted(poiCell)) sheet.setValue(column, row, poiCell.getDateCellValue());
			                    else sheet.setValue(column, row, poiCell.getNumericCellValue());
			                    break;
			                case Cell.CELL_TYPE_FORMULA:	
			                	CellValue poiCellValue = poiEvaluator.evaluate(poiCell);
			                	switch (poiCellValue.getCellType()) {
			                		case Cell.CELL_TYPE_NUMERIC:
			                			sheet.setValue(column, row, poiCellValue.getNumberValue());
			                			break;
			                		case Cell.CELL_TYPE_BOOLEAN:
					                	sheet.setValue(column, row, ""+poiCellValue.getBooleanValue());
					                    break;
					                case Cell.CELL_TYPE_STRING:
					                	sheet.setValue(column, row, poiCellValue.getStringValue());
					                    break;
				                    default:
				                    	break;
			                	}
			                    break;
			                default:
			                	break;
			            }
			        }
				}
		    }
			fis.close();
		} catch (Exception e) {
		}
	}
	
	public String getFontName() {
		return fontName;
	}

	/**
	 * Sets the default font to use when generating the xls
	 * 
	 * @param fontName: the name of the font 
	 */
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public short getFontSize() {
		return fontSize;
	}

	/**
	 * Sets the default font size to use when generating the xls
	 * 
	 * @param fontSize: the point size of the font 
	 */
	public void setFontSize(short fontSize) {
		this.fontSize = fontSize;
	}

	public String getFloatFormat() {
		return floatFormat;
	}

	/**
	 * Sets the default format to use for numbers when generating the xls 
	 * This should be in the form "###.0"
	 * 
	 * @param floatFormat: the format 
	 */
	public void setFloatFormat(String floatFormat) {
		this.floatFormat = floatFormat;
	}

	public String getIntegerFormat() {
		return integerFormat;
	}

	/**
	 * Sets the default format to use for integers when generating the xls 
	 * This should be in the form "### ###"
	 * 
	 * @param integerFormat: the format 
	 */
	public void setIntegerFormat(String integerFormat) {
		this.integerFormat = integerFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * Sets the default format to use for integers when generating the xls 
	 * This should be in the form "dd/MM/yy"
	 * 
	 * @param dateFormat: the format 
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public JxlsStyle getDefaultStyle() {
		return defaultStyle;
	}

	public JxlsStyle getDefaultDateStyle() {
		return defaultDateStyle;
	}

	public JxlsStyle getDefaultFloatStyle() {
		return defaultFloatStyle;
	}

	public JxlsStyle addStyle(int type) {
		return addStyle(null, type);
	}

	public JxlsStyle addStyle(String format) {
		return addStyle(null, format);
	}

	public JxlsStyle addClonedStyle(JxlsStyle style) {
		return addClonedStyle(null, style);
	}
	
	/**
	 * Creates a JxlsStyle which can be referenced when using sheet.setValue(column, row, value, style)
	 * 
	 * @param name the name to use when recalling the style
	 * @param type the type of value managed by the style (one of: TEXT, INTEGER, FLOAT, DATE)
	 * @return the JxlsStyle 
	 */
	public JxlsStyle addStyle(String name, int type) {
		return new JxlsStyle(this, name, type);
	}

	/**
	 * Creates a JxlsStyle which can be referenced when using sheet.setValue(column, row, value, style)
	 * 
	 * @param name the name to use when recalling the style
	 * @param format the format the style should use ("#.0%")
	 * @return the JxlsStyle
	 */
	public JxlsStyle addStyle(String name, String format) {
		return new JxlsStyle(this, name, format);
	}

	/**
	 * Creates a JxlsStyle based upon another existing JxlsStyle which can be referenced when using sheet.setValue(column, row, value, style)
	 * 
	 * @param name the name to use when recalling the style
	 * @param style the style to clone
	 * @return the cloned JxlsStyle
	 */
	public JxlsStyle addClonedStyle(String name, JxlsStyle style) {
		if (style == null) return null;
		return style.clone(this, name);
	}
	
	public JxlsStyle getStyle(String name) {
		return styles.get(name);
	}
	
	/**
	 * Creates a JxlsSheet in which cells can be added
	 * 
	 * @param name the name which will appear on the tab
	 * @return the JxlsSheet
	 */
	public JxlsSheet addSheet(String name) {
		JxlsSheet sheet = new JxlsSheet(this, name);
		return sheet;
	}

	/**
	 * Creates a JxlsSheet in which cells can be added
	 * 
	 * @param name the name which will appear on the tab
	 * @param index the index at which the sheet should appear in the tab list 
	 * @return the JxlsSheet
	 */
	public JxlsSheet addSheet(String name, int index) {
		JxlsSheet sheet = new JxlsSheet(this, name, index);
		return sheet;
	}
	
	/**
	 * Deletes a JxlsSheet
	 * 
	 * @param index the index of the tab to delete
	 */
	public void deleteSheet(int index) {
		sheets.remove(index);
	}
	
	/**
	 * Gets a sheet through its index
	 * 
	 * @param index the index of the sheet
	 * @return the JxlsSheet
	 */
	public JxlsSheet getSheet(int index) {
		if (index < 0 || index >= sheets.size()) return null;
		return sheets.elementAt(index);
	}

	/**
	 * Gets a sheet through its name
	 * 
	 * @param name the name of the sheet
	 * @return the JxlsSheet
	 */
	public JxlsSheet getSheet(String name) {
		return sheetNames.get(name);
	}

	/**
	 * Gets the list of sheets
	 * 
	 * @return a Vector<JxlsSheet>
	 */
	public Vector<JxlsSheet> getSheets() {
		return sheets;
	}

	/**
	 * Gets a map containing the sheets mapped by name
	 * 
	 * @return a Map<String, JxlsSheet>
	 */
	public Map<String, JxlsSheet> getSheetsMap() {
		return sheetNames;
	}

	/**
	 * Resets the Apache POI Workbook from the JxlsWorkbok to regenerate the Workbook on next createPOIWorkbook
	 * 
	 */
	public void deletePOIWorkbook() {
		poiWorkbook = null;
	}
	
	/**
	 * Creates an apache POI Workbook from the JxlsWorkbok

	 * @return a Workbook
	 */
	public Workbook createPOIWorkbook() {
		// return the previously created poiWorkbook if it exists
		if (poiWorkbook != null) return poiWorkbook;
		// only add default styles if they were not created before
		if (defaultStyle == null) { 
			defaultStyle = addStyle(TEXT);
			defaultDateStyle = addStyle(DATE).setAlign(RIGHT);
			defaultFloatStyle = addStyle(FLOAT).setAlign(RIGHT);
		}
		// re-initialize the styles for the POI to take into account changes in styles between 2 creations
		fonts = new HashMap<String, Font>();
		for (String name : styles.keySet()) styles.get(name).cellStyle = null;
		// create the workbook
		poiWorkbook = new HSSFWorkbook();
		if (sheets.size() == 0) (new JxlsSheet(this, "Sheet1")).createPOISheet(this, poiWorkbook);
		for (JxlsSheet sheet: sheets) sheet.createPOISheet(this, poiWorkbook); 
		// evaluate all formula after setting all cells
		FormulaEvaluator poiFormulaEvaluator = poiWorkbook.getCreationHelper().createFormulaEvaluator();
		for (int sheetNum=0; sheetNum<poiWorkbook.getNumberOfSheets(); sheetNum++) {
		    Sheet poiSheet = poiWorkbook.getSheetAt(sheetNum);
		    for (Row poiRow : poiSheet) {
		        for (Cell poiCell : poiRow) {
		            if (poiCell.getCellType() == Cell.CELL_TYPE_FORMULA) poiFormulaEvaluator.evaluateFormulaCell(poiCell);
		        }
		    }
		}
		return poiWorkbook;
	}
	
	/**
	 * Writes the xls to an OutputStream
	 * 
	 * @param os the OutputStream to write to
	 * @throws Exception if the OutputStream can not be opened
	 */
	public void write(OutputStream os) throws Exception {
		createPOIWorkbook().write(os);
		os.close();
	}
	
	/**
	 * Writes the xls to an HttpServletResponse, setting the correct mime type and headers
	 * 
	 * @param response the HttpServletResponse to write to
	 * @throws Exception if the HttpServletResponse can not be written
	 */
	public void write(HttpServletResponse response) throws Exception {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + name + ".xls");
		write(response.getOutputStream());
	}

	public static void main(String args[]) throws Exception {
		JxlsWorkbook wb = new JxlsWorkbook("Test");
		JxlsSheet sheet = wb.addSheet("Test");
		sheet.setValue(3, 4, "Pi", wb.addStyle(TEXT).setAlign(CENTER).setBold());
		sheet.setValue(4, 4, 3.141592654, wb.addStyle(FLOAT).setBorder(ALL, BORDER_THIN));
		sheet.setFormula(4, 5, "=2*$R4$C4", wb.addStyle("##0.0000"));
		sheet.setFormula(4, 6, "=2*R4C4", wb.addStyle("##0.000"));
		wb.write(new FileOutputStream("c:/_temp/Test.xls"));
		JxlsWorkbook wb2 = new JxlsWorkbook(new File("c:/_temp/Test.xls"));
		wb2.write(new FileOutputStream("c:/_temp/Test2.xls"));
	}


}



