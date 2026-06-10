package org.openxava.web;

import javax.servlet.http.*;
import org.openxava.controller.*;
import org.openxava.util.*;

/**
 * Utility class for managing messages in HTTP requests.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
public class LastMessages {

    private static final String MESSAGES_LAST_REQUEST = "xava_messagesLastRequest";
    private static final String ERRORS_LAST_REQUEST = "xava_errorsLastRequest";

    public static void memorize(HttpServletRequest request, String application, String module) {
        ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
        Object messages = request.getAttribute("messages");
        if (messages != null) {
            context.put(application, module, MESSAGES_LAST_REQUEST, messages);
        }
        Object errors = request.getAttribute("errors");
        if (errors != null) {
            context.put(application, module, ERRORS_LAST_REQUEST, errors);
        }
    }

    public static void restore(HttpServletRequest request, String application, String module) {
        ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
        if (context.exists(application, module, MESSAGES_LAST_REQUEST)) {
            Messages messages = (Messages) context.get(application, module, MESSAGES_LAST_REQUEST);
            request.setAttribute("messages", messages);
            context.remove(application, module, MESSAGES_LAST_REQUEST);
        }
        if (context.exists(application, module, ERRORS_LAST_REQUEST)) {
            Messages errors = (Messages) context.get(application, module, ERRORS_LAST_REQUEST);
            request.setAttribute("errors", errors);
            context.remove(application, module, ERRORS_LAST_REQUEST);
        }
    }
}
