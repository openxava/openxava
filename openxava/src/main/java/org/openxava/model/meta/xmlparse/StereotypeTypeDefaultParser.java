package org.openxava.model.meta.xmlparse;



import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class StereotypeTypeDefaultParser extends ParserBase {	

	
	
	public StereotypeTypeDefaultParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);
	}
	
	/**
	* @throws XavaException
	 */
	public static void configureStereotypeTypeDefault() {
		StereotypeTypeDefaultParser enParser = new StereotypeTypeDefaultParser("stereotype-type-default.xml", ENGLISH);
		enParser.parse();		
		StereotypeTypeDefaultParser esParser = new StereotypeTypeDefaultParser("tipo-estereotipo-defecto.xml", ESPANOL);
		esParser.parse();
	}
	
	/**
	* @throws XavaException
	 */
	private void createForStereotype(Node n) {		
		Element el = (Element) n;
		String name = el.getAttribute(xstereotype[lang]);		
		String type = el.getAttribute(xtype[lang]);			
		TypeStereotypeDefault._addForStereotype(name, type);		
	}
	
	/**
	* @throws XavaException
	 */
	private void createForStereotypes() {		
		NodeList l = getRoot().getElementsByTagName(xfor[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForStereotype(l.item(i));
		}
	}
			
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		createForStereotypes();	
	}
			
}