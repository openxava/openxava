package org.openxava.tab.meta.xmlparse;

import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class TabsDefaultValuesParser extends ParserBase {

	public TabsDefaultValuesParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);
	}
	
	/**
	* @throws XavaException
	 */
	public static void setupTabs() {
		TabsDefaultValuesParser enParser = new TabsDefaultValuesParser("tabs-default-values.xml", ENGLISH);
		enParser.parse();
		
		TabsDefaultValuesParser esParser = new TabsDefaultValuesParser("valores-defecto-tabs.xml", ESPANOL);
		esParser.parse();
	}
	
	/**
	* @throws XavaException
	 */
	private void addTabs(Element el) {
		MetaTab tab = TabParser.parseTab(el, lang);		
		
		addTabsForModel(tab, el);		
		addTabsExceptForModel(tab, el);
		addDefaultTab(tab, el);
	}	
	

	
	/**
	* @throws XavaException
	 */
	private void addTabsForModel(MetaTab tab, Element n) {		
		NodeList l = n.getElementsByTagName(xfor_model[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);		
			MetaTabsDefaultValues._putMetaTabForModel(el.getAttribute(xmodel[lang]), tab);
		}		
	}
	
	/**
	* @throws XavaException
	 */
	private void addTabsExceptForModel(MetaTab tab, Element n) {		
		NodeList l = n.getElementsByTagName(xexcept_for_model[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);		
			MetaTabsDefaultValues._putMetaTabExceptForModel(el.getAttribute(xmodel[lang]), tab);
		}		
	}
	
	/**
	* @throws XavaException
	 */
	private void addDefaultTab(MetaTab tab, Element n) {		
		NodeList l = n.getElementsByTagName(xfor_all_models[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);		
			MetaTabsDefaultValues._addDefaultMetaTab(tab);
		}		
	}
					
	/**
	* @throws XavaException
	 */
	private void createTabs() {
		NodeList l = getRoot().getElementsByTagName(xtab[lang]);
		int c = l.getLength();		
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);						
			addTabs(el);			
		}						
	}
			
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		createTabs();				
	}
		
}