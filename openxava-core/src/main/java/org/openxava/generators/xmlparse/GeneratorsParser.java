package org.openxava.generators.xmlparse;



import org.openxava.generators.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class GeneratorsParser extends ParserBase {

	
	
	public GeneratorsParser(String urlXmlFile, int language) {
		super(urlXmlFile, language);
	}
	
	public static void configureGenerators() throws XavaException {
		GeneratorsParser enParser = new GeneratorsParser("code-generators.xml", ENGLISH);
		enParser.parse();		
		GeneratorsParser esParser = new GeneratorsParser("generadores-codigo.xml", ESPANOL);
		esParser.parse();
	}
	
	private void createForStereotype(Node n) throws XavaException {
		Element el = (Element) n;
		String name = el.getAttribute(xname[lang]);
		String modelType = el.getAttribute(xmodel_type[lang]);
		String className = el.getAttribute(xclass[lang]);		
		GeneratorFactory._addForStereotype(name, modelType, className);
	}
		
	private void createForStereotypes() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xfor_stereotype[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createForStereotype(l.item(i));
		}
	}
	
	protected void createObjects() throws XavaException {
		createForStereotypes();	
	}
			
}