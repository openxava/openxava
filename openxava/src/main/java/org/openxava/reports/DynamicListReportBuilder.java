package org.openxava.reports;

import java.awt.Color;
import java.io.InputStream;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.openxava.model.meta.MetaProperty;
import org.openxava.tab.Tab;
import org.openxava.util.Strings;
import org.openxava.util.XavaResources;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;

/**
 * @author Javier Paniza
 * @since 8.0
 */
public class DynamicListReportBuilder {

	private static final int EXTRA_WIDTH = 15;
	private static final int MAX_CHARACTERS_PER_ROW = 122;
	private static final int WIDE_CHARACTERS_PER_ROW = 104;
	private static final int MEDIUM_CHARACTERS_PER_ROW = 63;
	private static final int NARROW_CHARACTERS_PER_ROW = 44;
	private static final int COLUMNS_SEPARATION = 10;
	private static final Color GRAY = new Color(0x80, 0x80, 0x80);

	private Tab tab;
	private int[] widths;
	private Integer columnCountLimit;
	private Locale locale;
	private HttpServletRequest request;
	private List<MetaProperty> metaProperties;
	private int totalWidth, letterWidth, letterSize, lineHeight;
	private int pageWidth, pageHeight, columnWidth, detailHeight;
	private int rowsInHeader, totalRecords, totalRecordsWidth;
	private String orientation;
	private Collection<String> totalProperties;

	public DynamicListReportBuilder(Tab tab, int[] widths, Integer columnCountLimit, Locale locale,
			HttpServletRequest request) {
		this.tab = tab;
		this.widths = widths;
		this.columnCountLimit = columnCountLimit;
		this.locale = locale;
		this.request = request;
	}

	public JasperReport build() throws Exception {
		return JasperCompileManager.compileReport(getJasperDesign());
	}

	public JasperDesign getJasperDesign() throws Exception {
		computeLayout();
		return createDesign();
	}

	private void computeLayout() throws Exception {
		metaProperties = getMetaProperties();
		totalProperties = tab.getTotalPropertiesNames();
		totalRecords = (tab.getSelectedKeys().length == 0) ? tab.getTotalSize() : tab.getSelectedKeys().length;
		limitWidths();
		totalWidth = adjustWithsToLabels();
		if (totalWidth > WIDE_CHARACTERS_PER_ROW) {
			if (totalWidth > MAX_CHARACTERS_PER_ROW) tightenWidths();
			else expandWidths(MAX_CHARACTERS_PER_ROW);
			orientation = "Landscape"; letterWidth = 4; letterSize = 7; lineHeight = 8;
			pageWidth = 842; pageHeight = 595; columnWidth = 780;
		} else if (totalWidth > MEDIUM_CHARACTERS_PER_ROW) {
			expandWidths(WIDE_CHARACTERS_PER_ROW);
			orientation = "Landscape"; letterWidth = 5; letterSize = 8; lineHeight = 10;
			pageWidth = 842; pageHeight = 595; columnWidth = 780;
		} else if (totalWidth > NARROW_CHARACTERS_PER_ROW) {
			expandWidths(MEDIUM_CHARACTERS_PER_ROW);
			orientation = "Portrait"; letterWidth = 5; letterSize = 8; lineHeight = 10;
			pageWidth = 595; pageHeight = 842; columnWidth = 535;
		} else {
			expandWidths(NARROW_CHARACTERS_PER_ROW);
			orientation = "Portrait"; letterWidth = 10; letterSize = 12; lineHeight = 15;
			pageWidth = 595; pageHeight = 842; columnWidth = 535;
		}
		rowsInHeader = calculateRowsInHeader();
		detailHeight = lineHeight;
		for (MetaProperty p : metaProperties) {
			if (p.isCompatibleWith(byte[].class)) { detailHeight = 32; break; }
		}
		totalRecordsWidth = columnWidth - 150;
	}

	private List<MetaProperty> getMetaProperties() {
		if (columnCountLimit == null) return new ArrayList<>(tab.getMetaProperties());
		List<MetaProperty> result = new ArrayList<>();
		int c = 0;
		for (MetaProperty p : tab.getMetaProperties()) {
			if (++c > columnCountLimit) break;
			result.add(p);
		}
		return result;
	}

	private void limitWidths() {
		if (columnCountLimit != null && widths.length > columnCountLimit)
			widths = Arrays.copyOf(widths, columnCountLimit);
	}

	private int adjustWithsToLabels() {
		int total = 0;
		for (int i = 0; i < metaProperties.size(); i++) {
			MetaProperty p = metaProperties.get(i);
			String label = p.getQualifiedLabel(locale);
			if (widths[i] == 0) widths[i] = p.getSize();
			int labelLength = Math.min(label.length(), 10);
			if (widths[i] < labelLength) widths[i] = labelLength;
			total += widths[i];
		}
		return total;
	}

	private int calculateRowsInHeader() {
		int rows = 1;
		for (int i = 0; i < metaProperties.size(); i++) {
			String label = metaProperties.get(i).getQualifiedLabel(locale);
			int r = (label.length() - 1) / (int) (widths[i] * 1.58) + 1;
			rows = Math.max(rows, r);
		}
		return rows;
	}

	private void expandWidths(int max) {
		int total = 0;
		List<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] < metaProperties.get(i).getQualifiedLabel(locale).length()) candidates.add(i);
			total += widths[i];
		}
		if (total < max && !candidates.isEmpty()) {
			int extra = (max - total) / candidates.size();
			for (int i : candidates) widths[i] += extra;
		}
	}

	private void tightenWidths() {
		int[] originalWidths = widths.clone();
		int littleTotal = 0, littleCount = 0;
		for (int w : widths) if (w <= 20) { littleTotal += w; littleCount++; }
		int bigCount = widths.length - littleCount;
		int widthForBig = bigCount == 0 ? 20 : (MAX_CHARACTERS_PER_ROW - littleTotal) / bigCount;
		if (widthForBig < 20) widthForBig = 20;
		int total = 0;
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] > 20 && widths[i] > widthForBig) widths[i] = widthForBig;
			total += widths[i];
		}
		if (total > MAX_CHARACTERS_PER_ROW) {
			metaProperties.remove(metaProperties.size() - 1);
			widths = Arrays.copyOf(originalWidths, originalWidths.length - 1);
			tightenWidths();
		}
	}

	private HorizontalTextAlignEnum getAlign(MetaProperty p) throws Exception {
		if (p.isNumber() && !p.hasValidValues()) return HorizontalTextAlignEnum.RIGHT;
		if (p.getType().equals(boolean.class)) return HorizontalTextAlignEnum.CENTER;
		return HorizontalTextAlignEnum.LEFT;
	}

	private JasperDesign createDesign() throws JRException, Exception {
		JasperDesign design = new JasperDesign();
		design.setName(Strings.change(tab.getModelName(), ".", "_"));
		design.setColumnCount(1);
		design.setPrintOrder(PrintOrderEnum.VERTICAL);
		design.setOrientation("Landscape".equals(orientation) ? OrientationEnum.LANDSCAPE : OrientationEnum.PORTRAIT);
		design.setPageWidth(pageWidth);
		design.setPageHeight(pageHeight);
		design.setColumnWidth(columnWidth);
		design.setColumnSpacing(0);
		design.setLeftMargin(30);
		design.setRightMargin(30);
		design.setTopMargin(20);
		design.setBottomMargin(20);
		design.setWhenNoDataType(WhenNoDataTypeEnum.NO_PAGES);
		design.setTitleNewPage(false);
		design.setSummaryNewPage(false);
		addStyles(design);
		addParameters(design);
		addFields(design);
		addVariable(design);
		addBackground(design);
		addTitle(design);
		design.setPageHeader(createBand(9));
		addColumnHeader(design);
		addDetail(design);
		addPageFooter(design);
		addSummary(design);
		return design;
	}

	private JRDesignBand createBand(int height) {
		JRDesignBand band = new JRDesignBand();
		band.setHeight(height);
		band.setSplitType(SplitTypeEnum.STRETCH);
		return band;
	}

	private void addStyles(JasperDesign design) throws JRException {
		String fn = "DejaVu Sans", enc = "Identity-H";
		JRDesignStyle normal = new JRDesignStyle();
		normal.setName("Arial_Normal"); normal.setDefault(true);
		normal.setFontName(fn); normal.setFontSize(8f);
		normal.setPdfFontName(fn); normal.setPdfEncoding(enc); normal.setPdfEmbedded(true);
		design.addStyle(normal);
		JRDesignStyle bold = new JRDesignStyle();
		bold.setName("Arial_Bold"); bold.setFontName(fn); bold.setFontSize(8f); bold.setBold(true);
		bold.setPdfFontName(fn); bold.setPdfEncoding(enc); bold.setPdfEmbedded(true);
		design.addStyle(bold);
		JRDesignStyle italic = new JRDesignStyle();
		italic.setName("Arial_Italic"); italic.setFontName(fn); italic.setFontSize(8f); italic.setItalic(true);
		italic.setPdfFontName(fn); italic.setPdfEncoding(enc); italic.setPdfEmbedded(true);
		design.addStyle(italic);
	}

	private void addParameters(JasperDesign design) throws JRException {
		addStringParam(design, "Title");
		addStringParam(design, "Organization");
		addStringParam(design, "Date");
		for (MetaProperty p : metaProperties)
			if (totalProperties.contains(p.getQualifiedName()))
				addStringParam(design, p.getQualifiedName() + "__TOTAL__");
	}

	private void addStringParam(JasperDesign design, String name) throws JRException {
		JRDesignParameter param = new JRDesignParameter();
		param.setName(name);
		param.setValueClass(String.class);
		design.addParameter(param);
	}

	private void addFields(JasperDesign design) throws JRException {
		for (MetaProperty p : metaProperties) {
			JRDesignField field = new JRDesignField();
			field.setName(Strings.change(p.getQualifiedName(), ".", "_"));
			field.setValueClass(p.isCompatibleWith(byte[].class) ? InputStream.class : String.class);
			design.addField(field);
		}
	}

	private void addVariable(JasperDesign design) throws JRException {
		JRDesignVariable var = new JRDesignVariable();
		var.setName("Variable_1");
		var.setValueClass(Integer.class);
		var.setIncrementType(IncrementTypeEnum.REPORT);
		JRDesignExpression expr = new JRDesignExpression();
		expr.setText("$V{REPORT_COUNT}");
		var.setExpression(expr);
		design.addVariable(var);
	}

	private void addBackground(JasperDesign design) {
		design.setBackground(createBand(0));
	}

	private void addTitle(JasperDesign design) {
		JRDesignBand band = createBand(25);
		band.addElement(createTextField(design, 0, 0, 200, 25,
				HorizontalTextAlignEnum.LEFT, "Arial_Normal", 8, "$P{Organization}", true, true));
		band.addElement(createTextField(design, 5, 5, columnWidth, 20,
				HorizontalTextAlignEnum.CENTER, "Arial_Normal", 15, "$P{Title}", true, true));
		JRDesignTextField count = createTextField(design, totalRecordsWidth, 0, 150, 25,
				HorizontalTextAlignEnum.RIGHT, "Arial_Normal", 8,
				"\"" + XavaResources.getString(request, "record_count") + ": \" + " + totalRecords, true, true);
		count.setPrintRepeatedValues(false);
		band.addElement(count);
		JRDesignLine line = createLine(design, 0, 23, columnWidth, 0, Color.BLACK, 1.0f, LineDirectionEnum.TOP_DOWN);
		line.setStretchType(StretchTypeEnum.NO_STRETCH);
		band.addElement(line);
		design.setTitle(band);
	}

	private void addColumnHeader(JasperDesign design) throws Exception {
		int hh = rowsInHeader * lineHeight + 8;
		JRDesignBand band = createBand(hh);
		JRDesignRectangle rect = new JRDesignRectangle(design);
		rect.setMode(ModeEnum.OPAQUE); rect.setX(0); rect.setY(0);
		rect.setWidth(columnWidth); rect.setHeight(hh - 5);
		rect.setForecolor(Color.BLACK); rect.setBackcolor(GRAY);
		rect.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		rect.setStretchType(StretchTypeEnum.NO_STRETCH);
		rect.setFill(FillEnum.SOLID);
		rect.getLinePen().setLineWidth(0.0f);
		band.addElement(rect);
		JRDesignLine top = createLine(design, 0, 0, columnWidth, 0, Color.BLACK, 0.5f, LineDirectionEnum.BOTTOM_UP);
		top.setStretchType(StretchTypeEnum.NO_STRETCH);
		band.addElement(top);
		JRDesignLine bot = createLine(design, 0, hh - 6, columnWidth, 0, Color.BLACK, 0.5f, LineDirectionEnum.BOTTOM_UP);
		bot.setStretchType(StretchTypeEnum.NO_STRETCH);
		band.addElement(bot);
		int x = 0;
		for (int i = 0; i < metaProperties.size(); i++) {
			MetaProperty p = metaProperties.get(i);
			int w = widths[i] * letterWidth + EXTRA_WIDTH;
			JRDesignStaticText text = new JRDesignStaticText(design);
			text.setMode(ModeEnum.TRANSPARENT);
			text.setX(x); text.setY(2); text.setWidth(w); text.setHeight(hh - 2);
			text.setForecolor(Color.WHITE); text.setBackcolor(Color.WHITE);
			text.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
			text.setPrintInFirstWholeBand(true); text.setPrintWhenDetailOverflows(true);
			text.setHorizontalTextAlign(getAlign(p));
			text.setVerticalTextAlign(VerticalTextAlignEnum.TOP);
			text.setStyleNameReference("Arial_Normal");
			text.setFontSize((float) letterSize);
			text.getParagraph().setLineSpacing(LineSpacingEnum.SINGLE);
			text.setText(p.getQualifiedLabel(locale));
			band.addElement(text);
			x += w + COLUMNS_SEPARATION;
		}
		design.setColumnHeader(band);
	}

	private void addDetail(JasperDesign design) throws Exception {
		JRDesignBand band = createBand(detailHeight + 2);
		JRDesignLine line = createLine(design, 0, 0, columnWidth, 0, GRAY, 0.5f, LineDirectionEnum.TOP_DOWN);
		line.setPositionType(PositionTypeEnum.FLOAT);
		line.setPrintWhenDetailOverflows(true);
		line.setStretchType(StretchTypeEnum.NO_STRETCH);
		band.addElement(line);
		int x = 0;
		for (int i = 0; i < metaProperties.size(); i++) {
			MetaProperty p = metaProperties.get(i);
			int w = widths[i] * letterWidth + EXTRA_WIDTH;
			String fn = Strings.change(p.getQualifiedName(), ".", "_");
			if (p.isCompatibleWith(byte[].class)) {
				JRDesignImage img = new JRDesignImage(design);
				img.setX(x); img.setY(2); img.setWidth(w); img.setHeight(30);
				img.setOnErrorType(OnErrorTypeEnum.BLANK);
				JRDesignExpression ie = new JRDesignExpression();
				ie.setText("$F{" + fn + "}");
				img.setExpression(ie);
				band.addElement(img);
			} else {
				JRDesignTextField tf = createTextField(design, x, 2, w, detailHeight,
						getAlign(p), "Arial_Normal", letterSize, "$F{" + fn + "}", true, true);
				tf.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
				band.addElement(tf);
			}
			x += w + COLUMNS_SEPARATION;
		}
		JRDesignSection ds = (JRDesignSection) design.getDetailSection();
		if (ds.getBands() != null && ds.getBands().length > 0) ds.removeBand(0);
		ds.addBand(band);
	}

	private void addPageFooter(JasperDesign design) {
		JRDesignBand band = createBand(27);
		JRDesignTextField page = createTextField(design, columnWidth - 210, 4, 174, 19,
				HorizontalTextAlignEnum.RIGHT, "Arial_Normal", 10, null, false, false);
		page.setTextAdjust(TextAdjustEnum.CUT_TEXT);
		page.setEvaluationTime(EvaluationTimeEnum.NOW);
		JRDesignExpression pe = new JRDesignExpression();
		pe.setText("\"" + XavaResources.getString(request, "page") + " \" + $V{PAGE_NUMBER} + \" "
				+ XavaResources.getString("of") + " \"");
		page.setExpression(pe);
		band.addElement(page);
		JRDesignTextField pnum = createTextField(design, columnWidth - 36, 4, 36, 19,
				HorizontalTextAlignEnum.LEFT, "Arial_Normal", 10, null, false, false);
		pnum.setTextAdjust(TextAdjustEnum.CUT_TEXT);
		pnum.setEvaluationTime(EvaluationTimeEnum.REPORT);
		JRDesignExpression pne = new JRDesignExpression();
		pne.setText("\" \" + $V{PAGE_NUMBER}");
		pnum.setExpression(pne);
		band.addElement(pnum);
		JRDesignLine line = createLine(design, 0, 1, columnWidth, 0, Color.BLACK, 2.0f, LineDirectionEnum.TOP_DOWN);
		band.addElement(line);
		JRDesignTextField date = createTextField(design, 1, 6, 209, 19,
				HorizontalTextAlignEnum.LEFT, "Arial_Normal", 10, "$P{Date}", false, false);
		date.setTextAdjust(TextAdjustEnum.CUT_TEXT);
		date.setEvaluationTime(EvaluationTimeEnum.NOW);
		band.addElement(date);
		design.setPageFooter(band);
	}

	private void addSummary(JasperDesign design) throws Exception {
		JRDesignBand band = createBand(19);
		JRDesignLine line = createLine(design, 0, 0, columnWidth, 0, GRAY, 0.5f, LineDirectionEnum.TOP_DOWN);
		line.setPositionType(PositionTypeEnum.FLOAT);
		line.setPrintWhenDetailOverflows(true);
		line.setStretchType(StretchTypeEnum.NO_STRETCH);
		band.addElement(line);
		int x = 0;
		for (int i = 0; i < metaProperties.size(); i++) {
			MetaProperty p = metaProperties.get(i);
			int w = widths[i] * letterWidth + EXTRA_WIDTH;
			if (totalProperties.contains(p.getQualifiedName())) {
				JRDesignTextField tf = createTextField(design, x, 2, w, lineHeight,
						getAlign(p), "Arial_Bold", letterSize,
						"$P{" + p.getQualifiedName() + "__TOTAL__}", true, true);
				tf.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
				band.addElement(tf);
			}
			x += w + COLUMNS_SEPARATION;
		}
		design.setSummary(band);
	}

	private JRDesignTextField createTextField(JasperDesign design, int x, int y, int w, int h,
			HorizontalTextAlignEnum ha, String style, int fs, String exprText,
			boolean transparent, boolean blankWhenNull) {
		JRDesignTextField tf = new JRDesignTextField(design);
		tf.setMode(transparent ? ModeEnum.TRANSPARENT : ModeEnum.OPAQUE);
		tf.setX(x); tf.setY(y); tf.setWidth(w); tf.setHeight(h);
		tf.setForecolor(Color.BLACK); tf.setBackcolor(Color.WHITE);
		tf.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		tf.setPrintRepeatedValues(true); tf.setRemoveLineWhenBlank(false);
		tf.setPrintInFirstWholeBand(false); tf.setPrintWhenDetailOverflows(false);
		tf.setHorizontalTextAlign(ha);
		tf.setVerticalTextAlign(VerticalTextAlignEnum.TOP);
		tf.setStyleNameReference(style);
		tf.setFontSize((float) fs);
		tf.getParagraph().setLineSpacing(LineSpacingEnum.SINGLE);
		tf.setBlankWhenNull(blankWhenNull);
		tf.setEvaluationTime(EvaluationTimeEnum.NOW);
		if (exprText != null) {
			JRDesignExpression expr = new JRDesignExpression();
			expr.setText(exprText);
			tf.setExpression(expr);
		}
		return tf;
	}

	private JRDesignLine createLine(JasperDesign design, int x, int y, int w, int h,
			Color forecolor, float lineWidth, LineDirectionEnum dir) {
		JRDesignLine line = new JRDesignLine(design);
		line.setMode(ModeEnum.OPAQUE);
		line.setX(x); line.setY(y); line.setWidth(w); line.setHeight(h);
		line.setForecolor(forecolor); line.setBackcolor(Color.WHITE);
		line.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		line.setPrintRepeatedValues(true); line.setRemoveLineWhenBlank(false);
		line.setPrintInFirstWholeBand(false); line.setPrintWhenDetailOverflows(false);
		line.setDirection(dir);
		line.setFill(FillEnum.SOLID);
		line.getLinePen().setLineWidth(lineWidth);
		line.getLinePen().setLineColor(forecolor);
		return line;
	}
}
