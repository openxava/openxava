package org.openxava.web;

import java.util.*;

import jakarta.servlet.http.*;

/**
 * Request wrapper that merges extra parameters (e.g. form values for module execution).
 * Extra parameters take precedence over the wrapped request.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ParametersHttpServletRequest extends HttpServletRequestWrapper {

	private final Map<String, String[]> parameters;

	public ParametersHttpServletRequest(HttpServletRequest request, Map<String, String[]> extraParameters) {
		super(request);
		Map<String, String[]> merged = new LinkedHashMap<>();
		merged.putAll(request.getParameterMap());
		// Extra parameters override, matching RequestDispatcher.include query string behavior for execute
		if (extraParameters != null) {
			merged.putAll(extraParameters);
		}
		this.parameters = java.util.Collections.unmodifiableMap(merged);
	}

	@Override
	public String getParameter(String name) {
		String[] values = parameters.get(name);
		return values == null || values.length == 0 ? null : values[0];
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return parameters;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return java.util.Collections.enumeration(parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return parameters.get(name);
	}

}
