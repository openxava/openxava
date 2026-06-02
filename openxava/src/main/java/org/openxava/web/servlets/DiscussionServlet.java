package org.openxava.web.servlets;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.formatters.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.editors.*;

/**
 * Servlet para gestionar los comentarios de discusión sin DWR.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "discussion", urlPatterns = "/xava/discussion")
public class DiscussionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application = request.getParameter("application");
        String module = request.getParameter("module");
        String discussionId = request.getParameter("discussionId");
        String commentContent = request.getParameter("commentContent");

        try {
            initRequest(request, response, application, module);
            
            DiscussionComment comment = new DiscussionComment();
            comment.setDiscussionId(discussionId);
            comment.setUserName(Users.getCurrent());
            comment.setComment(commentContent);
            XPersistence.getManager().persist(comment);
            
            trackModification(request, application, module, discussionId, commentContent);
            
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            try {
                XPersistence.commit();
            } finally {
                cleanRequest();
            }
        }
    }

    private void initRequest(HttpServletRequest request, HttpServletResponse response, String application, String module) {
        Servlets.setCharacterEncoding(request, response);
        ModuleContext context = getContext(request);
        if (context != null) context.setCurrentWindowId(request);
        checkSecurity(request, application, module);
        Requests.partialInit(request, application, module);
    }

    private void cleanRequest() {
        Requests.clean();
    }

    private ModuleContext getContext(HttpServletRequest request) {
        return (ModuleContext) request.getSession().getAttribute("context");
    }

    private void checkSecurity(HttpServletRequest request, String application, String module) {
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

    private void trackModification(HttpServletRequest request, String application, String module, String discussionId, String commentContent) {
        View view = (View) getContext(request).get(application, module, "xava_view");
        String property = getDiscussionProperty(view.getValues(), discussionId);
        Map<String, Object> oldChangedValues = new HashMap<>();
        oldChangedValues.put(property, XavaResources.getString("discussion_new_comment"));
        Map<String, Object> newChangedValues = new HashMap<>();
        String formattedContent = formatContent(request, commentContent);
        newChangedValues.put(property, formattedContent);
        AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
    }

    private String formatContent(HttpServletRequest request, String commentContent) {
        try {
            return new HtmlTextListFormatter().format(request, commentContent);
        } catch (Exception e) {
            return commentContent;
        }
    }

    private String getDiscussionProperty(Map<?, ?> values, String discussionId) {
        return (String) Maps.getKeyFromValue(values, discussionId, "DISCUSSION");
    }
}
