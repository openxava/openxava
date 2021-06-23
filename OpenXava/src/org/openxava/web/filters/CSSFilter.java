package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.controller.*;

/**
 * 
 * @since 6.5.3
 * @author Javier Paniza
 */
@WebFilter("/xava/style/*")
public class CSSFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// In order that @import "base.css" (or other @import) in CSS include the version number, 
		// so it doesn't use the cached CSS when OpenXava version changes.
		
		if (!((HttpServletRequest) request).getRequestURI().endsWith(".css")) { // If not PNGs are not correctly served
			chain.doFilter(request, response);
			return;
		}
		PrintWriter out = response.getWriter();
		CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		CharArrayWriter caw = new CharArrayWriter();
		String original = wrapper.toString();
		String refined = original.replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
		caw.write(refined);
		response.setContentLength(caw.toString().length());
		out.write(caw.toString());					
		out.close();
	}

}
