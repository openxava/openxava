package org.openxava.controller.meta.xmlparse;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class ControllersParser extends ParserBase {
	
	private static Log log = LogFactory.getLog(ControllersParser.class); 
	
	private String context; // only for spanish version (with swing and web)
	private boolean duplicateWarning; 	
	
	public ControllersParser(String xmlFileURL, int language, boolean duplicateWarning) {
		super(xmlFileURL, language);
		this.duplicateWarning = duplicateWarning; 
	}
	
	public static void configureControllers(String context) throws XavaException {
		ControllersParser defaultParser = new ControllersParser("default-controllers.xml", ENGLISH, false);
		defaultParser.setContext(context);
		defaultParser.parse();
		ControllersParser enParser = new ControllersParser("controllers.xml", ENGLISH, true);
		enParser.setContext(context);
		enParser.parse();		
		ControllersParser esParser = new ControllersParser("controladores.xml", ESPANOL, true);
		esParser.setContext(context);
		esParser.parse();
	}
	
	private MetaController createController(Node n) throws XavaException {
		Element el = (Element) n;
		MetaController result = new MetaController();
		result.setName(el.getAttribute(xname[lang]));
		String context = el.getAttribute(xcontext[lang]);
		if (!isContextComun(context) && !context.equals(this.context)) return null;		
		result.setLabel(el.getAttribute(xlabel[lang]));
		result.setClassName(el.getAttribute(xclass[lang]));
		result.setImage(el.getAttribute(ximage[lang]));
		result.setIcon(el.getAttribute(xicon[lang]));
		fillControllerElements(el, result);
		fillExtends(el, result);		
		fillActions(el, result);
		fillSubcontroller(el, result);
		return result;
	}
	
	private boolean isContextComun(String context) {
		return "comun".equals(context) || Is.emptyString(context);		
	}

	private void createControllers() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xcontroller[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			MetaController ctrl= createController(l.item(i));
			if (ctrl != null) {
				if (duplicateWarning && MetaControllers.contains(ctrl.getName())) {
					log.warn(XavaResources.getString("trying_to_load_controller_twice_warning", ctrl.getName()));
				}
				MetaControllers._addMetaController(ctrl);
			}
		}
	}
	
	private void createMetaObjects() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xobject[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			MetaObject obj= createObject(l.item(i));
			if (obj != null) {
				MetaControllers.addMetaObject(obj);
			}
		}
	}
	
	private void createEnvironmentVar() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xenvironment_var[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element el = (Element) l.item(i);
			String name = el.getAttribute(xname[lang]);
			String value = el.getAttribute(xvalue[lang]);
			MetaControllers.addEnvironmentVariable(name, value);			
		}
	}
	
	
	protected void createObjects() throws XavaException {
		createEnvironmentVar();
		createMetaObjects();
		createControllers();
	}			
	
	private MetaAction createAction(Node n) throws XavaException {
		Element el = (Element) n;
		MetaAction result = new MetaAction();
		result.setName(el.getAttribute(xname[lang]));
		String mode = el.getAttribute(xmode[lang]);
		if ("ALL".equals(mode)) mode = "";
		result.setMode(mode);
		result.setLabel(el.getAttribute(xlabel[lang]));
		result.setDescription(el.getAttribute(xdescription[lang]));
		result.setMethod(el.getAttribute(xmethod[lang])); // only in spanish/swing version
		result.setImage(el.getAttribute(ximage[lang]));
		result.setIcon(el.getAttribute(xicon[lang])); 
		result.setKeystroke(el.getAttribute(xkeystroke[lang]));		
		result.setClassName(el.getAttribute(xclass[lang]));
		result.setHidden(getAttributeBoolean(el, xhidden[lang]));
		result.setOnInit(getAttributeBoolean(el, xon_init[lang]));
		result.setAfterEachRequest(getAttributeBoolean(el, xafter_each_request[lang]));
		result.setOnEachRequest(getAttributeBoolean(el, xon_each_request[lang]));
		result.setBeforeEachRequest(getAttributeBoolean(el, xbefore_each_request[lang]));
		result.setByDefault(toByDefault(el.getAttribute(xby_default[lang])));
		result.setTakesLong(getAttributeBoolean(el, xtakes_long[lang]));
		result.setConfirm(getAttributeBoolean(el, xconfirm[lang]));
		result.setInEachRow(getAttributeBoolean(el, xin_each_row[lang]));
		result.setProcessSelectedItems(getAttributeBoolean(el, xprocess_selected_items[lang]));
		result.setAvailableOnNew(getAttributeBoolean(el, xavailable_on_new[lang], true));
		result.setLosesChangedData(getAttributeBoolean(el, xloses_changed_data[lang], false));
		fillSet(el, result);		
		fillUseObject(el, result);
		return result;
	}
	
	private int toByDefault(String byDefault) throws XavaException {		
		if (xnever[lang].equals(byDefault)) return MetaAction.NEVER;
		if (xalmost_never[lang].equals(byDefault)) return MetaAction.ALMOST_NEVER;
		if (xif_possible[lang].equals(byDefault)) return MetaAction.IF_POSSIBLE;
		if (xalmost_always[lang].equals(byDefault)) return MetaAction.ALMOST_ALWAYS;
		if (xalways[lang].equals(byDefault)) return MetaAction.ALWAYS;
		return MetaAction.NEVER;
	}

	private void fillSet(Element el, MetaAction container)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xset[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container._addMetaSet(createSet(l.item(i)));
		}
	}
	
	private MetaSet createSet(Node n) throws XavaException {
		Element el = (Element) n;
		MetaSet a = new MetaSet();		
		a.setPropertyName(el.getAttribute(xproperty[lang]));
		a.setPropertyNameFrom(el.getAttribute(xfrom[lang]));
		a.setValue(el.getAttribute(xvalue[lang]));		
		return a;
	}
	
	private MetaObject createObject(Node n) throws XavaException {
		Element el = (Element) n;
		MetaObject result = new MetaObject();
		result.setName(el.getAttribute(xname[lang]));
		result.setClassName(el.getAttribute(xclass[lang]));
		result.setValue(el.getAttribute(xvalue[lang]));
		result.setGlobal(el.getAttribute(xscope[lang]).equals(xglobal[lang]));
		return result;
	}
	
	private MetaSubcontroller createSubcontroller(Node n) throws XavaException {
		Element el = (Element) n;
		MetaSubcontroller result = new MetaSubcontroller();
		result.setImage(el.getAttribute(ximage[lang]));
		result.setIcon(el.getAttribute(xicon[lang])); 
		result.setControllerName(el.getAttribute(xcontroller[lang]));
		String mode = el.getAttribute(xmode[lang]);
		if ("ALL".equals(mode)) mode = "";
		result.setMode(mode);
		return result;
	}
	
	private MetaUseObject createUseObject(Node n) throws XavaException {
		Element el = (Element) n;
		MetaUseObject result = new MetaUseObject();
		result.setName(el.getAttribute(xname[lang]));
		result.setActionProperty(el.getAttribute(xaction_property[lang]));
		return result;
	}
	
	private void fillExtends(Element el, MetaController container)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xextends[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elDepends = (Element) l.item(i);			
			container.addParent(elDepends.getAttribute(xcontroller[lang]), elDepends.getAttribute(xexcluded_actions[lang]));
		}
	}

	private void fillSubcontroller(Element el, MetaController container)throws XavaException {
		NodeList l = el.getElementsByTagName(xsubcontroller[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++){
			container.addMetaSubcontroller(createSubcontroller(l.item(i)));
		}
	}

	private void fillControllerElements(Element el, MetaController result){
		// actions and subcontroller order by occurrence
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++){
			Node m = l.item(i);
			String nodeName = m.getNodeName();
			if (xaction[lang].equals(nodeName) || xsubcontroller[lang].equals(nodeName)){
				if (nodeName.equals(xaction[lang])) {
					MetaAction action = createAction(m);
					action.setMetaController(result);
					result.addMetaControllerElement(action);
				}
				else if (nodeName.equals(xsubcontroller[lang])){
					MetaSubcontroller subcontroller = createSubcontroller(m);
					result.addMetaControllerElement(subcontroller);
				}
			}
		}
	}
	
	private void fillActions(Element el, MetaController container)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xaction[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaAction(createAction(l.item(i)));
		}
	}
	
	private void fillUseObject(Element el, MetaAction container)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xuse_object[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaUseObject(createUseObject(l.item(i)));
		}
	}
		
	public String getContext() {
		return context;
	}

	public void setContext(String string) {
		context = string;
	}

}