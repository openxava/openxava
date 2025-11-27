package org.openxava.doc.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter("*.html")
public class BlogContentFilter implements Filter {
    
    private String cachedBlogContent = "";
    private long lastFetchTime = 0;
    private static final long CACHE_DURATION_MS = 3600000; // 1 hour
    private static final String BLOG_URL = "https://www.openxava.org/blog";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initial fetch
        fetchBlogContent();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Only process HTML files
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.endsWith(".html")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Wrap response to capture HTML
        CharResponseWrapper wrappedResponse = new CharResponseWrapper(httpResponse);
        
        chain.doFilter(request, wrappedResponse);
        
        // Get the HTML content
        String html = wrappedResponse.getContent();
        if (html == null || html.trim().isEmpty()) {
            return;
        }
        
        // Inject blog content after </h1>
        String modifiedHtml = injectBlogContent(html);
        
        // Write the modified HTML
        response.setContentLength(modifiedHtml.getBytes(StandardCharsets.UTF_8).length);
        response.getWriter().write(modifiedHtml);
    }
    
    private String injectBlogContent(String html) {
        // Find the closing </h1> tag and inject after it
        Pattern pattern = Pattern.compile("</h1>");
        Matcher matcher = pattern.matcher(html);
        
        if (matcher.find()) {
            String blogHtml = getBlogContentHtml();
            return matcher.replaceFirst("</h1>" + blogHtml);
        }
        
        return html; // Return original if no h1 found
    }
    
    private String getBlogContentHtml() {
        // Refresh cache if needed
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFetchTime > CACHE_DURATION_MS) {
            fetchBlogContent();
        }
        
        return cachedBlogContent;
    }
    
    private void fetchBlogContent() {
        try {
            URL url = new URL(BLOG_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "OpenXava-Doc-Bot/1.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse HTML to extract latest blog posts
                String blogHtml = parseBlogHtml(response.toString());
                if (blogHtml != null && !blogHtml.trim().isEmpty()) {
                    cachedBlogContent = blogHtml;
                    lastFetchTime = System.currentTimeMillis();
                }
            }
            connection.disconnect();
            
        } catch (Exception e) {
            // Log error but keep using cached content
            System.err.println("Error fetching blog content: " + e.getMessage());
        }
    }
    
    private String parseBlogHtml(String html) {
        // Simple regex to extract blog posts - adjust based on actual HTML structure
        // This is a basic implementation, you may need to refine it
        try {
            // Look for blog post links - adjust selectors based on actual blog structure
            Pattern postPattern = Pattern.compile("<a[^>]*href=\"([^\"]*blog/[^\"]*)\"[^>]*>([^<]+)</a>", Pattern.CASE_INSENSITIVE);
            Matcher matcher = postPattern.matcher(html);
            
            StringBuilder blogHtml = new StringBuilder();
            blogHtml.append("<div class=\"blog-notification\" style=\"background: #f8f9fa; color: #333; padding: 12px 10px; margin: 15px 0; font-size: 0.9em; font-weight: normal; border: 1px solid #dee2e6; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">");
            blogHtml.append("<strong>📝 Latest from OpenXava Blog:</strong><br>");
            
            int count = 0;
            while (matcher.find() && count < 3) { // Show max 3 latest posts
                String link = matcher.group(1);
                String title = matcher.group(2);
                
                // Make sure link is absolute
                if (!link.startsWith("http")) {
                    link = "https://www.openxava.org" + (link.startsWith("/") ? link : "/" + link);
                }
                
                blogHtml.append("• <a href=\"").append(link).append("\" target=\"_blank\" style=\"color: #007bff; text-decoration: none;\">")
                       .append(title.trim()).append("</a><br>");
                count++;
            }
            
            if (count == 0) {
                // Fallback if no posts found
                blogHtml.append("<a href=\"https://www.openxava.org/blog\" target=\"_blank\" style=\"color: #007bff; text-decoration: none;\">Visit OpenXava Blog</a>");
            }
            
            blogHtml.append("</div>");
            return blogHtml.toString();
            
        } catch (Exception e) {
            System.err.println("Error parsing blog HTML: " + e.getMessage());
            return "<div class=\"blog-notification\" style=\"background: #f8f9fa; color: #333; padding: 12px 10px; margin: 15px 0; font-size: 0.9em; font-weight: normal; border: 1px solid #dee2e6; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\"><a href=\"https://www.openxava.org/blog\" target=\"_blank\" style=\"color: #007bff; text-decoration: none;\">📝 Visit OpenXava Blog</a></div>";
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup if needed
    }
    
    // Response wrapper to capture HTML output
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
                        // Not implemented
                    }
                };
            }
            return outputStream;
        }
        
        public String getContent() {
            // Flush any pending writes
            if (writer != null) {
                writer.flush();
            }
            return output.toString();
        }
    }
}
