package org.openxava.mapping.xmlparse;



import org.openxava.mapping.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class ConvertersParser extends ParserBase {

	
	
	public ConvertersParser(String urlArchivoXml, int language) {
		super(urlArchivoXml, language);
	}
	
	/**
	* @throws XavaException
	 */
	public static void configureConverters() {
		ConvertersParser parserDefault = new ConvertersParser("default-converters.xml", ENGLISH);
		parserDefault.parse();		
		ConvertersParser enParser = new ConvertersParser("converters.xml", ENGLISH);
		enParser.parse();		
		ConvertersParser esParser = new ConvertersParser("conversores.xml", ESPANOL);
		esParser.parse();		
	}
	
	/**
	* @throws XavaException
	 */
	private void createForStereotype(Node n) {
		Element el = (Element) n;
		String stereotype = el.getAttribute(xstereotype[lang]);		
		String converterClass = el.getAttribute(xconverter_class[lang]);
		String cmpType = el.getAttribute(xcmp_type[lang]);
		Converters._addForStereotype(stereotype, converterClass, cmpType);
	}
	
	/**
	* @throws XavaException
	 */
	private void createForType(Node n) {
		Element el = (Element) n;
		String type = el.getAttribute(xtype[lang]);
		String converterClass = el.getAttribute(xconverter_class[lang]);
		String cmpType = el.getAttribute(xcmp_type[lang]);
		Converters._addForType(type, converterClass, cmpType);
	}
		
	/**
	* @throws XavaException
	 */
	private void createForStereotypes() {
		NodeList l = getRoot().getElementsByTagName(xfor_stereotype[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForStereotype(l.item(i));
		}
	}
	
	/**
	* @throws XavaException
	 */
	private void createForTypes() {
		NodeList l = getRoot().getElementsByTagName(xfor_type[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForType(l.item(i));
		}
	}	
	
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		createForStereotypes();
		createForTypes();
	}
			
}