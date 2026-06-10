package org.openxava.web.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import javax.swing.*;
import java.awt.event.*;

import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;

/**
 * Servlet handling keystroke/stroke actions retrieval.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "strokeActions", urlPatterns = "/xava/strokeActions")
public class StrokeActionsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(StrokeActionsServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application = request.getParameter("application");
        String module = request.getParameter("module");

        try {
            initRequest(request, response, application, module);
            ModuleContext context = getContext(request);
            if (context == null) {
                // If context is null, return empty JSON so client can handle it or reload
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().print("{}");
                return;
            }
            context.setCurrentWindowId(request);
            ModuleManager manager = (ModuleManager) context.get(application, module, "manager");

            Map<String, StrokeAction> actions = buildStrokeActions(manager);
            JSONObject json = new JSONObject();
            for (Map.Entry<String, StrokeAction> entry : actions.entrySet()) {
                StrokeAction sa = entry.getValue();
                JSONObject saJson = new JSONObject();
                saJson.put("name", sa.getName());
                saJson.put("confirmMessage", sa.getConfirmMessage());
                saJson.put("takesLong", sa.isTakesLong());
                json.put(entry.getKey(), saJson);
            }

            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(json.toString());

        } catch (Exception ex) {
            log.warn(XavaResources.getString("stroke_actions_errors"), ex);
            // Return empty response so setStrokeActions(null) reload logic triggers
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print("null");
        } finally {
            try {
                ModuleManager.commit();
            } finally {
                cleanRequest();
            }
        }
    }

    public static Map<String, StrokeAction> buildStrokeActions(ModuleManager manager) {
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
}
