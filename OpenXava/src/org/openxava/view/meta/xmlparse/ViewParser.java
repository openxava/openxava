package org.openxava.view.meta.xmlparse;

import org.openxava.filters.*;
import org.openxava.filters.meta.*;
import org.openxava.model.meta.xmlparse.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.openxava.view.meta.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class ViewParser extends XmlElementsNames {
	
	public static MetaView parseView(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaView v = new MetaView();
		v.setName(el.getAttribute(xname[lang]));
		v.setLabel(el.getAttribute(xlabel[lang]));
		v.setModelName(el.getAttribute(xmodel[lang]));
		v.setExtendsView(el.getAttribute(xextends[lang])); 
		String attributeMembers = el.getAttribute(xmembers[lang]);
		String elementMembers = getMemberElementMembers(el, lang);						
		if (!attributeMembers.equals("*") && !Is.emptyString(elementMembers)) {
			throw new XavaException("incompatible_attribute_element_members");
		} 		
		if (!Is.emptyString(elementMembers)){
			v.setMembersNames(elementMembers);
		}
		else  {
			v.setMembersNames(attributeMembers);
		}
		v.setAlignedByColumns(isAlignedByColumns(getMembersElement(el, lang), lang)); 
		fillMediator(el, v, lang);
		fillProperties(el, v, lang);
		fillReferenceViews(el, v, lang);
		fillCollectionViews(el, v, lang);
		fillPropertyViews(el, v, lang);
		fillGroups(el, v, lang);
		fillSections(el, v, lang); 
		return v;
	}
	
	private static Element getMembersElement(Element el, int lang) { 
		NodeList l = el.getElementsByTagName(xmembers[lang]);
		if (l.getLength() < 1)
			return null;
		return (Element) l.item(0);
	}

	private static String getMemberElementMembers(Element el, int lang) {
		return getMembers(getMembersElement(el, lang), lang);		
	}
	
	private static String getMembers(Node n, int lang) {
		if (n == null) return null; 
		NodeList list = n.getChildNodes();
		StringBuffer r = new StringBuffer();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = (Node) list.item(i);
			if (xgroup[lang].equals(node.getNodeName())) {
				String groupName = ((Element) node).getAttribute(xname[lang]);
				r.append("__GROUP__");
				r.append(groupName);
				r.append(',');
			}
			else if (xaction[lang].equals(node.getNodeName())) {
				String action = ((Element) node).getAttribute(xaction[lang]);
				r.append("__ACTION__");
				if (ParserUtil.getAttributeBoolean(((Element) node), xalways_enabled[lang])) {
					r.append("AE__"); 
				}
				r.append(action);
				r.append(',');
			}
			else {
				String nodeValue = node.getNodeValue();
				if (nodeValue != null) r.append(nodeValue);
			}
		}
		String s = r.toString();		
		return Is.emptyString(s)?null:s;
	}
	
	private static void fillSections(Element el, MetaView v, int lang) throws XavaException {
		NodeList nodesMembers = el.getElementsByTagName(xmembers[lang]);
		if (nodesMembers.getLength() == 0) return;
		fillSectionsImpl((Element) nodesMembers.item(0), v, lang);		
	}
	
	private static void fillSectionsImpl(Element el, MetaView v, int lang) throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element s = (Element) l.item(i);
			String type = s.getTagName();
			if (!type.equals(xsection[lang])) continue;
			String name = s.getAttribute(xname[lang]);
			String label = s.getAttribute(xlabel[lang]);
			if (Is.emptyStringAll(name, label)) {
				throw new XavaException("section_name_or_label_required");			
			}						
			String members = getMembers(s, lang);
			boolean alignedByColumns = isAlignedByColumns(s, lang);
			MetaView newSection = v.addSection(name, label, members, alignedByColumns);
			fillSectionsImpl(s, newSection, lang);
		}
	}
	
	private static boolean isAlignedByColumns(Element el, int lang) {
		if (el == null) return false;
		if (ParserUtil.getAttributeBoolean(el, xaligned_by_columns[lang])) return true;
		return XavaPreferences.getInstance().isAlignedByColumns();
	}
	
	private static void fillGroups(Element el, MetaView v, int lang) throws XavaException {
		NodeList l = el.getElementsByTagName(xgroup[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element n = (Element) l.item(i);
			String name = n.getAttribute(xname[lang]);
			String label = n.getAttribute(xlabel[lang]);
			String members = getMembers(n, lang);
			boolean alignedByColumns = isAlignedByColumns(n, lang);  
			v.addMetaGroup(name, label, members, alignedByColumns);
		}
	}
	
	private static void fillMediator(Element el, MetaView container, int lang) // only use in spanish/swing version
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		if (c == 0) return;
		Node found = null;
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (n.getNodeName().equals(xmediator[lang])) {
				found = n;
				break;
			}
		}
		if (found == null) {
			return;
		}		
		container.setMediatorClassName(getMediatorClass(found, lang));		
	}
	
	private static void fillMediator(Element el, MetaCollectionView container, int lang)
		throws XavaException {			
		NodeList l = el.getElementsByTagName(xmediator[lang]);
		int c = l.getLength();
		if (c == 0) return;
		if (c > 1) {
			throw new XavaException("only_1_mediator");
		}
		container.setMediatorClassName(getMediatorClass(l.item(0), lang));
	}	
		
	private static void fillReferenceViews(Element el, MetaView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xreference_view[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaViewReference(createMetaReferenceView(l.item(i), lang));
		}
	}
	
	private static void fillCollectionViews(Element el, MetaView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xcollection_view[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaViewCollection(createMetaCollectionView(l.item(i), lang));
		}
	}	
	
	private static void fillPropertyViews(Element el, MetaView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xproperty_view[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaViewProperty(createMetaPropertyView(l.item(i), lang));
		}
	}	
	
	private static MetaReferenceView createMetaReferenceView(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaReferenceView a = new MetaReferenceView();		
		a.setReferenceName(el.getAttribute(xreference[lang]));
		a.setViewName(el.getAttribute(xview[lang]));
		a.setReadOnly(ParserUtil.getAttributeBoolean(el, xread_only[lang]));
		a.setReadOnlyOnCreate(a.isReadOnly()); // Not supported in XML component yet
		a.setCollapsed(ParserUtil.getAttributeBoolean(el, xcollapsed[lang]));
		a.setAsAggregate(ParserUtil.getAttributeBoolean(el, xas_aggregate[lang]));
		a.setEditor(el.getAttribute(xeditor[lang]));
		if (!Is.emptyString(el.getAttribute(xframe[lang]))) {
			a.setFrame(ParserUtil.getAttributeBoolean(el, xframe[lang]));
		}
		if (!Is.emptyString(el.getAttribute(xcreate[lang]))) {
			a.setCreate(ParserUtil.getAttributeBoolean(el, xcreate[lang]) );
		}
		if (!Is.emptyString(el.getAttribute(xmodify[lang]))) {
			a.setModify(ParserUtil.getAttributeBoolean(el, xmodify[lang]) );
		}		
		if (!Is.emptyString(el.getAttribute(xsearch[lang]))) {
			a.setSearch(ParserUtil.getAttributeBoolean(el, xsearch[lang]) );
		}		
		a.setSearchListCondition(ParserUtil.getString(el, xsearch_list_condition[lang])); 
		fillOnChangeSearchAction(el, a, lang);
		fillMetaSearchAction(el, a, lang);
		fillMetaDescriptionsList(el, a, lang);
		fillActions(el, a, lang);
		return a;
	}
	
	private static MetaCollectionView createMetaCollectionView(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaCollectionView a = new MetaCollectionView();
		a.setCollectionName(el.getAttribute(xcollection[lang]));		
		a.setViewName(el.getAttribute(xview[lang]));
		a.setCollapsed(ParserUtil.getAttributeBoolean(el, xcollapsed[lang])); 
		a.setReadOnly(ParserUtil.getAttributeBoolean(el, xread_only[lang]));		
		a.setEditOnly(ParserUtil.getAttributeBoolean(el, xedit_only[lang]));
		a.setEditor(el.getAttribute(xeditor[lang]));
		if (a.isEditOnly() && a.isReadOnly()) {
			throw new XavaException("collection_onlyread_onlyedit_incompatible");
		}
		a.setCreateReference(ParserUtil.getAttributeBoolean(el, xcreate_reference[lang]));
		a.setModifyReference(ParserUtil.getAttributeBoolean(el, xmodify_reference[lang]));
		a.setAsAggregate(ParserUtil.getAttributeBoolean(el, xas_aggregate[lang]));
		fillMediator(el, a, lang);
		a.setPropertiesList(ParserUtil.getString(el, xlist_properties[lang]));
		a.setEditActionName(getAction(el, xedit_action[lang], lang));
		a.setViewActionName(getAction(el, xview_action[lang], lang));
		a.setNewActionName(getAction(el, xnew_action[lang], lang));
		a.setAddActionName(getAction(el, xadd_action[lang], lang)); 
		a.setSaveActionName(getAction(el, xsave_action[lang], lang));
		a.setHideActionName(getAction(el, xhide_detail_action[lang], lang));
		a.setRemoveActionName(getAction(el, xremove_action[lang], lang));
		a.setRemoveSelectedActionName(getAction(el, xremove_selected_action[lang], lang));
		a.setOnSelectElementActionName(getAction(el, xon_select_element_action[lang], lang));
		a.setSearchListCondition(ParserUtil.getString(el, xsearch_list_condition[lang])); 
		fillRowStyles(el, a, lang);
		fillDetailActions(el, a, lang); 		
		fillListActions(el, a, lang);
		fillRowActions(el, a, lang);
		return a;
	}	
	
	private static void fillRowStyles(Element el, MetaCollectionView container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xrow_style[lang])) {
				container.addMetaRowStyle(createRowStyle(d, lang));
			}
		}
	}
	
	public static MetaRowStyle createRowStyle(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaRowStyle style = new MetaRowStyle();
		style.setStyle(el.getAttribute(xstyle[lang]));
		style.setProperty(el.getAttribute(xproperty[lang]));
		style.setValue(el.getAttribute(xvalue[lang]));
		return style;
	}	

	private static MetaPropertyView createMetaPropertyView(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaPropertyView a = new MetaPropertyView();		
		a.setPropertyName(el.getAttribute(xproperty[lang]));
		a.setLabel(el.getAttribute(xlabel[lang]));	
		a.setReadOnly(ParserUtil.getAttributeBoolean(el, xread_only[lang]));
		a.setReadOnlyOnCreate(a.isReadOnly()); // Not supported in XML component yet
		a.setEditor(el.getAttribute(xeditor[lang])); 
		String labelFormat = el.getAttribute(xlabel_format[lang]);
		if (XNORMAL[lang].equals(labelFormat)) a.setLabelFormat(MetaPropertyView.NORMAL_LABEL);
		else if (XSMALL[lang].equals(labelFormat)) a.setLabelFormat(MetaPropertyView.SMALL_LABEL);
		else if (XNO_LABEL[lang].equals(labelFormat)) a.setLabelFormat(MetaPropertyView.NO_LABEL);
		else throw new XavaException("invalid_label_format", labelFormat);				
		a.setDisplaySize(ParserUtil.getAttributeInt(el, xdisplay_size[lang])); // @Trifon
		a.setLabelStyle(el.getAttribute(xlabel_style[lang]));
		fillOnChangeAction(el, a, lang);	
		fillActions(el, a, lang);
		return a;
	}
	
	private static void fillOnChangeAction(Element el, MetaPropertyView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xon_change[lang]);
		int c = l.getLength();
		if (c==0) return;
		if (c>1) {			
			throw new XavaException("no_more_1_on_change_in_property_view");
		}
		Element elAction = (Element) l.item(0);
		container.setOnChangeActionClassName(elAction.getAttribute(xclass[lang]));
	}
	
	private static void fillOnChangeSearchAction(Element el, MetaReferenceView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xon_change_search[lang]);
		int c = l.getLength();
		if (c==0) return;
		if (c>1) {			
			throw new XavaException("no_more_1_on_change_search_in_reference_view");
		}
		Element elAction = (Element) l.item(0);
		container.setOnChangeSearchActionClassName(elAction.getAttribute(xclass[lang]));
	}
				
	private static void fillMetaSearchAction(Element el, MetaReferenceView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xsearch_action[lang]);
		int c = l.getLength();
		if (c==0) return;
		if (c>1) {			
			throw new XavaException("no_more_1_search_action_in_reference_view");
		}
		container.setMetaSearchAction(createMetaSearchAction(l.item(0), lang));
	}

	private static String getAction(Element el, String elementName, int lang) throws XavaException {
		NodeList l = el.getElementsByTagName(elementName);
		int c = l.getLength();
		if (c==0) return null;
		if (c>1) throw new XavaException("no_more_1_x_action_in_collection_view", elementName);
		return ((Element) l.item(0)).getAttribute(xaction[lang]);						
	}
		
	private static void fillDetailActions(Element el, MetaCollectionView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xdetail_action[lang]);
		int c = l.getLength();
		for (int i=0; i<c; i++) {
			String accion = ((Element) l.item(i)).getAttribute(xaction[lang]);		
			container.addActionDetailName(accion);
		}
	}
	
	private static void fillListActions(Element el, MetaCollectionView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xlist_action[lang]);
		int c = l.getLength();
		for (int i=0; i<c; i++) {
			String action = ((Element) l.item(i)).getAttribute(xaction[lang]);		
			container.addActionListName(action);
		}
	}

	private static void fillRowActions(Element el, MetaCollectionView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xrow_action[lang]);
		int c = l.getLength();
		for (int i=0; i<c; i++) {
			String action = ((Element) l.item(i)).getAttribute(xaction[lang]);		
			container.addActionRowName(action);
		}
	}
		
	private static void fillActions(Element el, MetaMemberView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xaction[lang]);
		int c = l.getLength();
		for (int i=0; i<c; i++) {
			String action = ((Element) l.item(i)).getAttribute(xaction[lang]);		
			container.addActionName(action);
			if (ParserUtil.getAttributeBoolean((Element) l.item(i), xalways_enabled[lang])) {
				container.addAlwaysEnabledActionName(action);
			}
		}
	}
	
	private static void fillMetaDescriptionsList(Element el, MetaReferenceView container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xdescriptions_list[lang]);
		int c = l.getLength();
		if (c==0) return;
		if (c>1) {			
			throw new XavaException("no_more_1_descriptions_list_in_reference_view");
		}
		container.setMetaDescriptionsList(createMetaDescriptionsList(l.item(0), lang));
	}
	
	
	private static MetaSearchAction createMetaSearchAction(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaSearchAction m = new MetaSearchAction();
		String className = el.getAttribute(xclass[lang]);
		if (!Is.emptyString(className)) {
			m.setClassName(className);
		}
		String action = el.getAttribute(xaction[lang]);
		if (!Is.emptyString(action)) {
			m.setActionName(action);
		}		
		fillMetaSetsOfSearchAction(el, m, lang);	
		return m;
	}
	
	private static MetaDescriptionsList createMetaDescriptionsList(Node n, int lang) throws XavaException {
		Element el = (Element) n;		
		MetaDescriptionsList m = new MetaDescriptionsList();
		String descriptionProperty = el.getAttribute(xdescription_property[lang]);
		if (!Is.emptyString(descriptionProperty)) {
			m.setDescriptionPropertyName(descriptionProperty);
		}
		String descriptionProperties = el.getAttribute(xdescription_properties[lang]);
		if (!Is.emptyString(descriptionProperties)) {
			m.setDescriptionPropertiesNames(descriptionProperties);
		}				
		m.setDepends(el.getAttribute(xdepends[lang]));
		m.setCondition(el.getAttribute(xcondition[lang]));
		m.setOrderByKey(ParserUtil.getAttributeBoolean(el, xorder_by_key[lang]));
		m.setOrder(el.getAttribute(xorder[lang]));
		m.setShowReferenceView(ParserUtil.getAttributeBoolean(el, xshow_reference_view[lang])); 
		m.setForTabs(el.getAttribute(xfor_tabs[lang]));
		m.setNotForTabs(el.getAttribute(xnot_for_tabs[lang]));
		
		String labelFormat = el.getAttribute(xlabel_format[lang]);
		if (XNORMAL[lang].equals(labelFormat)) m.setLabelFormat(MetaPropertyView.NORMAL_LABEL);
		else if (XSMALL[lang].equals(labelFormat)) m.setLabelFormat(MetaPropertyView.SMALL_LABEL);
		else if (XNO_LABEL[lang].equals(labelFormat)) m.setLabelFormat(MetaPropertyView.NO_LABEL);
		else throw new XavaException("invalid_label_format", labelFormat);
		m.setLabelStyle(el.getAttribute(xlabel_style[lang]));
		
		String filter = el.getAttribute(xfilter[lang]);
		if (!Is.emptyString(filter)) {
			MetaFilter metaFilter = new MetaFilter();
			metaFilter.setClassName(filter);
			m.setMetaFilter(metaFilter);
		}

		return m;
	}
	
	
	private static String getMediatorClass(Node n, int lang) throws XavaException {
		Element el = (Element) n;		 
		return el.getAttribute(xclass[lang]);		
	}	
	
	private static void fillMetaSetsOfSearchAction(Element el, MetaSearchAction container, int lang) throws XavaException {
		NodeList l = el.getElementsByTagName(xset[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elPoner = (Element) l.item(i);		
			container.addPropertyValue(
				elPoner.getAttribute(xproperty[lang]),
				elPoner.getAttribute(xvalue[lang]));
		}
	}
	
	private static void fillProperties(Element el, MetaView container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String tipo = d.getTagName();
			if (tipo.equals(xproperty[lang])) {
				container.addMetaViewProperty(ModelParser.createProperty(d, lang));
			}
		}
	}
	
	
				
}