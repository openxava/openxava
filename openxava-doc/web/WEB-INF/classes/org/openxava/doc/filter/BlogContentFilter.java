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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter("*.html")
public class BlogContentFilter implements Filter {
    
    private String cachedBlogContentEn = null;
    private String cachedBlogContentEs = null;
    private long lastFetchTimeEn = 0;
    private long lastFetchTimeEs = 0;
    private static final long CACHE_DURATION_MS = 3600000; // 1 hour
    private static final String BLOG_URL_EN = "https://www.openxava.org/blog";
    private static final String BLOG_URL_ES = "https://www.openxava.org/es/blog";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initial fetch for both languages
        fetchBlogContent(false);
        fetchBlogContent(true);
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
        
        // Exclude index and getting started pages
        String pageName = requestURI.substring(requestURI.lastIndexOf('/') + 1);
        if (pageName.startsWith("index") || pageName.startsWith("getting-started") || 
            pageName.startsWith("how-to-install") || pageName.startsWith("project-templates")) {
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
        boolean isSpanish = requestURI.endsWith("_es.html");
        String modifiedHtml = injectBlogContent(html, isSpanish);
        
        // Reset content-length so container recalculates it for modified content
        httpResponse.setContentLength(-1);
        byte[] bytes = modifiedHtml.getBytes(StandardCharsets.UTF_8);
        httpResponse.setContentLength(bytes.length);
        httpResponse.getOutputStream().write(bytes);
    }
    
    private String injectBlogContent(String html, boolean spanish) {
        // Find the closing </h1> tag and inject after it
        Pattern pattern = Pattern.compile("</h1>");
        Matcher matcher = pattern.matcher(html);
        
        if (matcher.find()) {
            String blogHtml = getBlogContentHtml(spanish);
            return matcher.replaceFirst("</h1>" + blogHtml);
        }
        return html; // Return original if no h1 found
    }
    
    private String getBlogContentHtml(boolean spanish) {
        String cachedContent = spanish ? cachedBlogContentEs : cachedBlogContentEn;
        long lastFetch = spanish ? lastFetchTimeEs : lastFetchTimeEn;
        
        // Check if cache needs refresh
        long currentTime = System.currentTimeMillis();
        boolean cacheExpired = currentTime - lastFetch > CACHE_DURATION_MS;
        
        if (cacheExpired) {
            // Refresh in background, don't block the request
            final boolean sp = spanish;
            new Thread(() -> fetchBlogContent(sp)).start();
        }
        
        return cachedContent != null ? cachedContent : "";
    }
    
    private void fetchBlogContent(boolean spanish) {
        String blogUrl = spanish ? BLOG_URL_ES : BLOG_URL_EN;
        try {
            URL url = new URL(blogUrl);
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
                while ((line = reader.readLine()) != null) { // Read entire response
                    response.append(line);
                }
                reader.close();
                
                // Parse HTML to extract latest blog posts
                String blogHtml = parseBlogHtml(response.toString(), spanish);
                if (blogHtml != null && !blogHtml.trim().isEmpty()) {
                    if (spanish) {
                        cachedBlogContentEs = blogHtml;
                        lastFetchTimeEs = System.currentTimeMillis();
                    } else {
                        cachedBlogContentEn = blogHtml;
                        lastFetchTimeEn = System.currentTimeMillis();
                    }
                }
            }
            connection.disconnect();
            
        } catch (Exception e) {
            // Silently fail - notification just won't show
        }
    }
    
    private String parseBlogHtml(String html, boolean spanish) {
        
        // Search for h2 title
        Pattern h2Pattern = Pattern.compile("<h2[^>]*>([^<]+)</h2>", Pattern.CASE_INSENSITIVE);
        Matcher h2Matcher = h2Pattern.matcher(html);
        
        // Search for blog date
        Pattern datePattern = Pattern.compile("<p[^>]*class=\"blog-date\"[^>]*>([^<]+)</p>", Pattern.CASE_INSENSITIVE);
        Matcher dateMatcher = datePattern.matcher(html);
        
        // Search for "Read more" / "Leer más" link in <p class="links">
        Pattern linkPattern = Pattern.compile("<p[^>]*class=\"links\"[^>]*>\\s*<a[^>]*href=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher linkMatcher = linkPattern.matcher(html);
        
        try {
            StringBuilder blogHtml = new StringBuilder();
            
            if (h2Matcher.find() && dateMatcher.find() && linkMatcher.find()) {
                String title = h2Matcher.group(1).trim();
                String date = dateMatcher.group(1).trim();
                String link = linkMatcher.group(1).trim();
                
                // Check if article is older than 14 days
                long daysOld = calculateDaysOld(date, spanish);
                if (daysOld > 14) {
                    return "";
                }
                
                // Create slug from title for localStorage key
                String slug = title.toLowerCase()
                         .replaceAll("&nbsp;", " ")
                         .replaceAll("[^a-zA-Z0-9\\s.]", "")
                         .replaceAll("\\s+", "-")
                         .replaceAll("-+$", "");
                
                // Format date: remove year part
                // English: "November 19, 2025" -> "November 19"
                // Spanish: "19 de noviembre del 2025" -> "19 de noviembre"
                String shortDate;
                if (spanish) {
                    int delIndex = date.indexOf(" del ");
                    shortDate = delIndex > 0 ? date.substring(0, delIndex) : date;
                } else {
                    int commaIndex = date.indexOf(",");
                    shortDate = commaIndex > 0 ? date.substring(0, commaIndex) : date;
                }
                
                String newsLabel = spanish ? "Novedad: " : "News: ";
                String readMore = spanish ? "Leer más" : "Read more";
                String recentClass = daysOld <= 3 ? " recent" : "";
                
                // Script to check localStorage and hide if already dismissed
                blogHtml.append("<script>if(localStorage.getItem('blog-dismissed')==='" + slug + "')document.write('<style>.blog-notification{display:none}</style>')</script>");
                
                blogHtml.append("<div class=\"blog-notification" + recentClass + "\" data-slug=\"" + slug + "\">")
                       .append("<span class=\"blog-close\" onclick=\"localStorage.setItem('blog-dismissed',this.parentElement.dataset.slug);this.parentElement.style.display='none'\">×</span>")
                       .append(newsLabel)
                       .append("<b>").append(title).append("</b>")
                       .append(" - ")
                       .append("<i>").append(shortDate).append("</i>")
                       .append(" · ")
                       .append("<a href=\"").append(link).append("\" target=\"_blank\" onclick=\"localStorage.setItem('blog-dismissed','").append(slug).append("')\">").append(readMore).append("</a>")
                       .append("</div>");
            }
            return blogHtml.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    private long calculateDaysOld(String date, boolean spanish) {
        try {
            LocalDate articleDate;
            if (spanish) {
                // Spanish format: "19 de noviembre del 2025"
                // Normalize month names for parsing
                String normalized = date.toLowerCase()
                    .replace(" del ", " ")
                    .replace(" de ", " ")
                    .replace("enero", "01").replace("febrero", "02").replace("marzo", "03")
                    .replace("abril", "04").replace("mayo", "05").replace("junio", "06")
                    .replace("julio", "07").replace("agosto", "08").replace("septiembre", "09")
                    .replace("octubre", "10").replace("noviembre", "11").replace("diciembre", "12");
                // Now format is "19 11 2025"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MM yyyy");
                articleDate = LocalDate.parse(normalized, formatter);
            } else {
                // English format: "November 19, 2025"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                articleDate = LocalDate.parse(date, formatter);
            }
            return ChronoUnit.DAYS.between(articleDate, LocalDate.now());
        } catch (Exception e) {
            return 0; // Show notification if date parsing fails
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
