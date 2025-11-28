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
    
    private String cachedBlogContent = null;
    private long lastFetchTime = 0;
    private static final long CACHE_DURATION_MS = 3600000; // 1 hour
    private static final String BLOG_URL = "https://www.openxava.org/blog";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("BlogContentFilter: Initializing filter");
        // Initial fetch
        fetchBlogContent();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("BlogContentFilter.doFilter() HOLA SOY JAVI");        
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Only process HTML files
        String requestURI = httpRequest.getRequestURI();
        System.out.println("BlogContentFilter: Processing URI: " + requestURI);
        
        if (!requestURI.endsWith(".html")) {
            chain.doFilter(request, response);
            return;
        }
        
        System.out.println("BlogContentFilter: Processing HTML file: " + requestURI);
        
        // Wrap response to capture HTML
        CharResponseWrapper wrappedResponse = new CharResponseWrapper(httpResponse);
        
        chain.doFilter(request, wrappedResponse);
        
        // Get the HTML content
        String html = wrappedResponse.getContent();
        if (html == null || html.trim().isEmpty()) {
            System.out.println("BlogContentFilter: No HTML content found");
            return;
        }
        
        System.out.println("BlogContentFilter: HTML content length: " + html.length());
        
        // Inject blog content after </h1>
        String modifiedHtml = injectBlogContent(html);
        
        System.out.println("BlogContentFilter: Modified HTML length: " + modifiedHtml.length());
        
        // Write the modified HTML - let servlet container handle content-length automatically
        response.getWriter().write(modifiedHtml);
    }
    
    private String injectBlogContent(String html) {
        // Find the closing </h1> tag and inject after it
        Pattern pattern = Pattern.compile("</h1>");
        Matcher matcher = pattern.matcher(html);
        
        if (matcher.find()) {
            String blogHtml = getBlogContentHtml();
            String result = matcher.replaceFirst("</h1>" + blogHtml);
            
            // Debug: Show context around injection point
            int injectionIndex = result.indexOf(blogHtml);
            int start = Math.max(0, injectionIndex - 100);
            int end = Math.min(result.length(), injectionIndex + blogHtml.length() + 100);
            
            System.out.println("BlogContentFilter: Injection context:");
            System.out.println("BlogContentFilter: " + result.substring(start, end));
            
            return result;
        }
        
        System.out.println("BlogContentFilter: No </h1> tag found in HTML");
        return html; // Return original if no h1 found
    }
    
    private String getBlogContentHtml() {
        System.out.println("BlogContentFilter: getBlogContentHtml() called");
        System.out.println("BlogContentFilter: cachedBlogContent = " + (cachedBlogContent == null ? "null" : "not null"));
        System.out.println("BlogContentFilter: lastFetchTime = " + lastFetchTime);
        
        // Refresh cache if needed
        long currentTime = System.currentTimeMillis();
        boolean cacheExpired = currentTime - lastFetchTime > CACHE_DURATION_MS;
        boolean cacheEmpty = cachedBlogContent == null;
        
        System.out.println("BlogContentFilter: cacheExpired = " + cacheExpired + ", cacheEmpty = " + cacheEmpty);
        
        if (cacheEmpty || cacheExpired) {
            System.out.println("BlogContentFilter: Calling fetchBlogContent() because cache is " + (cacheEmpty ? "empty" : "expired"));
            fetchBlogContent();
        }
        
        if (cachedBlogContent == null) {
            System.out.println("BlogContentFilter: cachedBlogContent is still null after fetch, using fallback");
            return "<div class=\"blog-notification\"><strong>📝 Latest from OpenXava Blog:</strong><br><a href=\"https://www.openxava.org/blog\" target=\"_blank\">Visit OpenXava Blog</a></div>";
        }
        
        return cachedBlogContent;
    }
    
    private void fetchBlogContent() {
        System.out.println("BlogContentFilter: fetchBlogContent() called");
        try {
            System.out.println("BlogContentFilter: Fetching blog from: " + BLOG_URL);
            URL url = new URL(BLOG_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "OpenXava-Doc-Bot/1.0");
            
            System.out.println("BlogContentFilter: Making HTTP request...");
            int responseCode = connection.getResponseCode();
            System.out.println("BlogContentFilter: Response code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) { // Read entire response
                    response.append(line);
                }
                reader.close();
                
                System.out.println("BlogContentFilter: Read entire response, total length: " + response.length());
                
                // Parse HTML to extract latest blog posts
                String blogHtml = parseBlogHtml(response.toString());
                if (blogHtml != null && !blogHtml.trim().isEmpty()) {
                    cachedBlogContent = blogHtml;
                    lastFetchTime = System.currentTimeMillis();
                    System.out.println("BlogContentFilter: Successfully cached blog content");
                } else {
                    System.out.println("BlogContentFilter: parseBlogHtml() returned empty content");
                }
            } else {
                System.out.println("BlogContentFilter: HTTP error, response code: " + responseCode);
            }
            connection.disconnect();
            
        } catch (Exception e) {
            System.err.println("BlogContentFilter: Error fetching blog content: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String parseBlogHtml(String html) {
        System.out.println("BlogContentFilter: Parsing HTML, length: " + html.length());
        System.out.println("BlogContentFilter: First 1000 chars: " + html.substring(0, Math.min(1000, html.length())));
        System.out.println("BlogContentFilter: Looking for blog posts...");
        
        // Search for any h2 tags to see what's available
        Pattern h2Pattern = Pattern.compile("<h2[^>]*>([^<]+)</h2>", Pattern.CASE_INSENSITIVE);
        Matcher h2Matcher = h2Pattern.matcher(html);
        int h2Count = 0;
        while (h2Matcher.find() && h2Count < 3) {
            System.out.println("BlogContentFilter: Found h2[" + h2Count + "]: " + h2Matcher.group(1).trim());
            h2Count++;
        }
        
        // Search for any blog dates
        Pattern datePattern = Pattern.compile("<p[^>]*class=\"blog-date\"[^>]*>([^<]+)</p>", Pattern.CASE_INSENSITIVE);
        Matcher dateMatcher = datePattern.matcher(html);
        int dateCount = 0;
        while (dateMatcher.find() && dateCount < 3) {
            System.out.println("BlogContentFilter: Found date[" + dateCount + "]: " + dateMatcher.group(1).trim());
            dateCount++;
        }
        
        try {
            // Simple approach: use first h2 title and first date directly
            System.out.println("BlogContentFilter: Using first h2 and first date approach...");
            
            // Reset matchers to use existing patterns
            h2Matcher.reset();
            dateMatcher.reset();
            
            StringBuilder blogHtml = new StringBuilder();
            blogHtml.append("<div class=\"blog-notification\">");
            
            if (h2Matcher.find() && dateMatcher.find()) {
                String title = h2Matcher.group(1).trim();
                String date = dateMatcher.group(1).trim();
                
                // Create link from title (simple slug conversion)
                String link = "https://openxava.org/blog/" + 
                    title.toLowerCase()
                         .replaceAll("&nbsp;", " ")
                         .replaceAll("[^a-zA-Z0-9\\s.]", "")  // Keep dots for version numbers
                         .replaceAll("\\s+", "-")
                         .replaceAll("-+$", "");
                
                System.out.println("BlogContentFilter: Found post - Title: " + title + ", Date: " + date + ", Link: " + link);
                
                blogHtml.append("News: ")
                       .append("<b>").append(title).append("</b>")
                       .append(" - ")
                       .append("<i>").append(date.substring(0, date.indexOf(","))).append("</i>")
                       .append(" · ")
                       .append("<a href=\"").append(link).append("\" target=\"_blank\">Read more</a>");
            } else {
                System.out.println("BlogContentFilter: No blog post found with simple approach");
                // Fallback if no post found
                blogHtml.append("<a href=\"https://www.openxava.org/blog\" target=\"_blank\">Visit OpenXava Blog</a>");
            }
            
            blogHtml.append("</div>");
            String result = blogHtml.toString();
            System.out.println("BlogContentFilter: Generated HTML: " + result);
            return result;
            
        } catch (Exception e) {
            System.err.println("Error parsing blog HTML: " + e.getMessage());
            e.printStackTrace();
            return "<div class=\"blog-notification\"><a href=\"https://www.openxava.org/blog\" target=\"_blank\">📝 Visit OpenXava Blog</a></div>";
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
