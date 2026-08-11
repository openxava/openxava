package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Shared context for rendering module UI parts in Java.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ViewRenderContext {

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final Map<String, String> parameters;

	public ViewRenderContext(HttpServletRequest request, HttpServletResponse response) {
		this(request, response, null);
	}

	public ViewRenderContext(HttpServletRequest request, HttpServletResponse response, Map<String, String> parameters) {
		this.request = request;
		this.response = response;
		this.parameters = parameters == null ? java.util.Collections.emptyMap() : parameters;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public ModuleContext getModuleContext() {
		return (ModuleContext) request.getSession().getAttribute("context");
	}

	public ModuleManager getManager() {
		ModuleManager manager = (ModuleManager) getModuleContext().get(request, "manager", "org.openxava.controller.ModuleManager");
		manager.setSession(request.getSession());
		return manager;
	}

	public View getView() {
		return (View) getModuleContext().get(request, "xava_view");
	}

	public Style getStyle() {
		Style style = (Style) request.getAttribute("style");
		return style != null ? style : Style.getInstance();
	}

	public Messages getErrors() {
		Messages errors = (Messages) request.getAttribute("errors");
		if (errors == null) {
			errors = new Messages();
			request.setAttribute("errors", errors);
		}
		return errors;
	}

	public Messages getMessages() {
		Messages messages = (Messages) request.getAttribute("messages");
		if (messages == null) {
			messages = new Messages();
			request.setAttribute("messages", messages);
		}
		return messages;
	}

	public String getApplication() {
		String application = getParameter("application");
		return application != null ? application : request.getParameter("application");
	}

	public String getModule() {
		String module = getParameter("module");
		return module != null ? module : request.getParameter("module");
	}

	public String getParameter(String name) {
		if (parameters.containsKey(name)) return parameters.get(name);
		return request.getParameter(name);
	}

	public String getParameter(String name, String defaultValue) {
		String value = getParameter(name);
		return value == null || value.isEmpty() || "null".equals(value) ? defaultValue : value;
	}

	public boolean getBooleanParameter(String name, boolean defaultValue) {
		String value = getParameter(name);
		if (value == null) return defaultValue;
		return "true".equalsIgnoreCase(value);
	}

	public String decorateId(String name) {
		return Ids.decorate(request, name);
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	/**
	 * Parses a part descriptor such as {@code core.jsp?buttonBar=false} into a context
	 * with the extra query parameters.
	 */
	public static ViewRenderContext forPart(HttpServletRequest request, HttpServletResponse response, String partDescriptor) {
		Map<String, String> params = new HashMap<>();
		int q = partDescriptor.indexOf('?');
		if (q >= 0) {
			String query = partDescriptor.substring(q + 1);
			for (String pair : query.split("&")) {
				if (pair.isEmpty()) continue;
				int eq = pair.indexOf('=');
				if (eq < 0) params.put(pair, "");
				else params.put(pair.substring(0, eq), pair.substring(eq + 1));
			}
		}
		return new ViewRenderContext(request, response, params);
	}

	public static String partName(String partDescriptor) {
		int q = partDescriptor.indexOf('?');
		String name = q < 0 ? partDescriptor : partDescriptor.substring(0, q);
		int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
		if (slash >= 0) name = name.substring(slash + 1);
		if (name.endsWith(".jsp")) name = name.substring(0, name.length() - 4);
		return name;
	}

}
