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
	
	public static void configureStereotypeTypeDefault() throws XavaException {
		StereotypeTypeDefaultParser enParser = new StereotypeTypeDefaultParser("stereotype-type-default.xml", ENGLISH);
		enParser.parse();		
		StereotypeTypeDefaultParser esParser = new StereotypeTypeDefaultParser("tipo-estereotipo-defecto.xml", ESPANOL);
		esParser.parse();
	}
	
	private void createForStereotype(Node n) throws XavaException {		
		Element el = (Element) n;
		String name = el.getAttribute(xstereotype[lang]);		
		String type = el.getAttribute(xtype[lang]);			
		TypeStereotypeDefault._addForStereotype(name, type);		
	}
	
	private void createForStereotypes() throws XavaException {		
		NodeList l = getRoot().getElementsByTagName(xfor[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForStereotype(l.item(i));
		}
	}
			
	protected void createObjects() throws XavaException {
		createForStereotypes();	
	}
			
}