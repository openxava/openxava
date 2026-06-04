package org.openxava.web.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.dwr.*;
import org.openxava.web.editors.*;
import org.openxava.web.style.*;

/**
 * Servlet for Tab operations without DWR.
 * 
 * @author Migracion Fetch
 * @since 8.0
 */
@WebServlet(name = "tab", urlPatterns = "/xava/tab")
public class TabServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(TabServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("[TabServlet] action=" + action); // tmr
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter");
            return;
        }

        try {
            String application = request.getParameter("application");
            String module = request.getParameter("module");
            initRequest(request, response, application, module);

            if ("setFilterVisible".equals(action)) {
                boolean filterVisible = Boolean.parseBoolean(request.getParameter("filterVisible"));
                String tabObject = request.getParameter("tabObject");

                org.openxava.tab.Tab tab = getTab(request, application, module, tabObject);
                tab.setFilterVisible(filterVisible);
                response.setStatus(HttpServletResponse.SC_OK);

            } else if ("updateValue".equals(action)) {
                int row = Integer.parseInt(request.getParameter("row"));
                String property = request.getParameter("property");
                String value = request.getParameter("value");

                String result = updateValue(request, application, module, row, property, value);
                
                response.setContentType("text/plain; charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.print(result);
                writer.flush();

            } else if ("removeProperty".equals(action)) {
                System.out.println("[TabServlet] Executing removeProperty"); // tmr
                String property = request.getParameter("property");
                String tabObject = request.getParameter("tabObject");

                org.openxava.tab.Tab tab = getTab(request, application, module, tabObject);
                tab.removeProperty(property);
                response.setStatus(HttpServletResponse.SC_OK);
                System.out.println("[TabServlet] removeProperty executed"); // tmr

            } else if ("moveProperty".equals(action)) {
                String tableId = request.getParameter("tableId");
                int from = Integer.parseInt(request.getParameter("from"));
                int to = Integer.parseInt(request.getParameter("to"));

                TableId id = new TableId(tableId, 0);
                if (!id.isValid()) {
                    log.warn(XavaResources.getString("impossible_store_column_movement"));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tableId");
                    return;
                }

                org.openxava.tab.Tab tab = getTab(request, application, module, id.getTabObject());
                tab.moveProperty(from, to);
                response.setStatus(HttpServletResponse.SC_OK);

            } else if ("setColumnWidth".equals(action)) {
                String columnId = request.getParameter("columnId");
                int index = Integer.parseInt(request.getParameter("index"));
                int width = Integer.parseInt(request.getParameter("width"));

                TableId id = new TableId(columnId, 1);
                if (!id.isValid()) {
                    log.warn(XavaResources.getString("impossible_store_column_width"));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid columnId");
                    return;
                }

                try {
                    org.openxava.tab.Tab tab = getTab(request, application, module, id.getTabObject());
                    tab.setColumnWidth(index, width);
                } catch (ElementNotFoundException ex) {
                    // If it has not tab maybe it's a calculated collection
                    org.openxava.view.View view = (org.openxava.view.View) getContext(request).get(application, module, "xava_view");
                    org.openxava.view.View collectionView = view.getSubview(id.getCollection());
                    if (collectionView.isCollectionFromModel() || collectionView.isRepresentsElementCollection()) {
                        String column = columnId.substring(columnId.lastIndexOf("_col") + 4);
                        int columnIndex = Integer.parseInt(column);
                        collectionView.setCollectionColumnWidth(columnIndex, width);
                    } else {
                        collectionView.setCollectionColumnWidth(index, width);
                    }
                }
                response.setStatus(HttpServletResponse.SC_OK);

            } else if ("filterColumns".equals(action)) {
                String searchWord = request.getParameter("searchWord");

                String result = Servlets.getURIAsString(request, response, "/xava/editors/selectColumns.jsp?application=" + application + "&module=" + module + "&searchWord=" + searchWord);
                
                response.setContentType("text/plain; charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.print(result);
                writer.flush();

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing tab action: " + action, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }

    private void initRequest(HttpServletRequest request, HttpServletResponse response, String application, String module) {
        Servlets.setCharacterEncoding(request, response);
        ModuleContext context = getContext(request);
        if (context != null) context.setCurrentWindowId(request);
        checkSecurity(request, application, module);
        request.setAttribute("style", Style.getInstance());
        Requests.partialInit(request, application, module);
    }

    private void cleanRequest() {
        Requests.clean();
    }

    private static ModuleContext getContext(HttpServletRequest request) {
        return (ModuleContext) request.getSession().getAttribute("context");
    }

    private static void checkSecurity(HttpServletRequest request, String application, String module) {
        ModuleContext context = getContext(request);
        if (context == null) {
            throw new SecurityException("6859");
        }
        if (!context.exists(application, module, "manager")) {
            throw new SecurityException("9876");
        }
        if (context.exists(application, module, "naviox_locked")) {
            Boolean locking = (Boolean) context.get(application, module, "naviox_locking");
            if (!locking) {
                Boolean locked = (Boolean) context.get(application, module, "naviox_locked");
                if (locked) throw new SecurityException("3923");
            }
        }
    }

    private static org.openxava.tab.Tab getTab(HttpServletRequest request, String application, String module, String tabObject) {
        org.openxava.tab.Tab tab = (org.openxava.tab.Tab) getContext(request).get(application, module, tabObject);
        request.setAttribute("xava.application", application);
        request.setAttribute("xava.module", module);
        tab.setRequest(request);
        return tab;
    }

    private String updateValue(HttpServletRequest request, String application, String module, int row, String property, String value) {
        try {
            org.openxava.tab.Tab tab = getTab(request, application, module, "xava_tab"); // By now only in list mode, not in collections
            
            Map key = (Map) tab.getTableModel().getObjectAt(row);
            
            // Read old value before saving for undo support
            String oldValue = getFormattedValue(request, tab, key, property);
            
            Map<String, Object> values = new HashMap<>();
            try {
                Messages parsingErrors = new Messages();
                Object ovalue = WebEditors.parse(request, tab.getMetaProperty(property), value, parsingErrors, value);
                if (parsingErrors.contains()) {
                    return "ERROR: " + parsingErrors;
                }
                values.put(property, ovalue);
            } catch (ElementNotFoundException ex) {
                if (!tab.getMetaTab().getMetaModel().containsMetaReference(property)) {
                    throw ex;
                }
                Messages parsingErrors = new Messages(); 
                Map<String, Object> referenceValues = getReferenceValues(tab.getMetaTab().getMetaModel().getMetaReference(property), value, request, parsingErrors);
                if (parsingErrors.contains()) {
                    return "ERROR: " + parsingErrors;
                }
                values.put(property, referenceValues);
            }
            MapFacade.setValues(tab.getModelName(), key, values);
            String propertyLabel = Labels.get(property, request.getLocale()).toLowerCase();
            String message = XavaResources.getString(request, "value_saved_for_property_in_row", propertyLabel, row + 1);
            String undoLabel = XavaResources.getString(request, "undo");
            String restoreMessage = XavaResources.getString(request, "value_restored_for_property_in_row", propertyLabel, row + 1);
            return message + "\tUNDO:" + undoLabel + "\t" + oldValue + "\t" + restoreMessage; 
        } catch (Exception ex) {
            Messages errors = ModuleManager.manageException(ex);        
            return "ERROR: " + errors;
        }
    }

    private String getFormattedValue(HttpServletRequest request, org.openxava.tab.Tab tab, Map key, String property) {
        try {
            if (tab.getMetaTab().getMetaModel().containsMetaReference(property)) {
                MetaReference ref = tab.getMetaTab().getMetaModel().getMetaReference(property);
                MetaModel refModel = ref.getMetaModelReferenced();
                Collection<String> keyPropertiesNames = refModel.getAllKeyPropertiesNames();
                Map<String, Object> memberNames = new HashMap<>();
                for (String keyName : keyPropertiesNames) {
                    memberNames.put(property + "." + keyName, null);
                }
                Map refValues = MapFacade.getValues(tab.getModelName(), key, memberNames);
                Map<String, Object> keyValues = new HashMap<>();
                for (String keyName : keyPropertiesNames) {
                    keyValues.put(keyName, refValues.get(property + "." + keyName));
                }
                return DescriptionsLists.toKeyString(refModel, keyValues);
            } else {
                Map<String, Object> memberNames = new HashMap<>();
                memberNames.put(property, null);
                Map currentValues = MapFacade.getValues(tab.getModelName(), key, memberNames);
                Object oldRawValue = currentValues.get(property);
                return WebEditors.format(request, tab.getMetaProperty(property), oldRawValue, new Messages(), "", true);
            }
        } catch (Exception ex) {
            log.warn("Could not get old value for undo", ex);
            return "";
        }
    }

    private Map<String, Object> getReferenceValues(MetaReference ref, String value, HttpServletRequest request, Messages errors) {
        Map<String, Object> referenceValues = new HashMap<>();
        org.openxava.web.DescriptionsLists.fillReferenceValues(
            referenceValues, ref, value, null, null, 
            request, errors, "", false);
        return referenceValues;
    }
}
