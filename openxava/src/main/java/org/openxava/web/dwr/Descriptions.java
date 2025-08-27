package org.openxava.web.dwr;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.util.*;
import org.openxava.formatters.*;

/**
 * DWR endpoint to fetch descriptions incrementally for @DescriptionsList.
 * Keeps compatibility with server-side filtering/formatting used by descriptionsEditor.jsp
 */
public class Descriptions extends DWRBase {

    private static final Log log = LogFactory.getLog(Descriptions.class);

    /**
     * Returns up to "limit" suggestions filtered by "term" for the given property in the current view/module.
     */
    public List<Map<String, String>> getDescriptions(
            HttpServletRequest request, HttpServletResponse response,
            String application, String module,
            String propertyKey,
            String term, int limit,
            int offset
    ) {
        List<Map<String, String>> out = new ArrayList<>();        
        try {
            initRequest(request, response, application, module);

            // Set application and module parameters for filters that expect them in request
            request.setAttribute("xava.application", application);
            request.setAttribute("xava.module", module);

            // Prepare calculator key (must match descriptionsEditor.jsp)
            String descriptionsCalculatorKey = "xava." + propertyKey + ".descriptionsCalculator";
            DescriptionsCalculator calculator = (DescriptionsCalculator) request.getSession().getAttribute(descriptionsCalculatorKey);
            if (calculator == null) {
                throw new XavaException("descriptions_calculator_not_found", descriptionsCalculatorKey); 
            }

            // No filter/formatter/parameters accepted via DWR for security; use the preconfigured calculator from JSP
            // Retrieve preconfigured formatter from session (set by JSP) without accepting class names from client
            IFormatter formatter = (IFormatter) request.getSession().getAttribute(propertyKey + ".descriptionsFormatter");
            

            // Log effective configuration
            if (log.isDebugEnabled()) {
                log.debug("Descriptions.getDescriptions term='" + term + "', limit=" + limit + ", offset=" + offset);
            }

            // Get descriptions using database-level pagination
            int max = sanitizeLimit(limit);
            String qt = normalize(term);
            
            Collection descriptions;
            if (qt.isEmpty()) {
                descriptions = calculator.getDescriptionsPaginated(max, offset);
            } else {
                // Use database-level filtering with search term
                descriptions = calculator.getDescriptionsPaginatedWithSearch(max, offset, qt);
            }
            
            if (log.isDebugEnabled()) {
                int size = 0;
                try { size = descriptions==null?0:descriptions.size(); } catch(Exception ignore) {}
                log.debug("Descriptions.getDescriptions paginated descriptions size=" + size + ", term='" + term + "', limit=" + max + ", offset=" + offset);
            }

            int count = 0;
            java.util.Iterator it = descriptions.iterator();
            
            // Create simple {label, value} objects for jQuery UI
            List<Map<String, String>> simpleItems = new ArrayList<>();
            
            // Process results (filtering already done in calculator when needed)
            while (it.hasNext()) {
                KeyAndDescription kd = (KeyAndDescription) it.next();
                // Ensure that showCode is false so the code is not displayed in the UI
                kd.setShowCode(false);
                String label = formatter == null ? String.valueOf(kd.getDescription()) : formatter.format(request, kd.getDescription());
                // Encode to transport-safe U+XXXX sequences for diacritics, non-ASCII and backslash
                label = encodeToUPlusCodes(label);
                
                // No additional filtering needed - calculator already filtered if term was provided
                Map<String, String> item = new HashMap<>(2);
                item.put("label", label); // Visible description
                item.put("value", String.valueOf(kd.getKey())); // Value for the hidden input
                item.put("position", String.valueOf(count)); // Position for reference
                simpleItems.add(item);
                count++;
                if (count >= max) break;
            }
            
            // Assign to the final result
            out = simpleItems;
            
            if (log.isDebugEnabled()) {
                log.debug("Descriptions.getDescriptions returning " + out.size() + " filtered items");
            }
            
            return out;
        }
        catch (Exception ex) {
            log.error(XavaResources.getString("getting_descriptions_error"), ex); 
            // Return a single entry labeled "ERROR" so user notices the problem
            Map<String, String> errorItem = new HashMap<>(2);
            errorItem.put("label", "ERROR");
            errorItem.put("value", "");
            errorItem.put("position", "0");
            out.add(errorItem);
        }
        finally {
            cleanRequest();
        }
        return out;
    }

    // ---- helpers ----

    private static String nvl(String a, String b) { return Is.emptyString(a) ? (b == null ? "" : b) : a; }

    private static int sanitizeLimit(int limit) {
        if (limit <= 0) return 60;
        return Math.min(limit, 100);
    }


    /**
     * Encodes text to transport-safe "U+XXXX" sequences.
     * - Normalize to NFD so accents become combining marks.
     * - Replace any combining mark (U+0300–U+036F), any non-ASCII code point (>= 0x80),
     *   and the ASCII backslash (U+005C) with "U+XXXX" (uppercase hex), keeping other ASCII as-is.
     * Intended to avoid client/DWR/JSON issues; the client converts back to characters.
     */
    private static String encodeToUPlusCodes(String s) {
        if (s == null) return "";
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD);
        StringBuilder out = new StringBuilder(nfd.length() * 2);
        for (int i = 0; i < nfd.length(); ) {
            int cp = nfd.codePointAt(i);
            boolean isCombining = (cp >= 0x0300 && cp <= 0x036F);
            if (isCombining || cp >= 0x80 || cp == 0x5C) { // also encode backslash
                out.append(String.format("U+%04X", cp));
            } else {
                out.appendCodePoint(cp);
            }
            i += Character.charCount(cp);
        }
        return out.toString();
    }

    private static String normalize(String s) throws UnsupportedEncodingException {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT);
    }
}
