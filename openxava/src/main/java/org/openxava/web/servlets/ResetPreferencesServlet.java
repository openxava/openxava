package org.openxava.web.servlets;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import org.openxava.util.impl.*;

/**
 * Resets all user preferences.
 * <p>
 * Replaces the former resetPreferences.jsp.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet("/xava/resetPreferences")
public class ResetPreferencesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String zxy = request.getParameter("zxy");
			if ("HOljkso83".equals(zxy)) {
				UserPreferences.removeAll();
			}
		}
		catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

}
