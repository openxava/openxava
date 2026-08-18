package org.openxava.web.servlets;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * Handles email unsubscription requests.
 * <p>
 * Replaces the former unsubscribe.jsp.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet("/xava/unsubscribe")
public class UnsubscribeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Servlets.setCharacterEncoding(request, response);
		String email = request.getParameter("email");
		String module = request.getParameter("module");
		String key = request.getParameter("key");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		if (Is.emptyString(email, module)) {
			out.print(XavaResources.getString(request, "incorrect_url"));
			return;
		}
		try {
			if (Is.emptyString(key)) {
				EmailNotifications.unsubscribeAllEntitiesOfModule(email, module);
				out.print(XavaResources.getString(request, "email_unsubscription_all_records", module));
			}
			else {
				EmailNotifications.unsubscribeFromEntity(email, module, key);
				out.print(XavaResources.getString(request, "email_unsubscription_one_record", key.replace("::", ""), module));
			}
		}
		finally {
			XPersistence.commit();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
