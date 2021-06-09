package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebFilter("/xava/style/*")
public class CSSFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long ini = System.currentTimeMillis(); // tmp
		PrintWriter out = response.getWriter();
		CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
		// tmp chain.doFilter(request, wrapper);
		chain.doFilter(request, response);
		String uri = ((HttpServletRequest) request).getRequestURI();
		// TMP ME QUEDÉ POR AQUÍ: YA FUNCIONA. ESTABA INTENTANDO MEJORAR EL RENDIMIENTO
		/*
		if(uri.endsWith("/base.css")) {
			// tmp out.write(wrapper.toString());
		} else {
			CharArrayWriter caw = new CharArrayWriter();
			String original = wrapper.toString();
			String refined = original.replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
			caw.write(refined);
			response.setContentLength(caw.toString().length());
			out.write(caw.toString());					
		}
		*/
		out.close();
		long cuesta = System.currentTimeMillis() - ini;
		System.out.println("[CSSFilter.doFilter] " + uri + " cuesta=" + cuesta); // tmp
	}

}
