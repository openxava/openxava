package org.openxava.web.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * Servlet for View operations without DWR.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "view", urlPatterns = "/xava/view")
public class ViewServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(ViewServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        if (operation == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing operation parameter");
            return;
        }

        try {
            switch (operation) {
                case "setFrameClosed" -> handleSetFrameClosed(request, response);
                case "moveCollectionElement" -> handleMoveCollectionElement(request, response);
                default -> sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown operation: " + operation);
            }
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing view operation: " + operation, e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }

    private void handleSetFrameClosed(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String frameId = request.getParameter("frameId");
        boolean closed = Boolean.parseBoolean(request.getParameter("closed"));
        if (frameId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing frameId");
            return;
        }
        String[] id = frameId.split("_");
        if (id.length < 3 || !"ox".equals(id[0])) {
            log.warn(XavaResources.getString("impossible_store_frame_status"));
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        String application = id[1];
        String module = id[2];
        initRequest(request, response, application, module);

        org.openxava.view.View view = (org.openxava.view.View) getContext(request).get(application, module, "xava_view");
        if (view != null) {
            view.setFrameClosed(frameId, closed);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            log.warn("xava_view not found in context for " + application + "/" + module);
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "xava_view not found");
        }
    }

    private void handleMoveCollectionElement(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tableId = request.getParameter("tableId");
        String fromStr = request.getParameter("from");
        String toStr = request.getParameter("to");
        if (tableId == null || fromStr == null || toStr == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing tableId, from, or to");
            return;
        }
        int from = Integer.parseInt(fromStr);
        int to = Integer.parseInt(toStr);

        TableId id = new TableId(tableId, 0);
        if (!id.isValid()) {
            log.error(XavaResources.getString("impossible_store_collection_element_movement"));
            throw new XavaException("impossible_store_collection_element_movement");
        }

        initRequest(request, response, id.getApplication(), id.getModule());
        org.openxava.view.View view = getView(request, id.getApplication(), id.getModule());
        try {
            view.getSubview(id.getCollection()).moveCollectionElement(from, to);
            XPersistence.commit();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            XPersistence.rollback();
            log.error(XavaResources.getString("impossible_store_collection_element_movement"), ex);
            throw new XavaException("impossible_store_collection_element_movement");
        }
    }

    private org.openxava.view.View getView(HttpServletRequest request, String application, String module) { 
        org.openxava.view.View view = (org.openxava.view.View)        
            getContext(request).get(application, module, "xava_view"); 
        request.setAttribute("xava.application", application);
        request.setAttribute("xava.module", module);
        view.setRequest(request);
        return view;
    }
}
