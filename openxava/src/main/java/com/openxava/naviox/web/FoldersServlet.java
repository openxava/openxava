/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2026 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.naviox.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;
import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "folders", urlPatterns = "/xava/folders")
public class FoldersServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(FoldersServlet.class);
    private static IFoldersProvider provider;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        if (operation == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing operation parameter");
            return;
        }
        String application = request.getParameter("application");
        String module = request.getParameter("module");
        try {
            initRequest(request, response, application, module);
            String result = null;
            switch (operation) {
                case "goFolder" -> {
                    String folderOid = request.getParameter("folderOid");
                    result = getProvider().goFolder(request, response, folderOid);
                }
                case "goBack" -> result = getProvider().goBack(request, response);
                case "goHome" -> result = getProvider().goHome(request, response);
                default -> {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown operation: " + operation);
                    return;
                }
            }
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().print(result != null ? result : "");
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing folders operation: " + operation, e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }

    private static IFoldersProvider getProvider() {
        if (provider == null) {
            try {
                provider = (IFoldersProvider) Class.forName(NaviOXPreferences.getInstance().getFoldersProviderClass()).newInstance();
            } catch (Exception ex) {
                log.warn(XavaResources.getString("provider_creation_error", "Folders"), ex);
                throw new XavaException("provider_creation_error", "Folders");
            }
        }
        return provider;
    }
}
