package org.openxava.web;

import java.util.*;

import jakarta.servlet.http.*;

/**
 * Request wrapper that merges extra parameters (e.g. form values for module execution).
 * Extra parameters take precedence over the wrapped request.
 * <p>
 * Does not freeze the parameter map: lookups always consult the wrapped request so that
 * nested {@code RequestDispatcher.include} query-string parameters (merged by Tomcat into
 * an {@code ApplicationHttpRequest} under this wrapper) remain visible to JSPs.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ParametersHttpServletRequest extends HttpServletRequestWrapper {

	private final Map<String, String[]> extraParameters;

	public ParametersHttpServletRequest(HttpServletRequest request, Map<String, String[]> extraParameters) {
		super(request);
		if (extraParameters == null || extraParameters.isEmpty()) {
			this.extraParameters = java.util.Collections.emptyMap();
		}
		else {
			this.extraParameters = java.util.Collections.unmodifiableMap(new LinkedHashMap<>(extraParameters));
		}
	}

	@Override
	public String getParameter(String name) {
		String[] values = extraParameters.get(name);
		if (values != null) {
			return values.length == 0 ? null : values[0];
		}
		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (extraParameters.isEmpty()) {
			return super.getParameterMap();
		}
		Map<String, String[]> merged = new LinkedHashMap<>(super.getParameterMap());
		merged.putAll(extraParameters);
		return java.util.Collections.unmodifiableMap(merged);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		if (extraParameters.isEmpty()) {
			return super.getParameterNames();
		}
		return java.util.Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = extraParameters.get(name);
		if (values != null) {
			return values;
		}
		return super.getParameterValues(name);
	}

	public Map<String, String[]> getExtraParameters() {
		return extraParameters;
	}

}
