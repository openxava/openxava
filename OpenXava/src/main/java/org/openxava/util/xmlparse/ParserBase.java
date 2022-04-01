package org.openxava.util.xmlparse;


import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
abstract public class ParserBase extends XmlElementsNames {
	
	private static Log log = LogFactory.getLog(ParserBase.class);

	protected final static int ENGLISH = 0;
	protected final static int ESPANOL = 1;
	protected int lang;
	 			
	private Element root;
	private String xmlFileURL;
	
	public ParserBase(String xmlFileURL) {
		// assert(xmlFileURL)
		this.xmlFileURL = xmlFileURL;
	}
	
	public ParserBase(String xmlFileURL, int language) {
		// assert(xmlFileURL)
		this.xmlFileURL = xmlFileURL;
		this.lang = language;
	}
	
	abstract protected void createObjects() throws XavaException;
	
	protected boolean getBoolean(Element el, String label) {
		return ParserUtil.getBoolean(el, label);
	}
	
	protected boolean getAttributeBoolean(Element el, String label) {
		return ParserUtil.getAttributeBoolean(el, label);
	}
	
	protected boolean getAttributeBoolean(Element el, String label, boolean defaultValue) { 
		return ParserUtil.getAttributeBoolean(el, label, defaultValue); 
	}
	
	protected Element getElement(Element el, String label) {
		return ParserUtil.getElement(el, label);
	}
	
	protected int getInt(Element el, String label) throws XavaException {
		return ParserUtil.getInt(el, label);
	}
	
	protected int getAttributeInt(Element el, String label) throws XavaException {
		return ParserUtil.getAttributeInt(el, label);
	}
	
	protected org.w3c.dom.Element getRoot() {
		return root;
	}

	protected String getString(Element el, String label) {
		return ParserUtil.getString(el, label);
	}
	
	public void parse() throws XavaException {
		String xmlFileCompleteURL = null;
		try {						
			Enumeration resources = getClass().getClassLoader().getResources(xmlFileURL);
			while (resources.hasMoreElements()) {
				URL resource = (URL) resources.nextElement();
				xmlFileCompleteURL = resource.toExternalForm();				
				_parse(xmlFileCompleteURL);				
			}			
		} 
		catch (XavaException ex) {
			throw ex;
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("xml_loading_error", xmlFileCompleteURL);
		}
	}
	
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException { 
		DocumentBuilder	documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		documentBuilder.setEntityResolver(new XMLEntityResolver());
		return documentBuilder;
	}	

	private void _parse(String xmlFileCompleteURL) throws XavaException {
		try {						
			Document doc = getDocumentBuilder().parse(xmlFileCompleteURL);			
			root = (Element) doc.getDocumentElement();
			createObjects();
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("xml_loading_error", xmlFileCompleteURL);
		}		
	}
	
}