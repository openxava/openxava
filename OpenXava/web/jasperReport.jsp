<%="<?xml version='1.0' encoding='" + org.openxava.util.XSystem.getEncoding() + "' ?>"%>

<!DOCTYPE jasperReport PUBLIC "-//JasperReports//DTD Report Design//EN" 
"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd">

<%-- 
If you modify this file please past the manual tests in 
OpenXavaTest/src/org/openxava/test/tests/PrettyPrintingTest.txt  
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="java.util.List" %> 
<%@ page import="java.util.Collection" %> 
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.util.Primitives" %>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.tab.meta.MetaTab" %>
<%@ page import="org.openxava.tab.Tab"%> 
<%@ page import="org.openxava.component.MetaComponent" %>
<%@ page import="org.openxava.model.meta.MetaModel" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.XSystem"%>
<%@ page import="org.openxava.util.XavaPreferences"%>

<%!
private static int EXTRA_WIDTH = 15; 
private static int MAX_CHARACTERS_PER_ROW = 122;   // If you modify these
private static int WIDE_CHARACTERS_PER_ROW = 104; // values please verify
private static int MEDIUM_CHARACTERS_PER_ROW = 63; // if the tests in PrettyPrintingTest
private static int NARROW_CHARACTERS_PER_ROW = 44; // pass for the 4 branch of the if below

private int [] parseWidths(String widths, Integer columnCountLimit) { 
	String [] tokens = widths.split("[\\[\\], ]+");		
	int size = columnCountLimit == null?tokens.length - 1:columnCountLimit.intValue(); 
	int [] result = new int[size];
	for (int i=0; i<result.length; i++) {
		result[i] = Integer.parseInt(tokens[i+1]);
	}	
	return result;	
}

private int adjustWithsToLabels(List metaProperties, int [] widths, Locale locale) {  
	int totalWidth = 0;	
	int i=0;
	for (Iterator it = metaProperties.iterator(); it.hasNext(); i++) {
		MetaProperty p = (MetaProperty) it.next();
		String label = p.getQualifiedLabel(locale);  
		if (widths[i] == 0) widths[i] = p.getSize();
		int labelLength = Math.min(label.length(), 10); 
		if (widths[i] < labelLength) widths[i] = labelLength;
		totalWidth+=widths[i];		
	}
	return totalWidth;
}

private int calculateRowsInHeader(List metaProperties, int [] widths, Locale locale) { 	 	
	int rowsInHeader = 1;
	int i=0;
	for (Iterator it = metaProperties.iterator(); it.hasNext(); i++) {
		MetaProperty p = (MetaProperty) it.next();
		String label = p.getQualifiedLabel(locale); 
		int rows = (label.length() - 1) / (int) (widths[i] * 1.58) + 1; 
		rowsInHeader = Math.max(rowsInHeader, rows);	
	}
	return rowsInHeader;
}

private void expandWidths(List metaProperties, int [] widths, int max, Locale locale) { 
	int totalWidth = 0;
	Collection candidates = new java.util.ArrayList();
	for (int i=0; i<widths.length; i++) {
		if (widths[i] < ((MetaProperty) metaProperties.get(i)).getQualifiedLabel(locale).length()) {
			candidates.add(new Integer(i));
		}
		totalWidth += widths[i];
	}
	if (totalWidth < max && !candidates.isEmpty()) {
		int extra = (max - totalWidth) / candidates.size();
		for (Iterator it = candidates.iterator(); it.hasNext(); ) {
			int i = ((Integer) it.next()).intValue();
			widths[i] = widths[i] + extra;
		}
	}	
}

private int [] tightenWidths(List metaProperties, int [] widths) { 
	int [] originalWidths = widths.clone(); 
	int littleOnesTotal = 0;
	int littleOnesCount = 0;
	for (int i=0; i<widths.length; i++) {
		if (widths[i] <= 20) {
			littleOnesTotal+=widths[i];
			littleOnesCount++;
		}
	}	
	int spaceForBigOnes = MAX_CHARACTERS_PER_ROW - littleOnesTotal;
	int bigOnesCount = widths.length - littleOnesCount; 
	int widthForBig = bigOnesCount==0?20:spaceForBigOnes / bigOnesCount; 
	if (widthForBig < 20) widthForBig = 20;
	int total = 0; 
	for (int i=0; i<widths.length; i++) {
		if (widths[i] > 20 && widths[i] > widthForBig) widths[i] = widthForBig;
		total += widths[i];
	}		
	if (total > MAX_CHARACTERS_PER_ROW) {
		metaProperties.remove(metaProperties.size() - 1);
		widths = org.apache.commons.lang.ArrayUtils.remove(originalWidths, originalWidths.length - 1);
		return tightenWidths(metaProperties, widths);
	}
	return widths;
}

private String getAlign(MetaProperty p) throws Exception {
	String align = "Left";
	if (p.isNumber() && !p.hasValidValues()) align = "Right";
	else if (p.getType().equals(boolean.class)) align = "Center";
	return align;
}

private List getMetaProperties(Tab tab, Integer columnCountLimit) { 
	if (columnCountLimit == null) return new java.util.ArrayList(tab.getMetaProperties()); 
	List result = new java.util.ArrayList(); 
	int c = 0;
	for (MetaProperty p: tab.getMetaProperties()) {
		if (++c > columnCountLimit) break; 
		result.add(p);
	}
	return result;
}
%>

<%
Tab tab = (Tab) request.getSession().getAttribute("xava_reportTab");
String reportName = Strings.change(tab.getModelName(), ".", "_"); 
Collection totalProperties = tab.getTotalPropertiesNames();  		
String language = request.getParameter("language");
if (language == null) language = org.openxava.util.Locales.getCurrent().getDisplayLanguage();
language = language == null?request.getLocale().getDisplayLanguage():language;
Locale locale = new Locale(language, "");
String scolumnCountLimit = request.getParameter("columnCountLimit");
Integer columnCountLimit = scolumnCountLimit == null?null:Integer.parseInt(scolumnCountLimit);
List metaProperties = getMetaProperties(tab, columnCountLimit);
int columnsSeparation = 10;
int [] widths = parseWidths(request.getParameter("widths"), columnCountLimit); 
int totalWidth = adjustWithsToLabels(metaProperties, widths, locale); 
int letterWidth;
int letterSize;
int detailHeight;
int pageWidth;
int pageHeight;
int columnWidth;
String orientation = null;

if (totalWidth > WIDE_CHARACTERS_PER_ROW) {
	if (totalWidth > MAX_CHARACTERS_PER_ROW) tightenWidths(metaProperties, widths);
	else expandWidths(metaProperties, widths, MAX_CHARACTERS_PER_ROW, locale); 
	orientation="Landscape";
	letterWidth = 4;
	letterSize = 7;
	detailHeight = 8; 
	pageWidth=842;
	pageHeight=595;
	columnWidth=780;	
}
else if (totalWidth > MEDIUM_CHARACTERS_PER_ROW) {
	expandWidths(metaProperties, widths, WIDE_CHARACTERS_PER_ROW, locale); 
	orientation="Landscape";
	letterWidth = 5;  
	letterSize=8;
	detailHeight = 10;
	pageWidth=842;
	pageHeight=595;
	columnWidth=780;	
} 
else if (totalWidth > NARROW_CHARACTERS_PER_ROW) {
	expandWidths(metaProperties, widths, MEDIUM_CHARACTERS_PER_ROW, locale); 
	orientation="Portrait";
	letterWidth = 5; 
	letterSize=8;
	detailHeight = 10;
	pageWidth=595;
	pageHeight=842;
	columnWidth=535;
}
else {
	expandWidths(metaProperties, widths, NARROW_CHARACTERS_PER_ROW, locale);
	orientation="Portrait";
	letterWidth = 10;
	letterSize = 12;
	detailHeight = 15;
	pageWidth=595;
	pageHeight=842;
	columnWidth=535;
}

int rowsInHeader = calculateRowsInHeader(metaProperties, widths, locale);
%>

<jasperReport
		 name="<%=reportName%>"
		 columnCount="1"
		 printOrder="Vertical"
		 orientation="<%=orientation%>"
		 pageWidth="<%=pageWidth%>"
		 pageHeight="<%=pageHeight%>"
		 columnWidth="<%=columnWidth%>"
		 columnSpacing="0"
		 leftMargin="30"
		 rightMargin="30"
		 topMargin="20"
		 bottomMargin="20"
		 whenNoDataType="NoPages"
		 isTitleNewPage="false"
		 isSummaryNewPage="false">		 
	<%
	String fontPath=request.getSession().getServletContext().getRealPath("/WEB-INF/fonts/").concat(System.getProperty("file.separator"));
	String fontName="DejaVu Sans";
	String fontNameExt="DejaVuSans.ttf";
	String boldFontNameExt="DejaVuSans-Bold.ttf";	
	String pdfEncoding="Identity-H";
	%>	
	<reportFont name="Arial_Normal" isDefault="true" fontName="<%=fontName%>" size="8" pdfFontName="<%=fontPath.concat(fontNameExt)%>" pdfEncoding="<%=pdfEncoding%>" isPdfEmbedded="true"/>
	<reportFont name="Arial_Bold" isDefault="false" fontName="<%=fontName%>" size="8" isBold="true" pdfFontName="<%=fontPath.concat(boldFontNameExt)%>" pdfEncoding="<%=pdfEncoding%>" isPdfEmbedded="true"/>
	<reportFont name="Arial_Italic" isDefault="false" fontName="<%=fontName%>" size="8" isItalic="true" pdfFontName="<%=fontPath.concat(fontNameExt)%>" pdfEncoding="<%=pdfEncoding%>" isPdfEmbedded="true"/>

	<parameter name="Title" class="java.lang.String"/>	
	<parameter name="Organization" class="java.lang.String"/>
	<parameter name="Date" class="java.lang.String"/>
	<%	 
	for (Iterator it = metaProperties.iterator(); it.hasNext();) {
		MetaProperty p = (MetaProperty) it.next();				
		if (totalProperties.contains(p.getQualifiedName())) {				 
	%>
	<parameter name="<%=p.getQualifiedName()%>__TOTAL__" class="java.lang.String"/> 	
	<%
		}
	}
	%>	
		
	<%		 
	for (Iterator it = metaProperties.iterator(); it.hasNext();) {
		MetaProperty p = (MetaProperty) it.next();
	%>
	<field name="<%=Strings.change(p.getQualifiedName(), ".", "_")%>" class="java.lang.String"/> 	
	<%
	}
	%>	
		<background>
			<band height="0"  isSplitAllowed="true" >
			</band>
		</background>
		<title>
			<band height="25"  isSplitAllowed="true" >

				<textField>
					<reportElement
						mode="Transparent"
						x="0"
						y="0"
						width="200"
						height="25"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Left" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="8"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$P{Organization}</textFieldExpression>					
				</textField>
						
				<textField>
					<reportElement
						mode="Transparent"
						x="5"
						y="5"
						width="<%=columnWidth%>"
						height="20"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Center" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="15"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$P{Title}</textFieldExpression>					
				</textField>
				

				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="23"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="1Point" fill="Solid" />
				</line>

				<!-- Top line
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="3"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="1Point" fill="Solid" />
				</line>
				-->
			</band>
		</title>
		<pageHeader>
			<band height="9"  isSplitAllowed="true" >
			</band>
		</pageHeader>
		<% 
		int headerHeight = rowsInHeader * detailHeight + 8; 
		%>
		<columnHeader>
			<band height="<%=headerHeight%>" isSplitAllowed="true" >
				<rectangle radius="0" >
					<reportElement
						mode="Opaque"
						x="0"
						y="0"
						width="<%=columnWidth%>"
						height="<%=headerHeight - 5%>"
						forecolor="#000000"
						backcolor="#808080"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="None" fill="Solid" />
				</rectangle>
				<line direction="BottomUp">
					<reportElement
						mode="Opaque"
						x="0"
						y="0"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
				<line direction="BottomUp">
					<reportElement
						mode="Opaque"
						x="0"
						y="<%=headerHeight - 6%>"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
<% 
int x = 0;
int i = 0;
for (Iterator it = metaProperties.iterator(); it.hasNext(); i++) {			
	MetaProperty p = (MetaProperty) it.next();
	int width=widths[i]*letterWidth + EXTRA_WIDTH; 		
%>
				<staticText>
					<reportElement
						mode="Transparent"
						x="<%=x%>"
						y="2"
						width="<%=width%>"
						height="<%=headerHeight-2%>"
						forecolor="#FFFFFF"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="true"
						isPrintWhenDetailOverflows="true"/>
					<textElement textAlignment="<%=getAlign(p)%>" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="<%=letterSize%>"/>
					</textElement>
					<% String label = "<![CDATA[" + p.getQualifiedLabel(locale) + "]]>"; %>
					<text><%=label%></text>					
				</staticText>
<%
	x+=(width+columnsSeparation);
}
%>				
			</band>
		</columnHeader>
		
		<detail>
			<band height="<%=detailHeight + 2%>"  isSplitAllowed="true" >
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="0" 
						width="<%=columnWidth%>"
						height="0"
						forecolor="#808080"
						backcolor="#FFFFFF"
						positionType="Float"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="true"/>					
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
<% 
x = 0;
i = 0;
for (Iterator it = metaProperties.iterator(); it.hasNext(); i++) {			
	MetaProperty p = (MetaProperty) it.next();	
	int width=widths[i]*letterWidth + EXTRA_WIDTH; 
%>								
				<textField isStretchWithOverflow="true" pattern="" isBlankWhenNull="true" evaluationTime="Now" hyperlinkType="None" >
					<reportElement
						mode="Transparent"
						x="<%=x%>"
						y="2"
						width="<%=width%>"
						height="<%=detailHeight%>"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="<%=getAlign(p)%>" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="<%=letterSize%>"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$F{<%=Strings.change(p.getQualifiedName(), ".", "_")%>}</textFieldExpression>
				</textField>
<%
	x+=(width+columnsSeparation);
}
%>				
			</band>
		</detail>
		<pageFooter>
			<band height="27"  isSplitAllowed="true" >
				<textField isStretchWithOverflow="false" pattern="" isBlankWhenNull="false" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=columnWidth - 210%>"
						y="4"
						width="174"
						height="19"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Right" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10"/>
					</textElement>
				<%
				String iniPageLabel = "<![CDATA[\"" + XavaResources.getString(request, "page") + " \"";
				String endPageLabel = " \" " + XavaResources.getString("of") + " \"]]>";
				%>
				<textFieldExpression class="java.lang.String"><%=iniPageLabel%> + $V{PAGE_NUMBER} + <%=endPageLabel%></textFieldExpression>
				</textField>
				<textField isStretchWithOverflow="false" pattern="" isBlankWhenNull="false" evaluationTime="Report" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=columnWidth - 36%>"
						y="4"
						width="36"
						height="19"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Left" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10" />
					</textElement>
				<textFieldExpression   class="java.lang.String"><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
				</textField>
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="1"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="2Point" fill="Solid" />
				</line>
				<textField isStretchWithOverflow="false" pattern="" isBlankWhenNull="false" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="1"
						y="6"
						width="209"
						height="19"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Left" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10"/>
					</textElement>
				<textFieldExpression   class="java.lang.String">
					<![CDATA[$P{Date}]]>
				</textFieldExpression>
				</textField>
			</band>
		</pageFooter>
		<summary>
			<band height="19" isSplitAllowed="true" >
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="0" 
						width="<%=columnWidth%>"
						height="0"
						forecolor="#808080"
						backcolor="#FFFFFF"
						positionType="Float"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="true"/>					
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
<% 
x = 0;
i = 0;
for (Iterator it = metaProperties.iterator(); it.hasNext(); i++) {			
	MetaProperty p = (MetaProperty) it.next();	
	int width=widths[i]*letterWidth + EXTRA_WIDTH;
	if (totalProperties.contains(p.getQualifiedName())) { 
%>								
				<textField isStretchWithOverflow="true" pattern="" isBlankWhenNull="true" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=x%>"
						y="2"
						width="<%=width%>"
						height="<%=detailHeight%>"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="<%=getAlign(p)%>" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Bold" size="<%=letterSize%>"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$P{<%=p.getQualifiedName()%>__TOTAL__}</textFieldExpression>
				</textField>
<%
	} 			
	x+=(width+columnsSeparation);
}
%>					
			</band>
		</summary>
</jasperReport>
