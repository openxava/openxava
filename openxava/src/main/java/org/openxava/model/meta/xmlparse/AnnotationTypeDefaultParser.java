package org.openxava.model.meta.xmlparse;



import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * 
 * @author: Javier Paniza
 */
public class AnnotationTypeDefaultParser extends ParserBase {	

	
	
	public AnnotationTypeDefaultParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);
	}
	
	/**
	* @throws XavaException
	 */
	public static void configureAnnotationTypeDefault() {
		AnnotationTypeDefaultParser enParser = new AnnotationTypeDefaultParser("annotation-type-default.xml", ENGLISH);
		enParser.parse();		
		AnnotationTypeDefaultParser esParser = new AnnotationTypeDefaultParser("tipo-anotacion-defecto.xml", ESPANOL);
		esParser.parse();
	}
	
	/**
	* @throws XavaException
	 */
	private void createForAnnotation(Node n) {		
		Element el = (Element) n;
		String annotation = el.getAttribute(xannotation[lang]);		
		String type = el.getAttribute(xtype[lang]);			
		TypeAnnotationDefault._addForAnnotation(annotation, type);		
	}
	
	/**
	* @throws XavaException
	 */
	private void createForAnnotations() {		
		NodeList l = getRoot().getElementsByTagName(xfor[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForAnnotation(l.item(i));
		}
	}
			
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		createForAnnotations();	
	}
			
}