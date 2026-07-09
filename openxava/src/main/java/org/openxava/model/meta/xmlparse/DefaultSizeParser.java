package org.openxava.model.meta.xmlparse;



import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class DefaultSizeParser extends ParserBase {

	
	
	public DefaultSizeParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);
	}
	
	/**
	* @throws XavaException
	 */
	public static void configureDefaultSize() {
		DefaultSizeParser enParser = new DefaultSizeParser("default-size.xml", ENGLISH);
		enParser.parse();		
		DefaultSizeParser esParser = new DefaultSizeParser("longitud-defecto.xml", ESPANOL);
		esParser.parse();
	}
	
	/**
	* @throws XavaException
	 */
	private void createForStereotype(Node n) {
		Element el = (Element) n;
		String name = el.getAttribute(xname[lang]);
		try {
			String ssize = el.getAttribute(xsize[lang]);
			int size = Integer.parseInt(ssize);
			DefaultSize._addForStereotype(name, size);
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "stereotype", name);
		}
		
		try {
			String sscale = el.getAttribute(xscale[lang]);
			if(sscale != null && sscale.length() > 0) {
				int scale = Integer.parseInt(sscale);
				DefaultSize._addScaleForStereotype(name, scale);
			}
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "stereotype", name); 
		}		
	}
	
	/**
	* @throws XavaException
	 */
	private void createForAnnotation(Node n) { 
		Element el = (Element) n;
		String className = el.getAttribute(xclass[lang]);
		try {
			String ssize = el.getAttribute(xsize[lang]);
			int size = Integer.parseInt(ssize);
			DefaultSize._addForAnnotation(className, size);			
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "annotation", className);
		}

		try {
			String sscale = el.getAttribute(xscale[lang]);
			if(sscale != null && sscale.length() > 0) {
				int scale = Integer.parseInt(sscale);
				DefaultSize._addScaleForAnnotation(className, scale);
			}
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "annotation", className); 
		}		
	}
	
	/**
	* @throws XavaException
	 */
	private void createForType(Node n) {
		Element el = (Element) n;
		String className = el.getAttribute(xclass[lang]);
		try {
			String ssize = el.getAttribute(xsize[lang]);
			int size = Integer.parseInt(ssize);
			DefaultSize._addForType(className, size);			
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "type", className);
		}

		try {
			String sscale = el.getAttribute(xscale[lang]);
			if(sscale != null && sscale.length() > 0) {
				int scale = Integer.parseInt(sscale);
				DefaultSize._addScaleForType(className, scale);
			}
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "type", className); 
		}
		
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
	private void createForAnnotations() { 
		NodeList l = getRoot().getElementsByTagName(xfor_annotation[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForAnnotation(l.item(i));
		}
	}
	
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		createForStereotypes();
		createForTypes();
		createForAnnotations(); 
	}
			
}