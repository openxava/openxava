/*
 * XavaPhone. Mobile UI for OpenXava application (unimplemented).
 * Copyright 2026 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.phone.web;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;
import com.openxava.naviox.util.*;
import com.openxava.phone.impl.*;

/**
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "phoneList", urlPatterns = "/xava/phoneList")
public class PhoneListServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(PhoneListServlet.class);
    private static IPhoneListProvider provider;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application = request.getParameter("application");
        String module = request.getParameter("module");
        String searchWord = request.getParameter("searchWord");
        String rowAction = request.getParameter("rowAction");
        try {
            initRequest(request, response, application, module);
            String result = getProvider().filter(request, response, application, module, searchWord, rowAction);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().print(result != null ? result : "");
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing phone list filter", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }

    private static IPhoneListProvider getProvider() {
        if (provider == null) {
            try {
                provider = (IPhoneListProvider) Class.forName(NaviOXPreferences.getInstance().getPhoneListProviderClass()).newInstance();
            } catch (Exception ex) {
                log.warn(XavaResources.getString("provider_creation_error", "PhoneList"), ex);
                throw new XavaException("provider_creation_error", "PhoneList");
            }
        }
        return provider;
    }
}
