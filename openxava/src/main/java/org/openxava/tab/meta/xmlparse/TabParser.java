package org.openxava.tab.meta.xmlparse;




import org.openxava.filters.meta.*;
import org.openxava.filters.meta.xmlparse.*;
import org.openxava.model.meta.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * 
 * @author: Javier Paniza
 */
public class TabParser extends XmlElementsNames {

	
	
	public static MetaTab parseTab(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaTab e = new MetaTab();
		e.setName(el.getAttribute(xname[lang]));
		String excludeByKey = el.getAttribute(xexclude_by_key[lang]);
		if (!Is.emptyString(excludeByKey)) {
			e.setExcludeByKey(Boolean.valueOf(excludeByKey).booleanValue());
		}		
		String excludeAll = el.getAttribute(xexclude_all[lang]);
		if (!Is.emptyString(excludeAll)) {
			e.setExcludeAll(Boolean.valueOf(excludeAll).booleanValue());
		}
		e.setDefaultPropertiesNames(ParserUtil.getString(el, xproperties[lang]));		
		e.setMetaFilter(createFilter(el, lang));
		fillRowStyles(el, e, lang);
		e.setBaseCondition(ParserUtil.getString(el, xbase_condition[lang]));		
		e.setDefaultOrder(ParserUtil.getString(el, xdefault_order[lang]));
		e.setEditor(el.getAttribute(xeditor[lang]));
		e.setEditors(el.getAttribute(xeditors[lang]));
		fillProperties(el, e, lang);
		return e;
	}
		
	private static MetaFilter createFilter(Element el, int lang) throws XavaException {
		NodeList l = el.getChildNodes();				
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Node n = l.item(i);			
			if (xfilter[lang].equals(n.getNodeName())) {
				return FilterParser.parseFilter(l.item(i), lang);
			} 
		}
		return null;		
	}
		
	private static void fillProperties(Element el, MetaTab container, int lang) // tmr Posiblemente no haga falta
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xproperty[lang])) {
				container.addMetaProperty(createProperty(d, lang));
			}
		}
	}
	
	private static MetaProperty createProperty(Node n, int lang) throws XavaException { // tmr Posiblemente no haga falta
		Element el = (Element) n;
		MetaProperty p = new MetaProperty();
		p.setName(el.getAttribute(xname[lang]));
		p.setLabel(el.getAttribute(xlabel[lang]));
		p.setStereotype(el.getAttribute(xstereotype[lang]));
		p.setTypeName(el.getAttribute(xtype[lang]));
		p.setSize(ParserUtil.getAttributeInt(el, xsize[lang]));
		if (!Is.emptyString(el.getAttribute(xscale[lang]))) { 
			p.setScale(ParserUtil.getAttributeInt(el, xscale[lang]));
		} 
		p.setHidden(ParserUtil.getAttributeBoolean(el, xhidden[lang]));
		p.setVersion(ParserUtil.getAttributeBoolean(el, xversion[lang]));
		p.setSearchKey(ParserUtil.getAttributeBoolean(el, xsearch_key[lang]));
		boolean key = ParserUtil.getAttributeBoolean(el, xkey[lang]);
		if (key) p.setKey(key);
		Element elValidValues = ParserUtil.getElement(el, xvalid_values[lang]);
		if (elValidValues != null) {
			NodeList l = elValidValues.getElementsByTagName(xvalid_value[lang]);
			int c = l.getLength();
			for (int i = 0; i < c; i++) {
				Element validValue = (Element) l.item(i);
				p.addValidValue(validValue.getAttribute(xvalue[lang]));
			}
		}
		if (Is.emptyString(el.getAttribute(xrequired[lang]))) {
			// Calculating a valid default value for required
			p.setRequired(p.isKey() && (!(p.hasCalculatorDefaultValueOnCreate() || p.isHidden())));
		}
		else {
			p.setRequired(ParserUtil.getAttributeBoolean(el, xrequired[lang]));
		}
		p.setCalculation(ParserUtil.getString(el, xcalculation[lang]));
		return p;
	}
	
	private static void fillRowStyles(Element el, MetaTab container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xrow_style[lang])) {
				container.addMetaRowStyle(createRowStyle(d, lang));
			}
		}
	}
	
	public static MetaRowStyle createRowStyle(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaRowStyle style = new MetaRowStyle();
		style.setStyle(el.getAttribute(xstyle[lang]));
		style.setProperty(el.getAttribute(xproperty[lang]));
		style.setValue(el.getAttribute(xvalue[lang]));
		return style;
	}	
			
}