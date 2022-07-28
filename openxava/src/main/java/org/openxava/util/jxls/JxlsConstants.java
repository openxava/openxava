package org.openxava.util.jxls;

import org.apache.poi.hssf.util.*;
import org.apache.poi.ss.usermodel.*;

public interface JxlsConstants {
	
	/* tmr
	public static final short EMPTY = CellStyle.NO_FILL;
	public static final short BLACK = HSSFColor.BLACK.index;
	public static final short WHITE = HSSFColor.WHITE.index;
	public static final short RED = HSSFColor.RED.index;;
	public static final short BLUE = HSSFColor.BLUE.index;;
	public static final short GREEN = HSSFColor.GREEN.index;;
	public static final short LIGHT_YELLOW = HSSFColor.LIGHT_YELLOW.index;;
	public static final short LIGHT_GREY = HSSFColor.GREY_25_PERCENT.index;;
	public static final short LIGHT_GREEN = HSSFColor.LIGHT_GREEN.index;;
	*/
	
	// tmr ini
	// tmr Migration
	public static final short EMPTY = (short) FillPatternType.NO_FILL.ordinal(); // Not sure about this, it was CellStyle.NO_FILL 
	public static final short BLACK = HSSFColor.HSSFColorPredefined.BLACK.getIndex();
	public static final short WHITE = HSSFColor.HSSFColorPredefined.WHITE.getIndex(); 
	public static final short RED = HSSFColor.HSSFColorPredefined.RED.getIndex(); 
	public static final short BLUE = HSSFColor.HSSFColorPredefined.BLUE.getIndex(); 
	public static final short GREEN = HSSFColor.HSSFColorPredefined.GREEN.getIndex(); 
	public static final short LIGHT_YELLOW = HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex(); 
	public static final short LIGHT_GREY = HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex(); 
	public static final short LIGHT_GREEN = HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex(); 	
	// tmr fin

	public static final short NONE = 0;
	public static final short LEFT = 1;
	public static final short RIGHT = 2;
	public static final short LEFT_RIGHT = LEFT + RIGHT;
	public static final short TOP = 8;
	public static final short BOTTOM = 16;
	public static final short TOP_BOTTOM = TOP + BOTTOM;
	public static final short CENTER = 64;
	public static final short ALL = LEFT + RIGHT + TOP + BOTTOM;

	public static final boolean BOLD = true;
	public static final boolean PLAIN = false;

	public static final int TEXT = 0;
	public static final int INTEGER = 1;
	public static final int FLOAT = 2;
	public static final int DATE = 3;

	public static final int AUTO_SIZE = -1;
	
	/* tmr
	public static final short BORDER_NONE = CellStyle.BORDER_NONE;
	public static final short BORDER_THIN = CellStyle.BORDER_THIN;
	public static final short BORDER_THICK = CellStyle.BORDER_THICK;
	*/
	// tmr ini
	// tmr Migration
	public static final short BORDER_NONE = BorderStyle.NONE.getCode();
	public static final short BORDER_THIN = BorderStyle.THIN.getCode(); 
	public static final short BORDER_THICK = BorderStyle.THICK.getCode(); 
	// tmr fin
	
}
