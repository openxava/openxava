package org.openxava.tests;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.logging.*;
import org.apache.pdfbox.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;
import org.htmlunit.*;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.html.*;
import org.htmlunit.javascript.*;
import org.htmlunit.javascript.host.event.*;
import org.openxava.application.meta.*;
import org.openxava.component.*;
import org.openxava.controller.meta.*;
import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.tab.impl.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;
import org.xml.sax.*;

import junit.framework.*;


/**
 * Base class for creating a junit test that runs against an OpenXava module. <p>
 * 
 * Look at
 * <a href="https://openxava.org/OpenXavaDoc/docs/testing_en.html">
 * 	Automated testing
 * </a> with OpenXava.
 * 
 * @author Javier Paniza
 */

abstract public class ModuleTestBase extends TestCase { 
	
	private final static String EDITABLE_SUFIX = "_EDITABLE_";
	private final static String ACTION_PREFIX = "action"; 
	
	private static Log log = LogFactory.getLog(ModuleTestBase.class);
	
	private static Properties xavaJunitProperties;
	private static boolean isDefaultLocaleSet = false;
	private static String defaultLocale;
	private static String host;
	private static String port;
	private static int loginFormIndex = -1;
	private static Collection excludedActions;  
	private static Collection ignoredActions;
	private static Map<String, BrowserVersion> defaultBrowserVersions = new HashMap<>(); 
	private static WebClient utilClient; 
	
	private String locale;
	private MetaModule metaModule;
	private MetaModel metaModel;
	private MetaView metaView;
	private MetaTab metaTab;	
	private String application;
	private String module;
	private WebClient client; 
	private HtmlPage page; 
	private HtmlForm form;  
	private String lastNotNotifiedPropertyName; 
	private String lastNotNotifiedPropertyValue;
	private String previousModule;
	private String popupPDFAsText; 
	private String [] popupPDFLines;
	private int popupPDFPageCount;
	
	static {		
		XSystem._setLogLevelFromJavaLoggingLevelOfXavaPreferences();
		Logger.getLogger("com.gargoylesoftware").setLevel(Level.SEVERE);
		DataSourceConnectionProvider.setUseHibernateConnection(true);
		System.setProperty("java.awt.headless", "true"); // To work with a headless Java
	}
	
	/**
	 * To test the specified module of the specified application. <p>
	 * 
	 * You can send <code>null</code> for <code>null</code> in such a case you
	 * must use {@link #changeModule} at the very first of your test methods.<br> 
	 */
	public ModuleTestBase(String nameTest, String application, String module) {
		super(nameTest);
		MetaControllers.setContext(MetaControllers.WEB);
		this.application = application;
		this.module = module;
	}

	/**
	 * To test the specified module of the default application. <p>
	 * 
	 * In this case the application is obtained from the <code>application</code>
	 * property in <code>xava-junit.properties</code> file.<br>
	 * You can send <code>null</code> for <code>null</code> in such a case you
	 * must use {@link #changeModule} at the very first of your test methods.<br> 
	 */	
	public ModuleTestBase(String nameTest, String module) {
		this(nameTest,
			getXavaJunitProperties().getProperty("application", MetaApplications.getApplicationsNames().iterator().next().toString()),
			module);
	}
	
	
	protected void setUp() throws Exception {
		locale = null;
		XPersistence.reset(); 
		XPersistence.setPersistenceUnit("junit");
		resetPreferences();
		resetModule();	
	}
	
	private void resetPreferences() throws Exception {
		getUtilClient().getPage("http://" + getHost() + ":" + getPort() + getContextPath() + "xava/resetPreferences.jsp?zxy=HOljkso83");
	}
	
	private static WebClient getUtilClient() throws Exception { 
		if (utilClient == null) {
			utilClient = new WebClient(); 
		}
		return utilClient;
	}
	
	protected void tearDown() throws Exception {
		XPersistence.commit();
		client.close(); 
		
		client = null;  
		page = null; 
		form = null; 
	}

	protected void login(String user, String password) throws Exception {
		String originalModule = module;
		selectModuleInPage("SignIn");
		try { 
			setValue("user", user); 
		}
		catch (ElementNotFoundException ex) {
			reload(); // Under high load sometime the page ajax loading takes some time, and this is the only way we found to solve the issue
			setValue("user", user);
		}
		setValue("password", password);
		execute("SignIn.signIn");
		assertNoErrors();
		selectModuleInPage(originalModule);	
	}
	
	private void setFormValueNoRefresh(String name, String value) throws Exception {
		setFormValue(name, value, false);
	}
	
	private void setFormValue(String name, String value) throws Exception {
		setFormValue(name, value, true);
	}
	
	private void setFormValue(String name, String value, boolean refreshIfNeeded) throws Exception {		
		setFormValue(name, value, refreshIfNeeded, true);
	}
	
	private void setFormValue(String name, String value, boolean refreshIfNeeded, boolean decorateName) throws Exception { 
		setFormValue(name, value, refreshIfNeeded, decorateName, false);
	}

	private void setFormValue(String name, String value, boolean refreshIfNeeded, boolean decorateName, boolean alwaysThrowChangedEvent) throws Exception { 
		boolean refreshNeeded = false;		
		String id = decorateName?decorateId(name):name;
		// Setting value to xava_previous_focus and xava_current_focus is for deceiving
		// the server to it will think that the focus is in the current
		// editor. The ideal way would be to put the real focus in editor, but HtmlUnit
		// throws the onchange events two times (when focus move, and when value changes)
		// in this case, which it's worse.
		try {
			getInputById("xava_previous_focus").setValue(id);
			getInputById("xava_current_focus").setValue("");			
		}
		catch (ElementNotFoundException ex) {
			log.warn(XavaResources.getString("impossible_set_focus_properties")); 
		}
		try {	
			HtmlInput input = getInputByName(id); 			
			assertNotDisable(name, input);
			if (input instanceof HtmlCheckBoxInput) {
				if ("true".equalsIgnoreCase(value) && !input.isChecked() ||
					"false".equalsIgnoreCase(value) && input.isChecked()) 
				{
					input.click();
					if (!Is.emptyString(input.getOnClickAttribute())) {
						refreshNeeded = true;
					}
				}				
			}
			else if (input instanceof HtmlRadioButtonInput) {
				setRadioButtonsValue(id, value);
			}
			else if (input instanceof HtmlHiddenInput) {
				input.setValue(value);
				DomElement previousElement = input.getPreviousElementSibling();
				if (previousElement instanceof HtmlInput && previousElement.hasAttribute("data-values")) { // It's an autocomplete
					HtmlInput autocomplete = (HtmlInput) previousElement;
					autocomplete.setValue("Some things"); // A trick to avoid that JavaScript reset the real value
					((HtmlInput) input.getNextElementSibling()).setValue("Some things"); // A trick to avoid that JavaScript reset the real value
				}				
			}
			else {
				input.setValue(value);				
			}
			if (hasOnChange(input) || alwaysThrowChangedEvent) {
				refreshNeeded = true;
				input.fireEvent(Event.TYPE_CHANGE);
			}
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			try {	
				HtmlSelect select = getSelectByName(id);
				
				assertNotDisable(name, select);
				select.setSelectedAttribute(value, true);
				select.blur(); 
				refreshNeeded = hasOnChange(select);
			}
			catch (org.htmlunit.ElementNotFoundException ex2) {
				HtmlTextArea textArea = getTextAreaByName(id); 
				assertNotDisable(name, textArea);
				String textAreaClass = textArea.getAttribute("class");
				if (textAreaClass != null && textAreaClass.contains("html-text")) {
					textArea.setText("<p>" + value + "</p>"); // To simulate the TinyCME behavior
				}
				else textArea.setText(value);
				
				refreshNeeded = hasOnChange(textArea); 
			}
		}		
		if (refreshIfNeeded && refreshNeeded) {			
			refreshPage();			
		}
	}
	
	private boolean hasOnChange(HtmlElement el) { 
		String cssClass = el.getAttribute("class");
		if (cssClass != null) {
			if (cssClass.contains("xava_onchange")) return true;
			if (XavaPreferences.getInstance().isFilterOnChange()) {
				if (cssClass.contains("xava_combo_condition_value") || cssClass.contains("xava_comparator")) return true;
			}
		}		
		if (el.hasEventHandlers("onchange")) return true; 
		DomNode parent = el.getParentNode();
		if (parent instanceof HtmlElement) return hasOnChange((HtmlElement) parent);
		return false;
	}
	
	private void assertNotDisable(String name, HtmlElement element) { 		
		assertTrue(XavaResources.getString("element_cannot_be_disabled", name), !is("disabled", element)); 
		assertTrue(XavaResources.getString("element_cannot_be_readonly", name), !is("readonly", element)); 
	}

	private boolean is(String attribute, HtmlElement element) {
		String value = element.getAttribute(attribute);		
		if (HtmlElement.ATTRIBUTE_NOT_DEFINED.equals(value)) return false;
		if (HtmlElement.ATTRIBUTE_VALUE_EMPTY.equals(value)) return true;
		return !"false".equalsIgnoreCase(value);		
	}
	
	private void setRadioButtonsValue(String name, String value) {
		for (Iterator it=getForm().getInputsByName(name).iterator(); it.hasNext(); ) {
			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) it.next();
			if (radioButton.getValue().equals(value)) {
				radioButton.setChecked(true);
				break;
			}
		}
	}
	
	private String getRadioButtonsValue(String name) {
		for (Iterator it=getForm().getInputsByName(name).iterator(); it.hasNext(); ) {
			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) it.next();
			if (radioButton.isChecked()) {
				return radioButton.getValue();
			}
		}
		return "";
	}
	
	private void refreshPage() throws Exception {
		resetForm(); 				
	}

	private String getFormValue(String name) throws Exception {
		String id = decorateId(name);
		try {			
			HtmlInput input = getInputByName(id);
			if (input instanceof HtmlRadioButtonInput) {
				return getRadioButtonsValue(id);
			}
			else if (input instanceof HtmlCheckBoxInput) {
				return Boolean.toString(input.isChecked());
			}
			else {				
				return input.getValue();
			}			
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			try {				
				HtmlSelect select = getSelectByName(id); 
				return ((HtmlOption )select.getSelectedOptions().get(0)).getValueAttribute();
			}
			catch (org.htmlunit.ElementNotFoundException ex2) {
				return getTextAreaByName(id).getText(); 
			}
		}
	}
	
	/*
	 * This is needed because a bug of HtmlUnit 2.6/2.7, where to get values from
	 * form fails in some circumstances. Though currently OX uses HtmlUnit 2.5
	 */
	private HtmlInput getInputByName(String name) { 
		HtmlElement el = page.getElementByName(name);
		if (el instanceof HtmlInput) {
			return (HtmlInput) el;
		}
		else {
			throw new org.htmlunit.ElementNotFoundException("input", "name", name);
		}
	}
	
	/*
	 * This is needed because a bug of HtmlUnit 2.6/2.7, where to get values from
	 * form fails in some circumstances. Though currently OX uses HtmlUnit 2.5
	 */
	private HtmlSelect getSelectByName(String name) {  
		HtmlElement el = page.getElementByName(name);
		if (el instanceof HtmlSelect) {
			return (HtmlSelect) el;
		}
		else {
			throw new org.htmlunit.ElementNotFoundException("select", "name", name);
		}
	}
	
	/*
	 * This is needed because a bug of HtmlUnit 2.6/2.7, where to get values from
	 * form fails in some circumstances. Though currently OX uses HtmlUnit 2.5
	 */
	private HtmlTextArea getTextAreaByName(String name) {  
		HtmlElement el = page.getElementByName(name);
		if (el instanceof HtmlTextArea) {
			return (HtmlTextArea) el;
		}
		else {
			throw new org.htmlunit.ElementNotFoundException("text area", "name", name);
		}
	}
	
	

	private String [] getFormValues(String name) {		
		List elements = getForm().getInputsByName(name);
		if (elements.isEmpty()) {
			elements = getForm().getSelectByName(name).getSelectedOptions();			
		}
		Collection<String> values = new ArrayList<String>();
		for (Iterator it = elements.iterator(); it.hasNext(); ) {
			HtmlElement el = (HtmlElement) it.next();
			if (el instanceof HtmlCheckBoxInput) {
				HtmlCheckBoxInput checkBox = (HtmlCheckBoxInput) el; 
				if (checkBox.isChecked()) {
					values.add(checkBox.getValue());
				}
			}
			else if (el instanceof HtmlInput) {
				HtmlInput input = (HtmlInput) el; 
				values.add(input.getValue());
			}
			else {
				values.add(el.getAttribute("value"));
			}			
		}
		return XCollections.toStringArray(values);
	}
	
	private boolean isOneMultipleSelect(List elements) { 
		if (elements.size() != 1) return false;
		Object element = elements.get(0);
		if (!(element instanceof HtmlSelect)) return false;
		return ((HtmlSelect) element).isMultipleSelectEnabled();
	}

	private void setFormValues(String name, String [] values) throws Exception {		
		List elements = new ArrayList(page.getElementsByName(name));		
		boolean refreshPage = false;
		if (isOneMultipleSelect(elements)) { 
			HtmlSelect select = (HtmlSelect) elements.get(0);
			unselectOptions(select);
			for (int i = 0; i < values.length; i++) {
				select.setSelectedAttribute(values[i], true);
			}			
			if (hasOnChange(select)) { 
				refreshPage = true;
			}			
		}
		else {
			int i=0;
			Collection<String> valuesCollection = Arrays.asList(values);
			for (Iterator it = elements.iterator(); it.hasNext(); i++) {
				Object element = it.next();
				if (element instanceof HtmlCheckBoxInput) {
					HtmlCheckBoxInput checkbox = (HtmlCheckBoxInput) element;
					String value = checkbox.getValue();
					checkbox.setChecked(valuesCollection.contains(value));
					if (hasOnChange(checkbox)) { 
						refreshPage = true;
					}
				}
				else if (element instanceof HtmlInput) { 
					HtmlInput input = (HtmlInput) element;
					input.setValue(values[i]);
					if (hasOnChange(input)) { 
						refreshPage = true;
					}
				}
				else if (element instanceof HtmlSelect) { 				
					HtmlSelect select = (HtmlSelect) element;		
					select.setSelectedAttribute(values[i], true);			
					if (hasOnChange(select)) {
						refreshPage = true;
					}
				}
			}
		}
		if (refreshPage) refreshPage();
	}
			

	private void unselectOptions(HtmlSelect select) { 
		for (Iterator it = select.getOptions().iterator(); it.hasNext(); ) {
			HtmlOption option = (HtmlOption) it.next();
			option.setSelected(false);
		}
	}
				
	/**
	 * User logout. <p>
	 */
	protected void logout() throws Exception {
		page = ((HtmlAnchor)getHtmlPage().getByXPath("//a[contains(@href, '" + getContextPath() + "naviox/signOut.jsp')]").get(0)).click(); 
	}
	
	/**
	 * Like close navigator, open again, and reexecute the module.
	 */
	protected void resetModule() throws Exception {
		client = createWebClient(); 
		
		if (getLocale() != null) {
			client.addRequestHeader("Accept-Language", getLocale());			
			Locale.setDefault(new Locale(getLocale(), ""));
		}
		if (this.module != null) { 		
			page = (HtmlPage) client.getPage(getModuleURL()); 
			if (!getMetaModule().isDoc()) {
				resetForm();
			}
		}			
		if (page != null) { // Null when no module is specified to start the test, maybe afterwards changeModule will be used 
			restorePage();
		}
	}
	
	/**
	 * The browser emulation used for the test. <p>
	 * 
	 * If you overwrite this method maybe some methods of ModuleTestBase 
	 * would not work correctly. Usually you overwrite it to test using 
	 * directly the HtmlUnit API.<br/>
	 * 
	 * The use of <b>this method is discouraged</b> because binds your test
	 * to a HTML implementation.
	 * Before to use this method look for another more abstract method
	 * in this class.
	 * 
	 * @since 5.5
	 */
	protected BrowserVersion getBrowserVersion() throws Exception { 
		return getDefaultBrowserVersion();
	}
	
	private BrowserVersion getDefaultBrowserVersion() throws Exception {
		BrowserVersion defaultBrowserVersion = defaultBrowserVersions.get(getLocale());
		if (defaultBrowserVersion == null) {
			defaultBrowserVersion = createBrowserVersion(getLocale());
			defaultBrowserVersions.put(getLocale(), defaultBrowserVersion);
		}
		return defaultBrowserVersion;
	}
	
	private static BrowserVersion createBrowserVersion(String locale) throws Exception { 
		return new BrowserVersion.BrowserVersionBuilder(BrowserVersion.BEST_SUPPORTED)
			.setUserAgent(BrowserVersion.BEST_SUPPORTED.getUserAgent() + " HtmlUnit")
			.setBrowserLanguage(locale) 
			.build();
	}

	/**
	 * Like clicking on the reload button of the browser.
	 * 
	 * @since 4.1.2
	 */
	protected void reload() throws Exception {
		page = (HtmlPage) page.refresh();
		if (!getMetaModule().isDoc()) {
			resetForm();
		}
	}
		
	protected void selectModuleInPage(String module) throws Exception { 
		changeModule(application, module, false);
	}

	protected void changeModule(String module) throws Exception {
		changeModule(this.application, module);
	}
	
	protected void changeModule(String application, String module) throws Exception {
		changeModule(application, module, true);
	}
	
	private void changeModule(String application, String module, boolean reloadPage) throws Exception { 
		this.application = application;
		this.module = module;
		metaModule = null;
		metaModel = null;
		metaView = null;
		metaTab = null;				
		if (reloadPage) page = (HtmlPage) client.getPage(getModuleURL()); 
		resetForm();	
		restorePage(); 
	}
	
		
	protected String getModuleURL() throws XavaException { 
		return "http://" + getHost() + ":" + getPort() + getContextPath() + "modules/" + module + "?modulesLimit=0"; 
	}
	
	/**
	 * The model used as prefix to method like getValue, assertValue, etc. <p>
	 * 
	 * By default is the model of module.
	 * The effect of the this setting is only for the life of one test.
	 * 
	 * @deprecated Now the model is deduced automatically 
	 */
	protected void setModel(String defaultModel) { 
	}
	
	/**
	 * Put the model of the module as the default model of the test. <p>
	 * 
	 * This is the default setting, hence this method is called
	 * to restore the original setting.	 
	 * 
	 * @deprecated Now this is done automatically
	 */
	protected void setModelToModuleSetting() { 		
	}
	
	/**
	 * Execute the action clicking in the link or button.
	 */
	protected void execute(String action) throws Exception {
		waitUntilPageIsLoaded(); // Needed when a setValue() before throws an onchange action (not easily reproducible, depend on performance)
		throwChangeOfLastNotNotifiedProperty();		
		if (page.getElementsByName(Ids.decorate(application, module, ACTION_PREFIX + "." + action)).size() > 1) { // Action of list/collection
			execute(action, null);
			return;
		}	

		HtmlElement element  = getElementById(action);
		openPopupIfActionInSubcontroler(element);
		showListCustomizationControlsIfElementHidden(element);
		element.click();
		resetForm(); 		
		restorePage(); 		
	}
	
	private void showListCustomizationControlsIfElementHidden(HtmlElement element) throws Exception { 
		if (!element.isDisplayed()) {
			for (HtmlElement showLink: getHtmlPage().getBody().getElementsByAttribute("a", "class", "xava_customize_list ox-image-link")) {
				showLink.click();
			}
		}
	}

	private void openPopupIfActionInSubcontroler(HtmlElement actionElement) throws Exception {
		HtmlElement subcontroller = getAncestorWithClass(actionElement, "ox-subcontroller");
		if (subcontroller == null) return;
		if (subcontroller.isDisplayed()) return; 
		HtmlElement parent = (HtmlElement) subcontroller.getParentNode();
		HtmlAnchor link = parent.getOneHtmlElementByAttribute("a", "class", "xava_subcontroller");
		HtmlElement icon = link.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		icon.click();
	}

	private HtmlElement getAncestorWithClass(DomNode element, String cssClass) { 
		DomNode parent = element.getParentNode();
		if (parent == null) return null;
		if (parent instanceof HtmlElement) {
			HtmlElement parentElement = (HtmlElement) parent;
			if (cssClass.equals(parentElement.getAttribute("class"))) {
				return parentElement;
			}
		}
		return getAncestorWithClass(parent, cssClass);
	}

	/** 
	 * Wait until the current AJAX request is done and update the page if needed. <p>
	 * 
	 * Usually is only needed to call this method when you use directly HtmlUnit APIs.
	 * @since 5.7
	 */
	protected void waitAJAX() throws Exception {
		resetForm();
		restorePage();
	}
	
	private void throwChangeOfLastNotNotifiedProperty() throws Exception {		
		if (lastNotNotifiedPropertyName != null) {
			setFormValueNoRefresh(lastNotNotifiedPropertyName, lastNotNotifiedPropertyValue);
			lastNotNotifiedPropertyName = null;
			lastNotNotifiedPropertyValue = null;
		}
	}
										 	
	private void waitUntilPageIsLoaded() throws Exception { 		
		client.waitForBackgroundJavaScriptStartingBefore(10000);		
		if (getLoadedParts().endsWith("ERROR")) {
			fail(XavaResources.getString("ajax_loading_parts_error"));
		}
	}
	
	private void assertSystemError() { 
		Object systemError = page.getElementById("xava_system_error"); 
		if (systemError != null) {
			fail(((HtmlElement) systemError).asNormalizedText()); 
		}
	}

	private HtmlElement getElementById(String id) {
		return page.getHtmlElementById(decorateId(id));		
	}
	
	private HtmlInput getInputById(String id) { 
		return (HtmlInput) getElementById(id);
	}
	
	/**
	 * Decorate the name to produced an unique identifier as the used by
	 * OX for HTML elements.
	 */
	protected String decorateId(String name) { 
		name = Strings.change(name, ".KEY", DescriptionsLists.COMPOSITE_KEY_SUFFIX);
		return Ids.decorate(application, module, name);
	}

	protected void assertFocusOn(String name) throws Exception {
		String expectedFocusProperty = decorateId(name);			
		DomElement element = page.getFocusedElement(); 
		String focusProperty = element==null?null:element.getAttribute("name");
		if (focusProperty != null && focusProperty.endsWith("__CONTROL__")) { 
			focusProperty = focusProperty.replaceFirst("__CONTROL__$", "");
		}
		assertEquals(XavaResources.getString("focus_in_unexpected_place"), expectedFocusProperty, focusProperty);		
	}
	
	/**
	 * Executes an action simulating a real click in the button or link. <p>
	 * 
	 * In addition to execute the action this method throws the corresponding 
	 * events of clicking the real link, like the focus lost of the current
	 * editor, for example.<br/>
	 * 
	 * @deprecated Since 7.2 the regular execute() uses clicking always, so you can use execute() instead
	 */
	@Deprecated
	protected void executeClicking(String action, String arguments) throws Exception {
		execute(action, arguments);
	}
	
	private HtmlElement getElementForAction(String action, String arguments) {
		for (Iterator it = page.getAnchors().iterator(); it.hasNext(); ) {			
			HtmlAnchor anchor = (HtmlAnchor) it.next();
			if (!module.equals(anchor.getAttribute("data-module"))) continue;
			if (action.equals(anchor.getAttribute("data-action"))) {
				if (arguments == null && Is.emptyString(anchor.getAttribute("data-argv"))) return anchor; // 'ReferenceSearch.choose'
				if (arguments == null) continue;
				if (arguments.equals(anchor.getAttribute("data-argv"))) return anchor; // 'List.viewDetail', 'row=0'
				if (("," + arguments).equals(anchor.getAttribute("data-argv"))) return anchor; // 'List.filter', ',collection=deliveryPlaces'
			}
		}		
		
		// For Cards
		if (arguments != null && arguments.startsWith("row=")) {
			String row= arguments.substring(4);
			for (HtmlElement el: page.getBody().getElementsByAttribute("div", "data-row", row)) { 
				if (action.equals(el.getAttribute("data-action"))) return el;
			}
		}
		
		// Bottom buttons
		List<HtmlElement> buttons = page.getBody().getElementsByAttribute("input", "data-action", action);
		if (!buttons.isEmpty()) return buttons.get(0);
		
		return null;
	}
	
	protected void execute(String action, String arguments) throws Exception { 
		throwChangeOfLastNotNotifiedProperty();
		HtmlElement element = null;
		element = getElementForAction(action, arguments);
		if (element != null) {
			openPopupIfActionInSubcontroler(element);
			element.click();
			resetForm(); 
		}
		else { 
			if (isReferenceActionWithObsoleteStyle(action, arguments)) {		
				log.warn(XavaResources.getString("keyProperty_obsolete_style")); 
				execute(action, refineArgumentsForReferenceActionWithObsoleteStyle(arguments));
				return;
			}
			fail(XavaResources.getString("clickable_not_found", action));
		}			

		restorePage();		
	}
	
	private void restorePage() throws Exception { 			
		Page newPage = client.getWebWindows().get(0).getEnclosedPage();
		
		page = newPage instanceof HtmlPage?(HtmlPage) newPage:null;

		if (pageNotLoaded()) {
			newPage = (HtmlPage) client.getPage(getModuleURL());
			page = newPage instanceof HtmlPage?(HtmlPage) newPage:null;
			if (pageLoaded()) {
				resetForm();
			}
		}		
		
		popupPDFAsText = null; 
		popupPDFLines = null; 
		popupPDFPageCount = -1;
	}
	
	private boolean pageLoaded() throws Exception { 
		return !pageNotLoaded();
	}
		
	private boolean pageNotLoaded() throws Exception { 
		if (page == null) return true;
		if (isXavaPage()) return false;
		return page.asNormalizedText().contains("HTTP 404"); 
	}
	
	private boolean isXavaPage() { 
		try {
			((HtmlPage) page).getHtmlElementById(Ids.decorate(application, module, "loaded_parts"));
			return true;
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			return false;
		}
	}

	private String refineArgumentsForReferenceActionWithObsoleteStyle(String arguments) { 
		int idx1 = arguments.indexOf("keyProperty=xava.");
		int idx2 = arguments.indexOf(".", idx1 + 17);		
		return arguments.substring(0, idx1 + 12) + arguments.substring(idx2 + 1);
	}
	
	private boolean isReferenceActionWithObsoleteStyle(String action, String arguments) {  
		return action.startsWith("Reference.") && arguments.indexOf("keyProperty=xava.") >= 0;
	}
	
	protected void executeDefaultAction() throws Exception {
 		HtmlButton button = getForm().getButtonByName("xava.DEFAULT_ACTION");
 		page = (HtmlPage) button.click();
		resetForm();
		restorePage(); 
	}
	
	protected void assertExists(String name) throws Exception {		 
		if (!hasElementByName(name) && !existsCollection(name)) {
			fail(XavaResources.getString("must_exist", name)); 
		}
	}

	protected void assertNotExists(String name) throws Exception { 		 		
		if (hasElementByName(name) || existsCollection(name)) {
			fail(XavaResources.getString("must_not_exist", name)); 
		} 
	}
	
	private boolean existsCollection(String collection) {
		return hasElementById("collection_" + collection + ".");
	}
	
	/**
	 * In the case of combo (descriptionsEditor.jsp) (or his read only version)
	 * the value that is visualized to user.
	 * @return
	 */
	protected String getDescriptionValue(String name) throws Exception {		
		return getFormValue(decorateId(name) + "__DESCRIPTION__"); 
	}
		
	protected String getValue(String name) throws Exception {		
		return getFormValue(decorateId(name)).trim(); 
	}
	
	/**
	 * For properties with multiple values
	 */
	protected String [] getValues(String name) throws Exception {		
		return getFormValues(decorateId(name));
	}	
	
	protected String getLabel(String name) throws Exception { 
		try {
			return getElementById("label_" + name).asNormalizedText().trim(); 
		}
		catch (ElementNotFoundException ex) {		
			return getElementById("frame_" + name + "header").getParentNode()
					.asNormalizedText().replaceFirst("\\([0-9]+\\)$", "").trim();
		}
	}
	
	/**
	 * In case we does not work with main view.
	 * 
	 * @deprecated  The model is automatically deduced, so you can use just getValue(String name)
	 */
	protected String getValue(String model, String name) throws Exception {		
		return getFormValue(name);  
	}
		
	/**
	 * Only for debug.
	 */
	protected void printHtml() throws Exception {
		log.debug(getHtml());		
	}
	
	/**
	 * Util for web applications, but using it make the test web dependent. 
	 * 
	 * @param type text/html, application/pdf, etc.
	 */
	protected void assertContentType(String type) {
		assertEquals(XavaResources.getString("content_type_not_match"), type, page.getWebResponse().getContentType());
	}
	
	/**
	 * Util for web applications, but using it makes the test web dependent. 
	 * 
	 * @param popup  The window number
	 * @param type text/html, application/pdf, etc.
	 * @since 4.3
	 */
	protected void assertContentTypeForPopup(int popup, String type) {
		for (int i=0; !type.equals(getPopupResponse(popup).getContentType()) && i<20; i++) {
			try { Thread.sleep(500); } catch (Exception ex) { }
		}
		assertEquals(XavaResources.getString("content_type_not_match"), type, getPopupResponse(popup).getContentType());
	}	
	
	/**
	 * Util for web applications, but using it makes the test web dependent. 
	 * 
	 * @param type text/html, application/pdf, etc.
	 */
	protected void assertContentTypeForPopup(String type) {
		assertContentTypeForPopup(-1, type); 
	}	
	
	/**
	 * Response for a popup window
	 * @return
	 */
	private WebResponse getPopupResponse(int popup) { 
		return getPopupPage(popup).getWebResponse();		
	}
	
	/**
	 * Page for a popup window
	 * 
	 * @return
	 */
	private Page getPopupPage(int popup) { 
		List windows = client.getWebWindows();		
		if (windows.size() < 2) {
			fail(XavaResources.getString("popup_window_not_found"));
		}		
		if (popup < 0) { 
			for (int i=windows.size() - 1; i > 0; i--) {
				Page page = ((WebWindow) windows.get(i)).getEnclosedPage();
				if (page != null) return page;
			}
		}
		else {
			Page page = ((WebWindow) windows.get(popup + 1)).getEnclosedPage();
			if (page != null) return page;
		}
		fail(XavaResources.getString("popup_window_not_found"));
		return null;
	}	
	
	/**
	 * 
	 * 
	 * @since 4.3
	 */
	protected void assertPopupCount(int count) throws Exception { 
		List windows = client.getWebWindows();
		assertEquals(XavaResources.getString("unexpected_popup_count"), count, windows.size() - 1); 
	}
	
	protected void assertNoPopup() throws Exception {
		List windows = client.getWebWindows();
		assertTrue(XavaResources.getString("unexpected_popup"), windows.size() < 2);
	}
	
	/**
	 * Current HTML code.
	 * <p>
	 * It is not very advisable because this will cause dependency
	 * to HTML and it will be difficult migrate to another presentation technology.
	 */
	protected String getHtml() throws Exception {
		return page.asXml()	
			.replaceAll("&apos;", "'") 
			.replaceAll("&lt;", "<")
			.replaceAll("&gt;", ">")	
			.replaceAll("&quot;", "\"");
	}
	
	/**
	 * The text of the response.
	 */
	protected String getText() throws IOException {
		return page.asNormalizedText();
	}

	/**
	 * The text of the response for popup window.
	 */
	protected String getPopupText() throws Exception {
		return getPopupPage(-1).getWebResponse().getContentAsString();
	}
	
	/**
	 * The content of the response for popup window.
	 * @since 6.4.2
	 */
	protected InputStream getPopupContentAsStream() throws Exception {  
		return getPopupPage(-1).getWebResponse().getContentAsStream();
	}	
	
	/**
	 * The content of the PDF in the popup window as text.
	 * 
	 * @since 4.6
	 */
	protected String getPopupPDFAsText() throws Exception { 
		if (popupPDFAsText == null) {
			InputStream is = getPopupPage(-1).getWebResponse().getContentAsStream();
			byte[] pdfBytes = convertInputStreamToByteArray(is);
			PDDocument doc = Loader.loadPDF(pdfBytes);
			popupPDFAsText = new PDFTextStripper().getText(doc);
			doc.close();
			is.close();
		}
		return popupPDFAsText;
	}
	
	private byte[] convertInputStreamToByteArray(InputStream inputStream) throws Exception {
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[16384];
	    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }
	    buffer.flush();
	    return buffer.toByteArray();
	}
	
	/**
	 * Only for debug.
	 * 
	 * @since 4.6
	 */
	protected void printPopupPDFAsText() throws Exception { 
		log.debug(getPopupPDFAsText());		
	}
	
	/**
	 * Number of pages of the PDF in the popup window
	 *  
	 * @since 5.0 
	 */
	protected int getPopupPDFPageCount() throws Exception {
		if(popupPDFPageCount == -1) {
			InputStream is = getPopupPage(-1).getWebResponse().getContentAsStream();
			byte[] pdfBytes = convertInputStreamToByteArray(is);
			PDDocument doc = Loader.loadPDF(pdfBytes);
			popupPDFPageCount = doc.getNumberOfPages();
			doc.close();
			is.close();
		}
		return popupPDFPageCount;
	}
	
	/**
	 * The specified line as text of PDF in the popup window.
	 * 
	 * @since 4.6
	 */
	protected String getPopupPDFLine(int lineNumber) throws Exception { 		  	
		return getPopupPDFLines()[lineNumber];
	}

	/**
	 * Assert the value for the specified line as text of PDF in the popup window.
	 * 
	 * @since 4.6
	 */	
	protected void assertPopupPDFLine(int lineNumber, String expectedContent) throws Exception { 		  		
		assertEquals(XavaResources.getString("pdf_line_not_match"), expectedContent, getPopupPDFLine(lineNumber)); 
	}
	
	/**
	 * Assert the count of lines of the PDF in the popup window.
	 * 
	 * @since 4.6
	 */	
	protected void assertPopupPDFLinesCount(int expectedCount) throws Exception { 		  		
		assertEquals(XavaResources.getString("pdf_lines_count_not_match"), expectedCount, getPopupPDFLinesCount()); 
	}
	
	/**
	 * The count of lines of the PDF in the popup window.
	 * 
	 * @since 4.6
	 */	
	protected int getPopupPDFLinesCount() throws Exception { 		  				  		
		return getPopupPDFLines().length;
	}
	
	private String [] getPopupPDFLines() throws Exception { 		  		
		if (popupPDFLines == null) popupPDFLines = getPopupPDFAsText().split("\\r?\\n");  		
		return popupPDFLines;
	}	
	
	
	/**
	 * @param Varargs since 4m5.
	 */
	protected void setConditionValues(String ... values) throws Exception { 
		setCollectionCondition("conditionValue", values);
	}
	
	/**
	 * @since 4.4
	 */
	protected void setConditionValuesTo(String ... values) throws Exception { 
		setCollectionCondition("conditionValueTo", values);
	}

	/**
	 * To be used from Groovy, that does not work with setConditionValues(String ... values).
	 * 
	 * @since 4.0.1
	 */
	protected void setConditionValues(List values) throws Exception {
		String [] avalues = new String[values.size()];
		values.toArray(avalues);
		setCollectionCondition("conditionValue", avalues);
	}
	
	/**
	 * To be used from Groovy, that does not work with setConditionComparators(String ... values).
	 * 
	 * @since 4.0.1
	 */
	protected void setConditionComparators(List values) throws Exception {
		String [] avalues = new String[values.size()];
		values.toArray(avalues);
		setConditionComparators(avalues);
	}
	
	/**
	 * @since 5.6
	 */
	protected void clearCondition() throws Exception {   
		page.executeJavaScript("openxava.clearCondition('" + application + "', '" + module + "', '')"); 
		waitAJAX(); 
	}
	
	private void setCollectionCondition(String id, String[] values) throws Exception {
		for (int i=0; i<values.length; i++) {
			try {
				setFormValue(id + "." + i, values[i]);  
			}
			catch (org.htmlunit.ElementNotFoundException ex) {
				break;
			}
		}
	}

	/**
	 * 
	 * @param values  varargs since 4m5 
	 */
	protected void setConditionComparators(String ... values) throws Exception { 
		filterConditionComparators(values);
		setCollectionCondition("conditionComparator", values); 
	}
	
	protected void setConditionValues(String collection, String [] values) throws Exception { 
		String collectionId = Tab.COLLECTION_PREFIX + Strings.change(collection, ".", "_") + "_conditionValue";
		setCollectionCondition(collectionId, values);
	}
	
	private boolean conditionValuesEquals(String[] expectedValues, String[] currentValues) {
		int i=0;
		for (i=0; i<expectedValues.length; i++) {
			if (i >= currentValues.length) return emptyFrom(expectedValues, i);
			if (!Is.equal(expectedValues[i], currentValues[i])) return false;			
		}
		return emptyFrom(currentValues, i);
	}

	private boolean emptyFrom(String[] values, int initial) {
		for (int i=initial; i<values.length; i++) {
			if (!Is.emptyString(values[i])) return false;
		}
		return true;
	}

	protected void setConditionComparators(String collection, String [] values) throws Exception { 
		filterConditionComparators(values);
		setCollectionCondition(Tab.COLLECTION_PREFIX + collection + "_conditionComparator", values);
	}	
	
	private void filterConditionComparators(String[] values) {
		for (int i = 0; i < values.length; i++) {
			if ("=".equals(values[i]) || "eq".equals(values[i])) values[i] = Tab.EQ_COMPARATOR;
			if ("<>".equals(values[i]) || "ne".equals(values[i])) values[i] = Tab.NE_COMPARATOR;
			if (">=".equals(values[i]) || "ge".equals(values[i])) values[i] = Tab.GE_COMPARATOR;
			if ("<=".equals(values[i]) || "le".equals(values[i])) values[i] = Tab.LE_COMPARATOR;
			if (">".equals(values[i]) || "gt".equals(values[i])) values[i] = Tab.GT_COMPARATOR;
			if ("<".equals(values[i]) || "lt".equals(values[i])) values[i] = Tab.LT_COMPARATOR;
		}		
	}

	protected void setValueNotNotify(String name, String value) throws Exception {
		String qualifiedName = decorateId(name); 
		HtmlInput input = getForm().getInputByName(qualifiedName);
		input.setValue(value); 
		lastNotNotifiedPropertyName = qualifiedName; 
		lastNotNotifiedPropertyValue = value; 
	}
	
	protected void setValue(String name, String value) throws Exception {
		setFormValue(decorateId(name), value);
	}	
		
	/**
	 * For multiple values properties
	 */
	protected void setValues(String name, String [] values) throws Exception {					
		setFormValues(decorateId(name), values); 
	}
	
	
	protected void setFileValue(String name, String filePath) throws Exception {
		setFormValue(name, filePath, true, false); 			
	}
	
	/**
	 * In case we do not work with main view. <p>
	 * 
	 * @deprecated  Now model is deduced automatically, so you can use setValue(String model, String value)
	 */
	protected void setValue(String model, String name, String value) throws Exception {  
		setValue(name, value); 
	}
	
	protected void assertLabel(String name, String expectedLabel) throws Exception {		
		assertEquals(XavaResources.getString("unexpected_label", name), expectedLabel, getLabel(name));		
	}
	
	/** @since 6.4 */
	protected void assertLabel(int sectionIndex, String expectedLabel) throws Exception{ 
		assertLabel("xava_view_section" + sectionIndex + "_sectionName", expectedLabel);
	}
	
	protected void assertNoLabel(String name) throws Exception{
		try{
			getLabel(name);
			fail(XavaResources.getString("label_found_in_ui", name));
		}
		catch(ElementNotFoundException ex){
		}
	}
	
	protected void assertValue(String name, String value) throws Exception {		
		assertEquals(XavaResources.getString("unexpected_value", name), value, getValue(name));		
	}

	/**
	 * For multiple values property.
	 */
	protected void assertValues(String name, String [] values) throws Exception {				
		assertEquals(XavaResources.getString("unexpected_value", name), Arrays.asList(values), Arrays.asList(getValues(name)));		
	}
				
	protected void assertValueIgnoringCase(String name, String value) throws Exception {		
		assertTrue(XavaResources.getString("unexpected_value", name), value.equalsIgnoreCase(getValue(name)));		
	}
		
	protected void assertValue(String model, String name, String value) throws Exception {
		assertEquals(XavaResources.getString("unexpected_value_in_model", name, model), value, getValue(model, name));
	}
	
	protected void assertDescriptionValue(String name, String value) throws Exception {
		assertEquals(XavaResources.getString("unexpected_description", name), value, getDescriptionValue(name));		
	}

	protected boolean existsAction(String action) throws Exception {		
		return getActions().contains(action);
	}
	
	protected void assertAction(String action) throws Exception {		
		assertTrue(XavaResources.getString("action_not_found_in_ui", action), getActions().contains(action));
	}
	
	protected void assertAction(String action, String arguments) throws Exception { 		
		assertTrue(XavaResources.getString("action_with_arguments_not_found_in_ui", action, arguments), getElementForAction(action, arguments) != null); 
	}
	
	protected void assertNoAction(String action) throws Exception {
		assertTrue(XavaResources.getString("action_found_in_ui", action), !getActions().contains(action));
	}
	
	protected void assertNoAction(String action, String arguments) throws Exception { 		
		assertTrue(XavaResources.getString("action_with_arguments_found_in_ui", action, arguments), getElementForAction(action, arguments) == null); 
	}
	
	private Collection getActions() throws Exception { 
		String dialog = getTopDialog();
		if (dialog == null) return getActions(getElementById("core"));
		return getActions(getElementById(dialog));		
	}	
	
	private Collection getActions(HtmlElement el) { 		
		Collection hiddens = el.getElementsByAttribute("input", "type", "hidden");				
		Set actions = new HashSet();		
		for (Iterator it = hiddens.iterator(); it.hasNext(); ) {
			HtmlInput input = (HtmlInput) it.next();
			if (!input.getNameAttribute().startsWith(Ids.decorate(application, module, ACTION_PREFIX))) continue;
			String actionName = removeActionPrefix(input.getNameAttribute());
			if (!getExcludedActions().contains(actionName)) {  
				actions.add(removeActionPrefix(input.getNameAttribute()));
			}
		}	
		return actions;				
	}
			
	private static Collection getExcludedActions() { 
		if (excludedActions == null) {
			excludedActions = new ArrayList();
			// The next actions are always available since 5.2. We do this to avoid migration work for developers
			excludedActions.add("List.moveColumnToRight");
			excludedActions.add("List.moveColumnToLeft");
			excludedActions.add("List.addColumns");
			//
			excludedActions.add("EmailNotifications.subscribe"); // Available since v5.9 
		}
		return excludedActions;
	}
	
	private static Collection getIgnoredActions() { 
		if (ignoredActions == null) {
			ignoredActions = new ArrayList();
			// The next actions is no longer available since 5.2. We do this to avoid migration work for developers
			ignoredActions.add("List.customize");
		}
		return ignoredActions;
	}


	protected void assertActions(String [] expectedActions) throws Exception {
		Collection actionsInForm = getActions();		
		Collection left = new ArrayList();		
		for (int i = 0; i < expectedActions.length; i++) {
			String expectedAction = expectedActions[i];
			if (actionsInForm.contains(expectedAction)) {
				actionsInForm.remove(expectedAction);
			}
			else {
				if (!getIgnoredActions().contains(expectedAction)) { 
					left.add(expectedAction);
				}
			}				
		}			

		if (!left.isEmpty()) {
			fail(XavaResources.getString("actions_expected", left));
		}
		if (!actionsInForm.isEmpty()) {
			fail(XavaResources.getString("actions_not_expected", actionsInForm));
		}
	} 
		
	private String removeActionPrefix(String action) {
		String bareAction = Ids.undecorate(action);
		return bareAction.substring(ACTION_PREFIX.length() + 1);
	}

	protected String getValueInList(int row, String name) throws Exception {		
		int column = getMetaTab().getPropertiesNames().indexOf(name);		
		return getValueInList(row, column);
	}
	
	protected String getValueInList(int row, int column) throws Exception {
		HtmlTableCell cell = getTableCellInList(row, column);
		for (HtmlElement input: cell.getElementsByTagName("input")) {
			if (input.getAttribute("class").contains("editor")) {
				return input.getAttribute("value");
			}
		}
		return cell.asNormalizedText().trim();
	}
	
	/**
	 * @since 5.7 
	 */
	protected String getValueInList(int row) throws Exception { 
		return getElementInList(row).asNormalizedText().trim(); 
	}
	
	private HtmlElement getListElement(String id, String errorId) {  
		try {
			return getElementById(id);
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			fail(XavaResources.getString(errorId, id));
			return null;
		}		
	}
	
	private HtmlTable getTable(String id, String errorId) { 
		return (HtmlTable) getListElement(id, errorId); 
	}
		
	private HtmlTableCell getTableCellInList(int row, int column) throws Exception {
		HtmlTable table = getTable("list", "list_not_displayed");
		int columnIncrement = getColumnIncrement(table, column);
		return table.getCellAt(row+2, column+columnIncrement);
	}
	
	private HtmlElement getElementInList(int index) throws Exception { 
		HtmlElement list = getListElement("list", "list_not_displayed");
		return getChild(list, index);
	}
	
	private HtmlTableRow getTableRow(String tableId, int row) throws Exception {
		return getTable(tableId, "collection_not_displayed").getRow(row+2);
	}
	
	private HtmlElement getChild(HtmlElement parent, int index) {
		int i=0;
		for (DomElement element: parent.getChildElements()) {
			if (i++ == index) return (HtmlElement) element;
		}
		throw new IndexOutOfBoundsException(XavaResources.getString("elements_out_of_bounds", index, i - 1)); 
	}
		
	protected String getValueInCollection(String collection, int row, String name) throws Exception {
		String elementCollectionPropertyName = collection + "." + row + "." + name;
		if (hasElementByName(elementCollectionPropertyName)) return getValue(elementCollectionPropertyName);
		int column = getPropertiesList(collection).indexOf(name);
		return getValueInCollection(collection, row, column);
	}
	
	/**
	 * Sets the value of a property in a specific row of the list.
	 * 
	 * This method allows you to modify the value of a property directly in the list view,
	 * simulating the user editing the cell in-place. Works with properties that are configured
	 * as editable in the list.
	 * 
	 * For reference properties using @DescriptionsList, note that even though the list displays
	 * the description or name of the reference, when setting a value you must use the key property
	 * of the reference. For example, if you have a family reference that shows descriptions in the list,
	 * you would read the value using family.description but set it using family.number.
	 * 
	 * Example:
	 * <pre>
	 * // Simple property
	 * setValueInList(0, "unitPrice", "17.00"); // Sets the first row's unitPrice to 17.00
	 * 
	 * // Reference property with @DescriptionsList
	 * assertValueInList(1, "family.description", "HARDWARE"); // Reading shows the description
	 * setValueInList(1, "family.number", "3");    // Setting uses the key property
	 * 
	 * // Reference property with multiple keys
	 * Warehouse warehouse = new Warehouse();
	 * warehouse.setZoneNumber(4);
	 * warehouse.setNumber(13);
	 * setValueInList(1, "warehouse.KEY", toKeyString(warehouse)); // For composite keys
	 * </pre>
	 * 
	 * @param row The row number (0-based) in the list
	 * @param name The property name to set, can be a simple property or a qualified property (e.g. "family.number")
	 * @param value The value to set for the property
	 * @since 7.6 
	 */
	protected void setValueInList(int row, String name, String value) throws Exception {
		String [] propertyTokens = name.split("\\.", 2);
		String propertyName = propertyTokens[0] + "." + row;
		if (propertyTokens.length > 1) propertyName += "." + propertyTokens[1]; 
		setValue(propertyName, value);
	}	
	
	/**
	 * Sets the value of a property in a specific row and column of the list.
	 * 
	 * This method allows you to modify the value of a property directly in the list view
	 * by specifying the column index instead of the property name. The column index corresponds
	 * to the order of properties displayed in the list.
	 * 
	 * Example:
	 * <pre>
	 * setValueInList(0, 2, "17.00"); // Sets the first row's third column to 17.00
	 * </pre>
	 * 
	 * @param row The row number (0-based) in the list
	 * @param column The column number (0-based) in the list
	 * @param value The value to set for the property
	 * @since 7.6 
	 */	
	protected void setValueInList(int row, int column, String value) throws Exception {
		String name = getMetaTab().getPropertiesNames().get(column);
		setValueInList(row, name, value);
	}
	
	/**
	 * Asserts that a property in a specific row of the list is editable.
	 * 
	 * This method verifies that a property in the list can be edited in-place by the user.
	 * It fails the test if the property is not editable in the specified row.
	 * 
	 * Example:
	 * <pre>
	 * assertEditableInList(0, "unitPrice"); // Verifies the unitPrice in first row is editable
	 * assertEditableInList(1, "family.description"); // Verifies a reference property is editable
	 * </pre>
	 * 
	 * @param row The row number (0-based) in the list
	 * @param name The property name to check, can be a simple property or a qualified property
	 * @throws Exception If the property is not editable or if any error occurs during verification
	 * @since 7.6 
	 */		
	protected void assertEditableInList(int row, String name) throws Exception {
		try {
			assertEditable(toPropertyNameForEditableInList(row, name));
		}
		catch (ElementNotFoundException ex) {
			fail(XavaResources.getString("property_in_list_row_not_editable_should_be", name, row));
		}
	}
	
	/**
	 * Asserts that a property in a specific row of the list is NOT editable.
	 * 
	 * This method verifies that a property in the list cannot be edited in-place by the user.
	 * It fails the test if the property is editable in the specified row when it should not be.
	 * 
	 * Example:
	 * <pre>
	 * assertNoEditableInList(0, "extendedDescription"); // Verifies the property is not editable
	 * assertNoEditableInList(1, "id"); // Verifies a key property is not editable
	 * </pre>
	 * 
	 * This is particularly useful for testing properties that should be read-only in the list,
	 * such as calculated fields, formula properties, or properties explicitly excluded from
	 * the editable properties configuration.
	 * 
	 * @param row The row number (0-based) in the list
	 * @param name The property name to check, can be a simple property or a qualified property
	 * @throws Exception If the property is editable or if any error occurs during verification
	 * @since 7.6 
	 */		
	protected void assertNoEditableInList(int row, String name) throws Exception { 
		try {
			assertEditable(toPropertyNameForEditableInList(row, name));
			fail(XavaResources.getString("property_in_list_row_editable_should_not_be", name, row));
		}
		catch (ElementNotFoundException ex) {
			// There is no editor in the list cell, so it's not editable (or maybe it does not exist at all)
		}
	}
	
	/**
	 * Asserts that a property in a specific row and column of the list is editable.
	 * 
	 * This method verifies that a property in the list can be edited in-place by the user,
	 * using column index instead of property name. The column index corresponds to the
	 * order of properties displayed in the list.
	 * 
	 * Example:
	 * <pre>
	 * assertEditableInList(0, 2); // Verifies the third column in first row is editable
	 * </pre>
	 * 
	 * @param row The row number (0-based) in the list
	 * @param column The column number (0-based) in the list
	 * @throws Exception If the property is not editable or if any error occurs during verification
	 * @since 7.6 
	 */		
	protected void assertEditableInList(int row, int column) throws Exception {
		String name = getMetaTab().getPropertiesNames().get(column);
		assertEditableInList(row, name);
	}
	
	/**
	 * Asserts that a property in a specific row and column of the list is NOT editable.
	 * 
	 * This method verifies that a property in the list cannot be edited in-place by the user,
	 * using column index instead of property name. It fails the test if the property is
	 * editable in the specified row and column when it should not be.
	 * 
	 * Example:
	 * <pre>
	 * assertNoEditableInList(0, 3); // Verifies the fourth column in first row is not editable
	 * </pre>
	 * 
	 * This is particularly useful for testing properties that should be read-only in the list,
	 * such as calculated fields, formula properties, or properties explicitly excluded from
	 * the editable properties configuration.
	 * 
	 * @param row The row number (0-based) in the list
	 * @param column The column number (0-based) in the list
	 * @throws Exception If the property is editable or if any error occurs during verification
	 * @since 7.6 
	 */		
	protected void assertNoEditableInList(int row, int column) throws Exception { 
		String name = getMetaTab().getPropertiesNames().get(column);
		assertNoEditableInList(row, name);
	}
	
	private String toPropertyNameForEditableInList(int row, String name) {
		String baseName = Strings.firstToken(name, "."); // For qualified properties we get the reference name
		return baseName + "." + row;
	}
	
	/**
	 * @since 5.0 
	 */
	protected void setValueInCollection(String collection, int row, String name, String value) throws Exception {
		String elementCollectionPropertyName = collection + "." + row + "." + name;
		if (!hasElementByName(elementCollectionPropertyName)) throw new XavaException("method_only_for_element_collections", "setValueInCollection()"); 
		setValue(elementCollectionPropertyName, value);
	}
	
	/**
	 * @since 5.0 
	 */
	protected void setValueInCollection(String collection, int row, int column, String value) throws Exception {
		setValueInCollection(collection, row, getPropertiesList(collection).get(column), value);
	}
	
	private List<String> getPropertiesList(String collection) throws Exception {
		collection = getCollectionPrefix() + collection;
		MetaCollectionView metaCollectionView = getMetaView().getMetaCollectionView(collection);
		List<String> propertiesList = metaCollectionView==null?null:metaCollectionView.getPropertiesListNames();
		if (propertiesList == null || propertiesList.isEmpty()) propertiesList = getMetaModel().getMetaCollection(collection).getMetaReference().getMetaModelReferenced().getPropertiesNamesWithoutHiddenNorTransient();
		return removePropertySuffix(propertiesList);
	}	
	
	private List<String> removePropertySuffix(List<String> propertiesList) { 
		List<String> result = new ArrayList<String>();
		for (String property: propertiesList) {
			if (property.endsWith("+")) result.add(property.substring(0, property.length() - 1));
			else result.add(property);
		}
		return result;
	}

	private String getCollectionPrefix() {  
		String viewMember = getViewMember();
		if (Is.emptyString(viewMember)) return "";
		return viewMember + ".";
	}
	
	private String getViewMember() { 
		return getInputById("view_member").getValue(); 
	}
	
	protected String getValueInCollection(String collection, int row, int column) throws Exception {
		try {
			String name = getPropertiesList(collection).get(column); 
			String elementCollectionPropertyName = collection + "." + row + "." + name;
			if (hasElementByName(elementCollectionPropertyName)) return getValue(elementCollectionPropertyName);
		}
		catch (org.openxava.util.ElementNotFoundException ex) {
			// Because sometimes is needed to explore collections not contained in the module model
		}
		catch (IndexOutOfBoundsException ex) {
			// Because sometimes is needed to explore collections not contained in the module model
		}
		return toString(getTableCellInCollection(collection, row, column)); 
	}
	
	private String toString(HtmlTableCell cell) { 
		String result = cell.asNormalizedText().trim();
		if (result.length() > 0 && result.charAt(0) > 60000) { // Sometimes when we have characters from other encondings we can have an unrecognized character for a space 
			result = result.substring(1);
		}
		return result;
	}
	
	private HtmlTableCell getTableCellInCollection(String collection, int row, int column) throws Exception {		
		HtmlTable table = getTable(collection, "collection_not_displayed");
		row = collectionHasFilterHeader(table)?row + 2:row + 1;
		return table.getCellAt(row, column + getColumnIncrement(table));
	}

	private int getColumnIncrement(HtmlTable table) { 
		int increment = 0;
		for (int i=0;;i++) {
			HtmlTableCell cell = table.getCellAt(0, i);
			if (cell == null) break;
			if (!(Is.emptyString(cell.asNormalizedText()) || cell.asXml().toLowerCase().contains("<input type=\"checkbox\""))) break; 
			increment++;
		}
		if (increment == 0 && table.getId().contains("xavaPropertiesList")) return 2;
		return increment;
	}
	
	private boolean hasLinks(HtmlElement element) {
		return !element.getElementsByTagName("a").isEmpty(); 
	}

	protected void assertRowStyleInList(int row, String expectedStyle) throws Exception {
		assertRowStyle("list", row, expectedStyle);
	}
	
	protected void assertRowStyleInCollection(String collection, int row, String expectedStyle) throws Exception {
		assertRowStyle(collection, row, expectedStyle);
	}	
	
	private void assertRowStyle(String tableId, int row, String expectedStyle) throws Exception {
		HtmlTableRow tableRow = getTableRow(tableId, row);
		String style = tableRow.getAttribute("class");
		assertTrue(style.contains(expectedStyle));
	}
	
	protected void assertNoRowStyleInList(int row) throws Exception {
		assertNoRowStyle("list", row);
	}
	
	protected void assertNoRowStyleInCollection(String collection, int row) throws Exception {
		assertNoRowStyle(collection, row);
	}	
	
	private void assertNoRowStyle(String tableId, int row) throws Exception {
		HtmlTableRow tableRow = getTableRow(tableId, row);
		String style = tableRow.getAttribute("class");
		assertEquals(XavaResources.getString("row_style_not_excepted"), 
			new StringTokenizer(getDefaultRowStyle(row)).countTokens(),
			new StringTokenizer(style).countTokens());
	}

	private String getDefaultRowStyle(int row) {
		return (row % 2 == 0)?"ox-list-pair":"ox-list-odd";
	}

	private boolean collectionHasFilterHeader(HtmlTable table) {
		return table.getRowCount() > 1 && 
			!table.getCellAt(1, 0).
				getElementsByAttribute("input", "name", 
					Ids.decorate(application, module,
						ACTION_PREFIX + ".List.filter")).isEmpty();
	}
	
	protected boolean _collectionHasFilterHeader(String collection) throws Exception {  
		return collectionHasFilterHeader(getTable(collection, "collection_not_displayed"));
	}	
	
	/**
	 * Rows count displayed with data. <p>
	 * 
	 * Exclude heading and footing, and the not displayed data (maybe in cache).
	 */
	protected int getListRowCount() throws Exception {
		HtmlElement listElement = getListElement("list", XavaResources.getString("list_not_displayed"));
		if (listElement instanceof HtmlTable) return getListTableRowCount((HtmlTable) listElement);
		else return getListDivRowCount((HtmlDivision) listElement);
	}
	
	
	private int getListDivRowCount(HtmlDivision div) { 
		int elementCount = div.getChildElementCount();
		if (elementCount == 1) return div.asXml().contains("ox-no-objects")?0:1; 
		if (elementCount > EntityTab.DEFAULT_CHUNK_SIZE && div.asXml().contains("xava_loading_more_elements")) return elementCount - 2; 
		return elementCount;
	}

	private int getListTableRowCount(HtmlTable table) throws Exception { 
		if (table.getRowCount() > 2 && "nodata".equals(table.getRow(2).getId())) { 
			return 0;
		}						
		int increment = 2; // The header and the summation row
		if (collectionHasFilterHeader(table)) increment++; // The filter
		return table.getRowCount() - increment;
	}

	
	protected int getListColumnCount() throws Exception {
		return getListColumnCount("list", XavaResources.getString("list_not_displayed"));
	}
	
	protected int getCollectionColumnCount(String collection) throws Exception {
		return getListColumnCount(collection, XavaResources.getString("collection_not_displayed", collection)); 
	}
	
	private int getListColumnCount(String id, String message) throws Exception {
		HtmlTable table = getTable(id, message);
		if (isSimpleList(table)) return table.getRow(0).getCells().size(); 
		int c = 0;
		for (HtmlTableCell cell: table.getRow(0).getCells()) {
			if (cell.isDisplayed()) c++;
		}
		return c - 2;
	}	
	
	/**
	 * Row count displayed with data. <p>
	 * Excludes heading and footing, and not displayed data (but cached). 
	 */
	protected int getCollectionRowCount(String collection) throws Exception {		
		HtmlTable table = getTable(collection, XavaResources.getString("collection_not_displayed"));
		if (isSimpleList(table)) return table.getRows().size() - 1; 
		int count = 0;
		for (HtmlTableRow row: table.getRows()) {
			if (!Is.emptyString(row.getId()) && !row.getId().equals("nodata") && !row.getId().contains("_list_filter_")) {
				if (isDisplayed(row)) count++;
				else count--; // In this way we discount the empty row in element collection just above the hidden one
			}			
		}
		return count;
	}
	
	private boolean isSimpleList(HtmlTable table) { 
		return table.getAttribute("class").contains("ox-simple-list");
	}

	// Because HtmlElement.isDisplayed only works when CSS is active
	private boolean isDisplayed(DomElement element) { 
		String cssClass = element.getAttribute("class");
		if (cssClass != null && cssClass.contains("ox-display-none")) return false;
		
		String style = element.getAttribute("style");
		if (style == null) return true;
		return !(style.contains("display: none") || style.contains("display:none")); 		
	}

	/**
	 * Row count displayed with data. <p>
	 * Excludes heading and footing, and not displayed data (but cached). 
	 */
	protected void assertCollectionRowCount(String collection, int expectedCount) throws Exception {
		assertEquals(XavaResources.getString("collection_row_count", collection), expectedCount, getCollectionRowCount(collection));
	}
	
	/**
	 * Rows count displayed with data. <p>
	 * 
	 * Exclude headers and footing, and the not displayed data (maybe cached).
	 */
	protected void assertListRowCount(int expected) throws Exception {
		assertEquals(XavaResources.getString("list_row_count"), expected, getListRowCount());
	}
	
	protected void assertListColumnCount(int expected) throws Exception {
		assertEquals(XavaResources.getString("list_column_count"), expected, getListColumnCount());
	}
	
	protected void assertCollectionColumnCount(String collection, int expected) throws Exception {
		assertEquals(XavaResources.getString("collection_column_count", collection), expected, getCollectionColumnCount(collection)); 
	}	
	
		
	protected void assertValueInList(int row, String name, String value) throws Exception {
		assertEquals(XavaResources.getString("unexpected_value_in_list", name, new Integer(row)), value, getValueInList(row, name));
	}
	
	/** @since 5.7 */
	protected void assertValueInList(int row, String value) throws Exception { 
		assertEquals(XavaResources.getString("unexpected_value_in_list", "", new Integer(row)), value, getValueInList(row));
	}
	
	/** @since 5.8 */
	protected void assertValuesInList(int row, String ... values) throws Exception {   
		for (int i=0; i<values.length; i++) {
			assertValueInList(row, i, values[i]);
		}
	}
	
	protected void assertValueInList(int row, int column, String value) throws Exception {
		assertEquals(XavaResources.getString("unexpected_value_in_list", new Integer(column), new Integer(row)), value, getValueInList(row, column));
	}
	
	protected void assertValueInCollection(String collection, int row, String name, String value) throws Exception {
		assertEquals(XavaResources.getString("unexpected_value_in_collection", name, new Integer(row), collection), value, getValueInCollection(collection, row, name));
	}
	
	protected void assertValueInCollection(String collection, int row, int column, String value) throws Exception {
		assertEquals(XavaResources.getString("unexpected_value_in_collection", new Integer(column), new Integer(row), collection), value, getValueInCollection(collection, row, column));
	}
	
	protected void assertValueInCollectionIgnoringCase(String collection, int row, int column, String value) throws Exception {
		String valueInCollection = getValueInCollection(collection, row, column);
		assertTrue(XavaResources.getString("unexpected_value_in_collection", new Integer(column), new Integer(row), collection), value.equalsIgnoreCase(valueInCollection));
	}
	
	protected void assertLabelInCollection(String collection, int column, String label) throws Exception {
		assertLabelInList(collection, XavaResources.getString("collection_not_displayed", collection), column, label);
	}
	
	protected void assertLabelInList(int column, String label) throws Exception {
		assertLabelInList("list", XavaResources.getString("list_not_displayed"), column, label);
	}

	/**
	 *
	 * @since 4.1
	 */	
	protected void assertTotalInList(int column, String total) throws Exception { 
		assertTotalInList("list", XavaResources.getString("list_not_displayed"), 0, column, total);
	}
	
	/**
	 *
	 * @since 4.1
	 */	
	protected void assertTotalInList(String name, String total) throws Exception { 
		assertTotalInList(getMetaTab().getPropertiesNames().indexOf(name), total);
	}
	
	/**
	 *
	 * @since 4.1
	 */	
	protected void assertTotalInCollection(String collection, int column, String total) throws Exception { 
		assertTotalInList(collection, XavaResources.getString("collection_not_displayed", collection), 0, column, total);
	}

	/**
	 *
	 * @since 4.1
	 */	
	protected void assertTotalInCollection(String collection, int row, int column, String total) throws Exception {  
		assertTotalInList(collection, XavaResources.getString("collection_not_displayed", collection), row, column, total);
	}	

	/**
	 *
	 * @since 4.1
	 */
	protected void assertTotalInCollection(String collection, String name, String total) throws Exception { 
		int column = getPropertiesList(collection).indexOf(name);
		assertTotalInCollection(collection, column, total);
	}
	
	/**
	 *
	 * @since 4.3
	 */	
	protected void assertTotalInCollection(String collection, int row, String name, String total) throws Exception {  
		int column = getPropertiesList(collection).indexOf(name);
		assertTotalInCollection(collection, row, column, total);
	}
	
	
	private void assertLabelInList(String tableId, String message, int column, String label) throws Exception {
		HtmlTable table = getTable(tableId, message);
		int increment = getColumnIncrement(table, column); 
		assertEquals(XavaResources.getString("label_not_match", new Integer(column)), label, 
				table.getCellAt(0, column+increment).asNormalizedText().trim());
	}
	
	private void assertTotalInList(String tableId, String message, int row, int column, String total) throws Exception { 
		HtmlTable table = getTable(tableId, message);
		int rowInTable = table.getRowCount() - getTotalsRowCount(table) + row;
		column+=getColumnIncrement(table, column);
		HtmlTableCell cell = table.getCellAt(rowInTable, column);
		List<HtmlInput> inputs = cell.getElementsByAttribute("input", "type", "text");
		String value = inputs.isEmpty()?cell.asNormalizedText().trim():inputs.get(0).getValue(); 
		assertEquals(XavaResources.getString("total_not_match", new Integer(column)), total, value);
	}		
	
	private int getColumnIncrement(HtmlTable table, int originalColumn) {
		int increment = table.getCellAt(0, 1).asXml().contains("type=\"checkbox\"")
			|| table.getCellAt(0, 0).asXml().contains("xava_customize_list")?2:1; 	
		if (isElementCollection(table)) {
			int i=1;
			HtmlTableCell cell = table.getCellAt(0, i++);
			while (cell != null && i < originalColumn + increment + 2) {  
				String value = cell.asNormalizedText().trim();
				if (Is.emptyString(value)) increment++;
				cell = table.getCellAt(0, i++);
			}
		}		
		int i=1;
		HtmlTableCell cell = table.getCellAt(0, i);
		while (cell != null && i < originalColumn + increment + 1) {			
			if (!isDisplayed(cell)) increment++; 
			cell = table.getCellAt(0, ++i);
		} 
		return increment;
	}
	
	private boolean isElementCollection(HtmlTable table) { 
		HtmlElement container = (HtmlElement) table.getParentNode(); 
		if (XavaPreferences.getInstance().isResizeColumns()) container = (HtmlElement) container.getParentNode();
		String containerClass = container.getAttribute("class");
		if (containerClass == null) return false;
		return containerClass.contains("ox-element-collection");
	}

	private int getTotalsRowCount(HtmlTable table) { 
		int count = 0;
		for (int i=table.getRowCount() - 1; i >=1 && table.getRow(i).getId().equals(""); i--) count++; 
		return count;
	}

	protected void checkRow(int row) throws Exception {
		checkRow("selected", row);
	}

	protected void checkAll() throws Exception {
		checkAll("");		
	}
	
	private void checkAll(String id) throws Exception{
		HtmlInput input = getCheckable(Is.empty(id) ? "selected_all" : id);
		if (input.isChecked()){
			log.warn(XavaResources.getString("selected_all_already_selected"));
		}
		else{
			input.click();
			waitUntilPageIsLoaded();	
		}
	}
	
	protected void uncheckRow(int row) throws Exception {
		uncheckRow("selected", row);
	}
	
	protected void uncheckAll() throws Exception {
		uncheckAll("");
	}
	
	private void uncheckAll(String id) throws Exception{
		HtmlInput input = getCheckable(Is.empty(id) ? "selected_all" : id);
		if (input.isChecked()){
			input.click();
			waitUntilPageIsLoaded();
		}
		else{
			log.warn(XavaResources.getString("selected_all_already_unselected"));
		}
	}
	
	protected void checkRowCollection(String collection, int row) throws Exception {		
		if (_collectionHasFilterHeader(collection)) {
			checkRow(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected", row);
		}
		else {		
			checkRow(collection + ".__SELECTED__", row);
		}
	}
	
	protected void checkAllCollection(String collection) throws Exception {		
		if (_collectionHasFilterHeader(collection)) {
			checkAll(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected_all");
		}
		else {		
			checkAll(collection + ".selected_all"); 
		}
	}
	
	protected void uncheckAllCollection(String collection) throws Exception {		
		if (_collectionHasFilterHeader(collection)) {
			uncheckAll(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected_all");
		}
		else {		
			uncheckAll(collection + ".selected_all"); 
		}
	}
	
	protected void uncheckRowCollection(String collection, int row) throws Exception {		
		if (_collectionHasFilterHeader(collection)) {
			uncheckRow(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected", row);
		}
		else {		
			uncheckRow(collection + ".__SELECTED__", row);
		}
	}
	
	private HtmlInput getCheckable(String id, int row) { 
		return (HtmlInput) getForm().getInputByValue(id + ":" + row);
	}
	
	private HtmlInput getCheckable(String value) {
		return (HtmlInput) getForm().getInputByValue(value); 
	}
	
	private void checkRow(String id, int row) throws Exception {
		HtmlInput input = getCheckable(id, row);
		if (input.isChecked()){
			log.warn(XavaResources.getString("row_already_selected"));
		}
		else{
			input.click();
			waitUntilPageIsLoaded();
			if (!input.isChecked()) input.setChecked(true); // Because input.click() fails with HtmlUnit 2.5/2.6/2.7 in some circumstances						
		}
	}
	
	protected void checkRow(String id, String value) throws Exception {
		try {
			HtmlInput input = getForm().getInputByValue(id + ":" + value); 
			input.click();
			waitUntilPageIsLoaded();
			if (!input.isChecked()) input.setChecked(true); // Because input.click() fails with HtmlUnit 2.5/2.6/2.7 in some circumstances
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			fail(XavaResources.getString("must_exist", id));
		}
	}
		
	private void uncheckRow(String id, int row) throws Exception {
		HtmlInput input = getCheckable(id, row);
		if (input.isChecked()){
			input.click();
			waitUntilPageIsLoaded();
			if (!input.isChecked()) input.setChecked(false); // Because input.click() fails with HtmlUnit 2.5/2.6/2.7 in some circumstances
		}
		else{
			log.warn(XavaResources.getString("row_already_unselected"));
		}
	}
			
	protected void assertRowChecked(int row) { 
		assertRowChecked("selected", row);
	}
	
	protected void assertAllChecked() { 
		assertAllChecked("selected_all");
	}
	
	protected void assertRowCollectionChecked(String collection, int row) throws Exception { 
		if (_collectionHasFilterHeader(collection)) {
			assertRowChecked(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected", row);
		}
		else {			
			assertRowChecked(collection + ".__SELECTED__", row); 
		}
	}	

	protected void assertAllCollectionChecked(String collection) throws Exception { 
		if (_collectionHasFilterHeader(collection)) {
			assertAllChecked(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected_all");
		}
		else {			 
			assertAllChecked(collection + ".selected_all"); 			
		}
	}	
	
	private void assertRowChecked(String id, int row) { 
		assertTrue(XavaResources.getString("selected_rows_not_match"), 
				getCheckable(id, row).isChecked()); 		
	}	
	
	private void assertAllChecked(String id) { 
		assertTrue(XavaResources.getString("selected_all_not_checked"),	 
			getCheckable(id).isChecked()); 		
	}	
	
	protected void assertRowsChecked(int f1, int f2) {
		assertRowsChecked(new int [] {f1, f2});
	}
		
	protected void assertRowsChecked(int [] rows) {
		for (int i = 0; i < rows.length; i++) {
			assertRowChecked(rows[i]);
		}
	}
	
	protected void assertRowUnchecked(int row) { 
		assertRowUnchecked("selected", row);
	}

	protected void assertAllUnchecked() { 
		assertAllUnchecked("selected_all");
	}	
	
	protected void assertRowCollectionUnchecked(String collection, int row) throws Exception { 
		if (_collectionHasFilterHeader(collection)) {
			assertRowUnchecked(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected", row);
		}
		else {			
			assertRowUnchecked(collection + ".__SELECTED__", row); 
		}
	}	
	
	protected void assertAllCollectionUnchecked(String collection) throws Exception { 
		if (_collectionHasFilterHeader(collection)) {
			assertAllUnchecked(Tab.COLLECTION_PREFIX + collection.replace('.', '_') + "_selected_all");
		}
		else {			
			assertAllUnchecked(collection + ".selected_all"); 
		}
	}	
	
	private void assertRowUnchecked(String id, int row) { 
		assertTrue(XavaResources.getString("selected_row_unexpected", new Integer(row)), 
				!getCheckable(id, row).isChecked());
	}
	
	private void assertAllUnchecked(String id) { 
		assertTrue(XavaResources.getString("selected_all_unexpected"), 
				!getCheckable(id).isChecked());
	}
	
	protected void assertError(String message) throws Exception {
		assertMessage(message, "errors_table", "error_not_found", "errors_produced");
	}
	
	protected void assertMessage(String message) throws Exception {
		assertMessage(message, "messages_table", "message_not_found", "messages_produced");
	}
	
	/**
	 * @since 4.3
	 */
	protected void assertInfo(String message) throws Exception { 
		assertMessage(message, "infos_table", "info_not_found", "infos_produced"); 
	}
	
	/**
	 * @since 4.3
	 */
	protected void assertWarning(String message) throws Exception { 
		assertMessage(message, "warnings_table", "warning_not_found", "warnings_produced"); 
	}	
	
	private void assertMessage(String message, String tableId, String notFoundMessageId, String messagesProducedMessageId) throws Exception {
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById(tableId); 
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			fail(XavaResources.getString(notFoundMessageId, message));
			return;
		}
		int rc = table.getRowCount();
		StringBuffer messages = new StringBuffer();
		for (int i = 0; i < rc; i++) {
			String m = table.getCellAt(i, 0).asNormalizedText().trim();
			if (m.equals(message)) return;
			messages.append(m);
			messages.append('\n');												
		}
		log.error(XavaResources.getString(messagesProducedMessageId, messages));
		fail(XavaResources.getString(notFoundMessageId, message));
	}

	
	
	protected void assertErrorsCount(int expectedCount) throws Exception {
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById("errors_table"); 
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			if (expectedCount > 0) {
				fail(XavaResources.getString("no_error_and_expected", new Integer(expectedCount)));
			}
			return;
		}		
		assertEquals(XavaResources.getString("errors_count_unexpected"), expectedCount, table.getRowCount());
	}
	
	protected void assertMessagesCount(int expectedCount) throws Exception {
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById("messages_table");
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			if (expectedCount > 0) {
				fail(XavaResources.getString("no_message_and_expected", new Integer(expectedCount)));
			}
			return;
		}				
		assertEquals(XavaResources.getString("messages_count_unexpected"), expectedCount, table.getRowCount());
	}
	
	/**
	 * @since 4.3
	 */	
	protected void assertInfosCount(int expectedCount) throws Exception { 
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById("infos_table"); 
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			if (expectedCount > 0) {
				fail(XavaResources.getString("no_info_and_expected", new Integer(expectedCount))); 
			}
			return;
		}		
		assertEquals(XavaResources.getString("infos_count_unexpected"), expectedCount, table.getRowCount()); 
	}
	
	/**
	 * @since 4.3
	 */
	protected void assertWarningsCount(int expectedCount) throws Exception { 
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById("warnings_table");  
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			if (expectedCount > 0) {
				fail(XavaResources.getString("no_warning_and_expected", new Integer(expectedCount))); 
			}
			return;
		}		
		assertEquals(XavaResources.getString("warnings_count_unexpected"), expectedCount, table.getRowCount()); 
	}
	
	
			
	protected void assertNoError(String message) throws Exception {
		assertNoMessage(message, "errors_table", "error_found");
	}
	
	protected void assertNoMessage(String message) throws Exception {
		assertNoMessage(message, "messages_table", "message_found"); 
	}
	
	/**
	 * @since 4.3
	 */	
	protected void assertNoInfo(String message) throws Exception { 
		assertNoMessage(message, "infos_table", "info_found"); 
	}
	
	/**
	 * @since 4.3
	 */	
	protected void assertNoWarning(String message) throws Exception { 
		assertNoMessage(message, "warnings_table", "warning_found"); 
	}	
		
	private void assertNoMessage(String message, String id, String notFoundErrorId) throws Exception {
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById(id); 
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			return;
		}								
		int rc = table.getRowCount();				
		for (int i = 0; i < rc; i++) {
			String error = table.getCellAt(i, 0).asNormalizedText().trim();
			if (error.equals(message)) fail(XavaResources.getString(notFoundErrorId, message));
		}
	}
		
	/**
	 * The first message
	 */
	protected String getMessage() throws Exception {
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById("messages_table"); 
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			return "";
		}
		if (table.getRowCount() == 0) return "";
		return table.getCellAt(0, 0).asNormalizedText().trim();
	}	
	
	
	
	protected void assertNoErrors() throws Exception {
		assertNoMessages("errors_table", "Error");		
	}
	
	protected void assertNoMessages() throws Exception {
		assertNoMessages("messages_table", "Message"); 
	}

	/**
	 * 
	 * @since 5.4
	 */	
	protected void assertNoWarnings() throws Exception { 
		assertNoMessages("warnings_table", "Warning");		
	}
	
	/**
	 * 
	 * @since 5.4
	 */	
	protected void assertNoInfos() throws Exception { 
		assertNoMessages("infos_table", "Infos");		
	}
	
	private void assertNoMessages(String id, String label) throws Exception {
		HtmlTable table = null;
		try {
			table = (HtmlTable) getElementById(id); 
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			return;
		}				
		int rc = table.getRowCount();
		if (rc > 0) {
			for (int i = 0; i < rc; i++) {
				String message = table.getCellAt(i, 0).asNormalizedText().trim();
				log.error(XavaResources.getString("unexpected_message", label, message));							
			}			
			fail(XavaResources.getString("unexpected_messages", label.toLowerCase() + "s"));
		}
	}
	
	private MetaTab getMetaTab() throws XavaException {
		if (metaTab == null) {			
			metaTab = MetaComponent.get(getMetaModule().getModelName()).getMetaTab(getMetaModule().getTabName());			
		}
		return metaTab;
	}
	
	private MetaView getMetaView() throws XavaException {
		if (metaView == null) {						 			
			metaView = getMetaModel().getMetaView(getMetaModule().getViewName());			
		}
		return metaView;
	}
	
	private MetaModel getMetaModel() throws XavaException {
		if (metaModel == null) {			
			metaModel = MetaComponent.get(getMetaModule().getModelName()).getMetaEntity(); 									
		}
		return metaModel;
	}	
	
	private MetaModule getMetaModule() throws XavaException {
		if (metaModule == null) {
			metaModule = MetaApplications.getMetaApplication(this.application).getMetaModule(this.module);
		}
		return metaModule;
	}
	
	
	/**
	 *
	 * @since 5.1
	 */
	protected void assertValidValuesInCollection(String collection, int row, String name, String [][] values) throws Exception { 
		String elementCollectionPropertyName = collection + "." + row + "." + name;
		if (!hasElementByName(elementCollectionPropertyName)) throw new XavaException("method_only_for_element_collections", "assertValidValuesInCollection()");
		assertValidValues(elementCollectionPropertyName, values);
	}

	/**
	 *
	 * @since 5.1
	 */	
	protected void assertValidValuesCountInCollection(String collection, int row, String name, int count) throws Exception { 
		String elementCollectionPropertyName = collection + "." + row + "." + name;
		if (!hasElementByName(elementCollectionPropertyName)) throw new XavaException("method_only_for_element_collections", "assertValidValuesCountInCollection()");
		assertValidValuesCount(elementCollectionPropertyName, count);		
	}

	protected void assertValidValueNotExists(String name, String key) throws Exception {
		try {
			assertValidValueNotExistsWithHtmlSelect(name, key);
		}
		catch (ElementNotFoundException ex) {
			assertValidValueNotExistsWithUIAutocomplete(name, key);
		}
	}
	
	private void assertValidValueNotExistsWithUIAutocomplete(String name, String key) throws Exception {
		List<KeyAndDescription> validValues = getValidValuesWithUIAutocomplete(name);
		if (validValues.size() > 0){
			int i = 0;
			for (KeyAndDescription validValue: validValues) {
				if (key.equals((String) validValue.getKey())){
					fail(XavaResources.getString("option_found", name, key));
				}
			}
		}
	}
	
	private void assertValidValueNotExistsWithHtmlSelect(String name, String key) {
		Collection options = getSelectByName(decorateId(name)).getOptions();
		if (options.size() > 0){
			int i=0;
			for (Iterator it = options.iterator(); it.hasNext(); i++) {
				HtmlOption option = (HtmlOption) it.next();
				if (option.getValueAttribute().equals(key)) {
					fail(XavaResources.getString("option_found", name, key));
					break;
				}
			}
		}
	}
	
	protected void assertValidValues(String name, String [][] values) throws Exception {
		try {
			assertValidValuesWithHtmlSelect(name, values);
		}
		catch (ElementNotFoundException ex) {
			assertValidValuesWithUIAutocomplete(name, values);
		}
	}
	
	private void assertValidValuesWithHtmlSelect(String name, String [][] values) throws Exception {
		Collection options = getSelectByName(decorateId(name)).getOptions();
		assertEquals(XavaResources.getString("unexpected_valid_values", name), values.length, options.size());
		int i=0;
		for (Iterator it = options.iterator(); it.hasNext(); i++) {
			HtmlOption option = (HtmlOption) it.next();
			assertEquals(XavaResources.getString("unexpected_key", name), values[i][0], option.getValueAttribute()); 
			assertEquals(XavaResources.getString("unexpected_description", name), values[i][1], option.asNormalizedText());			
		}
	}

	private void assertValidValuesWithUIAutocomplete(String name, String [][] values) throws Exception { 
		List<KeyAndDescription> validValues = getValidValuesWithUIAutocomplete(name);
		assertEquals(XavaResources.getString("unexpected_valid_values", name), values.length, validValues.size() + 1);
		int i = 1;
		for (KeyAndDescription validValue: validValues) {			
			assertEquals(XavaResources.getString("unexpected_key", name), values[i][0], validValue.getKey());
			assertEquals(XavaResources.getString("unexpected_description", name), values[i][1], validValue.getDescription());
			i++;
		}		
	}
	
	private List<KeyAndDescription> getValidValuesWithUIAutocomplete(String name) throws Exception { 
		// 1) Locate the editor container and the button to open the menu
		String referenceName = name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
		HtmlElement editorContainer = getElementById("reference_editor_" + referenceName);

		List<HtmlElement> dropdownButtons = editorContainer.getByXPath(".//i[contains(@class, 'mdi-menu-down')]");
		if (dropdownButtons.isEmpty()) {
			fail(XavaResources.getString("dropdown_button_not_found", name));
		}

		// 2) Open the menu and wait for it to load
		dropdownButtons.get(0).click();
		Thread.sleep(700);

		// 3) Get the visible menu and capture all displayed labels (descriptions)
		List<HtmlElement> autocompleteMenus = page.getByXPath("//ul[contains(@class, 'ui-autocomplete')]");
		List<KeyAndDescription> validValues = new ArrayList<KeyAndDescription>();
		List<String> labels = new ArrayList<String>();
		if (!autocompleteMenus.isEmpty()) {
			HtmlElement visibleMenu = null;
			for (HtmlElement menu : autocompleteMenus) {
				String style = menu.getAttribute("style");
				if (style == null || !style.contains("display: none")) { visibleMenu = menu; break; }
			}
			if (visibleMenu != null) {
				List<HtmlElement> menuItems = visibleMenu.getByXPath(".//li[contains(@class, 'ui-menu-item')]/div[contains(@class,'ui-menu-item-wrapper')]");
				for (HtmlElement wrapper : menuItems) {
					labels.add(wrapper.getTextContent().trim());
				}
			}
		}

		// 4) For each label, select it so the hidden input receives the key, read it, then reopen the menu
		for (String label : labels) {
			// If the menu is not visible, open it again
			List<HtmlElement> menus = page.getByXPath("//ul[contains(@class, 'ui-autocomplete')]");
			HtmlElement visibleMenu = null;
			for (HtmlElement menu : menus) {
				String style = menu.getAttribute("style");
				if (style == null || !style.contains("display: none")) { visibleMenu = menu; break; }
			}
			if (visibleMenu == null) {
				// reopen
				dropdownButtons.get(0).click();
				Thread.sleep(400);
				menus = page.getByXPath("//ul[contains(@class, 'ui-autocomplete')]");
				for (HtmlElement menu : menus) {
					String style = menu.getAttribute("style");
					if (style == null || !style.contains("display: none")) { visibleMenu = menu; break; }
				}
			}

			if (visibleMenu != null) {
				// Find the item with the exact label and click to select
				String xpath = ".//div[contains(@class,'ui-menu-item-wrapper') and normalize-space(text())='" + label.replace("'", "&apos;") + "']";
				List<HtmlElement> candidates = visibleMenu.getByXPath(xpath);
				if (!candidates.isEmpty()) {
					candidates.get(0).click(); // select the item -> it fills the hidden input with the key
					Thread.sleep(200);
					// Read the key from the field's hidden input
					HtmlInput hidden = getForm().getInputByName(decorateId(name));
					String key = hidden.getValueAttribute();
					validValues.add(new KeyAndDescription(key, label));
				}
			}
		}


		// 5) Close the descriptions editor so it's ready for the next time
		try {
			List<HtmlElement> closeButtons = editorContainer.getByXPath(".//a[contains(@class,'xava_descriptions_editor_close')]");
			if (!closeButtons.isEmpty()) {
				closeButtons.get(0).click();
				Thread.sleep(200);
			}
		}
		catch (Exception ignore) { }

		return validValues;
	}

	
	private void nextTokens(StringTokenizer st, int count) { 
		for (int i=0; i<count; i++) {
			if (st.hasMoreTokens()) st.nextToken();
		}
	}
	
	protected void assertValidValuesCount(String name, int count) throws Exception {
		try {
			assertValidValuesCountWithHtmlSelect(name, count);
		}
		catch (ElementNotFoundException ex) {
			assertValidValuesCountWithUIAutocomplete(name, count);
		}
	}
	
	private void assertValidValuesCountWithHtmlSelect(String name, int count) throws Exception { 
		HtmlSelect select = getForm().getSelectByName(decorateId(name)); 
		assertEquals(XavaResources.getString("unexpected_valid_values", name), count, select.getOptionSize());
	}
	
	private void assertValidValuesCountWithUIAutocomplete(String name, int count) throws Exception { 
		List validValues = getValidValuesWithUIAutocomplete(name);
		assertEquals(XavaResources.getString("unexpected_valid_values", name), count, validValues.size() + 1 );
	}
	
	protected String [] getKeysValidValues(String name) throws Exception {
		try {
			return getKeysValidValuesWithHtmlSelect(name);
		}
		catch (ElementNotFoundException ex) {
			return getKeysValidValuesWithUIAutocomplete(name);
		}
	}
	
	private String [] getKeysValidValuesWithHtmlSelect(String name) throws Exception { 
		Collection options = getForm().getSelectByName(decorateId(name)).getOptions(); 
		String [] result = new String[options.size()];
		int i=0;
		for (Iterator it = options.iterator(); it.hasNext(); i++) {
			result[i] = ((HtmlOption) it.next()).getValueAttribute();
		}	
		return result;
	}
	
	private String [] getKeysValidValuesWithUIAutocomplete(String name) throws Exception { 
		List<KeyAndDescription> validValues = getValidValuesWithUIAutocomplete(name);
		String [] keys = new String[validValues.size() + 1];
		int i = 0;
		keys[i++] = "";
		for (KeyAndDescription validValue: validValues) {
			keys[i++] = (String) validValue.getKey();
		}
		return keys;
	}

	protected void assertEditable(String name) throws Exception {
		assertEditable(name, "true", XavaResources.getString("must_be_editable"));
	}
	
	protected void assertNoEditable(String name) throws Exception {
		assertEditable(name, "false", XavaResources.getString("must_not_be_editable"));
	}
		
	private void assertEditable(String name, String value, String  message) throws Exception {
		String v = getValue(name + EDITABLE_SUFIX);		
		assertTrue(name + " " + message, value.equals(v)); 		
	}
	
	/**
	 * @since 5.0 
	 */
	protected void assertEditableInCollection(String collection, int row, String name) throws Exception {
		assertEditableInCollection(collection, row, name, "true");
	}
	
	/**
	 * @since 5.0 
	 */
	protected void assertNoEditableInCollection(String collection, int row, String name) throws Exception {
		assertEditableInCollection(collection, row, name, "false");
	}
	
	/**
	 * @since 5.0 
	 */
	protected void assertEditableInCollection(String collection, int row, int column) throws Exception {
		assertEditableInCollection(collection, row, getPropertiesList(collection).get(column), "true");
	}
	
	/**
	 * @since 5.0 
	 */
	protected void assertNoEditableInCollection(String collection, int row, int column) throws Exception {
		assertEditableInCollection(collection, row, getPropertiesList(collection).get(column), "false");
	}
	
	private void assertEditableInCollection(String collection, int row, String name, String editable) throws Exception {
		String elementCollectionPropertyName = collection + "." + row + "." + name;
		MetaModel collectionModel = getMetaModel().getMetaCollection(collection).getMetaReference().getMetaModelReferenced();
		String referenceKeySuffix = ""; 
		if (collectionModel.containsMetaReference(name)) {
			String referenceKey = (String) collectionModel.getMetaReference(name).getMetaModelReferenced().getKeyPropertiesNames().iterator().next();
			referenceKeySuffix = "." + referenceKey;
			
		}
		if (hasElementByName(elementCollectionPropertyName + referenceKeySuffix)) { 
			assertEditable(elementCollectionPropertyName, editable, XavaResources.getString(editable.equals("true")?"must_be_editable":"must_not_be_editable")); 
		}
		else {
			assertTrue(XavaResources.getString("must_be_editable"), editable.equals("false"));
		}
	}
	
	protected void assertListTitle(String expectedTitle) throws Exception {
		HtmlElement element = null;
		try {
			element = (HtmlElement) page.getHtmlElementById("list-title");
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			fail(XavaResources.getString("title_not_displayed"));
			return;
		}				
		assertEquals(XavaResources.getString("incorrect_title"), expectedTitle, element.asNormalizedText());
	}
	
	protected void assertNoListTitle() throws Exception {		
		try {
			page.getHtmlElementById("list-title");
			fail(XavaResources.getString("title_displayed"));
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
		}				
	}
	
	protected void assertListNotEmpty() throws Exception {
		assertTrue(XavaResources.getString("minimum_1_elements_in_list"), getListRowCount() > 0);
	}
	
	protected void assertCollectionNotEmpty(String collection) throws Exception { 
		assertTrue(XavaResources.getString("minimum_1_elements_in_collection", collection), getCollectionRowCount(collection) > 0);
	}
	
	private void assertValidValueExistsWithHtmlSelect(String name, String key, String description) {
		Collection options = getSelectByName(decorateId(name)).getOptions();
		assertTrue(XavaResources.getString("unexpected_valid_values", name), options.size() > 0);
		int i=0;
		boolean found = false;
		for (Iterator it = options.iterator(); it.hasNext(); i++) {
			HtmlOption option = (HtmlOption) it.next();
			if (option.getValueAttribute().equals(key)) {
				found = true;
				assertEquals(XavaResources.getString("unexpected_description", name), description, option.asNormalizedText());
				break;
			}
		}
		if (!found) {
			fail(XavaResources.getString("option_not_found", name, key));
		}
	}
	
	private void assertValidValueExistsWithUIAutocomplete(String name, String key, String description) throws Exception {
		List<KeyAndDescription> validValues = getValidValuesWithUIAutocomplete(name);
		assertTrue(XavaResources.getString("unexpected_valid_values", name), validValues.size() > 0);
		int i = 0;
		boolean found = false;
		for (KeyAndDescription validValue: validValues) {
			if (key.equals((String) validValue.getKey())){
				found = true;
				assertEquals(XavaResources.getString("unexpected_description", name), description, (String) validValue.getDescription());
			}
		}
		if (!found) fail(XavaResources.getString("option_not_found", name, key));
	}
	
	/**
	 * @since 5.3
	 */
	protected void assertValidValueExists(String name, String key, String description) throws Exception {
		try {
			assertValidValueExistsWithHtmlSelect(name, key, description);
		}
		catch (ElementNotFoundException ex) {
			assertValidValueExistsWithUIAutocomplete(name, key, description);
		}
	}
		
	protected static String getPort() { 
		if (port == null) {
			port = getXavaJunitProperties().getProperty("port", "8080");
		}
		return port;		
	}
	
	protected static String getHost() { 
		if (host == null) {
			host = getXavaJunitProperties().getProperty("host", "localhost");
		}
		return host;		
	}	
	
	/**
	 * 
	 * @since 6.3
	 */
	protected String getContextPath() { 
		return getXavaJunitProperties().getProperty("contextPath", "/" + application + "/");
	}

		
	private static String getDefaultLocale() {
		if (!isDefaultLocaleSet) {
			defaultLocale = getXavaJunitProperties().getProperty("locale");
			if (Is.emptyString(defaultLocale)) {
				defaultLocale = null;
			}
			isDefaultLocaleSet = true;
		}
		return defaultLocale;
	}
			
	/**
	 * From file xava-junit.properties
	 * 
	 * @since 4m6  Before it was called getProperty() 
	 */
	static public String getXavaJUnitProperty(String id) {
		return getXavaJunitProperties().getProperty(id);
	}
	
	
	/**
	 * From file xava-junit.properties 
	 * 
	 * @since 4m6  Before it was called getProperty()
	 */	
	static public String getXavaJUnitProperty(String id, String defaultValue) { 
		return getXavaJunitProperties().getProperty(id, defaultValue);
	}
	
	private static Properties getXavaJunitProperties() {
		if (xavaJunitProperties == null) {
			try {
				xavaJunitProperties = new Properties();
				URL resource = ModuleTestBase.class.getClassLoader().getResource("xava-junit.properties");
				if (resource != null) {
					xavaJunitProperties.load(resource.openStream());
				}
			}
			catch (IOException ex) {					
				log.warn(XavaResources.getString("xavajunit_properties_file_warning"),ex);
			}							
		}
		return xavaJunitProperties;
	}
	
	private void resetForm() throws Exception {			
		waitUntilPageIsLoaded();		
		setNewModuleIfChanged(); 
		form = null; 		
	}
	
	private void setNewModuleIfChanged() throws Exception {		
		HtmlInput lastModuleChangeInput = (HtmlInput) page.getElementById("xava_last_module_change"); 
		if (lastModuleChangeInput == null) return;
		String lastModuleChange = lastModuleChangeInput.getValue();
		if (Is.emptyString(lastModuleChange)) return;
		String [] modules = lastModuleChange.split("::"); 
		if (!module.equals(modules[0])) return;
		previousModule = module; 
		module = modules[1];		
	}

	private void resetLoginForm() throws Exception { 
		form = ((HtmlForm)page.getForms().get(getLoginFormIndex()));		
	}
			
	/**
	 * Current HtmlForm (of HtmlUnit). <p>
	 * 
	 * This allow you to access directly to html form elements, but
	 * <b>it is not very advisable</b> because this will cause dependency
	 * to HTML and HtmlUnit so it will be difficult migrate to another 
	 * presentation technology.
	 */	
	protected HtmlForm getForm() { 
		if (form == null) {			
			form = page.getFormByName(decorateId("form"));
		}
		return form;	
	}	
		
	private int getLoginFormIndex() throws Exception {
		return loginFormIndex;
	}
	
	
	private int getFormIndexForInputElement(String inputElementName) throws SAXException {
		Collection forms = page.getForms(); 
		int i = 0;
		for (Iterator it = forms.iterator(); it.hasNext(); i++) {
			HtmlForm form = (HtmlForm) it.next();			
			if (hasInput(form, inputElementName)) {					
				return i;				
			}
		}
		return 0;
	}	
	
	private boolean hasElementByName(String elementName) {
		return !page.getElementsByName(decorateId(elementName)).isEmpty();
	}
	
	private boolean hasElementById(String elementId) { 
		try {
			getElementById(elementId);
			return true;
		}
		catch (org.htmlunit.ElementNotFoundException ex) {			
			return false;
		}		
	}		
		
	private boolean hasInput(HtmlForm form, String inputName) {
		try {
			form.getInputByName(inputName);
			return true;
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			return false;
		}
	}

	/**
	 * This allows you testing using HtmlUnit APIs directly. <p>
	 * 
	 * The use of <b>this method is discoraged</b> because binds your test
	 * to a HTML implementation.
	 * Before to use this method look for another more abstract method
	 * in this class.<br>
	 * By default CSS is disabled for performance, 
	 * if you need that CSS works for your test, write this line:
	 * <pre>
	 * getWebClient().setCssEnabled(true);
	 * </pre>
	 */
	protected WebClient getWebClient() {
		return client;
	}
	
	/**
	 * It creates and returns a new WebClient each time you call it. <p>
	 * 
	 * Also, it's the method used to create the WebClient used for the test, so
	 * you can overwrite it to refine the WebClient used for your test.<br>
	 * 
	 * This allows you testing using HtmlUnit APIs directly.<br>
	 * 
	 * The use of <b>this method is discoraged</b> because binds your test
	 * to a HTML implementation.
	 * Before to use this method look for another more abstract method
	 * in this class.<br>
	 * By default CSS is disabled for performance, 
	 * if you need that CSS works for your test, write this line:
	 * <pre>
	 * WebClient webClient = createWebClient();
	 * webClient.setCssEnabled(true);
	 * </pre>
	 * 
	 * @since 7.3
	 */	
	protected WebClient createWebClient() throws Exception {  
		WebClient newClient = new WebClient(getBrowserVersion()); 
		newClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		newClient.getOptions().setPrintContentOnFailingStatusCode(false); 
		newClient.getOptions().setThrowExceptionOnScriptError(false);
		newClient.getOptions().setCssEnabled(false);
		newClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
	    newClient.setCssErrorHandler(new SilentCssErrorHandler());
	    newClient.setIncorrectnessListener((message, origin) -> {});
	    return newClient;
	}


	protected String getLocale() { 
		return locale == null?getDefaultLocale():locale;
	}

	protected void setLocale(String locale) throws Exception {
		this.locale = locale;
		resetModule(); 
	}		
	
	/**
	 * Returns a string representations of the key of a POJO
	 * from the POJO itself. <p>
	 * 
	 * Useful for obtaining the value to put into a combo (a descriptions list)
	 * from a POJO object.<br>
	 */
	protected String toKeyString(Object pojo) throws Exception { 
		return MetaModel.getForPOJO(pojo).toString(pojo);
	}
	
	private String getLoadedParts() { 
		Page page = getWebClient().getCurrentWindow().getEnclosedPage();
		if (!(page instanceof HtmlPage)) return "";
		try {
			HtmlInput input = (HtmlInput) ((HtmlPage) page).getHtmlElementById(Ids.decorate(application, module, "loaded_parts"));
			return input.getValue();
		}
		catch (org.htmlunit.ElementNotFoundException ex) {
			return "";
		}
	}
	
	protected void assertNotEquals(String msg, String value1, String value2) { 
		assertTrue(msg + ": " + value1, !Is.equal(value1, value2));		
	}
	
	/**
	 * @since 4m1
	 */
	protected void assertDialog() throws Exception { 
		assertTrue(XavaResources.getString("dialog_must_be_displayed"), getTopDialog() != null || getTopDialog(previousModule) != null); 
	}

	/**
	 * @since 4m1
	 */
	
	protected void assertNoDialog() throws Exception { 
		assertTrue(XavaResources.getString("dialog_must_not_be_displayed"), getTopDialog() == null && getTopDialog(previousModule) == null); 
	}
	
	/**
	 * @since 4m1
	 */
	protected void closeDialog() throws Exception { 
		assertDialog();
		HtmlElement title = (HtmlElement) getElementById(getTopDialog()).getPreviousSibling();
		HtmlElement closeButton = title.getElementsByTagName("button").get(0); 
		page = closeButton.click();		
		resetForm();		
	}
	
	private String getTopDialog(String module) throws Exception { 
		int level = 0;
		for (level = 10; level > 0; level--) {
			try {
				HtmlElement el = page.getHtmlElementById(Ids.decorate(application, module, "dialog" + level)); 
				if (el != null && el.hasChildNodes()) break;
			}
			catch (ElementNotFoundException ex) {
			}			
		}
		if (level == 0) return null;
		return "dialog" + level;		
	}

	
	private String getTopDialog() throws Exception {
		return getTopDialog(module);
	}

	/**
	 * @since 4m1
	 */	
	protected void assertDialogTitle(String expectedTitle) throws Exception {		
		HtmlElement header = (HtmlElement) page.getHtmlElementById(decorateId(getTopDialog())).getPreviousSibling();
		HtmlElement title = header.getElementsByAttribute("span", "class", "ui-dialog-title").get(0);
		String label = title.asNormalizedText();
		assertEquals(XavaResources.getString("unexpected_dialog_title"), expectedTitle, label); 
	}
	
	

	/**
	 * This allows you testing using HtmlUnit APIs directly. <p>
	 * 
	 * The use of <b>this method is discouraged</b> because binds your test
	 * to a HTML implementation.
	 * Before to use this method look for another more abstract method
	 * in this class.
	 * 
	 * @since 4m4
	 */
	protected HtmlPage getHtmlPage() {
		return page;
	}

	/**
	 * Assert the content of a comment of DISCUSSION property as text,
	 * including header data like user and date. <p> 
	 * 
	 * @since 5.6
	 */
	protected void assertDiscussionCommentText(String name, int row, String extendedText) {
		assertEquals(extendedText, getDiscussionCommentText(name, row));
	}
	
	/**
	 * Get the content of a comment of DISCUSSION property as text,
	 * including header data like user and date. <p> 
	 * 
	 * @since 5.7
	 */
	protected String getDiscussionCommentText(String name, int row) { 
		int i=0;
		for (DomElement comment: getDiscussionCommentsElement(name).getChildElements()) {
			if (i++ == row) {
				return comment.asNormalizedText();
			}
		}
		throw new IndexOutOfBoundsException(XavaResources.getString("not_discussion_comment_at", row)); 
	}
	
	/**
	 * Get the content of a comment of DISCUSSION property as text,
	 * just the content of the comment excluding header data.<p> 
	 * 
	 * @since 5.7
	 */
	protected String getDiscussionCommentContentText(String name, int row) {  
		return getDiscussionCommentText(name, row).split("\n", 2)[1];
	}
	
	/**
	 * Assert the content of a comment of DISCUSSION property as text,
	 * just the content of the comment excluding header data. <p> 
	 * 
	 * @since 5.7
	 */
	protected void assertDiscussionCommentContentText(String name, int row, String expectedText) {  
		assertEquals(expectedText, getDiscussionCommentContentText(name, row));
	}

	/**
	 * Assert the amount of comments in a DISCUSSION property. <p> 
	 * 
	 * @since 5.6
	 */	
	protected void assertDiscussionCommentsCount(String name, int expectedCount) {
		HtmlElement comments = getDiscussionCommentsElement(name);
		assertEquals(expectedCount + 1, comments.getChildElementCount());
		assertFalse(isDisplayed(comments.getLastElementChild())); 
	}

	/**
	 * Post a new comment into a DISCUSSION property. <p> 
	 * 
	 * @since 5.6
	 */		
	protected void postDiscussionComment(String name, String commentContent) throws Exception { 
		String discussionId = getValue(name);
		String comment = commentContent.contains("'")? "\"<p>" + commentContent + "</p>\"": "'<p>" + commentContent + "</p>'"; 		
		getHtmlPage().executeJavaScript("discussionEditor.postMessageHtmlUnit('" + application + "', '" + module + "', '" + discussionId + "', " + comment + ")");
	}
	
	private HtmlElement getDiscussionElement(String name) {   
		return getHtmlPage().getHtmlElementById(decorateId("editor_" + name));
	}
	
	private HtmlElement getDiscussionCommentsElement(String name) {  
		return getDiscussionElement(name).getOneHtmlElementByAttribute("div", "class", "ox-discussion");
	}

	/**
	 * @since 5.6 
	 */
	protected void selectListConfiguration(String title) throws Exception {
		selectComboOption(getSelectListConfigurations(), title);
	}

	/**
	 * @since 5.6 
	 */	
	protected void assertListSelectedConfiguration(String expectedTitle) {   
		String title = getSelectListConfigurations().getSelectedOptions().get(0).asNormalizedText();
		assertEquals(expectedTitle, title); 
	}
	
	/**
	 * @since 5.6 
	 */	
	protected void assertListAllConfigurations(String ... expectedTitles) throws Exception {
		assertAllComboOptions(getSelectListConfigurations(), expectedTitles); 
	}
	
	
	/**
	 * @since 5.8 
	 */
	protected void selectGroupBy(String title) throws Exception {  
		selectComboOption(getSelectGroupBy(), title);
	}
	
	private void selectComboOption(HtmlSelect combo, String title) throws Exception {     
		HtmlOption option =  combo.getOptionByText(title);
		option.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
	}	

	/**
	 * @since 5.8 
	 */	
	protected void assertAllGroupBys(String ... expectedTitles) throws Exception {  
		assertAllComboOptions(getSelectGroupBy(), expectedTitles);
	}
	
	private void assertAllComboOptions(HtmlSelect combo, String ... expectedTitles) throws Exception {   
		List<String> titles = new ArrayList<String>();
		for (HtmlOption option: combo.getOptions()) {
			String title = option.asNormalizedText(); 
			titles.add(title);
		}
		List<String> expectedTitleList = Arrays.asList(expectedTitles);
		assertEquals(expectedTitleList, titles);
	}
	
	private HtmlSelect getSelectListConfigurations() { 
		try { 
			return getSelectInListTitle(0);	
		}
		catch (ElementNotFoundException ex) {
			return getPhoneListConfigurationsSelect();
		}
	}
	
	private HtmlSelect getPhoneListConfigurationsSelect() { 
		HtmlElement selectParent = getHtmlPage().getHtmlElementById("phone_list_configurations"); 
		return (HtmlSelect) selectParent.getElementsByTagName("select").get(0); 
	}

	
	private HtmlSelect getSelectGroupBy() { 
		return getSelectInListTitle(1);
	}
	
	private HtmlSelect getSelectInListTitle(int index) { 
		HtmlBody body = (HtmlBody) getHtmlPage().getElementsByTagName("body").get(0); 
		HtmlElement listTitle = body.getOneHtmlElementByAttribute("td", "class", "ox-list-title"); // This class depend on the style
		return (HtmlSelect) listTitle.getElementsByTagName("select").get(index); 
	}
	

	/**
	 * Assert if the property with an upload editor of has a file associated.
	 * 
	 * For properties with stereotypes IMAGE, PHOTO, FILE, etc.
	 * 
	 * Example:
	 * <pre>
	 * assertFile("photo", "image");
	 * </pre>
	 * 
	 * It tries to recover the file from the server and verify if it is of contentType. 
	 * 
	 * @param property  The property name of the current view with an upload editor
	 * @param contentType  The content type, such as image, image/png, text/html, etc.
	 * @since 6.2
	 */		
	protected void assertFile(String property, String contentType) throws Exception { 
		assertFile(property, 0, contentType, true); 
	}
	
	/**
	 * Assert if the property with an upload editor has a file associated.
	 * 
	 * For properties with stereotypes IMAGE, PHOTO, FILE, etc.
	 * 
	 * Example:
	 * <pre>
	 * assertFile("photo");
	 * </pre>
	 * 
	 * It tries to recover the file from the server. 
	 * 
	 * @param property  The property name of the current view with an upload editor
	 * @since 6.2
	 */		
	protected void assertFile(String property) throws Exception { 
		assertFile(property, 0, null, true); 
	}	

	/**
	 * Assert if the property with an upload editor with multiple files has a file associated in certain position.
	 * 
	 * For properties with stereotypes IMAGES_GALLERY, FILES, etc.
	 * 
	 * Example:
	 * <pre>
	 * assertFile("attachments", 2, "text/html");
	 * </pre>
	 * 
	 * It tries to recover the file from the server and verify if it is of contentType. 
	 * 
	 * @param property  The property name of the current view with an upload editor
	 * @param index  Position of the file
	 * @param contentType  The content type, such as image, image/png, text/html, etc.
	 * @since 6.2
	 */			
	protected void assertFile(String property, int index, String expectedType) throws Exception {  
		assertFile(property, index, expectedType, true); 
	}
	
	/**
	 * Assert if the property with an upload editor has not a file associated.
	 * 
	 * For properties with stereotypes IMAGE, PHOTO, FILE, etc.
	 * 
	 * Example:
	 * <pre>
	 * assertNoFile("photo");
	 * </pre>
	 *  
	 * @param property  The property name of the current view with an upload editor
	 * @since 6.2
	 */		
	protected void assertNoFile(String property) throws Exception {   
		assertFile(property, 0, null, false); 
	}	
	
	private void assertFile(String property, int index, String contentType, boolean present) throws Exception {  
		String fileURL = (String) getHtmlPage().executeJavaScript(
			"var input = document.getElementById('" + decorateId(property) + "');" +
			"uploadEditor.getFileURL(input)" 		
		).getJavaScriptResult();
		
		URL url = getHtmlPage().getWebResponse().getWebRequest().getUrl(); 
		String urlPrefix = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort(); 
		fileURL = urlPrefix + fileURL;
		HtmlElement input = getElementById(property);
		String dataFiles = input.getAttribute("data-files");
		if (!Is.emptyString(dataFiles)) {
			String [] ids = dataFiles.split(",");
			fileURL = fileURL + "&fileId=" + ids[index];
		}
		else {
			assertEquals(XavaResources.getString("unknown_id_for_file", index), 0, index); 
		}
		TopLevelWindow fileWindow = (TopLevelWindow) getWebClient().openWindow(new URL(fileURL), "loadingFile");
		WebResponse response = fileWindow.getEnclosedPage().getWebResponse();
		if (present) {
			assertTrue(XavaResources.getString("file_not_obtained"), response.getContentAsString().length() > 0); 
			if (contentType != null) {
				assertTrue(XavaResources.getString("file_not_of_expected_type", contentType, response.getContentType()), 
					response.getContentType().startsWith(contentType)); 
			}
		}
		else {
			assertTrue(XavaResources.getString("image_obtained"), response.getContentAsString().length() == 0);
		}		
		fileWindow.close(); 
	}


	/**
	 * Upload a file in a property with an upload editor.
	 * 
	 * For properties with stereotypes IMAGE, PHOTO, IMAGES_GALLERY, FILE, FILES, etc.
	 * 
	 * Example:
	 * <pre>
	 * uploadFile("photo", "test-images/cake.gif");
	 * </pre>
	 * 
	 * @param property  The property name of the current view with an upload editor
	 * @param fileURL  If the URL is relative it starts from the current project, if it is absolute (starts with /) it is used 'as is'.  
	 * @since 6.2
	 */
	protected void uploadFile(String property, String fileURL) throws Exception { 
		String imageAbsoluteURL = fileURL.startsWith("/")?
			fileURL:System.getProperty("user.dir") + "/"+ fileURL; 
		String decoratedProperty = decorateId(property);
		HtmlFileInput input = (HtmlFileInput) getHtmlPage().getElementById(decoratedProperty);
		input.setValue(imageAbsoluteURL);
		assertEquals(XavaResources.getString("input_for_upload_not_name"), "", input.getNameAttribute()); // Having name makes that JUnit and real browser behaves different, 																								
																						// and real browser would fail with element collection (try with Car module)
		getHtmlPage().executeJavaScript(
			"var formData = new FormData();" +
			"var input = document.getElementById('" + decoratedProperty + "');" +
			"formData.append('file', input.files[0]);" +
			"var xhr = new XMLHttpRequest();" +
			"xhr.open('POST', uploadEditor.getUploadURL(input));" +
			"xhr.send(formData);"				
		);
		waitAJAX();
	}

	/**
	 * Remove the current file from a property with an upload editor.
	 * 
	 * For properties with stereotypes IMAGE, PHOTO, FILE, etc.
	 * 
	 * Example:
	 * <pre>
	 * removeFile("photo");
	 * </pre>
	 * 
	 * It's like clicking in the X button of the file, if the file is actually deleted
	 * from database depends on the concrete editor.
	 * 
	 * @param property  The property name of the current view with an upload editor
	 * @since 6.2
	 */	
	protected void removeFile(String property) throws Exception { 
		String value = getValue(property);
		String fileId = value.length() == 32?value:null;
		removeFile(property, fileId); 
	}
	
	private void removeFile(String property, String fileId) throws Exception {
		String fileIdParam = fileId == null?"":" + '&fileId=" + fileId + "'";
		getHtmlPage().executeJavaScript(
			"var input = document.getElementById('" + decorateId(property) + "');" +	
			"var xhr = new XMLHttpRequest();" +
			"xhr.open('DELETE', uploadEditor.getUploadURL(input)" + fileIdParam + ");" +
			"xhr.send(null);"				
		);
		waitAJAX();	
	}	

	/**
	 * Assert the amount of files in a property with an upload multiple editor.
	 * 
	 * For properties with stereotypes IMAGES_GALLERY, FILES, etc.
	 * 
	 * Example:
	 * <pre>
	 * assertFilesCount("screenshots", 5);
	 * </pre>
	 * 
	 * @param property  The property name of the current view with an upload multiple editor
	 * @param expectedCount The expected number of files
	 * @since 6.2
	 */		
	protected void assertFilesCount(String property, int expectedCount) throws Exception { 
		HtmlInput input = getHtmlPage().getHtmlElementById(decorateId(property));
		if (expectedCount > 0) {
			assertEquals(expectedCount, input.getAttribute("data-files").split(",").length);
			assertEquals("", input.getAttribute("data-empty"));
		}
		else {
			assertEquals("", input.getAttribute("data-files"));
			assertEquals("true", input.getAttribute("data-empty"));
		}
	}
	
	/**
	 * Remove a file from a property with an upload multiple editor.
	 * 
	 * For properties with stereotypes IMAGES_GALLERY, FILES, etc.
	 * 
	 * Example:
	 * <pre>
	 * removeFile("screenshots", 2);
	 * </pre>
	 * 
	 * It's like clicking in the X button of the file, if the file is actually deleted
	 * from database depends on the concrete editor.
	 * 
	 * @param property  The property name of the current view with an upload multiple editor
	 * @param index  The position (0 based) of the file to remove
	 * @since 6.2
	 */		
	protected void removeFile(String property, int index) throws Exception {
		HtmlInput input = getHtmlPage().getHtmlElementById(decorateId(property));
		String fileIds = input.getAttribute("data-files");
		String fileId = fileIds.split(",")[index];
		removeFile(property, fileId); 
	}
	
	/**
	 * Move an element of collection from a row to another. <p>
	 * 
	 * It is for @OrderColumn collection where the user can reorder the rows with the mouse.<br>
	 * WARNING! This method does not work for all "from, to" combinations, so if it does not work
	 * just try with other combination.
	 * 
	 * Example:
	 * <pre>
	 * moveRow("details", 2, 0);
	 * </pre>
	 *  
	 * @param collection  The name of the collection
	 * @param from  Index of the origin row
	 * @param to  Index of the target row
	 * @since 6.6
	 */
	protected void moveRow(String collection, int from, int to) throws Exception {
		moveRow(collection, from, to, false);
	}
	
	/**
	 * Move an element of collection from a row to another. <p>
	 * 
	 * It is for @OrderColumn collection where the user can reorder the rows with the mouse.<br>
	 * WARNING! This method does not work for all "from, to" combinations, so if it does not work
	 * just try with other combination.
	 * 
	 * Example:
	 * <pre>
	 * moveRow("details", 2, 0);
	 * </pre>
	 *  
	 * @param collection  The name of the collection
	 * @param from  Index of the origin row
	 * @param to  Index of the target row
	 * @param classRequired  If true it verifies the CSS class of target row to assures it is sortable
	 * @since 6.6
	 */
	protected void moveRow(String collection, int from, int to, boolean classRequired) throws Exception {   
		// This method does not work for all "from, to" combinations, at least with HtmlUnit 2.15
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement fromRow = table.getRow(from + 1);
		HtmlElement fromHandler = fromRow.getElementsByAttribute("i", "class", "xava_handle mdi mdi-swap-vertical ui-sortable-handle").get(0);
		fromHandler.mouseDown();
		HtmlElement toRow = table.getRow(to + 1);
		if (classRequired) assertTrue(toRow.getAttribute("class").contains("xava_sortable_element_row"));
		toRow.mouseMove();
		toRow.mouseUp();
		Thread.sleep(500); 		
	}

}
