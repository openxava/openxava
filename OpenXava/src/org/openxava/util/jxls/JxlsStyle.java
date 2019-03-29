package org.openxava.util.jxls;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.openxava.util.*;

/**
 * JxlsStyle: a class to wrap and simplify the use of Apache POI CellStyle in the context of OpenXava
 * 
 * Principle:
 * 		JxlsStyle should not be instantiated directly, but rather created through workbook.addStyle(style)
 * 		each setProperty returns the style so the setProperty's can be chained
 * 
 * Usage:
 * <code>
 * 		JxlsWorkbook wb = new JxlsWorkbook("Test");
 *		JxlsSheet sheet = wb.addSheet("Test");
 *		JxlsStyle boldCenteredTextS = wb.addStyle(TEXT).setAlign(CENTER).setBold();
 *		JxlsStyle boldCenteredTextBorderedS = wb.addClonedStyle(boldCenteredTextS).setBorder(BOTTOM, BORDER_THIN);
 *		sheet.setValue(3, 4, "Pi", boldCenteredTextS);
 *		sheet.setValue(4, 4, "Pi", boldCenteredTextBorderedS);
 * </code>
 * @author Laurent Wibaux
 *
 */
public class JxlsStyle implements JxlsConstants {
	
	private short fontSize = 0;
	private String fontName = null;
	private boolean isBold = false;
	private short textColor = BLACK;
	private short bgColor = EMPTY;
	private short align = NONE;
	private int type = TEXT;
	private short borderTop = BORDER_NONE;
	private short borderRight = BORDER_NONE;
	private short borderBottom = BORDER_NONE;
	private short borderLeft = BORDER_NONE;
	private short borderColor = BLACK;
	private boolean wrap = false;
	private String format = null;

	protected CellStyle cellStyle = null;
	
	protected JxlsStyle(JxlsWorkbook workbook, String name, int type) {
		if (workbook == null) return;
		this.type = type;
		workbook.styles.put(Is.empty(name) ? ""+Math.random() : name, this);
	}
	
	protected JxlsStyle(JxlsWorkbook workbook, String name, String format) {
		if (workbook == null) return;
		this.type = FLOAT;
		this.format = format;
		workbook.styles.put(Is.empty(name) ? ""+Math.random() : name, this);
	}
	
	protected JxlsStyle clone(JxlsWorkbook workbook, String name) {
		JxlsStyle style = new JxlsStyle(workbook, name, type);
		style.fontSize = fontSize;
		style.isBold = isBold;
		style.align = align;
		style.textColor = textColor;
		style.bgColor = bgColor;
		style.fontName = fontName;
		style.borderTop = borderTop;
		style.borderRight = borderRight;
		style.borderBottom = borderBottom;
		style.borderLeft = borderLeft;
		style.borderColor = borderColor;
		style.wrap = wrap;
		style.format = format;
		return style;
	}
	
	/**
	 * Sets the font name to use in this style
	 * 
	 * @param fontName	the name of the font
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setFontName(String fontName) {
		this.fontName = fontName;
		cellStyle = null;
		return this;
	}
	
	/**
	 * Sets the font size to use in this style
	 * 
	 * @param fontSize	the size in points of the font
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setFontSize(int fontSize) {
		this.fontSize = (short)fontSize;
		cellStyle = null;
		return this;
	}

	/**
	 * Sets the format to use to display numbers using this style
	 * 
	 * @param format	the format encoded as per xls
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setFormat(String format) {
		this.format = format;
		cellStyle = null;
		return this;
	}

	/**
	 * Make the cell using this style bold
	 * 
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setBold() {
		this.isBold = true;
		cellStyle = null;
		return this;
	}

	/**
	 * Make the cell wraps its content
	 * 
	 * @return	the style to chain other settings
	 */
	public JxlsStyle setWrap(boolean wraps) {
		this.wrap = wraps;
		cellStyle = null;
		return this;
	}

	/**
	 * Make the cell using this style bold or not
	 * 
	 * @param isBold	true to set bold, false otherwise
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setBold(boolean isBold) {
		this.isBold = isBold;
		cellStyle = null;
		return this;
	}

	/**
	 * Sets the color to use to render the writing of the cell
	 * 
	 * @param	textColor	the color (one of JxlsConstants color, or one of HSSFColor.color.index)
	 * @return				the style to chain other settings
	 */
	public JxlsStyle setTextColor(short textColor) {
		this.textColor = textColor;
		cellStyle = null;
		return this;
	}

	/**
	 * Sets the color to use to render the background of the cell
	 * 
	 * @param	bgColor	the color (one of JxlsConstants color, or one of HSSFColor.?.index)
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setCellColor(short bgColor) {
		this.bgColor = bgColor;
		cellStyle = null;
		return this;
	}

	/**
	 * Sets the justification to use to render the content of the cell
	 * 
	 * @param	align	the alignment (one of LEFT, CENTER, RIGHT)
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setAlign(short align) {
		cellStyle = null;
		this.align = align;
		return this;
	}
	
	/**
	 * Sets the borders for the 4 sides of the cell
	 * 
	 * @param top		the border to use at the top (one of BORDER_NONE, BORDER_THIN, BORDER_THICK or CellStyle.BORDER_?)
	 * @param bottom	the border to use at the bottom 
	 * @param left		the border to use at the left 
	 * @param right		the border to use at the right 
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setBorders(short top, short bottom, short left, short right) {
		cellStyle = null;
		this.borderTop = top;
		this.borderBottom = bottom;
		this.borderLeft = left;
		this.borderRight = right;
		return this;
	}

	/**
	 * Sets the borders for some of the sides of the cell
	 * 
	 * @param place				the borders to sets (TOP, TOP+LEFT, TOP+BOTTOM...)
	 * @param borderStyle		the border style to use  (one of BORDER_NONE, BORDER_THIN, BORDER_THICK or CellStyle.BORDER_?)
	 * @return			the style to chain other settings
	 */
	public JxlsStyle setBorder(short place, short borderStyle) {
		cellStyle = null;
		if ((place & TOP) == TOP) this.borderTop = borderStyle;
		if ((place & BOTTOM) == BOTTOM) this.borderBottom = borderStyle;
		if ((place & LEFT) == LEFT) this.borderLeft = borderStyle;
		if ((place & RIGHT) == RIGHT) this.borderRight = borderStyle;
		return this;
	}

	/**
	 * Sets the border color for the 4 sides of the cell
	 * 
	 * @param	borderColor	the color (one of JxlsConstants color, or one of HSSFColor.color.index)
	 * @return				the style to chain other settings
	 */
	public JxlsStyle setBorderColor(short borderColor) {
		cellStyle = null;
		this.borderColor = borderColor;
		return this;
	}

	protected void createPOICellStyle(JxlsWorkbook workbook, Workbook poiWorkbook) {
		if (fontName == null) fontName = workbook.getFontName();
		if (fontSize == 0) fontSize =  workbook.getFontSize();
		String fontKey = fontName + fontSize + (isBold?"b":"p") + textColor;
		Font font = workbook.fonts.get(fontKey);
		if (font == null) {
			font = poiWorkbook.createFont();
			font.setBoldweight(isBold ? Font.BOLDWEIGHT_BOLD : Font.BOLDWEIGHT_NORMAL);
			font.setFontName(fontName);
			font.setColor(textColor);
			font.setFontHeightInPoints(fontSize);
			workbook.fonts.put(fontKey, font);
		}
		cellStyle = poiWorkbook.createCellStyle();
		cellStyle.setFont(font);
		if (bgColor != EMPTY) {
			cellStyle.setFillForegroundColor(bgColor);
			cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		} 
		if (align == NONE) {
			if (type == TEXT) align = LEFT;
			else align = RIGHT;
		}
		short poiAlign = CellStyle.ALIGN_LEFT;
		if (align == RIGHT) poiAlign = CellStyle.ALIGN_RIGHT;
		else if (align == CENTER) poiAlign = CellStyle.ALIGN_CENTER;
		cellStyle.setAlignment(poiAlign);
		cellStyle.setWrapText(wrap);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		if (borderTop != BORDER_NONE) {
			cellStyle.setBorderTop(borderTop);
			cellStyle.setTopBorderColor(borderColor);
		}
		if (borderBottom != BORDER_NONE) {
			cellStyle.setBorderBottom(borderBottom);
			cellStyle.setBottomBorderColor(borderColor);
		}
		if (borderLeft != BORDER_NONE) {
			cellStyle.setBorderLeft(borderLeft);
			cellStyle.setLeftBorderColor(borderColor);
		}
		if (borderRight != BORDER_NONE) {
			cellStyle.setBorderRight(borderRight);
			cellStyle.setRightBorderColor(borderColor);
		}
		DataFormat df = poiWorkbook.createDataFormat();
		if (format != null) cellStyle.setDataFormat(df.getFormat(format));
		else if (type == INTEGER) cellStyle.setDataFormat(df.getFormat(workbook.getIntegerFormat()));
		else if (type == FLOAT) cellStyle.setDataFormat(df.getFormat(workbook.getFloatFormat()));
		else if (type == DATE) cellStyle.setDataFormat(df.getFormat(workbook.getDateFormat()));
		else cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
	}
}

