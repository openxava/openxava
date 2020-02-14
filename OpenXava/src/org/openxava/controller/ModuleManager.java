package org.openxava.controller;

import java.lang.reflect.*;
import java.util.*;
import java.util.Collections;
import java.util.logging.*;
import java.util.stream.*;

import javax.inject.*;
import javax.persistence.*;
import javax.servlet.http.*;
import javax.validation.*;

import org.apache.commons.collections.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.logging.*;
import org.hibernate.envers.internal.tools.*;
import org.openxava.actions.*;
import org.openxava.application.meta.*;
import org.openxava.component.*;
import org.openxava.controller.meta.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.validators.ValidationException;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.meta.*;
import org.openxava.web.style.*;

/**
 * @author Javier Paniza
 */

public class ModuleManager implements java.io.Serializable {

	private static Log log = LogFactory.getLog(ModuleManager.class);

	static {
		MetaControllers.setContext(MetaControllers.WEB);
		XSystem._setLogLevelFromJavaLoggingLevelOfXavaPreferences();
		Logger.getLogger("org.directwebremoting").setLevel(Level.SEVERE); 
		setVersionInfo();
		log.info(getProduct() + " " + getVersion() + " (" + getVersionDate() + ")");
	}
	
	static private String product;

	final static public String getProduct() {
		return product;
	}	

	static private String version;

	final static public String getVersion() {
		return version;
	}

	static private String versionDate;

	final static private String getVersionDate() {
		return versionDate;
	}
	
	static private void setVersionInfo() {
		try {
			Properties properties = new Properties();
			properties.load(ModuleManager.class
					.getResourceAsStream("/xava-version.properties"));
			product = properties.getProperty("product", "OpenXava"); 
			version = properties.getProperty("version", "UNKNOW");
			versionDate = properties.getProperty("date", "UNKNOW");
		} catch (Exception ex) {
			log.warn(XavaResources.getString("openxava_version_problems"), ex);
			version = "UNKNOW";
			versionDate = "UNKNOW";
		}
	}

	private static String DEFAULT_MODE = IChangeModeAction.LIST;
	private static final String[] MODIFIED_CONTROLLERS = { "__MODIFIED_CONTROLLER__" }; 
	private static final String XAVA_META_ACTIONS_IN_LIST = "xava_metaActionsInList";

	private String user;
	private Collection metaActionsOnInit;
	private Collection metaActionsOnEachRequest;
	private Collection metaActionsBeforeEachRequest;
	private Collection metaActionsAfterEachRequest;
	private boolean moduleInitiated;
	private String defaultActionQualifiedName;
	private MetaModule metaModule;
	private String[] controllersNames;
	private Collection metaActions;
	private String applicationName;
	private String moduleName;
	private Set hiddenActions;
	private String modeControllerName;
	private Collection metaControllers;
	private MetaController metaControllerMode;
	transient private HttpSession session;
	private static Object refiner;
	private static Object reseter; 
	private String viewName = null;
	private String modeName;
	private String nextModule;
	private Stack<String> previousModules;
	private String defaultView = null;
	private boolean formUpload = false;
	private String previousMode;
	private boolean executingAction = false;
	private boolean reloadAllUINeeded;
	private boolean reloadViewNeeded;
	private boolean actionsChanged;
	private boolean showDialog;
	private boolean hideDialog;
	private MetaAction lastExecutedMetaAction;
	private int dialogLevel = 0;
	private boolean modifiedControllers = false;
	private String moduleDescription;
	private boolean resetFormPostNeeded = false;
	private boolean actionsAddedOrRemoved;
	private Collection<MetaSubcontroller> metaSubControllers;
	private Map<String,Collection<MetaAction>> subcontrollersMetaActions;
	private Collection<MetaControllerElement> metaControllerElements;
	private Set<String> actionsForPermalink;
	private boolean buttonsVisible = true;
	private boolean viewKeyEditable;
	private String moduleURL;
	private String goListAction = "Mode.list";
	private boolean formUploadNextTime = false; 

	/**
	 * HTML action bind to the current form.
	 * 
	 * @return
	 */
	public String getFormAction(HttpServletRequest request) {
		if (!isFormUpload())
			return "";
		String module = !getPreviousModules().isEmpty() ? previousModules
				.elementAt(0) : getModuleName();
		Object portletActionURL = request.getSession().getAttribute(
				Ids.decorate(getApplicationName(), module,
						"xava.portlet.uploadActionURL"));
		if (portletActionURL == null)
			return "";
		return "action='" + portletActionURL + "'";
	}

	private void updateXavaMetaActionsInList() {
		getContext().put(getApplicationName(), getModuleName(),
				XAVA_META_ACTIONS_IN_LIST, getMetaActions());
	}

	public void addMetaAction(MetaAction action) {
		if (getMetaActions().contains(action)) return;
		getMetaActions().add(action);
		getMetaControllerElements().add(action);
		defaultActionQualifiedName = null;
		actionsChanged = true;
		actionsAddedOrRemoved = true;		
		this.controllersNames = MODIFIED_CONTROLLERS;
	}

	public void removeMetaAction(MetaAction action) {
		getMetaActions().remove(action);
		getMetaControllerElements().remove(action);
		defaultActionQualifiedName = null;
		actionsChanged = true;
		actionsAddedOrRemoved = true;		
		this.controllersNames = MODIFIED_CONTROLLERS;
	}

	public Collection getRowActionsNames() {
		Collection actions = new ArrayList();
		for (MetaAction action : getMetaActions()) {
			if (action.isHidden())
				continue;
			if (action.getQualifiedName().equals(
					getEnvironment().getValue("XAVA_LIST_ACTION")))
				continue;
			if (action.isInEachRow()) {
				actions.add(action.getQualifiedName());
			}
		}
		return actions;
	}

	/**
	 * @since 5.5.1
	 */
	public Collection<MetaAction> getSubcontrollerMetaActions(String controllerName) {
		if (subcontrollersMetaActions == null) subcontrollersMetaActions = new HashMap<String, Collection<MetaAction>>();
		Collection<MetaAction> result = subcontrollersMetaActions.get(controllerName);
		if (result == null) {
			result = new ArrayList(MetaControllers.getMetaController(controllerName).getMetaActions());
			try {
				refine(result);
			} catch (Exception ex) {
				log.error(XavaResources.getString("controller_actions_error"), ex);
				return new ArrayList();
			}
			subcontrollersMetaActions.put(controllerName, result);
		}
		return result;
	}
	
	public Collection<MetaControllerElement> getMetaControllerElements() {
		 if (metaControllerElements == null){
			metaControllerElements = new ArrayList<MetaControllerElement>();
			try {
				Iterator it = getMetaControllers().iterator();
				while(it.hasNext()){
					MetaController mc = (MetaController) it.next();
					metaControllerElements.addAll(mc.getAllMetaControllerElements());
				}
				refine(metaControllerElements); 
			} 
		 	catch (Exception ex) {
				metaActions = null;
				metaControllerElements = null; 
				log.error(XavaResources.getString("controller_actions_error"), 
						ex);
				return new ArrayList();
			}
		 }
		return metaControllerElements;
	}

	public Collection getSubcontrollers() {
		if (metaSubControllers == null) {
			metaSubControllers = new ArrayList<MetaSubcontroller>();
			Iterator it = getMetaControllers().iterator();
			while (it.hasNext()) {
				MetaController mc = (MetaController) it.next();
				metaSubControllers.addAll(mc.getMetaSubcontrollers());
			}
		}
		return metaSubControllers;
	}
	
	private boolean appliesToListEditor(MetaAction action) { 
		if (!isListMode()) return true;
		if (!action.isProcessSelectedItems()) return true;
		if (getTab().getModelName() == null) return true;
		if (!Is.emptyString(getTab().getGroupBy())) return false; 
		MetaEditor metaEditor = MetaWebEditors.getMetaEditorByName(getTab().getEditor()); 
		if (metaEditor == null) {
			log.warn(XavaResources.getString("editor_not_exist", getTab().getEditor()));
			return true; 
		}
		return metaEditor.isSelectableItems();
	}
	
	public Collection<MetaAction> getMetaActions() {
		if (metaActions == null) {
			
			if (this.controllersNames == MODIFIED_CONTROLLERS) {
				metaActions = getMetaControllerElements().stream()
					.filter(e -> e instanceof MetaAction)
					.map(e -> (MetaAction) e)
					.collect(Collectors.toList());
				return metaActions;
			}
			
			Collection<MetaAction> ma = (Collection<MetaAction>) getContext()
					.get(getApplicationName(), getModuleName(),
							XAVA_META_ACTIONS_IN_LIST);
			if (isListMode() && ma != null && ma.size() > 0) {
				metaActions = ma;
				return metaActions;
			}

			try {
				Iterator it = getMetaControllers().iterator();
				metaActions = new ArrayList();
				while (it.hasNext()) {
					MetaController contr = (MetaController) it.next();
					metaActions.addAll(contr.getAllMetaActions());
				}
				refine(metaActions); 
			} catch (Exception ex) {
				metaActions = null;
				metaControllerElements = null; 
				log.error(XavaResources.getString("controller_actions_error"),
						ex);
				return new ArrayList();
			}
		}
		return metaActions;
	}
	
	public Collection getMetaActionsOnInit() {
		if (metaActionsOnInit == null) {
			try {
				Iterator it = getMetaControllers().iterator();
				metaActionsOnInit = new ArrayList();
				while (it.hasNext()) {
					MetaController contr = (MetaController) it.next();
					metaActionsOnInit.addAll(contr.getMetaActionsOnInit());
				}
				metaActionsOnInit.addAll(getMetaControllerMode().getMetaActionsOnInit()); 
			} catch (Exception ex) {
				log.error(
						XavaResources.getString("controller_init_action_error"),
						ex);
				return Collections.EMPTY_LIST;
			}
		}
		return metaActionsOnInit;
	}
	
	public Collection getMetaActionsMode() {
		try {
			return getMetaControllerMode().getAllNotHiddenMetaActions();
		} catch (Exception ex) {
			log.error(XavaResources.getString("controllers_actions_error",
					getModeControllerName()), ex);
			return new ArrayList();
		}
	}

	/**
	 * An iterator over <code>getMetaActions()</code> and
	 * <code>getMetaActionsMode()</code>.
	 * <p>
	 */
	public Iterator getAllMetaActionsIterator() {
		return org.apache.commons.collections.IteratorUtils
				.chainedIterator(new Iterator[] { getMetaActions().iterator(),
						getMetaActionsMode().iterator() });
	}

	private MetaController getMetaControllerMode() throws XavaException {
		if (metaControllerMode == null) {
			metaControllerMode = MetaControllers
					.getMetaController(getModeControllerName());
		}
		return metaControllerMode;
	}

	private Collection getMetaControllers() throws XavaException {
		if (metaControllers == null) {
			metaControllers = new ArrayList();
			String[] names = getControllersNames(); 
			for (int i = 0; i < names.length; i++) {
				if ("ListOnly".equals(names[i])) { // To not break old code. The combination of Void as mode controller and ListOnly as regular controller to create a list only module 
					setModeControllerName("Mode");
					continue; 
				}
				metaControllers.add(MetaControllers.getMetaController(names[i]));
			}
		}
		return metaControllers;
	}

	private void setupModuleControllers() throws XavaException {
		Collection controllers = getMetaModule().getControllersNames();
		String[] names = new String[controllers.size()];
		controllers.toArray(names);
		setControllersNames(names);
		getPreviousControllers().clear();
	}

	private String getModeControllerName() {
		return modeControllerName;
	}

	private void setModeControllerName(String controllerName) {
		metaControllerMode = null;
		this.modeControllerName = controllerName;
		if (!Is.anyEqual(this.modeControllerName, "Mode", "DetailOnly", "Void")) {
			this.modeControllerName = "Mode";
			String candidateGoListAction = controllerName + ".list";
			if (MetaControllers.containsMetaAction(candidateGoListAction)) {
				this.goListAction = candidateGoListAction; 
				log.warn(XavaResources.getString("mode_controller_not_recognized_only_go_list_action", controllerName, goListAction));
			}
			else {
				log.warn(XavaResources.getString("mode_controller_not_supported", controllerName));
			}
		}
	}
	
	/** @since 6.1.2 */
	public String getGoListAction() { 
		return goListAction;
	}

	public boolean hasProcessRequest(HttpServletRequest request) {
		return isFormUpload() || // May be that in this way upload forms does
									// not work well in multimodule
				actionOfThisModule(request);
	}
	
	private boolean actionOfThisModule(HttpServletRequest request) {
		return Is.equal(request.getParameter("xava_action_module"),
				getModuleName())
				&& Is.equal(request.getParameter("xava_action_application"),
						getApplicationName());
	}

	public void execute(HttpServletRequest request, Messages errors,
			Messages messages) {
		try {
			if (errors.isEmpty()) { // Only it's executed the action if there
									// aren't errors
				if (isFormUpload()) {
					parseMultipartRequest(request);
				}
				String xavaAction = getParameter(request, "xava_action");
				if (!Is.emptyString(xavaAction)) {
					String actionValue = request
							.getParameter("xava_action_argv");
					if ("undefined".equals(actionValue))
						actionValue = null;
					String range = request.getParameter("xava_action_range");
					if ("undefined".equals(range))
						range = null;
					String alreadyProcessed = request
							.getParameter("xava_action_already_processed");
					if ("undefined".equals(alreadyProcessed)
							|| alreadyProcessed == null)
						alreadyProcessed = "";

					// range
					int first = Is.empty(range) ? 0 : Integer.parseInt(range
							.substring(0, range.indexOf("_")));
					int last = Is.empty(range) ? 0 : Integer.parseInt(range
							.substring(range.indexOf("_") + 1));
					//
					for (int i = first; i <= last; i++) {
						if (!alreadyProcessed.contains("_" + i + "_")) {
							String av = Is.empty(range) ? actionValue
									: actionValue + ",row=" + i;
							MetaAction a = MetaControllers
									.getMetaAction(xavaAction);
							long ini = System.currentTimeMillis();
							executeAction(a, errors, messages, av, request);
							long time = System.currentTimeMillis() - ini;
							log(request, xavaAction + " " + time + "ms"); 
						}
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			errors.add("no_execute_action");
		}
	}
	
	public void log(HttpServletRequest request, String message) {
		log.debug(request.getRemoteHost() + " " + Users.getCurrent() + " " + message); 
	}

	private String getParameter(HttpServletRequest request, String parameter)
			throws FileUploadException {
		if (isFormUpload()) {
			List items = (List) request.getAttribute("xava.upload.fileitems");
			for (Iterator it = items.iterator(); it.hasNext();) {
				FileItem item = (FileItem) it.next();
				if (parameter.equals(Ids.undecorate(item.getFieldName())))
					return item.getString();
			}
			return null;
		} else {
			return request.getParameter(parameter);
		}
	}

	private void executeAction(MetaAction metaAction, Messages errors,
			Messages message, HttpServletRequest request) {
		executeAction(metaAction, errors, message, null, request);
	}

	private void executeAction(MetaAction metaAction, Messages errors,
			Messages message, String propertyValues, HttpServletRequest request) {
		executingAction = true;
		try {
			IAction action = metaAction.createAction();
			executeAction(action, metaAction, errors, message, propertyValues,
					request);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			errors.add("no_execute_action", metaAction.getId());
		} finally {
			executingAction = false;
		}
	}
	
	/** @since 5.9 */
	public boolean isExecutingAction() { 
		return executingAction;
	}
	
	/**
	 * @since 5.7 
	 */
	public void executeAction(String qualifiedActionName, Messages errors, 
			Messages messages, HttpServletRequest request) {
		MetaAction metaAction = MetaControllers.getMetaAction(qualifiedActionName);
		executeAction(metaAction, errors, messages, null, request);
	}
	
	/**
	 * @since 6.2 
	 */	
	public void executeAction(String qualifiedActionName, Messages errors,  
		Messages messages, String propertyValues, HttpServletRequest request) 
	{ 
		MetaAction metaAction = MetaControllers.getMetaAction(qualifiedActionName);
		executeAction(metaAction, errors, messages, propertyValues, request);
	}

	public void executeAction(IAction action, Messages errors,
			Messages messages, HttpServletRequest request) {
		executeAction(action, null, errors, messages, null, request);
	}
	
	/** @since 5.9 */
	public boolean isActionAvailable(MetaAction metaAction, Messages errors, 
		Messages messages, String propertyValues, HttpServletRequest request) 
	{
		try {
			if (implementsAvailableAction(metaAction)) {
				IAvailableAction action = (IAvailableAction) metaAction.createAction(); 
				prepareAction(action, metaAction, errors, messages, propertyValues, request); 
				return action.isAvailable();
			}
			return true;
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			errors.add("no_determine_available_action", metaAction.getId()); 
			return false; 
		}
	}
	
	private boolean implementsAvailableAction(MetaAction metaAction) throws Exception { 
		return IAvailableAction.class.isAssignableFrom(Class.forName(metaAction.getClassName()));
	}

	private void executeAction(IAction action, MetaAction metaAction,
			Messages errors, Messages messages, String propertyValues,
			HttpServletRequest request) { 
		try {
			Object previousView = getContext().get(applicationName, moduleName,	"xava_view");
			prepareAction(action, metaAction, errors, messages, propertyValues, request); 
			if (action instanceof IRemoteAction) {
				IRemoteAction remote = (IRemoteAction) action;
				remote.executeBefore();
				if (remote.isExecuteOnServer()) {
					remote = Server.execute(remote, remote.getPackageName());
					errors.removeAll();
					messages.removeAll();
					errors.add(remote.getErrors());
					messages.add(remote.getMessages());
					remote.setErrors(errors);
					remote.setMessages(messages);
				}
				remote.executeAfter();
				action = remote;
			} else {
				action.execute();
			}
			if (action instanceof IChangeModeAction) {
				IChangeModeAction modeChange = (IChangeModeAction) action;
				String nextMode = modeChange.getNextMode();
				if (IChangeModeAction.PREVIOUS_MODE.equals(nextMode)) {
					restorePreviousMode();
				} else if (!Is.emptyString(nextMode)) {
					memorizePreviousMode();
					setModeName(nextMode);
				}
				if (isListMode() && actionsAddedOrRemoved) { 
					updateXavaMetaActionsInList();
				}
			}
			if (formUploadNextTime) {
				setFormUpload(true);
				formUploadNextTime = false;
			}
			else {
				setFormUpload(false);
			}
			if (action instanceof ICustomViewAction) {
				ICustomViewAction customViewAction = (ICustomViewAction) action;
				String newView = customViewAction.getCustomView();
				if (ICustomViewAction.PREVIOUS_VIEW.equals(newView)) {
					restorePreviousCustomView();
					reloadViewNeeded = true;
				} else if (!Is.emptyString(newView)) {
					memorizeCustomView();
					setViewName(newView);
					reloadViewNeeded = true;
				}
			}
			if (action instanceof IChangeControllersAction) {
				IChangeControllersAction changeControllersAction = (IChangeControllersAction) action;
				String[] nextControllers = changeControllersAction
						.getNextControllers();
				setControllers(nextControllers);
				metaSubControllers = null;
			}
			if (action instanceof IHideActionAction) {
				String actionToHide = ((IHideActionAction) action)
						.getActionToHide();
				if (actionToHide != null) {
					addToHiddenActions(actionToHide);
				}
			}
			if (action instanceof IHideActionsAction) {
				String[] actionsToHide = ((IHideActionsAction) action)
						.getActionsToHide();
				if (actionsToHide != null) {
					for (int i = 0; i < actionsToHide.length; i++) {
						if (actionsToHide[i] != null) {
							addToHiddenActions(actionsToHide[i]);
						}
					}
				}
			}
			if (action instanceof IShowActionAction) {
				String actionToShow = ((IShowActionAction) action)
						.getActionToShow();
				if (actionToShow != null) {
					removeFromHiddenActions(actionToShow);
				}
			}
			if (action instanceof IShowActionsAction) {
				String[] actionsToShow = ((IShowActionsAction) action)
						.getActionsToShow();
				if (actionsToShow != null) {
					for (int i = 0; i < actionsToShow.length; i++) {
						if (actionsToShow[i] != null) {
							removeFromHiddenActions(actionsToShow[i]);
						}
					}
				}
			}
			if (action instanceof ILoadFileAction) {
				setFormUpload(((ILoadFileAction) action).isLoadFile());
			}
			getObjectsFromAction(action, metaAction);
			if (action instanceof IForwardAction) {
				IForwardAction forward = (IForwardAction) action;
				String uri = forward.getForwardURI();
				if (!Is.emptyString(uri)) {
					request.getSession().setAttribute("xava_forward", uri);
					request.getSession().setAttribute(
							"xava_forward_inNewWindow",
							String.valueOf(forward.inNewWindow()));
				}
			}
			if (action instanceof IMultipleForwardAction) {
				IMultipleForwardAction forward = (IMultipleForwardAction) action;
				String[] uri = forward.getForwardURIs();
				if (uri != null && uri.length > 0) {
					request.getSession().setAttribute("xava_forwards", uri);
				}
			}
			nextModule = null;
			if (action instanceof IChangeModuleAction) {
				IChangeModuleAction moduleChange = (IChangeModuleAction) action;
				nextModule = moduleChange.getNextModule();
				if (!Is.emptyString(nextModule)) {
					if (moduleChange.hasReinitNextModule()) {
						getContext().put(getApplicationName(), nextModule,
								"manager", null);
					}
					request.setAttribute("xava.sendParametersToTab", "false");
				}
			}
			if (action instanceof IChainAction) {
				IChainAction chainable = (IChainAction) action;
				String nextAction = chainable.getNextAction();
				if (!Is.emptyString(nextAction)) {
					MetaAction nextMetaAction = null;
					if (nextAction.indexOf('.') < 0 && metaAction != null) {
						nextMetaAction = metaAction.getMetaController()
								.getMetaAction(nextAction);
					} else {
						nextMetaAction = MetaControllers
								.getMetaAction(nextAction);
					}
					String argv = null;
					if (chainable instanceof IChainActionWithArgv) {
						argv = ((IChainActionWithArgv) chainable)
								.getNextActionArgv();
					}
					executeAction(nextMetaAction, action.getErrors(),
							action.getMessages(), argv, request);
				}
			}
			if (action instanceof IJavaScriptPostAction) {
				request.setAttribute("xava.postjs", ((IJavaScriptPostAction) action).getPostJavaScript());
			}
			if (!reloadViewNeeded) {
				Object currentView = getContext().get(applicationName,
						moduleName, "xava_view");
				reloadViewNeeded = currentView != previousView;
			}
			if (metaAction != null && !metaAction.isAfterEachRequest()) {   
				lastExecutedMetaAction = metaAction;
			}
			if (!(metaAction == null && executingAction)) { // For avoiding
															// commit on
															// OnChange actions
															// triggered from a
															// regular action
															// execution
				doCommit(); // after executing action
			}
		} catch (Exception ex) {
			manageException(metaAction, errors, messages, ex);
		}
	}
	
	private void prepareAction(IAction action, MetaAction metaAction, Messages errors, Messages messages,
			String propertyValues, HttpServletRequest request) throws Exception { 
		action.setErrors(errors);
		action.setMessages(messages);
		action.setEnvironment(getEnvironment());
		setObjectsInAction(action, metaAction, request);
		Map xavaValues = setPropertyValues(action, propertyValues);
		if (action instanceof IModuleContextAction) {
			((IModuleContextAction) action).setContext(getContext());
		}
		if (action instanceof IModelAction) {
			((IModelAction) action).setModel(getModelName());
		}
		if (action instanceof IRequestAction) {
			((IRequestAction) action).setRequest(request);
		}

		if (action instanceof IJDBCAction) {
			((IJDBCAction) action)
					.setConnectionProvider(DataSourceConnectionProvider
							.getByComponent(getModelName()));
		}

		if (action instanceof IPropertyAction) {
			String keyProperty = Ids.undecorate((String) xavaValues
					.get("xava.keyProperty"));
			if (Is.emptyString(keyProperty)) {
				throw new XavaException("property_action_error",
						action.getClass());
			}
			int idx = keyProperty.lastIndexOf('.');
			View view = (View) getContext().get(request, "xava_view");
			if (idx < 0) {
				((IPropertyAction) action).setProperty(keyProperty);
				((IPropertyAction) action).setView(view);
			} else {
				String subviewName = keyProperty.substring(0, idx);
				String propertyName = keyProperty.substring(idx + 1);
				((IPropertyAction) action).setProperty(propertyName);
				((IPropertyAction) action).setView(getSubview(view,
						subviewName));
			}
		}

		if (action instanceof IProcessLoadedFileAction) {
			List fileItems = (List) request
					.getAttribute("xava.upload.fileitems");
			String error = (String) request
					.getAttribute("xava.upload.error");
			if (!Is.emptyString(error))
				errors.add(error);
			((IProcessLoadedFileAction) action)
					.setFileItems(fileItems == null ? Collections.EMPTY_LIST
							: fileItems);
		}
		
	}
	
	/**
	 * 
	 * @since 4.2.2
	 */
	public void setControllers(String[] nextControllers) {
		if (nextControllers != IChangeControllersAction.SAME_CONTROLLERS) {
			if (nextControllers == IChangeControllersAction.DEFAULT_CONTROLLERS) {
				setupModuleControllers();
			} else if (nextControllers == IChangeControllersAction.PREVIOUS_CONTROLLERS) {
				restorePreviousControllers();
			} else {
				memorizeControllers();
				setControllersNames(nextControllers);
			}
		}
	}

	private void manageException(MetaAction metaAction, Messages errors, Messages messages, Exception ex) {
		if (ex instanceof ValidationException) {
			errors.add(((ValidationException) ex).getErrors());
			messages.removeAll();
			doRollback();
		} else if (ex instanceof javax.validation.ConstraintViolationException) {
			manageConstraintViolationException(metaAction, errors, messages,
					(javax.validation.ConstraintViolationException) ex);
		} else if (ex instanceof RollbackException) {			
			if (!errors.contains()) { 
				if (ex.getCause() instanceof javax.validation.ConstraintViolationException) {
					manageConstraintViolationException(metaAction, errors, messages,
							(javax.validation.ConstraintViolationException) ex.getCause());
				} else if (ex.getCause() != null
						&& ex.getCause().getCause() instanceof javax.validation.ConstraintViolationException) {
					manageConstraintViolationException(metaAction, errors,
							messages, (ConstraintViolationException) ex.getCause()
									.getCause());
				} else {
					manageRegularException(metaAction, errors, messages, ex);
				}
			} 
			doRollback();
			
		} else if (ex instanceof javax.persistence.PersistenceException) {
			if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException
					&& ((org.hibernate.exception.ConstraintViolationException) ex
							.getCause()).getConstraintName() != null) {
				manageHibernateConstraintViolationlException(
						metaAction,
						errors,
						messages,
						(org.hibernate.exception.ConstraintViolationException) ex
								.getCause());
			} else {
				manageRegularException(metaAction, errors, messages, ex);
			}
			doRollback();
		} else if (ex instanceof javax.validation.ValidationException) {
			errors.add(ex.getMessage());
			messages.removeAll();
			doRollback();			
		} else {
			manageRegularException(metaAction, errors, messages, ex);
			doRollback();
		}
	}
	
	private void manageHibernateConstraintViolationlException(
			MetaAction metaAction, Messages errors, Messages messages,
			org.hibernate.exception.ConstraintViolationException ex) {
		String constraintName = ex.getConstraintName().toLowerCase();
		errors.add(constraintName);
		messages.removeAll();
	}

	private void manageConstraintViolationException(MetaAction metaAction,
			Messages errors, Messages messages,
			javax.validation.ConstraintViolationException ex) {
		for (javax.validation.ConstraintViolation<?> violation : ex
				.getConstraintViolations()) {
			String attrName = violation.getPropertyPath() == null ? null
					: violation.getPropertyPath().toString();
			String domainClass = violation.getRootBeanClass().getSimpleName();
			String message = violation.getMessage();			
			if (message.startsWith("{") && message.endsWith("}")) {
				message = message.substring(1, message.length() - 1);
			}
			javax.validation.metadata.ConstraintDescriptor<?> descriptor = violation
					.getConstraintDescriptor();
			java.lang.annotation.Annotation annotation = descriptor
					.getAnnotation();
			if (annotation instanceof javax.validation.constraints.AssertTrue || 
				annotation instanceof org.openxava.annotations.EntityValidator) 
			{
				Object bean = violation.getRootBean();
				errors.add(message, bean);
				continue;
			}			
			Object invalidValue = violation.getInvalidValue();
			if (Is.emptyString(attrName) || domainClass == null	|| invalidValue == null) {
				errors.add(message);
			} else {
				errors.add("invalid_state", attrName, domainClass, "'" +
						   XavaResources.getString(message) + "'", invalidValue);
			}
		}
		messages.removeAll();
	}

	private void manageRegularException(MetaAction metaAction, Messages errors,
			Messages messages, Exception ex) {
		log.error(ex.getMessage(), ex);
		if (metaAction != null) {
			errors.add("no_execute_action", metaAction.getId(),
					"'" + ex.getLocalizedMessage() + "'");
		} else {
			errors.add("no_execute_action", "", "'" + ex.getLocalizedMessage()
					+ "'");
		}
		messages.removeAll();
	}

	private void memorizePreviousMode() {
		// At the moment we only memorize the last one
		previousMode = modeName == null ? DEFAULT_MODE : modeName;
	}

	private void restorePreviousMode() {
		// At the moment we only have memorized the last one
		if (previousMode != null)
			setModeName(previousMode);
	}

	private View getSubview(View view, String memberName) throws XavaException {
		memberName = Ids.undecorate(memberName);
		if (memberName.indexOf('.') < 0) {
			return view.getSubview(memberName);
		}
		StringTokenizer st = new StringTokenizer(memberName, ".");
		String subviewName = st.nextToken();
		String nextMember = st.nextToken();
		return getSubview(view.getSubview(subviewName), nextMember);
	}

	/**
	 * Init JPA and Hibernate in order to process the current request.
	 */
	public void resetPersistence() { 
		org.openxava.hibernate.XHibernate.setCmt(false);
		org.openxava.jpa.XPersistence.reset();
		org.openxava.hibernate.XHibernate.reset();
		if (reseter != null) {
			try {
				XObjects.execute(reseter, "reset", HttpSession.class, session);
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("reset_persistence_error"), ex);
				throw new XavaException("reset_persistence_error");
			}
			
		}
	}

	/**
	 * Commit the current JPA manager and Hibernate session, if they exist. Never fails.
	 * <p>
	 * If fails it does a rollback and put a message in log, but it does not throw an exception.
	 * This method is intended to be used after rendering the UI content, not for commit in your own logic. If
	 * you want commit from your logic use BaseAction.commit(), XPersistence.commit() or XHibernate.commit().  
	 */
	public static void commit() { // Usually after render page 
		try {
			doCommit();
			org.openxava.jpa.XPersistence.reset();
			org.openxava.hibernate.XHibernate.reset();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			doRollback();
		}
	}

	private static void doCommit() { 
		XPersistence.commit();
		XHibernate.commit();
	}

	static void doRollback() { 
		XPersistence.rollback();
		XHibernate.rollback();
	}

	public void parseMultipartRequest(HttpServletRequest request)
			throws FileUploadException {
		List fileItems = (List) request.getAttribute("xava.upload.fileitems");
		if (fileItems != null) return;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1000000);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {  
			request.setAttribute("xava.upload.fileitems",
				upload.parseRequest(request));
		}
		catch (FileUploadBase.InvalidContentTypeException ex) {
			request.setAttribute("xava.upload.fileitems", Collections.EMPTY_LIST);
			log.warn(XavaResources.getString("invalid_content_parsing_multipart") + ": " + ex.getMessage());
		}
	}
	
	public void formUploadNextTime() { 
		formUploadNextTime = true;
	}

	public Environment getEnvironment() throws XavaException {
		return getMetaModule().getEnvironment();
	}

	public void setControllersNames(String[] names) {
		metaControllers = null;
		metaActions = null;
		metaActionsOnInit = null;
		defaultActionQualifiedName = null;
		this.controllersNames = names;
		actionsChanged = true;
		subcontrollersMetaActions = null;
		metaControllerElements = null;
	}

	public void restorePreviousControllers() throws XavaException {
		Stack previousControllers = getPreviousControllers();
		if (previousControllers.isEmpty()) {
			setupModuleControllers();
			return;
		}
		Object controllers = previousControllers.pop();
		if (controllers instanceof String[]) { // The list of controllers
			setControllersNames((String[]) controllers);
		} else { // A collection of metaactions
			setControllersNames(MODIFIED_CONTROLLERS); 
			this.metaControllerElements = (Collection) controllers; 
			this.subcontrollersMetaActions = null; 
			this.defaultActionQualifiedName = null;
			this.actionsChanged = true;
			this.modifiedControllers = true; 
			this.metaActions = null; 
		}
	}

	private void restorePreviousCustomView() throws XavaException {
		Stack previousCustomViews = (Stack) getObjectFromContext("xava_previousCustomViews");
		if (previousCustomViews.isEmpty()) {
			setViewName(ICustomViewAction.DEFAULT_VIEW);
			return;
		}
		String view = (String) previousCustomViews.pop();
		setViewName(view);
	}

	public void memorizeControllers() throws XavaException {
		Stack previousControllers = getPreviousControllers();
		if (this.controllersNames == MODIFIED_CONTROLLERS) {
			previousControllers.push(this.metaControllerElements); 
		} else {
			previousControllers.push(this.controllersNames);
		}
	}
	
	private Stack getPreviousControllers() {
		Stack previousControllers = (Stack) getObjectFromContext("xava_previousControllers");
		return previousControllers;
	}

	private void memorizeCustomView() throws XavaException {
		Stack previousCustomViews = (Stack) getObjectFromContext("xava_previousCustomViews");
		previousCustomViews.push(this.viewName);
	}

	/**
	 * Returs all propeties with the 'xava.' prefix, these properties are not
	 * assigned to the action and they will be used internally ModuleManager.
	 */
	private Map setPropertyValues(IAction action, String propertyValues)
			throws Exception {
		if (Is.emptyString(propertyValues))
			return Collections.EMPTY_MAP;
		StringTokenizer st = new StringTokenizer(propertyValues, ",");
		Map values = new HashMap();
		Map xavaValues = null;
		while (st.hasMoreTokens()) {
			String propertyValue = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(propertyValue, "()=");
			if (!st2.hasMoreTokens()) {
				log.warn(XavaResources.getString("action_property_warning"));
				break;
			}
			String name = st2.nextToken().trim();
			if (!st2.hasMoreTokens()) {
				log.warn(XavaResources.getString("action_property_warning"));
				break;
			}
			String value = st2.nextToken().trim();
			if (!name.startsWith("xava.")) {
				values.put(name, value);
			} else {
				if (xavaValues == null)
					xavaValues = new HashMap();
				xavaValues.put(name, value);
			}
		}
		PropertiesManager mp = new PropertiesManager(action);
		mp.executeSetsFromStrings(values);
		return xavaValues == null ? Collections.EMPTY_MAP : xavaValues;
	}

	private void getObjectsFromAction(IAction action, MetaAction metaAction)
			throws XavaException {
		getObjectsFromActionUseObjects(action, metaAction);
		getObjectsFromActionInjectFields(action, metaAction);
	}

	private void getObjectsFromActionInjectFields(IAction action,
			MetaAction metaAction) {
		for (Field f : Classes.getFieldsAnnotatedWith(action.getClass(),
				Inject.class)) {
			String objectName = getObjectNameFromActionField(f);
			String property = f.getName();
			if (action instanceof IOnChangePropertyAction
					&& "view".equals(property)) { // In on change actions view
													// is injected from View
				continue;
			}
			Object value = null;
			try {
				f.setAccessible(true);
				value = f.get(action);
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				String actionName = metaAction == null ? action.getClass()
						.getSimpleName() : metaAction.getName();
				throw new XavaException("get_property_action_value_error",
						property, actionName);
			}
			if (value != null) {
				// The nulls are not assigned and thus we allow to have trasient
				// attributes
				// that it can lost on go and return from server without danger
				// of alter
				// the session value
				setObjectInContext(objectName, value);
			}
		}
	}

	private void getObjectsFromActionUseObjects(IAction action,
			MetaAction metaAction) {
		if (metaAction == null)
			return;
		PropertiesManager mp = new PropertiesManager(action);
		Iterator it = metaAction.getMetaUseObjects().iterator();
		while (it.hasNext()) {
			MetaUseObject metaUseObject = (MetaUseObject) it.next();
			String property = metaUseObject.getActionProperty();
			Object value = null;
			try {
				value = mp.executeGet(property);
			} catch (Exception ex) {
				log.warn(XavaResources.getString(
						"get_property_action_value_error", property,
						metaAction.getName()), ex);
			}
			if (value != null) {
				// The nulls are not assigned and thus we allow to have trasient
				// attributes
				// that it can lost on go and return from server without danger
				// of alter
				// the session value
				setObjectInContext(metaUseObject.getName(), value);
			}
		}
	}

	private String getObjectNameFromActionField(Field f) {
		String objectName = null;
		if (f.isAnnotationPresent(Named.class)) {
			Named named = f.getAnnotation(Named.class);
			objectName = named.value();
		} else {
			objectName = f.getName();
		}
		return objectName;
	}

	private void setObjectsInAction(IAction action, MetaAction metaAction,
			HttpServletRequest request) throws XavaException {
		setObjectsToActionUseObjects(action, metaAction, request);
		setObjectsToActionInjectFields(action, metaAction, request);
	}

	private void setObjectsToActionInjectFields(IAction action,
			MetaAction metaAction, HttpServletRequest request) {
		for (Field f : Classes.getFieldsAnnotatedWith(action.getClass(),
				Inject.class)) {
			String objectName = getObjectNameFromActionField(f);
			String property = f.getName();
			if (action instanceof IOnChangePropertyAction
					&& "view".equals(property)) { // In on change actions view
													// is injected from View
				continue;
			}
			Object value = getObjectFromContext(objectName);
			assignRequestToObject(request, value);

			try {
				f.setAccessible(true);
				f.set(action, value);

			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				String actionName = metaAction == null ? action.getClass()
						.getSimpleName() : metaAction.getName();
				throw new XavaException("set_property_action_value_error",
						property, actionName);
			}
			getSession().setAttribute(objectName, value);
		}
	}

	private void setObjectsToActionUseObjects(IAction action,
			MetaAction metaAction, HttpServletRequest request) {
		if (metaAction == null)
			return;
		PropertiesManager mp = new PropertiesManager(action);
		Iterator it = metaAction.getMetaUseObjects().iterator();
		while (it.hasNext()) {
			MetaUseObject metaUseObject = (MetaUseObject) it.next();
			String objectName = metaUseObject.getName();
			String property = metaUseObject.getActionProperty();
			Object value = getObjectFromContext(objectName);
			if (value == null) {
				value = createObject(objectName);
				setObjectInContext(objectName, value);
			}

			assignRequestToObject(request, value);

			try {
				mp.executeSet(property, value);
				log.warn(XavaResources.getString("use_inject_instead",
						property, metaAction.getQualifiedName()));
			} catch (Exception ex) {
				log.warn(XavaResources.getString(
						"set_property_action_value_error", property,
						metaAction.getName()), ex);
			}
			getSession().setAttribute(objectName, value);
		}
	}

	private void assignRequestToObject(HttpServletRequest request, Object object) {
		try {
			PropertiesManager pm = new PropertiesManager(object);
			if (pm.exists("request") && pm.hasSetter("request")) {
				pm.executeSet("request", request);
			}
		} catch (Exception ex) {
			log.warn(XavaResources.getString("inject_request_warning"), ex);
		}
	}

	private Object getObjectFromContext(String objectName) throws XavaException {
		return getContext().get(getApplicationName(), getModuleName(),
				toExactContextObjectName(objectName));
	}

	private String toExactContextObjectName(String objectName) {
		String exactName = objectName;
		if (exactName.indexOf('_') < 0
				&& !MetaControllers.containsMetaObject(objectName)) {
			exactName = "xava_" + objectName;
			if (!MetaControllers.containsMetaObject(exactName)) {
				for (String prefix : MetaControllers.getObjectPrefixes()) {
					exactName = prefix + "_" + objectName;
					if (MetaControllers.containsMetaObject(exactName)) {
						break;
					}
					exactName = objectName;
				}
			}
		}
		return exactName;
	}

	private void setObjectInContext(String objectName, Object value)
			throws XavaException {
		getContext().put(getApplicationName(), getModuleName(),
				toExactContextObjectName(objectName), value);
	}

	private ModuleContext getContext() {
		return (ModuleContext) getSession().getAttribute("context");
	}

	private Object createObject(String objectName) throws XavaException {
		MetaObject metaObject = MetaControllers.getMetaObject(objectName);
		return metaObject.createObject();
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getViewURL() {
		String viewName = getViewName();
		String r = null;
		if (viewName.startsWith("xava/"))
			r = viewName.substring(5);
		else
			r = "../" + viewName;
		if (r.toLowerCase().indexOf(".jsp") < 0)
			r = r + ".jsp";
		return r;
	}

	private String getViewName() {
		if (IChangeModeAction.LIST.equals(getModeName())) {
			return "xava/list";
		}
		if (viewName == null) {
			viewName = "xava/detail";
		} else if (dialogLevel > 0 && viewName.equals(defaultView))
			return "xava/detail";

		return viewName;
	}

	private void setViewName(String newView) throws XavaException {
		if (ICustomViewAction.DEFAULT_VIEW.equals(newView)) {
			viewName = defaultView;
		} else {
			viewName = newView;
		}
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String newName) throws XavaException {
		if (Is.equal(applicationName, newName))
			return;
		applicationName = newName;
		moduleName = null;
		controllersNames = null;
		reset();
	}

	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @return <tt>true</tt> if is new.
	 */
	public void setModuleName(String newModule) throws XavaException {
		if (Is.equal(moduleName, newModule))
			return;
		moduleName = newModule;
		metaControllerMode = null;
		reset();
		setupModuleControllers();
		if (!Is.emptyString(getMetaModule().getModeControllerName())) {
			setModeControllerName(getMetaModule().getModeControllerName());
			modeName = null;
		} else if (MetaComponent.get(getMetaModule().getModelName())
				.isTransient()) {
			setModeControllerName("DetailOnly");
			modeName = null;
		}
		if (!Is.emptyString(getMetaModule().getWebViewURL())) {
			defaultView = getMetaModule().getWebViewURL();
			setViewName(defaultView);
		}
		View view = (View) getContext().get(applicationName, moduleName,
				"xava_view");
		view.setModelName(getModelName());
		view.setViewName(getXavaViewName());
	}

	private String[] getControllersNames() {
		return controllersNames == null ? new String[0] : controllersNames;
	}

	public MetaModule getMetaModule() throws ElementNotFoundException, 
			XavaException {
		if (metaModule == null) {
			if (Is.emptyString(applicationName, moduleName)) {
				throw new XavaException("application_and_module_required");
			}
			metaModule = MetaApplications.getMetaApplication(applicationName)
					.getMetaModule(moduleName);

		}
		return metaModule;
	}

	public String getModuleDescription() {
		if (Is.emptyString(this.moduleDescription)) {
			try {
				return getMetaModule().getMetaApplication().getLabel() + " - "
						+ getMetaModule().getLabel();
			} catch (Exception ex) {
				return XavaResources.getString("unknow_module");
			}
		} else {
			return this.moduleDescription;
		}
	}

	public void setModuleDescription(String moduleDescription) {
		this.moduleDescription = moduleDescription;
	}

	public String getModelName() throws XavaException {
		return getMetaModule().getModelName();
	}

	public String getTabName() throws XavaException {
		return getMetaModule().getTabName();
	}

	public boolean isListMode() {
		return IChangeModeAction.LIST.equals(getModeName());
	}

	@Deprecated
	public boolean isSplitMode() { 
		return false; 
	}
	

	/** @since 4m6 */
	public boolean isDetailMode() {
		return IChangeModeAction.DETAIL.equals(getModeName());
	}
	
	/** @since 6.0 */
	public boolean isDetailModeOnly() { 
		return Is.anyEqual(getModeControllerName(), "DetailOnly", "Void");
	}

	public String getModeName() {
		return modeName == null ? DEFAULT_MODE : modeName;
	}

	private void setModeName(String newModelName) {
		if (Is.equal(modeName, newModelName))
			return;
		reloadAllUINeeded = true;
		modeName = newModelName;
		defaultActionQualifiedName = null;
	}

	public String getDefaultActionQualifiedName() {
		if (defaultActionQualifiedName == null) {
			Iterator it = getMetaActions().iterator();
			int max = -1;
			while (it.hasNext()) {
				MetaAction a = (MetaAction) it.next();
				if (!actionApplies(a)) continue; 
				if (!a.appliesToMode(getModeName())) continue;
				if (a.getByDefault() > max) {
					max = a.getByDefault();
					defaultActionQualifiedName = a.getQualifiedName();
				}
			}
			if (isListMode()) {
				MetaAction a = MetaControllers.getMetaAction("List.filter");
				if (a.getByDefault() > max) {
					max = a.getByDefault();
					defaultActionQualifiedName = a.getQualifiedName();
				}
			}
		}
		return defaultActionQualifiedName;
	}
	
	/**
	 * 
	 * @return null if not current default action.
	 * @since 5.4.1
	 */
	public MetaAction getDefaultMetaAction() { 
		if (Is.emptyString(getDefaultActionQualifiedName())) return null;
		return MetaControllers.getMetaAction(getDefaultActionQualifiedName());
	}

	public boolean isXavaView(HttpServletRequest request) throws XavaException {
		// For that a upload form does not delete the view data.
		// It's a ad hoc solution. It can be improved
		if (isFormUpload())	return false;
		
		if ("xava/detail".equals(getViewName())
				|| (getViewName().equals(defaultView))) {
			return true; // Because a custom JSP page can use xava_view too
		}
		return hasXavaEditor(request);
	}

	private boolean hasXavaEditor(HttpServletRequest request) {
		for (Object oparameter : request.getParameterMap().keySet()) {
			String parameter = (String) oparameter;
			if (parameter.endsWith("_EDITABLE_")) { // A dirty trick
				return true;
			}
		}
		return false;
	}

	public String getXavaViewName() throws XavaException {
		return getMetaModule().getViewName();
	}

	/*
	 * to execute.jsp, init 'actionsChanged' after execute
	 * 'assignValuesToWebView' and 'initModule'
	 */
	public void preInitModule(HttpServletRequest request) { 
		actionsChanged = false;
		actionsAddedOrRemoved = false;
		showDialog = dialogLevel > 0 && !hasProcessRequest(request) ? true
				: false; // When a dialog is shown and the user click in refresh
							// in browser we'll re-open the dialog
		hideDialog = false;
		reloadViewNeeded = false;
		if (modeControllerName == null) {
			if (!Is.emptyString(XavaPreferences.getInstance().getDefaultModeController())) {
				setModeControllerName(XavaPreferences.getInstance().getDefaultModeController());
			}
			else {
				setModeControllerName(Style.getInstance(request).getDefaultModeController());
			}
		}		
	}

	public void initModule(HttpServletRequest request, Messages errors,	Messages messages) {
		if (!Is.equal(Users.getCurrent(), user)) {
			user = Users.getCurrent();
			moduleInitiated = false;
		}
		if (!moduleInitiated) {
			modeName = getMetaActionsMode().isEmpty() ? IChangeModeAction.DETAIL
					: null;
			moduleInitiated = true;
			executeInitAction(request, errors, messages);
			reloadAllUINeeded = true;
			actionsForPermalink = toQualifiedNames(getMetaActions(), getMetaActionsMode());  
		} else {
			reloadAllUINeeded = false;
		}		
		viewKeyEditable = getView().isKeyEditable(); 
	}
		
	private Set<String> toQualifiedNames(Collection<MetaAction> ... metaActions) { 
		Set<String> result = new HashSet<String>();
		for (Collection<MetaAction> metaActionsGroup: metaActions) {
			for (MetaAction action: metaActionsGroup) {
				result.add(action.getQualifiedName());
			}
		}
		return result;
	}

	public void executeBeforeEachRequestActions(HttpServletRequest request,
			Messages errors, Messages messages) {
		executeActions(request, errors, messages,
				getMetaActionsBeforeEachRequest());
		if (!getMetaActionsBeforeEachRequest().isEmpty()) {
			defaultActionQualifiedName = null;
		}
	}
	
	public void executeBeforeLoadPage(HttpServletRequest request, Messages errors, Messages messages) {  
		try {			
			String detailId =  request.getParameter("detail");
			if (!Is.emptyString(detailId)) {
				getView().setModelName(getMetaModule().getModelName()); 
				Collection metaKeys = getView().getMetaModel().getMetaPropertiesKey();
				if (metaKeys.size() != 1) return;
				MetaProperty metaKey = (MetaProperty) metaKeys.iterator().next();
				getView().setValue(metaKey.getName(), metaKey.parse(detailId));
				if (!isDetailMode()) setModeName(IChangeModeAction.DETAIL);
				String searchAction =  getEnvironment().getValue("XAVA_SEARCH_ACTION");
				MetaAction searchMetaAction = MetaControllers.getMetaAction(searchAction);
				executeAction(searchMetaAction, errors, messages, request); 
			}			
			else {
				String action =  request.getParameter("action");
				if (!Is.emptyString(action)) {
					if (actionsForPermalink.contains(action)) {
						MetaAction metaAction = MetaControllers.getMetaAction(action);
						executeAction(metaAction, errors, messages, request);
					}
					else {
						if (!"SignIn".equals(getModuleName())) { 
							errors.add("action_not_available", "'" + action + "'");
						}
					}
				}	
			}
		}
		catch (Exception ex) {
			errors.add("onload_page_error");
			log.error(XavaResources.getString("onload_page_error"), ex);
		}
	}	
	
	private View getView() { 
		return (View) getContext().get(getApplicationName(), getModuleName(), "xava_view");
	}
	
	private Tab getTab() {  
		return (Tab) getContext().get(getApplicationName(), getModuleName(), "xava_tab");
	}
	
	public boolean hasInitForwardActions() {
		Iterator it = IteratorUtils.chainedIterator(new Iterator[] {
				getMetaActionsOnEachRequest().iterator(),
				getMetaActionsBeforeEachRequest().iterator(),
				getMetaActionsOnInit().iterator() });
		while (it.hasNext()) {
			MetaAction action = (MetaAction) it.next();
			if (isForwardAction(action)) return true; 
		}
		return false;
	}
	
	private boolean isForwardAction(MetaAction action) { 
		try {
			Class actionClass = Class.forName(action.getClassName());
			if (IForwardAction.class.isAssignableFrom(actionClass)) {
				return true;
			}
			if (IChainAction.class.isAssignableFrom(actionClass)) {
				// Because the IChainAction could chain to a IForwardAction
				return true;
			}
			return false;
		} catch (Exception ex) {
			log.warn(XavaResources.getString("is_forward_action_warning",
					action.getQualifiedName()), ex);
			return false;
		}		
	}

	public void executeOnEachRequestActions(HttpServletRequest request,
			Messages errors, Messages messages) {
		executeActions(request, errors, messages, getMetaActionsOnEachRequest());
	}

	public void executeAfterEachRequestActions(HttpServletRequest request,
			Messages errors, Messages messages) {
		executeActions(request, errors, messages,
				getMetaActionsAfterEachRequest());
	}

	private void executeActions(HttpServletRequest request, Messages errors,
			Messages messages, Collection metaActions) {
		if (!Is.emptyString(getNextModule()))
			return; // Another module is executing now
		boolean formUpload = isFormUpload();
		Iterator it = metaActions.iterator();
		while (it.hasNext()) {
			MetaAction a = (MetaAction) it.next();
			if (Is.emptyString(a.getMode())
					|| a.getMode().equals(getModeName())) {
				executeAction(a, errors, messages, request);
			}
		}
		setFormUpload(formUpload);
	}

	private void executeInitAction(HttpServletRequest request, Messages errors,
			Messages messages) {
		if (!Is.emptyString(getNextModule())) return; // Another module is executing now
		Iterator it = getMetaActionsOnInit().iterator();
		while (it.hasNext()) {
			MetaAction a = (MetaAction) it.next();
			executeAction(a, errors, messages, request);
		}
	}
	
	private Collection getMetaActionsOnEachRequest() {
		if (metaActionsOnEachRequest == null) {
			try {
				Iterator it = getMetaControllers().iterator();
				metaActionsOnEachRequest = new ArrayList();
				while (it.hasNext()) {
					MetaController contr = (MetaController) it.next();
					metaActionsOnEachRequest.addAll(contr
							.getMetaActionsOnEachRequest());
				}
			} catch (Exception ex) {
				log.error(XavaResources
						.getString("controller_on_each_request_action_error"),
						ex);
				return Collections.EMPTY_LIST;
			}
		}
		return metaActionsOnEachRequest;
	}

	private Collection getMetaActionsBeforeEachRequest() {
		if (metaActionsBeforeEachRequest == null) {
			try {
				Iterator it = getMetaControllers().iterator();
				metaActionsBeforeEachRequest = new ArrayList();
				while (it.hasNext()) {
					MetaController contr = (MetaController) it.next();
					metaActionsBeforeEachRequest.addAll(contr
							.getMetaActionsBeforeEachRequest());
				}
			} catch (Exception ex) {
				log.error(
						XavaResources
								.getString("controller_before_each_request_action_error"),
						ex);
				return Collections.EMPTY_LIST;
			}
		}
		return metaActionsBeforeEachRequest;
	}

	private Collection getMetaActionsAfterEachRequest() {
		if (metaActionsAfterEachRequest == null) {
			try {
				Iterator it = getMetaControllers().iterator();
				metaActionsAfterEachRequest = new ArrayList();
				while (it.hasNext()) {
					MetaController contr = (MetaController) it.next();
					metaActionsAfterEachRequest.addAll(contr
							.getMetaActionsAfterEachRequest());
				}
			} catch (Exception ex) {
				log.error(
						XavaResources
								.getString("controller_after_each_request_action_error"),
						ex);
				return Collections.EMPTY_LIST;
			}
		}
		return metaActionsAfterEachRequest;
	}

	public String getEnctype() {
		return isFormUpload() ? "ENCTYPE='multipart/form-data'" : "";
	}

	public boolean isButtonBarVisible() {
		if (!buttonsVisible) return false;  
		if (!getMetaActionsMode().isEmpty())
			return true;
		Iterator it = getMetaActions().iterator();
		while (it.hasNext()) {
			MetaAction action = (MetaAction) it.next();
			if (!action.isHidden() && (action.hasIcon() || action.hasImage())) {
				return true;
			}
		}
		if (!getSubcontrollers().isEmpty()) return true;
		return false;
	}
	
	public boolean isCoreViaAJAX(HttpServletRequest request) {
		Style style = (Style) request.getAttribute("style");
		return !style.isInsidePortal() || !getPreviousModules().isEmpty() || getDialogLevel() > 0 || hasInitForwardActions();
	}
	
	/** 
	 * @since 5.8
	 */
	public boolean isBottomButtonsVisible() { 
		if (getDialogLevel() > 0) return true; 
		return buttonsVisible;
	}
	
	/**
	 * Shows the top button bar and the buttons on bottom if they are hidden. 
	 * 
	 * @since 5.8
	 */
	public void showButtons() { 
		if (buttonsVisible) return; 
		buttonsVisible = true;
		reloadAllUINeeded = true; 
	}

	/**
	 * Hides the top button bar and the buttons on bottom. 
	 * 
	 * @since 5.8
	 */	
	public void hideButtons() { 
		if (!buttonsVisible) return; 
		buttonsVisible = false;
		reloadAllUINeeded = true; 
	}

	public boolean isFormUpload() {
		return formUpload;
	}

	private void setFormUpload(boolean b) {
		formUpload = b;
		if (formUpload) getContext().dontGenerateNewWindowIdNextTime(); 
	}
	
	public String getNextModule() {
		return nextModule;
	}

	public void setNextModule(String nextModule) {
		this.nextModule = nextModule;
	}

	private void addToHiddenActions(String action) {
		if (hiddenActions == null)
			hiddenActions = new HashSet();
		hiddenActions.add(action);
		actionsChanged = true;
		defaultActionQualifiedName = null;
		metaActions = null;
		subcontrollersMetaActions = null;
		metaControllerElements = null;
	}

	private void removeFromHiddenActions(String action) {
		if (hiddenActions == null)
			return;
		hiddenActions.remove(action);
		actionsChanged = true;
		defaultActionQualifiedName = null;
		metaActions = null;
		subcontrollersMetaActions = null;
		metaControllerElements = null;
	}
	
	public boolean actionApplies(MetaAction action) { 
		return !action.isHidden() && 
			(hiddenActions == null || !hiddenActions.contains(action.getQualifiedName())) && 
			appliesToListEditor(action) && 
			appliesToDetailState(action);
	}
	
	private boolean appliesToDetailState(MetaAction action) { 
		if (isListMode()) return true;
		if (action.isAvailableOnNew()) return true;
		return !getView().isKeyEditable();
	}

	private void refine(Collection collection) throws Exception { 
		if (refiner == null) return;
		XObjects.execute(refiner, "refine", MetaModule.class, getMetaModule(),
			Collection.class, collection); 
	}

	public boolean isReloadAllUINeeded() {
		return reloadAllUINeeded;
	}

	/**
	 * Is actions list change since the last action execution ?.
	 * <p>
	 */
	public boolean isActionsChanged() {
		if (actionsChanged) return true;
		if (isListMode()) return false;
		if (viewKeyEditable == getView().isKeyEditable()) return false;
		return isAnyNotAvailableAction();
	}
	
	private boolean isAnyNotAvailableAction() { 
		for (MetaAction action: getMetaActions()) {
			if (action.isAvailableOnNew()) return true;
		}
		return false;
	}
	
	/** @since 5.7 */
	public void setActionsChanged(boolean actionsChanged) { 
		this.actionsChanged = actionsChanged; 
	}

	public boolean isReloadViewNeeded() {
		return reloadViewNeeded || isListMode()
				|| !"xava/detail".equals(getViewName());
	}

	public Stack<String> getPreviousModules() {
		if (previousModules == null)
			previousModules = new Stack<String>();
		return previousModules;
	}

	public void setPreviousModules(Stack<String> previousModules) {
		this.previousModules = previousModules;
	}

	public void reset() {
		moduleInitiated = false;
		metaControllers = null;
		metaActions = null;
		defaultActionQualifiedName = null;
		metaModule = null;
		subcontrollersMetaActions = null;
		metaControllerElements = null;
		DescriptionsLists.resetDescriptionsCache(getSession());
	}

	public boolean isShowDialog() {
		return showDialog;
	}

	private void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
		dialogLevel++;
		if (dialogLevel < 0)
			dialogLevel = 0;
	}

	public boolean isHideDialog() {
		return hideDialog;
	}

	public void showDialog() {
		setShowDialog(true);
	}

	public void closeDialog() {
		setHideDialog(true);
	}

	public static void setRefiner(Object newRefiner) {
		refiner = newRefiner;
	}
	
	/**
	 * @since 5.2
	 */
	public static void setReseter(Object newReseter) { 
		reseter = newReseter;
	}	

	private void setHideDialog(boolean hideDialog) {
		reloadAllUINeeded = dialogLevel > 0 && hideDialog;
		this.hideDialog = dialogLevel > 0 && hideDialog;
		dialogLevel--;
		if (dialogLevel < 0)
			dialogLevel = 0;
	}
	
	/**
	 * 
	 * @since 5.7
	 */
	public String getPermanlinkAction() {  
		if (lastExecutedMetaAction == null) return null;
		String lastAction = lastExecutedMetaAction.getQualifiedName();
		if (!lastAction.endsWith(".new")) return null; 
		if (actionsForPermalink.contains(lastAction)) return lastAction;
		return null;
	}

	public MetaAction getLastExecutedMetaAction() {
		return lastExecutedMetaAction;
	}

	public int getDialogLevel() {
		return dialogLevel;
	}

	public void setResetFormPostNeeded(boolean resetFormPostNeeded) {
		this.resetFormPostNeeded = resetFormPostNeeded;
	}

	public boolean isResetFormPostNeeded() {
		return resetFormPostNeeded;
	}

	/**
	 * @since 5.9
	 */
	public String getModuleURL() {
		return moduleURL;
	}

	/**
	 * @since 5.9
	 */	
	public void setModuleURL(HttpServletRequest request) {
		this.moduleURL = request.getScheme() + "://" + request.getServerName() + ":" + 
				request.getServerPort() + request.getAttribute("javax.servlet.forward.request_uri");
	}
	
}
