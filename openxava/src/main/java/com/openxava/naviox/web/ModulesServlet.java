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
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "modules", urlPatterns = "/xava/modules")
public class ModulesServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(ModulesServlet.class);

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
            boolean hasResponse = false;
            switch (operation) {
                case "displayModulesList" -> {
                    try {
                        result = Servlets.getURIAsString(request, response,
                            "/naviox/" + NaviOXPreferences.getInstance().getModulesListJSP());
                    } catch (Exception ex) {
                        log.error(XavaResources.getString("display_modules_error"), ex);
                    } finally {
                        ModuleManager.commit();
                    }
                    hasResponse = true;
                }
                case "displayAllModulesList" -> {
                    String searchWord = request.getParameter("searchWord");
                    try {
                        result = Servlets.getURIAsString(request, response,
                            "/naviox/" + NaviOXPreferences.getInstance().getModulesListJSP() +
                            "?modulesLimit=999&searchWord=" + (searchWord != null ? searchWord : ""));
                    } catch (Exception ex) {
                        log.error(XavaResources.getString("display_modules_error"), ex);
                    } finally {
                        ModuleManager.commit();
                    }
                    hasResponse = true;
                }
                case "filter" -> {
                    String searchWord = request.getParameter("searchWord");
                    try {
                        if (request.getSession().getAttribute("context") == null) {
                            throw new SecurityException("1928");
                        }
                        result = Servlets.getURIAsString(request, response, "/naviox/selectModules.jsp?searchWord=" + (searchWord != null ? searchWord : ""));
                    } catch (SecurityException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        log.error(XavaResources.getString("display_modules_error"), ex);
                    } finally {
                        ModuleManager.commit();
                    }
                    hasResponse = true;
                }
                case "bookmarkCurrentModule" -> {
                    try {
                        com.openxava.naviox.Modules modulesObj = (com.openxava.naviox.Modules) request.getSession().getAttribute("modules");
                        if (modulesObj != null) {
                            modulesObj.bookmarkCurrentModule(request);
                        }
                    } catch (Exception ex) {
                        log.warn(XavaResources.getString("bookmark_module_problem"), ex);
                    }
                }
                case "unbookmarkCurrentModule" -> {
                    try {
                        com.openxava.naviox.Modules modulesObj = (com.openxava.naviox.Modules) request.getSession().getAttribute("modules");
                        if (modulesObj != null) {
                            modulesObj.unbookmarkCurrentModule(request);
                        }
                    } catch (Exception ex) {
                        log.warn(XavaResources.getString("unbookmark_module_problem"), ex);
                    }
                }
                case "closeModule" -> {
                    try {
                        int index = Integer.parseInt(request.getParameter("index"));
                        com.openxava.naviox.Modules modulesObj = (com.openxava.naviox.Modules) request.getSession().getAttribute("modules");
                        if (modulesObj != null) {
                            modulesObj.removeModule(index);
                        }
                    } finally {
                        ModuleManager.commit();
                    }
                }
                default -> {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown operation: " + operation);
                    return;
                }
            }
            if (hasResponse) {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().print(result != null ? result : "");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing modules operation: " + operation, e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }
}
