package org.openxava.tab.meta.xmlparse;




import org.openxava.filters.meta.*;
import org.openxava.filters.meta.xmlparse.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * 
 * @author: Javier Paniza
 */
public class TabParser extends XmlElementsNames {

	
	
	/**
	* @throws XavaException
	 */
	public static MetaTab parseTab(Node n, int lang) {
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
		return e;
	}
		
	/**
	* @throws XavaException
	 */
	private static MetaFilter createFilter(Element el, int lang) {
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
			
	/**
	* @throws XavaException
	 */
	private static void fillRowStyles(Element el, MetaTab container, int lang)
		{
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
	
	/**
	* @throws XavaException
	 */
	public static MetaRowStyle createRowStyle(Node n, int lang) {
		Element el = (Element) n;
		MetaRowStyle style = new MetaRowStyle();
		style.setStyle(el.getAttribute(xstyle[lang]));
		style.setProperty(el.getAttribute(xproperty[lang]));
		style.setValue(el.getAttribute(xvalue[lang]));
		return style;
	}	
			
}