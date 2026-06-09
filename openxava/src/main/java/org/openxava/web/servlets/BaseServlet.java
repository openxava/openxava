package org.openxava.web.servlets;

import java.io.IOException;
import javax.servlet.*;
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
public class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Servlets.setCharacterEncoding(request, response);
        super.service(request, response);
    }

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

    /**
     * Send an error response with a plain text message.
     * This avoids the default Tomcat error page that reveals server version.
     * @since 8.0
     */
    protected void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().print(message);
    }

}
