package org.openxava.web.servlets;

import javax.servlet.http.*;
import org.openxava.controller.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Base class for OpenXava servlets.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
public class ServletBase extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @since 8.0
     */
    protected static ModuleContext getContext(HttpServletRequest request) {
        return (ModuleContext) request.getSession().getAttribute("context");
    }

    /**
     * @since 8.0
     */
    protected void initRequest(HttpServletRequest request, HttpServletResponse response, String application, String module) {
        Servlets.setCharacterEncoding(request, response);
        ModuleContext context = getContext(request);
        if (context != null) context.setCurrentWindowId(request);
        checkSecurity(request, application, module);
        request.setAttribute("style", Style.getInstance());
        Requests.partialInit(request, application, module);
    }

    /**
     * @since 8.0
     */
    protected void cleanRequest() {
        Requests.clean();
    }

    /**
     * @since 8.0
     */
    protected static void checkSecurity(HttpServletRequest request, String application, String module) {
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

}
