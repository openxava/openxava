package org.openxava.tab.meta.xmlparse;

import org.openxava.tab.meta.*;
import org.openxava.util.XavaException;
import org.openxava.util.xmlparse.ParserBase;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author: Javier Paniza
 */
public class TabsDefaultValuesParser extends ParserBase {

	public TabsDefaultValuesParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);
	}
	
	public static void setupTabs() throws XavaException {
		TabsDefaultValuesParser enParser = new TabsDefaultValuesParser("tabs-default-values.xml", ENGLISH);
		enParser.parse();
		
		TabsDefaultValuesParser esParser = new TabsDefaultValuesParser("valores-defecto-tabs.xml", ESPANOL);
		esParser.parse();
				
	}
	
	private void addTabs(Element el) throws XavaException {
		MetaTab tab = TabParser.parseTab(el, lang);		
		
		addTabsForModel(tab, el);		
		addTabsExceptForModel(tab, el);
		addDefaultTab(tab, el);
	}	
	

	
	private void addTabsForModel(MetaTab tab, Element n) throws XavaException {		
		NodeList l = n.getElementsByTagName(xfor_model[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);		
			MetaTabsDefaultValues._putMetaTabForModel(el.getAttribute(xmodel[lang]), tab);
		}		
	}
	
	private void addTabsExceptForModel(MetaTab tab, Element n) throws XavaException {		
		NodeList l = n.getElementsByTagName(xexcept_for_model[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);		
			MetaTabsDefaultValues._putMetaTabExceptForModel(el.getAttribute(xmodel[lang]), tab);
		}		
	}
	
	private void addDefaultTab(MetaTab tab, Element n) throws XavaException {		
		NodeList l = n.getElementsByTagName(xfor_all_models[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);		
			MetaTabsDefaultValues._addDefaultMetaTab(tab);
		}		
	}
					
	private void createTabs() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xtab[lang]);
		int c = l.getLength();		
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);						
			addTabs(el);			
		}						
	}
			
	protected void createObjects() throws XavaException {
		createTabs();				
	}
		
}