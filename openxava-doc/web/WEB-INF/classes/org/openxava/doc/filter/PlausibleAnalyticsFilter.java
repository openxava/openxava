package org.openxava.doc.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter("*.html")
public class PlausibleAnalyticsFilter implements Filter {
    
    private static final Pattern HEAD_CLOSE_PATTERN = Pattern.compile("</head>", Pattern.CASE_INSENSITIVE);
    
    private static final String PLAUSIBLE_SCRIPT = 
        "<!-- Privacy-friendly analytics by Plausible -->\n" +
        "<script async src=\"https://plausible.io/js/pa-eO417ihGtNEIsgS2Vsrqo.js\"></script>\n" +
        "<script>\n" +
        "  window.plausible=window.plausible||function(){(plausible.q=plausible.q||[]).push(arguments)},plausible.init=plausible.init||function(i){plausible.o=i||{}};\n" +
        "  plausible.init()\n" +
        "</script>\n";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        if (!requestURI.endsWith(".html")) {
            chain.doFilter(request, response);
            return;
        }
        
        CharResponseWrapper wrappedResponse = new CharResponseWrapper(httpResponse);
        
        chain.doFilter(request, wrappedResponse);
        
        String html = wrappedResponse.getContent();
        if (html == null || html.trim().isEmpty()) {
            return;
        }
        
        String modifiedHtml = injectPlausibleScript(html);
        
        httpResponse.setContentLength(-1);
        byte[] bytes = modifiedHtml.getBytes(StandardCharsets.UTF_8);
        httpResponse.setContentLength(bytes.length);
        httpResponse.getOutputStream().write(bytes);
    }
    
    private String injectPlausibleScript(String html) {
        Matcher matcher = HEAD_CLOSE_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.replaceFirst(Matcher.quoteReplacement(PLAUSIBLE_SCRIPT) + "</head>");
        }
        return html;
    }
    
    @Override
    public void destroy() {
    }
    
    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private StringWriter output;
        private PrintWriter writer;
        private ServletOutputStream outputStream;
        
        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new StringWriter();
        }
        
        @Override
        public PrintWriter getWriter() {
            if (writer == null) {
                writer = new PrintWriter(output);
            }
            return writer;
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        output.write(b);
                    }
                    
                    @Override
                    public void write(byte[] b) throws IOException {
                        output.write(new String(b, StandardCharsets.UTF_8));
                    }
                    
                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        output.write(new String(b, off, len, StandardCharsets.UTF_8));
                    }
                    
                    @Override
                    public boolean isReady() {
                        return true;
                    }
                    
                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                    }
                };
            }
            return outputStream;
        }
        
        public String getContent() {
            if (writer != null) {
                writer.flush();
            }
            return output.toString();
        }
    }
}
