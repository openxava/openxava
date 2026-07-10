package org.openxava.application.meta.xmlparse;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
public class ApplicationParser extends ParserBase {

	private static Log log = LogFactory.getLog(ApplicationParser.class); 
	
	private ApplicationParser(String xmlFileURL, int language) {
		super(xmlFileURL, language);		
	}
	
	/**
	* @throws XavaException
	 */
	public static void configureApplications() {
		ApplicationParser enParser = new ApplicationParser("application.xml", ENGLISH);
		enParser.parse();		
		ApplicationParser esParser = new ApplicationParser("aplicacion.xml", ESPANOL);
		esParser.parse();
	}

	/**
	* @throws XavaException
	 */
	private void addApplication() {
		MetaApplication application = new MetaApplication();				
		application.setName(getRoot().getAttribute(xname[lang]));
		application.setLabel(getRoot().getAttribute(xlabel[lang]));
		addDefaultModule(application);
		addModules(application);
		MetaApplications._addMetaApplication(application);
	}
	
	/**
	* @throws XavaException
	 */
	private void addDefaultModule(MetaApplication application) {
		NodeList l = getRoot().getElementsByTagName(xdefault_module[lang]);
		int c = l.getLength();
		if (c > 0) {						
			NodeList controllersNodeList = ((Element) l.item(0)).getElementsByTagName(xcontroller[lang]); 			
			int controllersCount = controllersNodeList.getLength();
			for (int i = 0; i < controllersCount; i++) {
				Element elController = (Element) controllersNodeList.item(i);
				String s = elController.getAttribute(xname[lang]);
				application.addControllerForDefaultModule(s);
			}
						 
		}
		else { 
			application.addControllerForDefaultModule("Typical");
		}		
	}	
	
	/**
	* @throws XavaException
	 */
	private void addModules(MetaApplication application) {
		NodeList l = getRoot().getElementsByTagName(xmodule[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			MetaModule m = createModule(l.item(i));
			application.addMetaModule(m);
		}		
	}
		
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		addApplication();
	}
	
	/**
	* @throws XavaException
	 */
	private MetaModule createModule(Node n) {
		Element el = (Element) n;
		MetaModule m = new MetaModule();
		m.setName(el.getAttribute(xname[lang]));
		m.setFolder(Strings.change(el.getAttribute(xfolder[lang]), ".", "/"));
		m.setLabel(el.getAttribute(xlabel[lang]));
		m.setDescription(el.getAttribute(xdescription[lang]));
		m.setModelName(createModel(el));
		m.setViewName(createView(el));
		m.setTabName(createTab(el));
		m.setSwingViewClass(createSwingView(el));
		m.setWebViewURL(createWebView(el));
		m.setModeControllerName(createModeController(el));
		m.setMetaReport(createMetaReport(el));
		fillDoc(el, m);
		fillControllers(el, m);
		fillEnvironmentVar(el, m);		
		return m;
	}
	
	/**
	* @throws XavaException
	 */
	private String createModel(Element el) {
		NodeList l = el.getElementsByTagName(xmodel[lang]);
		if (l.getLength() > 0) {
			Element elModel = (Element) l.item(0);
			return elModel.getAttribute(xname[lang]);
		}
		return null;
	}
	
	/**
	* @throws XavaException
	 */
	private String createView(Element el) {
		NodeList l = el.getElementsByTagName(xview[lang]);
		if (l.getLength() > 0) {
			Element elModel = (Element) l.item(0);
			return elModel.getAttribute(xname[lang]);
		}
		return null;
	}
	
	/**
	* @throws XavaException
	 */
	private String createTab(Element el) {
		NodeList l = el.getElementsByTagName(xtab[lang]);
		if (l.getLength() > 0) {
			Element elModel = (Element) l.item(0);
			return elModel.getAttribute(xname[lang]);
		}
		return null;
	}
	
	/**
	* @throws XavaException
	 */
	private void fillDoc(Element el, MetaModule metaModule) {
		NodeList lDoc = el.getElementsByTagName(xdoc[lang]);
		if (lDoc.getLength() > 0) {
			Element elModel = (Element) lDoc.item(0);
			metaModule.setDocURL(elModel.getAttribute(xurl[lang]));
			String docLanguages = elModel.getAttribute(xlanguages[lang]);
			metaModule.setDocLanguages(docLanguages);
			log.warn("Module " + metaModule.getName() + " ignored. Doc modules are no longer supported"); // XavaResources.cannot be use at this point, thought it works produces collateral effects
			
		}
	}	
			
	/**
	* @throws XavaException
	 */
	private String createSwingView(Element el) {
		NodeList l = el.getElementsByTagName(xswing_view[lang]);
		if (l.getLength() > 0) {
			Element elSwingView = (Element) l.item(0);
			return elSwingView.getAttribute(xclass[lang]);
		}
		return null;
	}
	
	/**
	* @throws XavaException
	 */
	private String createWebView(Element el) {
		NodeList l = el.getElementsByTagName(xweb_view[lang]);
		if (l.getLength() > 0) {
			Element elWebView = (Element) l.item(0);
			return elWebView.getAttribute(xurl[lang]);
		}
		return null;
	}	
		
	/**
	* @throws XavaException
	 */
	private String createModeController(Element el) {
		NodeList l = el.getElementsByTagName(xmode_controller[lang]);
		if (l.getLength() > 0) {
			Element elModeController = (Element) l.item(0);
			return elModeController.getAttribute(xname[lang]);
		}
		return null;
	}	
	
	/**
	* @throws XavaException
	 */
	private MetaReport createMetaReport(Element el) {
		NodeList l = el.getElementsByTagName(xreport[lang]);
		if (l.getLength() > 0) {
			Element elReport = (Element) l.item(0);
			MetaReport metaReport = new MetaReport();
			metaReport.setModelName(elReport.getAttribute(xmodel[lang]));
			metaReport.setTabName(elReport.getAttribute(xtab[lang]));
			return metaReport;
		}
		return null;
	}
			
	/**
	* @throws XavaException
	 */
	private void fillControllers(Element el, MetaModule container) {
		NodeList l = el.getElementsByTagName(xcontroller[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elController = (Element) l.item(i);
			String s = elController.getAttribute(xname[lang]);
			container.addControllerName(s);
		}
	}
	
	/**
	* @throws XavaException
	 */
	private void fillEnvironmentVar(Element el, MetaModule container) {
		NodeList l = el.getElementsByTagName(xenvironment_var[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elVar = (Element) l.item(i);
			String name = elVar.getAttribute(xname[lang]);
			String value = elVar.getAttribute(xvalue[lang]);
			container.addEnvironmentVariable(name, value);
		}
	}
		
}