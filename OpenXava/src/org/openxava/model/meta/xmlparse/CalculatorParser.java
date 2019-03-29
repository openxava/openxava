package org.openxava.model.meta.xmlparse;



import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * 
 * @author: Javier Paniza
 */
public class CalculatorParser extends XmlElementsNames {

	
	
	public static MetaCalculator parseCalculator(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaCalculator e = new MetaCalculator();
		e.setClassName(el.getAttribute(xclass[lang]));
		e.setOnCreate(ParserUtil.getAttributeBoolean(el, xon_create[lang]));
		fillSets(el, e, lang);
		return e;
	}
	
	private static void fillSets(Element el, MetaSetsContainer container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xset[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaSet(createSet(l.item(i), lang));
		}
	}
		
	private static MetaSet createSet(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaSet a = new MetaSet();		
		a.setPropertyName(el.getAttribute(xproperty[lang]));		
		a.setPropertyNameFrom(el.getAttribute(xfrom[lang]));		
		a.setValue(el.getAttribute(xvalue[lang]));		
		return a;
	}
				
}