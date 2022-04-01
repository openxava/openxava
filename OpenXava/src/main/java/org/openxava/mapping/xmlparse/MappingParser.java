package org.openxava.mapping.xmlparse;



import org.openxava.component.*;
import org.openxava.mapping.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class MappingParser extends XmlElementsNames {

	
	
	public static EntityMapping parseEntityMapping(MetaComponent component, Node n, int lang) throws XavaException { 		
		Element el = (Element) n;
		EntityMapping e = new EntityMapping();
		e.setMetaComponent(component); 
		e.setTable(el.getAttribute(xtable[lang]));
		fillPropertiesMappings(el, e, lang);
		fillMutiplePropertyMapping(el, e, lang);
		fillReferenceMappings(el, e, lang);		
		return e;
	}
	
	public static AggregateMapping parseAggregateMapping(MetaComponent component, Node n, int lang) throws XavaException {
		Element el = (Element) n;
		AggregateMapping e = new AggregateMapping();
		e.setMetaComponent(component); 
		e.setModelName(el.getAttribute(xaggregate[lang]));
		e.setTable(el.getAttribute(xtable[lang]));		
		fillPropertiesMappings(el, e, lang);
		fillMutiplePropertyMapping(el, e, lang);
		fillReferenceMappings(el, e, lang);
		return e;
	}
	
	private static PropertyMapping createPropertyMapping(ModelMapping parent, Node n, int lang) throws XavaException {		
		Element el = (Element) n;
		PropertyMapping p = new PropertyMapping(parent);
		p.setProperty(el.getAttribute(xmodel_property[lang]));
		p.setColumn(el.getAttribute(xtable_column[lang]));
		if (Is.emptyString(p.getColumn())) p.setColumn(p.getProperty());
		p.setCmpTypeName(el.getAttribute(xcmp_type[lang]));
		p.setFormula(el.getAttribute(xformula[lang])); 
		fillConverter(el, p, lang);		
		return p;
	}
	
	private static PropertyMapping createMultiplePropertyMapping(ModelMapping parent, Node n, int lang) throws XavaException {		
		Element el = (Element) n;
		PropertyMapping p = new PropertyMapping(parent);
		p.setProperty(el.getAttribute(xmodel_property[lang]));
		p.setColumn(el.getAttribute(xtable_column[lang]));
		p.setCmpTypeName(el.getAttribute(xcmp_type[lang]));
		fillMultipleConverter(el, p, lang);
		fillCmpFields(el, p, lang);
		return p;
	}
	
	
	private static ReferenceMapping createReferenceMapping(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		ReferenceMapping p = new ReferenceMapping();
		p.setReference(el.getAttribute(xmodel_reference[lang]));
		fillReferenceMappingDetails(el, p, lang);
		return p;
	}		
	
	private static void fillReferenceMappingDetails(Element el, ReferenceMapping container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xreference_mapping_detail[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addDetail(createReferenceMappingDetail(l.item(i), lang));
		}
	}
	
	private static void fillConverter(Element el, PropertyMapping container, int lang)
		throws XavaException {			
		NodeList l = el.getElementsByTagName(xconverter[lang]);
		int c = l.getLength();
		if (c == 0) return;
		if (c > 1) {
			throw new XavaException("only_1_converter");
		}
		container.setConverterClassName(getConverterClassName(l.item(0), lang));
		fillSet((Element)l.item(0), container, lang);				
	}
	
	private static void fillConverter(Element el, ReferenceMappingDetail container, int lang)
		throws XavaException {			
		NodeList l = el.getElementsByTagName(xconverter[lang]);
		int c = l.getLength();
		if (c == 0) return;
		if (c > 1) {
			throw new XavaException("only_1_converter");
		}
		container.setConverterClassName(getConverterClassName(l.item(0), lang));
		fillSet((Element)l.item(0), container, lang);				
	}
	
	
	private static void fillMultipleConverter(Element el, PropertyMapping container, int lang)
		throws XavaException {			
		NodeList l = el.getElementsByTagName(xconverter[lang]);
		int c = l.getLength();
		if (c == 0) return;
		if (c > 1) {
			throw new XavaException("only_1_converter");
		}		
		container.setMultipleConverterClassName(getConverterClassName(l.item(0), lang));
		fillSet((Element)l.item(0), container, lang);				
	}
	
	
	private static void fillCmpFields(Element el, PropertyMapping container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xcmp_field[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addCmpField(createCmpField(l.item(i), lang));
		}
	}
			
	private static ReferenceMappingDetail createReferenceMappingDetail(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		ReferenceMappingDetail p = new ReferenceMappingDetail();
		p.setColumn(el.getAttribute(xtable_column[lang]));
		p.setReferencedModelProperty(el.getAttribute(xreferenced_model_property[lang]));
		p.setCmpTypeName(el.getAttribute(xcmp_type[lang]));
		fillConverter(el, p, lang);
		return p;
	}
	
	private static CmpField createCmpField(Node n, int lang) throws XavaException {		
		Element el = (Element) n;
		CmpField field = new CmpField();
		field.setColumn(el.getAttribute(xtable_column[lang]));
		field.setConverterPropertyName(el.getAttribute(xconverter_property[lang]));
		field.setCmpTypeName(el.getAttribute(xcmp_type[lang]));
		return field;
	}
		
	private static String getConverterClassName(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		return el.getAttribute(xclass[lang]);
	}
	
	private static String getCmpTypeName(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		return el.getAttribute(xcmp_type[lang]);
	}	
	
	
	private static void fillPropertiesMappings(Element el, ModelMapping container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xproperty_mapping[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addPropertyMapping(createPropertyMapping(container, l.item(i), lang));
		}
	}
	
	private static void fillMutiplePropertyMapping(Element el, ModelMapping container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xmultiple_property_mapping[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addPropertyMapping(createMultiplePropertyMapping(container, l.item(i), lang));
		}
	}
		
	private static void fillReferenceMappings(Element el, ModelMapping container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xreference_mapping[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addReferenceMapping(createReferenceMapping(l.item(i), lang));
		}
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
		a.setValue(el.getAttribute(xvalue[lang]));
		if (!Is.emptyString(el.getAttribute(xfrom[lang]))) {
			throw new XavaException("attribute_from_not_in_converter");
		}						
		return a;
	}
	
}