<?xml version="1.0" encoding="UTF-8"?>
<!--
	Converts a JRXML report template written for JasperReports 6 (or older) into
	the format required by JasperReports 7.

	JasperReports 7 replaced the Apache Commons Digester based parser with Jackson
	XML, changing the JRXML grammar: elements are unified into <element kind="..."/>,
	the <reportElement>, <textElement>, <font> and <graphicElement> wrappers
	disappear (their attributes are moved to the element itself) and several
	attribute and element names change.

	The mapping implemented here comes from the @JsonPropertyOrder and @JsonTypeName
	annotations of the JasperReports 7 interfaces (net.sf.jasperreports.engine.JRReport,
	JRStaticText, JRTextField, JRLine, JRRectangle, JREllipse, JRImage, JRSubreport,
	JRFrame, JRBreak, JRElementGroup, JRStyle, JRParameter, JRField, JRVariable,
	JRGroup, JRBand, JRLineBox, JRPen and JRParagraph).

	Constructs that are not supported (charts, crosstabs, component elements such as
	tables, lists and barcodes, and pre-3.0 attributes) stop the transformation with
	an error message, so that the report can be converted with Jaspersoft Studio 7.

	@since 8.0
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jr="http://jasperreports.sourceforge.net/jasperreports"
	exclude-result-prefixes="jr">

<xsl:output method="xml" indent="yes" encoding="UTF-8"
	cdata-section-elements="text expression initialValueExpression printWhenExpression
		patternExpression anchorNameExpression bookmarkLevelExpression
		hyperlinkReferenceExpression hyperlinkWhenExpression hyperlinkAnchorExpression
		hyperlinkPageExpression hyperlinkTooltipExpression defaultValueExpression
		filterExpression conditionExpression connectionExpression dataSourceExpression
		parametersMapExpression propertyExpression query template"/>

<xsl:strip-space elements="*"/>

<!-- ===================================================================== -->
<!-- Report                                                                -->
<!-- ===================================================================== -->

<xsl:template match="/jr:jasperReport | /jasperReport">
	<jasperReport>
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:apply-templates/>
	</jasperReport>
</xsl:template>

<xsl:template match="jr:import | import">
	<import><xsl:value-of select="@value"/></import>
</xsl:template>

<xsl:template match="jr:queryString | queryString">
	<query>
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:value-of select="."/>
	</query>
</xsl:template>

<xsl:template match="jr:subDataset | subDataset">
	<dataset>
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:apply-templates/>
	</dataset>
</xsl:template>

<!-- ===================================================================== -->
<!-- Bands and sections                                                    -->
<!-- ===================================================================== -->

<!--
	In JasperReports 7 these sections are bands themselves, so the <band> wrapper
	of JasperReports 6 has to be removed and its attributes moved to the section.
-->
<xsl:template match="jr:background | background | jr:title | title
		| jr:pageHeader | pageHeader | jr:columnHeader | columnHeader
		| jr:columnFooter | columnFooter | jr:pageFooter | pageFooter
		| jr:lastPageFooter | lastPageFooter | jr:summary | summary
		| jr:noData | noData">
	<xsl:element name="{local-name()}">
		<xsl:apply-templates select="*[local-name()='band']/@*" mode="attribute"/>
		<xsl:apply-templates select="*[local-name()='band']/*"/>
	</xsl:element>
</xsl:template>

<!-- detail, groupHeader and groupFooter are sections, they keep their bands -->
<xsl:template match="jr:detail | detail | jr:groupHeader | groupHeader
		| jr:groupFooter | groupFooter | jr:band | band">
	<xsl:element name="{local-name()}">
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:apply-templates/>
	</xsl:element>
</xsl:template>

<!-- ===================================================================== -->
<!-- Elements                                                              -->
<!-- ===================================================================== -->

<xsl:template match="jr:staticText | staticText">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'staticText'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:textField | textField">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'textField'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:line | line">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'line'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:rectangle | rectangle">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'rectangle'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:ellipse | ellipse">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'ellipse'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:image | image">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'image'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:subreport | subreport">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'subreport'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:frame | frame">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'frame'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:break | break">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'break'"/></xsl:call-template>
</xsl:template>

<xsl:template match="jr:elementGroup | elementGroup">
	<xsl:call-template name="element"><xsl:with-param name="kind" select="'elementGroup'"/></xsl:call-template>
</xsl:template>

<xsl:template name="element">
	<xsl:param name="kind"/>
	<element kind="{$kind}">
		<!-- attributes of the element itself and of the removed wrappers -->
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:apply-templates select="*[local-name()='reportElement']/@*" mode="attribute"/>
		<xsl:apply-templates select="*[local-name()='textElement']/@*" mode="attribute"/>
		<xsl:apply-templates select="*[local-name()='textElement']/*[local-name()='font']/@*" mode="attribute"/>
		<xsl:apply-templates select="*[local-name()='graphicElement']/@*" mode="attribute"/>
		<!-- children of the removed wrappers -->
		<xsl:apply-templates select="*[local-name()='reportElement']/*"/>
		<xsl:apply-templates select="*[local-name()='graphicElement']/*"/>
		<xsl:call-template name="graphicElementPen"/>
		<xsl:call-template name="paragraph"/>
		<!-- children of the element itself -->
		<xsl:apply-templates select="*[local-name()!='reportElement'
			and local-name()!='textElement' and local-name()!='graphicElement']"/>
	</element>
</xsl:template>

<!--
	The pen attribute of <graphicElement> was a shorthand in JasperReports 1/2.
	In JasperReports 7 it is a <pen> child element with lineWidth and lineStyle
	attributes. We generate it only if no <pen> child element already exists.
-->
<xsl:template name="graphicElementPen">
	<xsl:variable name="ge" select="*[local-name()='graphicElement']"/>
	<xsl:if test="$ge/@pen and not($ge/*[local-name()='pen'])">
		<pen>
			<xsl:attribute name="lineWidth">
				<xsl:choose>
					<xsl:when test="$ge/@pen='None'">0.0</xsl:when>
					<xsl:when test="$ge/@pen='Thin'">0.5</xsl:when>
					<xsl:when test="$ge/@pen='1Point'">1.0</xsl:when>
					<xsl:when test="$ge/@pen='2Point'">2.0</xsl:when>
					<xsl:when test="$ge/@pen='4Point'">4.0</xsl:when>
					<xsl:when test="$ge/@pen='Dotted'">1.0</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="lineStyle">
				<xsl:choose>
					<xsl:when test="$ge/@pen='Dotted'">Dashed</xsl:when>
					<xsl:otherwise>Solid</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</pen>
	</xsl:if>
</xsl:template>

<!--
	The lineSpacing attribute of <textElement> was moved to <paragraph> in
	JasperReports 6, and no longer exists in JasperReports 7.
-->
<xsl:template name="paragraph">
	<xsl:variable name="textElement" select="*[local-name()='textElement']"/>
	<xsl:variable name="paragraph" select="$textElement/*[local-name()='paragraph']"/>
	<xsl:if test="$paragraph or $textElement/@lineSpacing">
		<paragraph>
			<xsl:if test="$textElement/@lineSpacing and not($paragraph/@lineSpacing)">
				<xsl:attribute name="lineSpacing"><xsl:value-of select="$textElement/@lineSpacing"/></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="$paragraph/@*" mode="attribute"/>
			<xsl:apply-templates select="$paragraph/*"/>
		</paragraph>
	</xsl:if>
</xsl:template>

<!-- ===================================================================== -->
<!-- Expressions                                                           -->
<!-- ===================================================================== -->

<!--
	The class attribute of the expressions was removed in JasperReports 7, and
	several expressions are now simply named <expression>.
-->
<xsl:template match="jr:textFieldExpression | textFieldExpression
		| jr:imageExpression | imageExpression
		| jr:subreportExpression | subreportExpression
		| jr:variableExpression | variableExpression
		| jr:groupExpression | groupExpression
		| jr:subreportParameterExpression | subreportParameterExpression
		| jr:datasetParameterExpression | datasetParameterExpression
		| jr:hyperlinkParameterExpression | hyperlinkParameterExpression">
	<expression><xsl:value-of select="."/></expression>
</xsl:template>

<xsl:template match="jr:printWhenExpression | printWhenExpression
		| jr:initialValueExpression | initialValueExpression
		| jr:defaultValueExpression | defaultValueExpression
		| jr:patternExpression | patternExpression
		| jr:filterExpression | filterExpression
		| jr:conditionExpression | conditionExpression
		| jr:connectionExpression | connectionExpression
		| jr:dataSourceExpression | dataSourceExpression
		| jr:parametersMapExpression | parametersMapExpression
		| jr:anchorNameExpression | anchorNameExpression
		| jr:bookmarkLevelExpression | bookmarkLevelExpression
		| jr:hyperlinkReferenceExpression | hyperlinkReferenceExpression
		| jr:hyperlinkWhenExpression | hyperlinkWhenExpression
		| jr:hyperlinkAnchorExpression | hyperlinkAnchorExpression
		| jr:hyperlinkPageExpression | hyperlinkPageExpression
		| jr:hyperlinkTooltipExpression | hyperlinkTooltipExpression">
	<xsl:element name="{local-name()}"><xsl:value-of select="."/></xsl:element>
</xsl:template>

<xsl:template match="jr:parameterDescription | parameterDescription
		| jr:fieldDescription | fieldDescription">
	<description><xsl:value-of select="."/></description>
</xsl:template>

<!-- ===================================================================== -->
<!-- Styles                                                                -->
<!-- ===================================================================== -->

<!-- In JasperReports 7 the conditional style holds the style properties itself -->
<xsl:template match="jr:conditionalStyle | conditionalStyle">
	<conditionalStyle>
		<xsl:apply-templates select="*[local-name()='style']/@*" mode="attribute"/>
		<xsl:apply-templates select="*[local-name()='conditionExpression']"/>
		<xsl:apply-templates select="*[local-name()='style']/*"/>
	</conditionalStyle>
</xsl:template>

<!-- ===================================================================== -->
<!-- Subreports and datasets                                               -->
<!-- ===================================================================== -->

<xsl:template match="jr:subreportParameter | subreportParameter
		| jr:datasetParameter | datasetParameter">
	<parameter>
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:apply-templates/>
	</parameter>
</xsl:template>

<!-- ===================================================================== -->
<!-- Elements copied with just attribute and children conversion           -->
<!-- ===================================================================== -->

<xsl:template match="jr:property | property | jr:propertyExpression | propertyExpression
		| jr:template | template | jr:scriptlet | scriptlet
		| jr:style | style | jr:parameter | parameter | jr:field | field
		| jr:sortField | sortField | jr:variable | variable | jr:group | group
		| jr:box | box | jr:pen | pen | jr:topPen | topPen | jr:leftPen | leftPen
		| jr:bottomPen | bottomPen | jr:rightPen | rightPen
		| jr:paragraph | paragraph | jr:tabStop | tabStop
		| jr:text | text | jr:returnValue | returnValue
		| jr:hyperlinkParameter | hyperlinkParameter
		| jr:datasetRun | datasetRun">
	<xsl:element name="{local-name()}">
		<xsl:apply-templates select="@*" mode="attribute"/>
		<xsl:choose>
			<xsl:when test="*"><xsl:apply-templates/></xsl:when>
			<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
		</xsl:choose>
	</xsl:element>
</xsl:template>

<!-- ===================================================================== -->
<!-- Attributes                                                            -->
<!-- ===================================================================== -->

<xsl:template match="@*" mode="attribute">
	<xsl:variable name="name" select="local-name()"/>
	<xsl:choose>
		<!-- attributes of the report that keep the 'is' prefix in JasperReports 7 -->
		<xsl:when test="$name='isTitleNewPage' or $name='isSummaryNewPage'
				or $name='isSummaryWithPageHeaderAndFooter' or $name='isFloatColumnFooter'
				or $name='isIgnorePagination'">
			<xsl:copy/>
		</xsl:when>
		<!-- attributes losing the 'is' prefix in JasperReports 7 -->
		<xsl:when test="$name='isPrintRepeatedValues' or $name='isPrintInFirstWholeBand'
				or $name='isPrintWhenDetailOverflows' or $name='isRemoveLineWhenBlank'
				or $name='isBlankWhenNull' or $name='isBold' or $name='isItalic'
				or $name='isUnderline' or $name='isStrikeThrough' or $name='isPdfEmbedded'
				or $name='isUsingCache' or $name='isLazy' or $name='isDefault'
				or $name='isForPrompting' or $name='isRunToBottom'">
			<xsl:attribute name="{concat(
				translate(substring($name, 3, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),
				substring($name, 4))}"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<!-- renamed attributes -->
		<xsl:when test="$name='size'">
			<xsl:attribute name="fontSize"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<xsl:when test="$name='textAlignment'">
			<xsl:attribute name="hTextAlign"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<xsl:when test="$name='verticalAlignment'">
			<xsl:attribute name="vTextAlign"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<!--
			hAlign and vAlign applied both to texts and images, so in a style they are
			converted to the text and the image alignment, while in an image they are
			converted only to the image alignment.
		-->
		<xsl:when test="$name='hAlign'">
			<xsl:if test="local-name(..)='style' or local-name(..)='conditionalStyle'">
				<xsl:attribute name="hTextAlign"><xsl:value-of select="."/></xsl:attribute>
			</xsl:if>
			<xsl:if test=".!='Justified'">
				<xsl:attribute name="hImageAlign"><xsl:value-of select="."/></xsl:attribute>
			</xsl:if>
		</xsl:when>
		<xsl:when test="$name='vAlign'">
			<xsl:if test="local-name(..)='style' or local-name(..)='conditionalStyle'">
				<xsl:attribute name="vTextAlign"><xsl:value-of select="."/></xsl:attribute>
			</xsl:if>
			<xsl:if test=".!='Justified'">
				<xsl:attribute name="vImageAlign"><xsl:value-of select="."/></xsl:attribute>
			</xsl:if>
		</xsl:when>
		<xsl:when test="$name='hyperlinkType'">
			<xsl:attribute name="linkType"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<xsl:when test="$name='hyperlinkTarget'">
			<xsl:attribute name="linkTarget"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<xsl:when test="$name='subreportVariable'">
			<xsl:attribute name="fromVariable"><xsl:value-of select="."/></xsl:attribute>
		</xsl:when>
		<!-- attributes replaced by another one in JasperReports 7 -->
		<xsl:when test="$name='isStretchWithOverflow'">
			<xsl:attribute name="textAdjust">
				<xsl:choose>
					<xsl:when test=".='true'">StretchHeight</xsl:when>
					<xsl:otherwise>CutText</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:when>
		<xsl:when test="$name='isStyledText'">
			<xsl:attribute name="markup">
				<xsl:choose>
					<xsl:when test=".='true'">styled</xsl:when>
					<xsl:otherwise>none</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:when>
		<!-- values renamed in JasperReports 7 -->
		<xsl:when test="$name='stretchType'">
			<xsl:attribute name="stretchType">
				<xsl:choose>
					<xsl:when test=".='RelativeToTallestObject'">ElementGroupHeight</xsl:when>
					<xsl:when test=".='RelativeToBandHeight'">ContainerHeight</xsl:when>
					<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:when>
		<!-- attributes removed in JasperReports 7 -->
		<xsl:when test="$name='class' and (local-name(..)='textFieldExpression'
				or local-name(..)='imageExpression' or local-name(..)='subreportExpression'
				or local-name(..)='variableExpression' or local-name(..)='groupExpression')"/>
		<xsl:when test="$name='schemaLocation' or ($name='lineSpacing' and local-name(..)='textElement')"/>
		<!-- attributes of JasperReports 2 and older, removed in 7 and ignored -->
		<xsl:when test="$name='topBorder' or $name='leftBorder' or $name='bottomBorder'
				or $name='rightBorder' or $name='topBorderColor' or $name='leftBorderColor'
				or $name='bottomBorderColor' or $name='rightBorderColor'
				or $name='isStretchOverflow'"/>
		<!-- pen attribute of graphicElement is converted to a <pen> child by the element template -->
		<xsl:when test="$name='pen' and local-name(..)='graphicElement'"/>
		<!-- pen attribute on box and border/borderColor of JasperReports 1/2, ignored -->
		<xsl:when test="$name='pen' or $name='border' or $name='borderColor'"/>
		<xsl:otherwise>
			<xsl:copy/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- ===================================================================== -->
<!-- Anything else is not supported                                        -->
<!-- ===================================================================== -->

<!-- the text content is always written by the template of its element -->
<xsl:template match="text()"/>

<xsl:template match="*">
	<xsl:message terminate="yes">
		<xsl:value-of select="concat('jrxml_migration_unsupported_element:', local-name())"/>
	</xsl:message>
</xsl:template>

</xsl:stylesheet>
