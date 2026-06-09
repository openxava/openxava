package org.openxava.web.servlets;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import javax.swing.*;
import java.awt.event.*;

import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.actions.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.hotswap.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.web.*;

/**
 * Servlet handling "Hotwire-like" AJAX HTML over-the-wire execution,
 * replacing the legacy DWR Module remote class.
 * 
 * @since 8.0
 */
@WebServlet(name = "hotwire", urlPatterns = "/xava/hotwire")
public class HotwireServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(HotwireServlet.class);

    /**
     * @since 8.0
     */
    private static class Result implements Serializable {

        private static final long serialVersionUID = 1L;

        private String error;
        private boolean reload;
        private String forwardURL;
        private boolean forwardInNewWindow;
        private String[] forwardURLs;
        private String nextModule;
        private String application;
        private String module;
        private boolean showDialog;
        private boolean hideDialog;
        private int dialogLevel;
        private String dialogTitle;
        private boolean resizeDialog;
        private String focusPropertyId;
        private String viewMember;
        private int currentRow;
        private Map<String, int[]> selectedRows;
        private String urlParam;
        private boolean viewSimple;
        private boolean dataChanged;
        private boolean hasPostJS;
        private String[] editorsWithError;
        private String[] editorsWithoutError;
        private String[] propertiesUsedInCalculations;
        private Map<String, StrokeAction> strokeActions;
        private Map<String, Object> changedParts;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public boolean isReload() {
            return reload;
        }

        public void setReload(boolean reload) {
            this.reload = reload;
        }

        public String getForwardURL() {
            return forwardURL;
        }

        public void setForwardURL(String forwardURL) {
            this.forwardURL = forwardURL;
        }

        public boolean isForwardInNewWindow() {
            return forwardInNewWindow;
        }

        public void setForwardInNewWindow(boolean forwardInNewWindow) {
            this.forwardInNewWindow = forwardInNewWindow;
        }

        public String[] getForwardURLs() {
            return forwardURLs;
        }

        public void setForwardURLs(String[] forwardURLs) {
            this.forwardURLs = forwardURLs;
        }

        public String getNextModule() {
            return nextModule;
        }

        public void setNextModule(String nextModule) {
            this.nextModule = nextModule;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public boolean isShowDialog() {
            return showDialog;
        }

        public void setShowDialog(boolean showDialog) {
            this.showDialog = showDialog;
        }

        public boolean isHideDialog() {
            return hideDialog;
        }

        public void setHideDialog(boolean hideDialog) {
            this.hideDialog = hideDialog;
        }

        public int getDialogLevel() {
            return dialogLevel;
        }

        public void setDialogLevel(int dialogLevel) {
            this.dialogLevel = dialogLevel;
        }

        public String getDialogTitle() {
            return dialogTitle;
        }

        public void setDialogTitle(String dialogTitle) {
            this.dialogTitle = dialogTitle;
        }

        public boolean isResizeDialog() {
            return resizeDialog;
        }

        public void setResizeDialog(boolean resizeDialog) {
            this.resizeDialog = resizeDialog;
        }

        public String getFocusPropertyId() {
            return focusPropertyId;
        }

        public void setFocusPropertyId(String focusPropertyId) {
            this.focusPropertyId = focusPropertyId;
        }

        public String getViewMember() {
            return viewMember;
        }

        public void setViewMember(String viewMember) {
            this.viewMember = viewMember;
        }

        public int getCurrentRow() {
            return currentRow;
        }

        public void setCurrentRow(int currentRow) {
            this.currentRow = currentRow;
        }

        public Map<String, int[]> getSelectedRows() {
            return selectedRows;
        }

        public void setSelectedRows(Map<String, int[]> selectedRows) {
            this.selectedRows = selectedRows;
        }

        public String getUrlParam() {
            return urlParam;
        }

        public void setUrlParam(String urlParam) {
            this.urlParam = urlParam;
        }

        public boolean isViewSimple() {
            return viewSimple;
        }

        public void setViewSimple(boolean viewSimple) {
            this.viewSimple = viewSimple;
        }

        public boolean isDataChanged() {
            return dataChanged;
        }

        public void setDataChanged(boolean dataChanged) {
            this.dataChanged = dataChanged;
        }

        public boolean isHasPostJS() {
            return hasPostJS;
        }

        public void setHasPostJS(boolean hasPostJS) {
            this.hasPostJS = hasPostJS;
        }

        public String[] getEditorsWithError() {
            return editorsWithError;
        }

        public void setEditorsWithError(String[] editorsWithError) {
            this.editorsWithError = editorsWithError;
        }

        public String[] getEditorsWithoutError() {
            return editorsWithoutError;
        }

        public void setEditorsWithoutError(String[] editorsWithoutError) {
            this.editorsWithoutError = editorsWithoutError;
        }

        public String[] getPropertiesUsedInCalculations() {
            return propertiesUsedInCalculations;
        }

        public void setPropertiesUsedInCalculations(String[] propertiesUsedInCalculations) {
            this.propertiesUsedInCalculations = propertiesUsedInCalculations;
        }

        public Map<String, StrokeAction> getStrokeActions() {
            return strokeActions;
        }

        public void setStrokeActions(Map<String, StrokeAction> strokeActions) {
            this.strokeActions = strokeActions;
        }

        public Map<String, Object> getChangedParts() {
            return changedParts;
        }

        public void setChangedParts(Map<String, Object> changedParts) {
            this.changedParts = changedParts;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String application = request.getParameter("application");
            String module = request.getParameter("module");

            initRequest(request, response, application, module);

            String additionalParameters = request.getParameter("additionalParameters");
            boolean firstRequest = "true".equals(request.getParameter("firstRequest"));
            String baseFolder = request.getParameter("baseFolder");

            Map<String, Object> values = jsonToMap(request.getParameter("values"));
            Map<String, Object> multipleValues = jsonToMap(request.getParameter("multipleValues"));
            String[] selected = jsonToStringArray(request.getParameter("selectedRows"));
            String[] deselected = jsonToStringArray(request.getParameter("deselectedRows"));

            Result result = executeRequest(request, response, application, module, additionalParameters,
                    values, multipleValues, selected, deselected, firstRequest, baseFolder);

            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(resultToJson(result));

        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing hotwire request", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }

    /**
     * Executes the Module request. Reusable for non-multipart and multipart cases.
     */
    public static Result executeRequest(HttpServletRequest request, HttpServletResponse response,
            String application, String module, String additionalParameters, Map<String, Object> values,
            Map<String, Object> multipleValues, String[] selected, String[] deselected,
            boolean firstRequest, String baseFolder) throws Exception {

        RequestProcessor processor = new RequestProcessor(request, response, application, module,
                additionalParameters, values, multipleValues, selected, deselected, firstRequest, baseFolder);
        return processor.request();
    }

    private static Map<String, Object> jsonToMap(String jsonStr) {
        if (jsonStr == null || "null".equals(jsonStr) || "undefined".equals(jsonStr) || jsonStr.trim().isEmpty()) return null;
        try {
            JSONObject json = new JSONObject(jsonStr);
            if (json.length() == 0) return null; // Empty object {} treated as null (no values)
            Map<String, Object> map = new HashMap<>();
            for (String key : json.keySet()) {
                Object val = json.get(key);
                if (val == JSONObject.NULL) {
                    map.put(key, null);
                } else if (val instanceof JSONArray) {
                    JSONArray arr = (JSONArray) val;
                    String[] strArr = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        strArr[i] = arr.optString(i, "");
                    }
                    map.put(key, strArr);
                } else {
                    map.put(key, val);
                }
            }
            return map;
        } catch (Exception e) {
            log.warn("Failed to parse JSON map parameter: " + jsonStr, e);
            return null;
        }
    }

    private static String[] jsonToStringArray(String jsonStr) {
        if (jsonStr == null || "null".equals(jsonStr) || "undefined".equals(jsonStr) || jsonStr.trim().isEmpty()) return null;
        try {
            JSONArray json = new JSONArray(jsonStr);
            if (json.length() == 0) return null; // Empty array [] treated as null
            String[] array = new String[json.length()];
            for (int i = 0; i < json.length(); i++) {
                array[i] = json.optString(i, null);
            }
            return array;
        } catch (Exception e) {
            log.warn("Failed to parse JSON string array parameter: " + jsonStr, e);
            return null;
        }
    }

    private static String resultToJson(Result result) {
        JSONObject json = new JSONObject();
        json.put("application", result.getApplication());
        json.put("module", result.getModule());
        json.put("error", result.getError());
        json.put("reload", result.isReload());
        json.put("forwardURL", result.getForwardURL());
        json.put("forwardInNewWindow", result.isForwardInNewWindow());
        if (result.getForwardURLs() != null) {
            json.put("forwardURLs", new JSONArray(result.getForwardURLs()));
        }
        json.put("nextModule", result.getNextModule());
        json.put("focusPropertyId", result.getFocusPropertyId());
        json.put("showDialog", result.isShowDialog());
        json.put("hideDialog", result.isHideDialog());
        json.put("resizeDialog", result.isResizeDialog());
        json.put("dialogTitle", result.getDialogTitle());
        json.put("dialogLevel", result.getDialogLevel());
        json.put("viewMember", result.getViewMember());
        json.put("currentRow", result.getCurrentRow());
        json.put("urlParam", result.getUrlParam());
        json.put("viewSimple", result.isViewSimple());
        json.put("hasPostJS", result.isHasPostJS());
        json.put("dataChanged", result.isDataChanged());

        if (result.getPropertiesUsedInCalculations() != null) {
            json.put("propertiesUsedInCalculations", new JSONArray(result.getPropertiesUsedInCalculations()));
        }
        if (result.getEditorsWithError() != null) {
            json.put("editorsWithError", new JSONArray(result.getEditorsWithError()));
        }
        if (result.getEditorsWithoutError() != null) {
            json.put("editorsWithoutError", new JSONArray(result.getEditorsWithoutError()));
        }
        if (result.getSelectedRows() != null) {
            json.put("selectedRows", new JSONObject(result.getSelectedRows()));
        }
        if (result.getStrokeActions() != null) {
            JSONObject strokeActionsJson = new JSONObject();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) result.getStrokeActions()).entrySet()) {
                StrokeAction sa = (StrokeAction) entry.getValue();
                JSONObject saJson = new JSONObject();
                saJson.put("name", sa.getName());
                saJson.put("confirmMessage", sa.getConfirmMessage());
                saJson.put("takesLong", sa.isTakesLong());
                strokeActionsJson.put((String) entry.getKey(), saJson);
            }
            json.put("strokeActions", strokeActionsJson);
        }
        if (result.getChangedParts() != null) {
            JSONObject changedPartsJson = new JSONObject();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) result.getChangedParts()).entrySet()) {
                changedPartsJson.put((String) entry.getKey(), entry.getValue());
            }
            json.put("changedParts", changedPartsJson);
        }
        return json.toString();
    }

    /**
     * Inner class processing requests on a per-request thread-safe basis,
     * maintaining parameters and calculated values safely scoped.
     */
    private static class RequestProcessor {

        private static final String MESSAGES_LAST_REQUEST = "xava_messagesLastRequest";
        private static final String ERRORS_LAST_REQUEST = "xava_errorsLastRequest";
        private static final String MEMBERS_WITH_ERRORS_IN_LAST_REQUEST = "xava_membersWithErrorsInLastRequest";
        private static final String PAGE_RELOADED_LAST_TIME = "xava_pageReloadedLastTime";

        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final String application;
        private final String module;
        private final String additionalParameters;
        private final Map<String, Object> values;
        private final Map<String, Object> multipleValues;
        private final String[] selected;
        private final String[] deselected;
        private final boolean firstRequest;
        private final String baseFolder;

        private ModuleManager manager;

        public RequestProcessor(HttpServletRequest request, HttpServletResponse response,
                String application, String module, String additionalParameters, Map<String, Object> values,
                Map<String, Object> multipleValues, String[] selected, String[] deselected,
                boolean firstRequest, String baseFolder) {
            this.request = request;
            this.response = response;
            this.application = application;
            this.module = module;
            this.additionalParameters = additionalParameters;
            this.values = values;
            this.multipleValues = multipleValues;
            this.selected = selected;
            this.deselected = deselected;
            this.firstRequest = firstRequest;
            this.baseFolder = Is.emptyString(baseFolder) ? "/xava/" : "/" + baseFolder + "/";
        }

        public Result request() throws Exception {
            long ini = System.currentTimeMillis();
            Result result = new Result();
            result.setApplication(application);
            result.setModule(module);
            try {
                // Initialize request details (re-asserting encoding and context scoped values)
                Servlets.setCharacterEncoding(request, response);
                ModuleContext context = getContext(request);
                if (context != null) context.setCurrentWindowId(request);
                checkSecurity(request, application, module);
                request.setAttribute("style", org.openxava.web.style.Style.getInstance());
                Requests.partialInit(request, application, module);

                setPageReloadedLastTime(false);
                this.manager = (ModuleManager) context.get(application, module, "manager");
                restoreLastMessages();
                getURIAsStream("execute.jsp", values, multipleValues, selected, deselected, additionalParameters);
                setDialogLevel(result);
                Map<String, Object> changedParts = new HashMap<>();
                result.setChangedParts(changedParts);
                String forwardURI = (String) request.getSession().getAttribute("xava_forward");
                String[] forwardURIs = (String[]) request.getSession().getAttribute("xava_forwards");
                if (!Is.emptyString(forwardURI)) {
                    memorizeLastMessages();
                    if (forwardURI.startsWith("http://") || forwardURI.startsWith("https://") || forwardURI.startsWith("javascript:")) {
                        result.setForwardURL(forwardURI);
                    } else {
                        result.setForwardURL(getScheme(request) + "://" +
                                getServerName(request) + ":" + getServerPort(request) +
                                request.getContextPath() + forwardURI);
                    }
                    result.setForwardInNewWindow("true".equals(request.getSession().getAttribute("xava_forward_inNewWindow")));
                    request.getSession().removeAttribute("xava_forward");
                    request.getSession().removeAttribute("xava_forward_inNewWindow");
                } else if (forwardURIs != null) {
                    memorizeLastMessages();
                    for (int i = 0; i < forwardURIs.length; i++) {
                        if (!(forwardURIs[i].startsWith("http://") || forwardURIs[i].startsWith("https://"))) {
                            forwardURIs[i] = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + forwardURIs[i];
                        }
                    }
                    request.getSession().removeAttribute("xava_forwards");
                    result.setForwardURLs(forwardURIs);
                } else if (manager.getNextModule() != null) {
                    changeModule(result);
                } else {
                    fillResult(result, values, multipleValues, selected, deselected, additionalParameters);
                }
                result.setViewMember(getView().getMemberName());
                result.setStrokeActions(getStrokeActions());
                result.setSelectedRows(getSelectedRows());
                result.setUrlParam(getUrlParam());
                result.setViewSimple(getView().isSimple());
                result.setDataChanged(getView().isDataChanged());
                return result;
            } catch (SecurityException ex) {
                if (wasPageReloadedLastTime()) {
                    setPageReloadedLastTime(false);
                    result.setError(ex.getMessage());
                } else {
                    setPageReloadedLastTime(true);
                    result.setReload(true);
                    request.getSession().invalidate();
                }
                return result;
            } catch (Throwable ex) {
                if (isLinkageError(ex)) {
                    Result linkageResult = handleLinkageError(result);
                    if (linkageResult != null) {
                        return linkageResult;
                    }
                }
                log.error(ex.getMessage(), ex);
                result.setError(ex.getMessage());
                return result;
            } finally {
                try {
                    ModuleManager.commit();
                } finally {
                    Requests.clean();
                }
                long time = System.currentTimeMillis() - ini;
                log.debug(XavaResources.getString("request_time") + "=" + time + " ms");
            }
        }

        private Object getServerPort(HttpServletRequest request) {
            String port = request.getHeader("x-forwarded-port");
            return Is.emptyString(port) ? request.getServerPort() : port;
        }

        private String getServerName(HttpServletRequest request) {
            String host = request.getHeader("x-forwarded-host");
            return Is.emptyString(host) ? request.getServerName() : host;
        }

        private String getScheme(HttpServletRequest request) {
            String scheme = request.getHeader("x-forwarded-scheme");
            return Is.emptyString(scheme) ? request.getScheme() : scheme;
        }

        private String getUrlParam() {
            if (firstRequest) return null;
            Stack<?> previousViews = (Stack<?>) getContext(request).get(application, module, "xava_previousViews");
            if (!previousViews.isEmpty()) return "";
            View view = getView();
            Map<?, ?> key = view.getKeyValuesWithValue();
            MetaModel moduleMetaModel = MetaModel.get(manager.getModelName());
            boolean modelFromModule = moduleMetaModel.getPOJOClass().isAssignableFrom(view.getMetaModel().getPOJOClass());
            if (modelFromModule && key.size() == 1 && !moduleMetaModel.getMetaComponent().isTransient()) {
                String id = key.values().iterator().next().toString();
                return "detail=" + id;
            } else if (key.isEmpty()) {
                String action = manager.getPermanlinkAction();
                if (action != null) {
                    return "action=" + action;
                }
            }
            return "";
        }

        private Map<String, int[]> getSelectedRows() {
            Map<String, int[]> result = getView().getChangedCollectionsSelectedRows();
            return result.isEmpty() ? null : result;
        }

        private void setPageReloadedLastTime(boolean b) {
            if (b) request.getSession().setAttribute(PAGE_RELOADED_LAST_TIME, Boolean.TRUE);
            else request.getSession().removeAttribute(PAGE_RELOADED_LAST_TIME);
        }

        private boolean wasPageReloadedLastTime() {
            return request.getSession().getAttribute(PAGE_RELOADED_LAST_TIME) != null;
        }

        private Map<String, StrokeAction> getStrokeActions() {
            java.util.Iterator<?> it = manager.getAllMetaActionsIterator();
            Map<String, StrokeAction> result = new HashMap<>();
            while (it.hasNext()) {
                MetaAction action = (MetaAction) it.next();
                if (!action.hasKeystroke()) continue;
                if (!manager.actionApplies(action)) continue;

                KeyStroke key = KeyStroke.getKeyStroke(action.getKeystroke());
                if (key == null) {
                    continue;
                }
                int keyCode = key.getKeyCode();
                boolean ctrl = (key.getModifiers() & InputEvent.CTRL_DOWN_MASK) > 0;
                boolean alt = (key.getModifiers() & InputEvent.ALT_DOWN_MASK) > 0;
                boolean shift = (key.getModifiers() & InputEvent.SHIFT_DOWN_MASK) > 0;
                String id = keyCode + "," + ctrl + "," + alt + "," + shift;
                result.put(id, new StrokeAction(action.getQualifiedName(), action.getConfirmMessage(Locales.getCurrent()), action.isTakesLong()));
            }
            return result;
        }

        private void changeModule(Result result) {
            String nextModule = manager.getNextModule();
            boolean previousModule = IChangeModuleAction.PREVIOUS_MODULE.equals(nextModule);
            if (previousModule) {
                nextModule = manager.getPreviousModules().peek().toString();
                manager.getPreviousModules().pop();
                getContext(request).remove(application, module, "xava_currentModule");
                getContext(request).remove(application, nextModule, "xava_currentModule");
            } else {
                if (manager.getPreviousModules().contains(nextModule)) {
                    throw new XavaException("module_reentrance_not_allowed", nextModule);
                }
                manager.getPreviousModules().push(module);
            }

            if (!manager.getPreviousModules().isEmpty() && !previousModule) {
                getContext(request).put(application, module, "xava_currentModule", nextModule);
            }

            ModuleManager nextManager = (ModuleManager) getContext(request).get(application, nextModule, "manager", "org.openxava.controller.ModuleManager");
            nextManager.setPreviousModules(manager.getPreviousModules());

            manager.setNextModule(null);
            memorizeLastMessages(nextModule);
            result.setNextModule(nextModule);
        }

        private InputStream getURIAsStream(String jspFile, Map<String, Object> values, Map<String, Object> multipleValues, String[] selected, String[] deselected, String additionalParameters) throws Exception {
            return Servlets.getURIAsStream(request, response, getURI(jspFile, values, multipleValues, selected, deselected, additionalParameters));
        }

        private String getURIAsString(String jspFile, Map<String, Object> values, Map<String, Object> multipleValues, String[] selected, String[] deselected, String additionalParameters) throws Exception {
            if (jspFile == null) return "";
            if (jspFile.startsWith("html:")) return jspFile.substring(5); // No need to filterHTML (replace commas) thanks to JSON
            return Servlets.getURIAsString(request, response, getURI(jspFile, values, multipleValues, selected, deselected, additionalParameters));
        }

        private void fillResult(Result result, Map<String, Object> values, Map<String, Object> multipleValues, String[] selected, String[] deselected, String additionalParameters) throws Exception {
            Map<String, Object> changedParts = result.getChangedParts();
            View view = getView();
            setPostJS(result);
            view.resetCollectionsCache();

            if (manager.isShowDialog() || manager.isHideDialog() || firstRequest) {
                if (manager.getDialogLevel() > 0) {
                    changedParts.put(decorateId("dialog" + manager.getDialogLevel()),
                        getURIAsString("core.jsp?buttonBar=false", values, multipleValues, selected, deselected, additionalParameters)
                    );
                    result.setFocusPropertyId(getView().getFocusPropertyId());
                    return;
                }
            }

            Collection<String> propertiesUsedInCalculations = new HashSet<>();
            Map<String, View> changedCollectionsTotals = view.getChangedCollectionsTotals();
            Map<String, Object> changeParts = getChangedParts(values, propertiesUsedInCalculations, changedCollectionsTotals);
            for (Map.Entry<String, Object> changedPart : changeParts.entrySet()) {
                String htmlContent = getURIAsString((String) changedPart.getValue(), values, multipleValues, selected, deselected, additionalParameters);
                changedParts.put(changedPart.getKey(), htmlContent);
            }

            fillPropertiesUsedInCalculationsFromSumCollectionProperties(propertiesUsedInCalculations, changedCollectionsTotals);

            if (!propertiesUsedInCalculations.isEmpty()) {
                result.setPropertiesUsedInCalculations(XCollections.toStringArray(propertiesUsedInCalculations));
            }

            fillEditorsWithError(result);

            Messages errors = (Messages) request.getAttribute("errors");
            if (errors.contains() && changedParts.get("errors") == null) {
                put(changedParts, "errors",
                    getURIAsString("errors.jsp", values, multipleValues, selected, deselected, additionalParameters)
                );
            }
            if (!manager.isListMode()) {
                result.setFocusPropertyId(getView().getFocusPropertyId());
            } else {
                result.setFocusPropertyId(Lists.FOCUS_PROPERTY_ID);
            }

            if (result.isHideDialog()) result.setFocusPropertyId(null);
        }

        private void setPostJS(Result result) {
            String postJS = (String) request.getAttribute("xava.postjs");
            if (Is.emptyString(postJS)) return;
            Map<String, Object> changedParts = result.getChangedParts();
            StringBuilder script = new StringBuilder();
            script.append("<script type='text/javascript' nonce='");
            script.append(Nonces.get(request));
            script.append("'>");
            script.append("openxava.postJS=function(application, module) {");
            script.append(postJS);
            script.append("}");
            script.append("</script>");
            result.setHasPostJS(true);
            changedParts.put(decorateId("postjs"), script.toString());
        }

        private void fillPropertiesUsedInCalculationsFromSumCollectionProperties(Collection<String> propertiesUsedInCalculations, Map<String, View> changedCollectionsTotals) {
            if (manager.isFormUpload()) return;

            View view = getView();

            for (String collection : view.getChangedCollections().keySet()) {
                View subview = getView().getSubview(collection);
                fillPropertiesUsedInCalculationsFromSumCollectionPropertiesForSubview(propertiesUsedInCalculations, view, subview, collection);
            }

            for (String totalProperty : changedCollectionsTotals.keySet()) {
                String collection = Strings.firstToken(totalProperty, ":");
                View containerView = changedCollectionsTotals.get(totalProperty);
                View subview = containerView.getSubview(collection);
                if (subview.isRepresentsElementCollection()) {
                    fillPropertiesUsedInCalculationsFromSumCollectionPropertiesForSubview(propertiesUsedInCalculations, view, subview, collection);
                }
            }
        }

        private void fillPropertiesUsedInCalculationsFromSumCollectionPropertiesForSubview(
                Collection<String> propertiesUsedInCalculations, View view, View subview, String collection) {
            int count = subview.getMetaPropertiesList().size();
            for (int i = 0; i < count; i++) {
                String sumProperty = subview.getCollectionTotalPropertyName(0, i);
                if (sumProperty.endsWith("_SUM_")) {
                    String qualifiedSumProperty = collection + "." + sumProperty;
                    if (view.isPropertyUsedInCalculation(qualifiedSumProperty)) {
                        propertiesUsedInCalculations.add(qualifiedSumProperty);
                    }
                }
            }
        }

        private void setDialogLevel(Result result) {
            result.setDialogLevel(manager.getDialogLevel());
            if (manager.isShowDialog() && manager.isHideDialog()) return;
            if (firstRequest && manager.getDialogLevel() > 0) {
                result.setShowDialog(true);
                restoreDialogTitle(result);
                if (result.getDialogTitle() == null) setDialogTitle(result);
            } else if (manager.isShowDialog()) {
                result.setShowDialog(manager.isShowDialog());
                setDialogTitle(result);
            } else if (manager.isHideDialog()) {
                result.setHideDialog(true);
                restoreDialogTitle(result);
            }
            result.setResizeDialog(manager.getDialogLevel() > 0
                && (getView().isReloadNeeded() || manager.isReloadViewNeeded() || getView().hasChangedCollections()));
        }

        private void restoreDialogTitle(Result result) {
            result.setDialogTitle((String) getView().getObject("xava.dialogTitle"));
        }

        private void setDialogTitle(Result result) {
            result.setDialogTitle(manager.getDialogTitle());
            getView().putObject("xava.dialogTitle", result.getDialogTitle());
        }

        private Map<String, Object> getChangedParts(Map<String, Object> values, Collection<String> propertiesUsedInCalculations, Map<String, View> changedCollectionsTotals) {
            Map<String, Object> result = new HashMap<>();
            if (values == null || manager.isReloadAllUINeeded() || manager.isFormUpload()) {
                put(result, "core", "core.jsp");
            } else {
                manager.isActionsChanged();
                if (manager.isActionsChanged()) {
                    if (manager.getDialogLevel() > 0) {
                        put(result, "bottom_buttons", "bottomButtons.jsp?buttonBar=false");
                    } else {
                        put(result, "button_bar", "buttonBar.jsp");
                        put(result, "bottom_buttons", "bottomButtons.jsp");
                    }
                }
                Messages errors = (Messages) request.getAttribute("errors");
                put(result, "errors", errors.contains() ? "errors.jsp" : null);
                Messages messages = (Messages) request.getAttribute("messages");
                put(result, "messages", messages.contains() ? "messages.jsp" : null);

                if (manager.isReloadViewNeeded() || getView().isReloadNeeded()) {
                    put(result, "view", manager.getViewURL());
                } else {
                    fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(result, propertiesUsedInCalculations);
                    fillChangedCollections(result);
                    fillChangedCollectionsTotals(result, changedCollectionsTotals);
                    fillChangedCollectionSizesInSections(result);
                    fillChangedSections(result);
                    fillChangedLabels(result);
                }
            }
            return result;
        }

        private void fillChangedLabels(Map<String, Object> result) {
            for (Iterator<?> it = getView().getChangedLabels().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<?, ?> en = (Map.Entry<?, ?>) it.next();
                put(result, "label_" + en.getKey(), "html:" + en.getValue());
            }
        }

        private void fillEditorsWithError(Result result) {
            Collection<String> editorsWithoutError = new HashSet<>();
            if (getContext(request).exists(application, module, MEMBERS_WITH_ERRORS_IN_LAST_REQUEST)) {
                View view = getView();
                Collection<?> lastErrors = (Collection<?>) getContext(request).get(application, module, MEMBERS_WITH_ERRORS_IN_LAST_REQUEST);
                for (Iterator<?> it = lastErrors.iterator(); it.hasNext(); ) {
                    String member = (String) it.next();
                    addEditor(editorsWithoutError, view, member);
                }
                getContext(request).remove(application, module, MEMBERS_WITH_ERRORS_IN_LAST_REQUEST);
            }

            Messages errors = (Messages) request.getAttribute("errors");
            Collection<String> editorsWithError = new HashSet<>();
            if (!errors.isEmpty()) {
                View view = getView();
                Collection<String> members = new HashSet<>();
                for (Iterator<?> it = errors.getMembers().iterator(); it.hasNext(); ) {
                    String member = (String) it.next();
                    String qualifiedMember = addEditor(editorsWithError, view, member);
                    if (qualifiedMember != null) members.add(qualifiedMember);
                }
                if (!members.isEmpty()) {
                    getContext(request).put(application, module, MEMBERS_WITH_ERRORS_IN_LAST_REQUEST, members);
                }
            }

            editorsWithoutError.removeAll(editorsWithError);
            if (!editorsWithoutError.isEmpty()) result.setEditorsWithoutError(XCollections.toStringArray(editorsWithoutError));
            if (!editorsWithError.isEmpty()) result.setEditorsWithError(XCollections.toStringArray(editorsWithError));
        }

        private String addEditor(Collection<String> editors, View view, String member) {
            String qualifiedMember = view.getQualifiedNameForDisplayedPropertyOrReferenceWithNotCompositeEditor(member);
            if (qualifiedMember != null) {
                String container = Strings.firstToken(qualifiedMember, ".");
                String viewModelName = view.getModelName().contains(".") ? Strings.lastToken(view.getModelName(), ".") : view.getModelName();
                if (container.equals(viewModelName)) {
                    String memberWithoutModel = Strings.noFirstTokenWithoutFirstDelim(qualifiedMember, ".");
                    String prefix = view.getMetaModel().containsMetaReference(memberWithoutModel) ? "reference_editor_" : "editor_";
                    editors.add(prefix + memberWithoutModel);
                } else {
                    for (MetaReference ref : view.getMetaModel().getMetaReferences()) {
                        if (ref.isAggregate() && ref.getName().equals(container)) {
                            String memberWithoutModel = Strings.noFirstTokenWithoutFirstDelim(qualifiedMember, ".");
                            String memberWithReference = ref.getName() + "." + memberWithoutModel;
                            String prefix = ref.getMetaModelReferenced().containsMetaReference(memberWithoutModel) ? "reference_editor_" : "editor_";
                            editors.add(prefix + memberWithReference);
                        }
                    }
                }
            } else {
                String memberWithoutModel = Strings.noFirstTokenWithoutFirstDelim(member, ".");
                if (view.getMetaModel().containsMetaReference(memberWithoutModel)) {
                    try {
                        View subview = view.getSubview(memberWithoutModel);
                        for (MetaProperty p : subview.getMetaProperties()) {
                            if (subview.isEditable(p)) {
                                editors.add("editor_" + memberWithoutModel + "___" + p.getName());
                            }
                        }
                    } catch (ElementNotFoundException ex) {
                        // Could be an error over a member not displayed in this view
                    }
                }
            }
            return qualifiedMember;
        }

        private void fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(Map<String, Object> result, Collection<String> propertiesUsedInCalculations) {
            View view = getView();
            Collection<?> changedMembers = view.getChangedPropertiesActionsAndReferencesWithNotCompositeEditor().entrySet();
            for (Iterator<?> it = changedMembers.iterator(); it.hasNext(); ) {
                Map.Entry<?, ?> en = (Map.Entry<?, ?>) it.next();
                String qualifiedName = (String) en.getKey();
                String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
                View containerView = (View) en.getValue();
                String referenceAsDescriptionsListParam = "";
                if (containerView.displayAsDescriptionsListAndReferenceView() && !qualifiedName.contains(".")) {
                    containerView = containerView.getParent();
                    referenceAsDescriptionsListParam = "&descriptionsList=true";
                }
                MetaModel metaModel = containerView.getMetaModel();
                boolean isReference = metaModel.containsMetaReference(name);
                boolean isInsideElementCollection = false;
                if (qualifiedName.contains(":")) {
                    isInsideElementCollection = true;
                    name = qualifiedName.substring(qualifiedName.lastIndexOf(':') + 1);
                    qualifiedName = qualifiedName.replace(":", "");
                    try {
                        containerView.getMetaReference(name);
                        isReference = true;
                    } catch (ElementNotFoundException ex) {
                        isReference = false;
                    }
                }
                if (isReference) {
                    String referenceKey = decorateId(qualifiedName);
                    MetaReference metaReference = containerView.getMetaReference(name);
                    if (isInsideElementCollection) {
                        metaReference = metaReference.cloneMetaReference();
                        metaReference.setName(name);
                    }
                    request.setAttribute(referenceKey, metaReference);
                    put(result, "reference_editor_" + qualifiedName,
                        "reference.jsp?referenceKey=" + referenceKey +
                        referenceAsDescriptionsListParam +
                        "&onlyEditor=true&viewObject=" + containerView.getViewObject());
                } else {
                    put(result, "editor_" + qualifiedName,
                        "editorWrapper.jsp?propertyName=" + name +
                        "&editable=" + containerView.isEditable(name) +
                        "&throwPropertyChanged=" + containerView.throwsPropertyChanged(name) +
                        "&viewObject=" + containerView.getViewObject() +
                        "&propertyPrefix=" + containerView.getPropertyPrefix());
                    if ((containerView.hasEditableChanged() ||
                        (containerView.hasKeyEditableChanged() && metaModel.isKeyOrSearchKey(name))) &&
                        containerView.propertyHasActions(name) ||
                        containerView.propertyHasChangedActions(name))
                    {
                        put(result, "property_actions_" + qualifiedName,
                            "propertyActions.jsp?propertyKey=" + qualifiedName +
                            "&propertyName=" + name +
                            "&editable=" + containerView.isEditable(name) +
                            "&viewObject=" + containerView.getViewObject() +
                            "&lastSearchKey=" + containerView.isLastSearchKey(name));
                    }
                    if (containerView.getCollectionRootOrRoot().isPropertyUsedInCalculation(qualifiedName)) propertiesUsedInCalculations.add(qualifiedName);
                }
            }
        }

        private void fillChangedCollections(Map<String, Object> result) {
            View view = getView();
            Collection<?> changedCollections = view.getChangedCollections().entrySet();
            for (Iterator<?> it = changedCollections.iterator(); it.hasNext(); ) {
                Map.Entry<?, ?> en = (Map.Entry<?, ?>) it.next();
                String qualifiedName = (String) en.getKey();
                String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
                View containerView = (View) en.getValue();
                put(result, "frame_" + qualifiedName + "header",
                    "collectionFrameHeader.jsp?collectionName=" + name +
                    "&viewObject=" + containerView.getViewObject() +
                    "&propertyPrefix=" + containerView.getPropertyPrefix());
                put(result, "collection_" + qualifiedName + ".",
                    "collection.jsp?collectionName=" + name +
                    "&viewObject=" + containerView.getViewObject() +
                    "&propertyPrefix=" + containerView.getPropertyPrefix());
            }
        }

        private void fillChangedCollectionsTotals(Map<String, Object> result, Map<String, View> changedCollectionsTotals) {
            Collection<?> changedCollections = changedCollectionsTotals.entrySet();
            for (Iterator<?> it = changedCollections.iterator(); it.hasNext(); ) {
                Map.Entry<?, ?> en = (Map.Entry<?, ?>) it.next();
                String[] key = ((String) en.getKey()).split(":");
                String qualifiedName = key[0];
                String row = key[1];
                String column = key[2];
                String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
                View containerView = (View) en.getValue();
                put(result, "collection_total_" + row + "_" + column + "_" + qualifiedName + ".",
                    "editors/collectionTotal.jsp?collectionName=" + name +
                    "&viewObject=" + containerView.getViewObject() +
                    "&row=" + row +
                    "&column=" + column +
                    "&propertyPrefix=" + containerView.getPropertyPrefix());
            }
        }

        private void fillChangedCollectionSizesInSections(Map<String, Object> result) {
            View view = getView();
            Collection<Map.Entry<View, Integer>> changedCounts = view.getChangedCollectionSizesInSections().entrySet();
            for (Map.Entry<View, Integer> en : changedCounts) {
                View containerView = en.getKey();
                Integer size = en.getValue();
                put(result, containerView.getViewObject() + "_collectionSize", "html:(" + size + ")");
            }
        }

        private void fillChangedSections(Map<String, Object> result) {
            View view = getView();
            View changedSections = view.getChangedSectionsView();
            if (changedSections != null) {
                put(result, "sections_" + changedSections.getViewObject(),
                    "sections.jsp?viewObject=" + changedSections.getViewObject() +
                    "&propertyPrefix=" + changedSections.getPropertyPrefix());
            }
        }

        private View getView() {
            View view = (View) getContext(request).get(application, module, "xava_view");
            view.setPropertyPrefix("");
            return view;
        }

        private void memorizeLastMessages() {
            memorizeLastMessages(module);
        }

        private void memorizeLastMessages(String module) {
            LastMessages.memorize(request, application, module);
        }

        private void restoreLastMessages() {
            LastMessages.restore(request, application, module);
        }

        private String getURI(String jspFile, Map<String, Object> values, Map<String, Object> multipleValues, String[] selected, String[] deselected, String additionalParameters) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder(getURIPrefix());
            result.append(jspFile);
            if (jspFile.endsWith(".jsp")) result.append('?');
            else result.append('&');
            result.append("application=");
            result.append(application);
            result.append("&module=");
            result.append(module);
            addValuesQueryString(result, values, multipleValues, selected, deselected);
            if (!Is.emptyString(additionalParameters)) result.append(additionalParameters);
            if (firstRequest) result.append("&firstRequest=true");
            return result.toString();
        }

        private String getURIPrefix() {
            return baseFolder;
        }

        private void put(Map<String, Object> result, String key, Object value) {
            result.put(decorateId(key), value);
        }

        private String decorateId(String name) {
            return Ids.decorate(application, module, name);
        }

        private void addValuesQueryString(StringBuilder sb, Map<String, Object> values, Map<String, Object> multipleValues, String[] selected, String[] deselected) throws UnsupportedEncodingException {
            if (values == null) return;
            if (multipleValues != null) {
                SortedMap<String, Object> sortedMultipleValues = new TreeMap<>(multipleValues);
                for (Iterator<?> it = sortedMultipleValues.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<?, ?> en = (Map.Entry<?, ?>) it.next();
                    String addedKey = addMultipleValuesQueryString(sb, en.getKey(), en.getValue());
                    values.remove(decorateId(addedKey));
                }
                values.remove(decorateId("xava_multiple"));
            }
            for (Iterator<?> it = values.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<?, ?> en = (Map.Entry<?, ?>) it.next();
                if (!en.getKey().toString().equals(decorateId("xava_selected"))) {
                    sb.append('&');
                    sb.append(filterKey(en.getKey()));
                    sb.append('=');
                    sb.append(filterValue(en.getValue()));
                }
            }
            if (selected != null) {
                for (int i = 0; i < selected.length; i++) {
                    String[] s = selected[i].split(":");
                    sb.append('&');
                    sb.append(s[0]);
                    sb.append('=');
                    sb.append(s[1]);
                }
            }
            if (deselected != null) {
                for (int i = 0; i < deselected.length; i++) {
                    if (!deselected[i].contains("[")) continue;
                    String r = deselected[i].replace("[false", "").replace("]", ",");
                    r = r.substring(0, r.length() - 1);
                    sb.append('&');
                    sb.append("deselected=");
                    sb.append(r);
                }
            }
        }

        private String filterKey(Object key) {
            String skey = (String) key;
            int idx = skey.indexOf("::");
            String undecorated = idx < 0 ? Ids.undecorate(skey) : Ids.undecorate(skey.substring(0, idx));
            return Is.anyEqual(undecorated, "application", "module") ? undecorated + "_VALUE_" : undecorated;
        }

        private String addMultipleValuesQueryString(StringBuilder sb, Object key, Object value) {
            if (value == null) return null;
            String filteredKey = filterKey((String) key);
            if (key.toString().indexOf("::") >= 0) {
                sb.append('&');
                sb.append(filteredKey);
                sb.append('=');
                sb.append(value);
            } else {
                String[] tokens = value.toString().split("\n");
                for (int i = 1; i < tokens.length - 1; i++) {
                    sb.append('&');
                    sb.append(filteredKey);
                    sb.append('=');
                    sb.append(tokens[i].substring(tokens[i].indexOf('"') + 1, tokens[i].lastIndexOf('"')));
                }
            }
            return filteredKey;
        }

        private Object filterValue(Object value) throws UnsupportedEncodingException {
            if (value == null) return null;
            if (value.toString().startsWith("[reference:")) {
                return "true";
            }
            String charsetName = request.getCharacterEncoding();
            if (charsetName == null) {
                charsetName = XSystem.getEncoding();
            }
            return URLEncoder.encode(value.toString(), charsetName);
        }

        private boolean isLinkageError(Throwable ex) {
            try {
                if (!Hotswap.isActive()) return false;
                return ex.getCause() != null &&
                        ex.getCause().getCause() instanceof java.lang.LinkageError;
            } catch (Exception e) {
                return false;
            }
        }

        private Result handleLinkageError(Result result) {
            try {
                File tempDir = new File("temp");
                if (tempDir.exists() && tempDir.isDirectory()) {
                    Files.deleteDir(tempDir);
                    log.warn(XavaResources.getString("linkage_error_tomcat_temp_cleaned"));
                    result.setReload(true);
                    return result;
                }
            } catch (Exception e) {
                log.error(XavaResources.getString("error_cleaning_tomcat_temp_directory", e.getMessage()), e);
            }
            return null;
        }
    }
}
