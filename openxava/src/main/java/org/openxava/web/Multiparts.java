package org.openxava.web;

import javax.servlet.http.*;
import org.openxava.controller.*;
import org.openxava.web.servlets.*;

/**
 * Utility class for handling multipart requests.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
public class Multiparts {

    public static void request(HttpServletRequest request, HttpServletResponse response, String application, String module) throws Exception {
        if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            HotwireServlet.executeRequest(request, response, application, module, null, null, null, null, null, false, null);
            LastMessages.memorize(request, application, module);
            ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
            ModuleManager manager = (ModuleManager) context.get(application, module, "manager");
            manager.setResetFormPostNeeded(true);
        } else {
            ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
            ModuleManager manager = (ModuleManager) context.get(application, module, "manager");
            manager.formUploadNextTime();
        }
    }
}
