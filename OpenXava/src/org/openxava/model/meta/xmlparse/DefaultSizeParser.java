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
	
	public static void configureDefaultSize() throws XavaException {
		DefaultSizeParser enParser = new DefaultSizeParser("default-size.xml", ENGLISH);
		enParser.parse();		
		DefaultSizeParser esParser = new DefaultSizeParser("longitud-defecto.xml", ESPANOL);
		esParser.parse();
	}
	
	private void createForStereotype(Node n) throws XavaException {
		Element el = (Element) n;
		String name = el.getAttribute(xname[lang]);
		try {
			String ssize = el.getAttribute(xsize[lang]);
			int size = Integer.parseInt(ssize);
			DefaultSize._addForStereotype(name, size);
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "estereotipo", name);
		}
		
		try {
			String sscale = el.getAttribute(xscale[lang]);
			if(sscale != null && sscale.length() > 0) {
				int scale = Integer.parseInt(sscale);
				DefaultSize._addScaleForStereotype(name, scale);
			}
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "estereotipo", name); 
		}		
	}
	
	private void createForType(Node n) throws XavaException {
		Element el = (Element) n;
		String className = el.getAttribute(xclass[lang]);
		try {
			String ssize = el.getAttribute(xsize[lang]);
			int size = Integer.parseInt(ssize);
			DefaultSize._addForType(className, size);			
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "tipo", className);
		}

		try {
			String sscale = el.getAttribute(xscale[lang]);
			if(sscale != null && sscale.length() > 0) {
				int scale = Integer.parseInt(sscale);
				DefaultSize._addScaleForType(className, scale);
			}
		}
		catch (NumberFormatException ex) {			
			throw new XavaException("default_size_number", "tipo", className); 
		}
		
	}
		
	private void createForStereotypes() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xfor_stereotype[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForStereotype(l.item(i));
		}
	}
	
	private void createForTypes() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xfor_type[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForType(l.item(i));
		}
	}
	
	
	protected void createObjects() throws XavaException {
		createForStereotypes();
		createForTypes();
	}
			
}