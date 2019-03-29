package org.openxava.model.meta.xmlparse;

import java.util.*;



import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class AspectsParser extends ParserBase {
	

	private static Collection allModels;
	private static Map models;
	private static Map exceptModels;
	private boolean applyParsing;
	


	public AspectsParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);
	}
	
	public static void clear() {
		allModels = null;
		models = null;
		exceptModels = null;			
	}
		
	public static void configureAspects() throws XavaException {
		AspectsParser enParser = new AspectsParser("aspects.xml", ENGLISH);
		AspectsParser esParser = new AspectsParser("aspectos.xml", ESPANOL);
		
		// apply of aspects
		enParser.setApplyParsing(true);
		enParser.parse();				
		esParser.setApplyParsing(true);
		esParser.parse();
		
		// aspects definitions		
		enParser.setApplyParsing(false);
		enParser.parse();				
		esParser.setApplyParsing(false);
		esParser.parse();	
		
		clear();
	}
	
	
	private void createAspect(Node n) throws XavaException {
		Element el = (Element) n;	
		String name = el.getAttribute(xname[lang]);
		
		String models = getModelsForAspect(name);
		String exceptModels = getExceptModelsForAspect(name);
		if (Is.emptyStringAll(models, exceptModels) && !isAspectDefineForAllModels(name)) return; 
				
		int scope = Aspects.ALL;
		if (!Is.emptyString(models)) scope = Aspects.FOR;
		else if (!Is.emptyString(exceptModels)) {
			scope = Aspects.EXCEPT_FOR;
			models = exceptModels;
		}
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xpostcreate_calculator[lang])) {
				Aspects.addMetaCalculatorPostCreate(models, scope, CalculatorParser.parseCalculator(d, lang));							
			}
			else if (type.equals(xpostload_calculator[lang])) {
				Aspects.addMetaCalculatorPostLoad(models, scope, CalculatorParser.parseCalculator(d, lang));							
			}			
			else if (type.equals(xpostmodify_calculator[lang])) {
				Aspects.addMetaCalculatorPostModify(models, scope, CalculatorParser.parseCalculator(d, lang));							
			}
			else if (type.equals(xpreremove_calculator[lang])) {
				Aspects.addMetaCalculatorPreRemove(models, scope, CalculatorParser.parseCalculator(d, lang));							
			}						
		}		
	}
	
	private boolean isAspectDefineForAllModels(String name) {
		if (allModels == null) return false;
		return allModels.contains(name);
	}

	private String getExceptModelsForAspect(String name) {
		if (exceptModels == null) return "";
		String r = (String) exceptModels.get(name);
		return r==null?"":r;
	}

	private String getModelsForAspect(String name) {
		if (models == null) return "";
		String r = (String) models.get(name);
		return r==null?"":r;
	}

	private void createApply(Node n) throws XavaException {
		Element el = (Element) n;	
		String aspect = el.getAttribute(xaspect[lang]);
		String forModels = el.getAttribute(xfor_models[lang]);
		String exceptForModels = el.getAttribute(xexcept_for_models[lang]);
		if (!Is.emptyString(forModels) && !Is.emptyString(exceptForModels)) {
			throw new XavaException("for_models_and_except_for_model_not_compatible");
		}		
		if (Is.emptyStringAll(forModels, exceptForModels)) {
			if (allModels == null) allModels = new HashSet();
			allModels.add(aspect);
			return;
		}
		if (!Is.emptyString(forModels)) {
			if (models == null) models = new HashMap();
			models.put(aspect, forModels);
		}
		else {
			if (exceptModels == null) exceptModels = new HashMap();
			exceptModels.put(aspect, exceptForModels);
		}		
	}
	
		
	protected void createApplys() throws XavaException {		
		NodeList l = getRoot().getElementsByTagName(xapply[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createApply(l.item(i));
		}
	}
	
	protected void createAspects() throws XavaException {		
		NodeList l = getRoot().getElementsByTagName(xaspect[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			createAspect(l.item(i));
		}
	}
	
	
	protected void createObjects() throws XavaException {
		if (isApplyParsing()) createApplys();
		else createAspects();
	}

	public boolean isApplyParsing() {
		return applyParsing;
	}

	public void setApplyParsing(boolean aspectsParsing) {
		this.applyParsing = aspectsParsing;
	}
			
}