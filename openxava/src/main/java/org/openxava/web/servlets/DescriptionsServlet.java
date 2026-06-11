package org.openxava.web.servlets;

import java.io.*;
import java.text.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.calculators.*;
import org.openxava.controller.*;
import org.openxava.formatters.*;
import org.openxava.util.*;

/**
 * Servlet for incremental Descriptions fetching.
 * 
 * @author Javier Paniza
 * @since 8.0
 */
@WebServlet(name = "descriptions", urlPatterns = "/xava/descriptions")
public class DescriptionsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(DescriptionsServlet.class);

    @Override
    protected void initRequest(HttpServletRequest request, HttpServletResponse response, String application, String module) {
        super.initRequest(request, response, application, module);
        
        // Ensure before-each-request actions are executed; throw if errors are produced
        executeBeforeEachRequestActions(request, application, module);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        if (operation == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing operation parameter");
            return;
        }

        try {
            String application = request.getParameter("application");
            String module = request.getParameter("module");
            initRequest(request, response, application, module);

            switch (operation) {
                case "getDescriptions" -> handleGetDescriptions(request, response, application, module);
                case "getDescription" -> handleGetDescription(request, response, application, module);
                default -> sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown operation: " + operation);
            }
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing descriptions operation: " + operation, e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }

    private void handleGetDescriptions(HttpServletRequest request, HttpServletResponse response, String application, String module) throws IOException {
        String propertyKey = request.getParameter("propertyKey");
        String term = request.getParameter("term");
        int limit = 60;
        try {
            String limitStr = request.getParameter("limit");
            if (limitStr != null) limit = Integer.parseInt(limitStr);
        } catch (NumberFormatException e) {}
        int offset = 0;
        try {
            String offsetStr = request.getParameter("offset");
            if (offsetStr != null) offset = Integer.parseInt(offsetStr);
        } catch (NumberFormatException e) {}

        List<Map<String, String>> out = new ArrayList<>();        
        try {
            // Set application and module parameters for filters that expect them in request
            request.setAttribute("xava.application", application);
            request.setAttribute("xava.module", module);

            // Prepare calculator key (must match descriptionsEditor.jsp) using normalized propertyKey
            String normalizedPropertyKey = propertyKey == null ? "" : propertyKey.replaceAll("___\\d+___", "___");
            String descriptionsCalculatorKey = "xava." + normalizedPropertyKey + ".descriptionsCalculator";
            DescriptionsCalculator calculator = (DescriptionsCalculator) request.getSession().getAttribute(descriptionsCalculatorKey);
            if (calculator == null) {
                throw new XavaException("descriptions_calculator_not_found", descriptionsCalculatorKey); 
            }

            // Retrieve preconfigured formatter from session (set by JSP) without accepting class names from client
            IFormatter formatter = (IFormatter) request.getSession().getAttribute(propertyKey + ".descriptionsFormatter");

            // Get descriptions using database-level pagination
            int max = sanitizeLimit(limit);
            String qt = normalize(term);
            
            Collection descriptions;
            if (qt.isEmpty()) {
                descriptions = calculator.getDescriptions(max, offset);
            } else {
                // Use database-level filtering with search term
                descriptions = calculator.getDescriptions(max, offset, qt);
            }
            
            int count = 0;
            java.util.Iterator it = descriptions.iterator();
            
            // Create simple {label, value} objects for jQuery UI
            List<Map<String, String>> simpleItems = new ArrayList<>();
            
            // Process results
            while (it.hasNext()) {
                KeyAndDescription kd = (KeyAndDescription) it.next();
                // Ensure that showCode is false so the code is not displayed in the UI
                kd.setShowCode(false);
                String label = formatter == null ? String.valueOf(kd.getDescription()) : formatter.format(request, kd.getDescription());
                // Encode to transport-safe U+XXXX sequences for diacritics, non-ASCII and backslash
                label = encodeToUPlusCodes(label);
                
                Map<String, String> item = new HashMap<>(2);
                item.put("label", label); // Visible description
                item.put("value", encodeToUPlusCodes(String.valueOf(kd.getKey()))); // Value for the hidden input
                item.put("position", String.valueOf(count)); // Position for reference
                simpleItems.add(item);
                count++;
                if (count >= max) break;
            }
            
            out = simpleItems;
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

        // Serialize out to JSONArray
        JSONArray array = new JSONArray();
        for (Map<String, String> item : out) {
            JSONObject obj = new JSONObject();
            obj.put("label", item.get("label"));
            obj.put("value", item.get("value"));
            obj.put("position", item.get("position"));
            array.put(obj);
        }

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(array.toString());
        writer.flush();
    }

    private void handleGetDescription(HttpServletRequest request, HttpServletResponse response, String application, String module) throws IOException {
        String propertyKey = request.getParameter("propertyKey");
        String value = request.getParameter("value");

        try {
            // Ensure filters can access application/module context
            request.setAttribute("xava.application", application);
            request.setAttribute("xava.module", module);
            String normalizedPropertyKey = propertyKey == null ? "" : propertyKey.replaceAll("___\\d+___", "___");
            String descriptionsCalculatorKey = "xava." + normalizedPropertyKey + ".descriptionsCalculator";
            DescriptionsCalculator calculator = (DescriptionsCalculator) request.getSession().getAttribute(descriptionsCalculatorKey);
            if (calculator == null) {
                throw new XavaException("descriptions_calculator_not_found", descriptionsCalculatorKey);
            }

            IFormatter formatter = (IFormatter) request.getSession().getAttribute(propertyKey + ".descriptionsFormatter");

            if (Is.emptyString(value)) {
                sendResponse(response, "");
                return;
            }

            KeyAndDescription kd = calculator.findDescriptionByKey(value);
            if (kd == null) {
                sendResponse(response, "");
                return;
            }

            kd.setShowCode(false);
            String label = formatter == null ? String.valueOf(kd.getDescription()) : formatter.format(request, kd.getDescription());
            sendResponse(response, encodeToUPlusCodes(label));
        }
        catch (Exception ex) {
            log.error(XavaResources.getString("getting_descriptions_error"), ex);
            sendResponse(response, "");
        }
    }

    private void sendResponse(HttpServletResponse response, String text) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(text);
        writer.flush();
    }

    /** Executes before-each-request actions and throws if any error is produced. */
    private void executeBeforeEachRequestActions(HttpServletRequest request, String application, String module) {
        ModuleManager manager = (ModuleManager) getContext(request).get(application, module, "manager");
        Messages errors = new Messages();
        Messages messages = new Messages();
        manager.executeBeforeEachRequestActions(request, errors, messages);
        if (!errors.isEmpty()) {
            // Prefer translated error strings if available
            Collection<?> errs = errors.getStrings(request);
            String detail = (errs == null || errs.isEmpty()) ? errors.toString() : errs.toString();
            throw new XavaException(detail);
        }
    }

    private static int sanitizeLimit(int limit) {
        if (limit <= 0) return 60;
        return Math.min(limit, 100);
    }

    /**
     * Encodes text to transport-safe "U+XXXX" sequences.
     */
    private static String encodeToUPlusCodes(String s) {
        if (s == null) return "";
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD);
        StringBuilder out = new StringBuilder(nfd.length() * 2);
        for (int i = 0; i < nfd.length(); ) {
            int cp = nfd.codePointAt(i);
            boolean isCombining = (cp >= 0x0300 && cp <= 0x036F);
            if (isCombining || cp >= 0x80 || cp == 0x5C || cp == 0x2F) { // also encode backslash and slash
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
