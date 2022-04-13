package org.openxava.model.meta.xmlparse;



import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.util.xmlparse.*;
import org.openxava.validators.meta.*;
import org.w3c.dom.*;

/**
 * @author Javier Paniza; modified by Radoslaw Ostrzycki, Newitech
 */
public class ModelParser extends XmlElementsNames {
	
	
	
	public static MetaEntity parseEntity(Node n, String name, int lang) throws XavaException {
		Element el = (Element) n;
		MetaEntity e = new MetaEntity();
		e.setName(name);
		e.setQualifiedName(name);
		e.setLabel(el.getAttribute(xlabel[lang]));
		if (hasBean(el, lang)) {
			e.setPOJOClassName(getBeanClass(el, lang));
			e.setPojoGenerated(false);
		}				
		else {
			e.setPojoGenerated(true);
		}
		e.setXmlComponent(true); 

		fillMembers(el, e, lang);
		return e;
	}
		
	public static MetaAggregate parseAggregate(Node n, MetaModel container, int lang) throws XavaException {
		Element el = (Element) n;
		if (hasBean(el, lang)) {
			MetaAggregateForReference r = createAggregateForReference(n, container, lang);
			r.setPojoGenerated(false);
			return r;
		}
		else {
			String name = el.getAttribute(xname[lang]);
			if (container.containsMetaReferenceWithModel(name)) {
				MetaAggregateForReference r = createAggregateForReference(n, container, lang);
				r.setPojoGenerated(true);
				return r;
			}
			else {
				MetaAggregateForCollection r = createAggregateForCollection(n, container, lang);
				r.setPojoGenerated(true);
				return r;
			}
		}		
	}

	private static MetaAggregateForReference createAggregateForReference(Node n, MetaModel container, int lang) throws XavaException {	
		Element el = (Element) n;
		MetaAggregateForReference a = new MetaAggregateForReference();
		a.setName(el.getAttribute(xname[lang]));
		a.setQualifiedName(container.getQualifiedName() + "." + a.getName()); 
		a.setLabel(el.getAttribute(xlabel[lang]));		
		if (hasBean(el, lang)) {
			a.setBeanClass(getBeanClass(el, lang));
		}				
		fillMembers(el, a, lang);
		return a;
	}
	
	private static MetaAggregateForCollection createAggregateForCollection(Node n, MetaModel container, int lang) throws XavaException {	
		Element el = (Element) n;		
		MetaAggregateForCollection a = new MetaAggregateForCollection();
		a.setName(el.getAttribute(xname[lang]));
		a.setQualifiedName(container.getQualifiedName() + "." + a.getName());
		a.setLabel(el.getAttribute(xlabel[lang]));
		fillMembers(el, a, lang);
		return a;
	}
			
	private static void fillMembers(Element el, MetaModel container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xproperty[lang])) {
				container.addMetaProperty(createProperty(d, lang));
			}
			else if (type.equals(xreference[lang])) {
				container.addMetaReference(createReference(d, lang));
			}
			else if (type.equals(xcollection[lang])) {
				container.addMetaCollection(createCollection(d, lang));
			}
			else if (type.equals(xmethod[lang])) {
				container.addMetaMethod(createMethod(d, lang));
			}			
			else if (type.equals(xfinder[lang])) {				
				container.addMetaFinder(createFinder(d, lang));
			}
			else if (type.equals(xpostcreate_calculator[lang])) {
				container.addMetaCalculatorPostCreate(CalculatorParser.parseCalculator(d, lang));							
			}
			else if (type.equals(xpostload_calculator[lang])) {
				container.addMetaCalculatorPostLoad(CalculatorParser.parseCalculator(d, lang));							
			}			
			else if (type.equals(xpostmodify_calculator[lang])) {
				container.addMetaCalculatorPostModify(CalculatorParser.parseCalculator(d, lang));							
			}
			else if (type.equals(xpreremove_calculator[lang])) {
				container.addMetaCalculatorPreRemove(CalculatorParser.parseCalculator(d, lang));							
			}						
			else if (type.equals(xvalidator[lang])) {
				container.addMetaValidator(createValidator(d, lang));
			}									
			else if (type.equals(xremove_validator[lang])) {
				container.addMetaValidatorRemove(createValidator(d, lang));
			}
			else if (type.equals(ximplements[lang])) {
				container.addInterfaceName(d.getAttribute(xinterface[lang]));
			}			
		}		
		Aspects.fillImplicitCalculators(container);
	}
			
	private static void fillValidator(Element el, MetaProperty container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xvalidator[lang])) {
				MetaValidator metaValidator = createValidator(d, lang);
				if (metaValidator.containsMetaSetsWithoutValue()) {
					throw new XavaException("property_value_for_property_validator_incorrect", container.getName(), "");
				}
				container.addMetaValidator(metaValidator);
			}
		}
	}
	
	private static void fillPostremoveCalculator(Element el, MetaCollection container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xpostremove_calculator[lang])) {
				container.addMetaCalculatorPostRemove(CalculatorParser.parseCalculator(d, lang));
			}
		}
	}	
					
	private static boolean hasBean(Element el, int lang) {
		return ParserUtil.getElement(el, xbean[lang]) != null;
	}	
	
	/**
	 * @return Null if no exists a bean element.
	 */
	private static String getBeanClass(Element el, int lang) throws XavaException {
		Element elBean = ParserUtil.getElement(el, xbean[lang]);
		if (elBean == null) {
			throw new XavaException("xml_element_not_found", xbean[lang], el.getAttribute(xname[lang]));
		}
		return elBean.getAttribute(xclass[lang]);
	}
		

	public static MetaProperty createProperty(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaProperty p = new MetaProperty();
		p.setName(el.getAttribute(xname[lang]));
		p.setLabel(el.getAttribute(xlabel[lang]));
		p.setStereotype(el.getAttribute(xstereotype[lang]));
		p.setTypeName(el.getAttribute(xtype[lang]));
		p.setSize(ParserUtil.getAttributeInt(el, xsize[lang]));
		if (!Is.emptyString(el.getAttribute(xscale[lang]))) { 
			p.setScale(ParserUtil.getAttributeInt(el, xscale[lang]));
		} 
		p.setHidden(ParserUtil.getAttributeBoolean(el, xhidden[lang]));
		p.setVersion(ParserUtil.getAttributeBoolean(el, xversion[lang]));
		p.setSearchKey(ParserUtil.getAttributeBoolean(el, xsearch_key[lang]));
		boolean key = ParserUtil.getAttributeBoolean(el, xkey[lang]);
		if (key) p.setKey(key);
		fillValidator(el, p, lang);				
		Element elValidValues = ParserUtil.getElement(el, xvalid_values[lang]);
		if (elValidValues != null) {
			NodeList l = elValidValues.getElementsByTagName(xvalid_value[lang]);
			int c = l.getLength();
			for (int i = 0; i < c; i++) {
				Element validValue = (Element) l.item(i);
				p.addValidValue(validValue.getAttribute(xvalue[lang]));
			}
		}
		p.setMetaCalculator(createCalculator(el, lang));
		p.setMetaCalculatorDefaultValue(createDefaultValueCalculator(el, lang));
		if (Is.emptyString(el.getAttribute(xrequired[lang]))) {
			// Calculating a valid default value for required
			p.setRequired(p.isKey() && (!(p.hasCalculatorDefaultValueOnCreate() || p.isHidden())));
		}
		else {
			p.setRequired(ParserUtil.getAttributeBoolean(el, xrequired[lang]));
		}
		p.setCalculation(ParserUtil.getString(el, xcalculation[lang]));
		return p;
	}
	
	private static MetaFinder createFinder(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaFinder b = new MetaFinder();
		b.setName(el.getAttribute(xname[lang]));		
		b.setArguments(el.getAttribute(xarguments[lang]));
		b.setCollection(ParserUtil.getAttributeBoolean(el, xcollection[lang]));		
		b.setCondition(ParserUtil.getString(el, xcondition[lang]));		
		b.setOrder(ParserUtil.getString(el, xorder[lang]));
		return b;
	}
	
	private static MetaMethod createMethod(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaMethod m = new MetaMethod();
		m.setName(el.getAttribute(xname[lang]));
		m.setTypeName(el.getAttribute(xtype[lang]));		
		m.setArguments(el.getAttribute(xarguments[lang]));
		m.setExceptions(el.getAttribute(xexceptions[lang]));
		m.setMetaCalculator(createCalculator(el, lang));
		return m;
	}
			
	private static MetaValidator createValidator(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		String name = el.getAttribute(xname[lang]);
		String className = el.getAttribute(xclass[lang]);
			
		if (!Is.emptyString(name) && !Is.emptyString(className)) {
			throw new XavaException("name_and_class_not_compatible");
		}
		if (Is.emptyStringAll(name, className)) {
			throw new XavaException("name_or_class_required");
		}
		
		MetaValidator e = new MetaValidator();
		e.setName(name);
		e.setClassName(className);	
		e.setOnlyOnCreate(ParserUtil.getAttributeBoolean(el, xonly_on_create[lang]));
		fillSet(el, e, lang);
		return e;
	}
	
	private static void fillSet(Element el, MetaSetsContainer container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xset[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaSet(createSet(l.item(i), lang));
		}
	}
	
	
	private static MetaSet createSet(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaSet a = new MetaSet();		
		a.setPropertyName(el.getAttribute(xproperty[lang]));
		a.setPropertyNameFrom(el.getAttribute(xfrom[lang]));
		a.setValue(el.getAttribute(xvalue[lang]));		
		return a;
	}
	
	
			
	private static MetaCalculator createCalculator(Element el, int lang) throws XavaException {
		NodeList l = el.getElementsByTagName(xcalculator[lang]);
		int c = l.getLength();
		if (c > 1) {			
			throw new XavaException("property_no_more_1_calculator");
		}
		if (c < 1) return null;
		return CalculatorParser.parseCalculator(l.item(0), lang);
	}
	
	private static MetaCalculator createDefaultValueCalculator(Element el, int lang) throws XavaException {
		NodeList l = el.getElementsByTagName(xdefault_value_calculator[lang]);
		int c = l.getLength();
		if (c > 1) {			
			throw new XavaException("no_more_1_default_value_calculator");
		}
		if (c < 1) return null;
		return CalculatorParser.parseCalculator(l.item(0), lang);
	}
		
	private static MetaReference createReference(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaReference r = new MetaReference();
		String name = el.getAttribute(xname[lang]); 		
		String model = el.getAttribute(xmodel[lang]);
		if (Is.emptyString(name) && Is.emptyString(model)) {
			throw new XavaException("name_or_model_required");
		}
		r.setName(name);
		r.setReferencedModelName(model);
		r.setLabel(el.getAttribute(xlabel[lang]));		
		r.setRequired(ParserUtil.getAttributeBoolean(el, xrequired[lang]));
		r.setKey(ParserUtil.getAttributeBoolean(el, xkey[lang]));
		r.setSearchKey(ParserUtil.getAttributeBoolean(el, xsearch_key[lang])); 
		r.setRole(el.getAttribute(xrole[lang]));
		r.setMetaCalculatorDefaultValue(createDefaultValueCalculator(el, lang));
		
		return r;
	}
		
	private static MetaCollection createCollection(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaCollection c = new MetaCollection();
		c.setName(el.getAttribute(xname[lang]));
		c.setLabel(el.getAttribute(xlabel[lang]));				
		c.setMinimum(ParserUtil.getAttributeInt(el, xminimum[lang]));
		c.setMaximum(ParserUtil.getAttributeInt(el, xmaximum[lang]));
		c.setCondition(ParserUtil.getString(el, xcondition[lang]));
		c.setOrder(ParserUtil.getString(el, xorder[lang]));
		c.setMetaReference(createReference(ParserUtil.getElement(el, xreference[lang]), lang));
		c.setMetaCalculator(createCalculator(el, lang));
		fillPostremoveCalculator(el, c, lang);
		return c;
	}	
	
}

