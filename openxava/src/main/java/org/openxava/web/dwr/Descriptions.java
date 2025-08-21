package org.openxava.web.dwr;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.converters.*;
import org.openxava.filters.*;
import org.openxava.formatters.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.web.*;

/**
 * DWR endpoint to fetch descriptions incrementally for @DescriptionsList.
 * Keeps compatibility with server-side filtering/formatting used by descriptionsEditor.jsp
 */
public class Descriptions extends DWRBase {

    private static final Log log = LogFactory.getLog(Descriptions.class);

    /**
     * Returns up to "limit" suggestions filtered by "term" for the given property in the current view/module.
     */
    public List<Map<String, String>> getSuggestions(
            HttpServletRequest request, HttpServletResponse response,
            String application, String module,
            String propertyKey, String viewObject,
            String term, int limit,
            String condition, String orderByKey, String order,
            String filterClass, String descriptionsFormatterClass,
            String parameterValuesProperties, String parameterValuesStereotypes,
            String model, String keyProperty, String keyProperties,
            String descriptionProperty, String descriptionProperties,
            int offset // Pagination offset (new parameter)
    ) {
        List<Map<String, String>> out = new ArrayList<>();
        try {
            initRequest(request, response, application, module);

            // View and style for potential parsing/formatting needs
            viewObject = (Is.emptyString(viewObject) ? "xava_view" : viewObject);
            View view = (View) getContext(request).get(application, module, viewObject);

            // Prepare calculator (similar to descriptionsEditor.jsp)
            String modelForId = "." + view.getModelName();
            String conditionForId = Is.emptyString(condition) ? "" : "." + condition;
            String orderByKeyForId = Is.emptyString(orderByKey) ? "" : "." + orderByKey;
            String orderForId = Is.emptyString(order) ? "" : "." + order;

            String descriptionsCalculatorKey = propertyKey + modelForId + conditionForId + orderByKeyForId + orderForId + ".descriptionsCalculator";
            DescriptionsCalculator calculator = (DescriptionsCalculator) request.getSession().getAttribute(descriptionsCalculatorKey);
            if (calculator == null) {
                calculator = new DescriptionsCalculator();
                calculator.setCondition(condition);
                calculator.setOrder(order);
                calculator.setOrderByKey(orderByKey);

                // Mandatory model and key/description configuration from client
                if (Is.emptyString(model)) model = view.getModelName();
                calculator.setModel(model);
                calculator.setKeyProperty(nvl(keyProperty, null));
                calculator.setKeyProperties(nvl(keyProperties, null));
                calculator.setDescriptionProperty(nvl(descriptionProperty, null));
                calculator.setDescriptionProperties(nvl(descriptionProperties, null));
                calculator.setUseConvertersInKeys(true);

                request.getSession().setAttribute(descriptionsCalculatorKey, calculator);
            }

            // Ensure mandatory configuration is present even if calculator existed
            if (!Is.emptyString(condition)) calculator.setCondition(condition);
            if (!Is.emptyString(order)) calculator.setOrder(order);
            if (!Is.emptyString(orderByKey)) calculator.setOrderByKey(orderByKey);
            if (Is.emptyString(calculator.getModel())) {
                if (Is.emptyString(model)) model = view.getModelName();
                calculator.setModel(model);
            }
            if (Is.emptyString(calculator.getKeyProperty())) calculator.setKeyProperty(nvl(keyProperty, null));
            if (Is.emptyString(calculator.getKeyProperties())) calculator.setKeyProperties(nvl(keyProperties, null));
            if (Is.emptyString(calculator.getDescriptionProperty())) calculator.setDescriptionProperty(nvl(descriptionProperty, null));
            if (Is.emptyString(calculator.getDescriptionProperties())) calculator.setDescriptionProperties(nvl(descriptionProperties, null));
            calculator.setUseConvertersInKeys(true);

            // Filter (IFilter) like in JSP
            IFilter filter = null;
            if (!Is.emptyString(filterClass)) {
                String filterKey = propertyKey + ".filter";
                filter = (IFilter) request.getSession().getAttribute(filterKey);
                if (filter == null) {
                    try {
                        filter = (IFilter) Class.forName(filterClass).newInstance();
                        request.getSession().setAttribute(filterKey, filter);
                    }
                    catch (Exception ex) {
                        log.warn(XavaResources.getString("descriptionsEditor_filter_warning", propertyKey), ex);
                    }
                }
                if (filter instanceof IRequestFilter) {
                    ((IRequestFilter) filter).setRequest(request);
                }
            }

            // Formatter (IFormatter) like in JSP
            IFormatter formatter = null;
            if (!Is.emptyString(descriptionsFormatterClass)) {
                String descriptionsFormatterKey = propertyKey + ".descriptionsFormatter";
                formatter = (IFormatter) request.getSession().getAttribute(descriptionsFormatterKey);
                if (formatter == null) {
                    try {
                        formatter = (IFormatter) Class.forName(descriptionsFormatterClass).newInstance();
                        request.getSession().setAttribute(descriptionsFormatterKey, formatter);
                    }
                    catch (Exception ex) {
                        log.warn(XavaResources.getString("descriptionsEditor_descriptions_formatter_warning", propertyKey), ex);
                    }
                }
            }

            // Parameter values (dependent filters), same logic as JSP
            if (!Is.emptyString(parameterValuesStereotypes) || !Is.emptyString(parameterValuesProperties)) {
                java.util.List p = new java.util.ArrayList();
                java.util.Iterator it = null;
                if (!Is.emptyString(parameterValuesStereotypes)) {
                    it = view.getPropertiesNamesFromStereotypesList(parameterValuesStereotypes).iterator();
                }
                else if (!Is.emptyString(parameterValuesProperties)) {
                    it = java.util.Arrays.asList(parameterValuesProperties.split(",")).iterator();
                }
                while (it != null && it.hasNext()) {
                    String propertyName = (String) it.next();
                    Object parameterValue = view.getValue(propertyName);
                    MetaProperty metaProperty = view.getMetaProperty(propertyName);
                    try {
                        if (WebEditors.mustToFormat(metaProperty, view.getViewName())) {
                            String name = Ids.decorate(request, propertyName);
                            request.setAttribute(name + ".value", parameterValue);
                        }
                        PropertyMapping mapping = metaProperty.getMapping();
                        if (mapping != null) {
                            IConverter converter = mapping.getConverter();
                            if (converter != null) {
                                parameterValue = converter.toDB(parameterValue);
                            }
                        }
                    }
                    catch (Exception ex) {
                        // Ignore conversion problems here; just use raw value
                    }
                    p.add(parameterValue);
                }
                calculator.setParameters(p, filter);
            }
            else if (filter != null) {
                calculator.setParameters(null, filter);
            }

            // Log effective configuration
            if (log.isDebugEnabled()) {
                log.debug("Descriptions.getSuggestions called with"
                        + " term='" + term + "', limit=" + limit + ", offset=" + offset
                        + ", model=" + (calculator==null?"":calculator.getModel())
                        + ", keyProperty=" + (calculator==null?"":calculator.getKeyProperty())
                        + ", keyProperties=" + (calculator==null?"":calculator.getKeyProperties())
                        + ", descriptionProperty=" + (calculator==null?"":calculator.getDescriptionProperty())
                        + ", descriptionProperties=" + (calculator==null?"":calculator.getDescriptionProperties())
                        + ", condition=" + condition + ", orderByKey=" + orderByKey + ", order=" + order
                );
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
                log.debug("Descriptions.getSuggestions paginated descriptions size=" + size + ", term='" + term + "', limit=" + max + ", offset=" + offset);
            }

            int count = 0;
            java.util.Iterator it = descriptions.iterator();
            
            // Create simple {label, value} objects for jQuery UI
            List<Map<String, String>> simpleItems = new ArrayList<>();
            
            // Process results (filtering already done in calculator when needed)
            while (it.hasNext()) {
                KeyAndDescription kd = (KeyAndDescription) it.next();
                // Asegurarse de que showCode sea false para evitar que se muestre el código en la UI
                kd.setShowCode(false);
                String label = formatter == null ? String.valueOf(kd.getDescription()) : formatter.format(request, kd.getDescription());
                
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
                log.debug("Descriptions.getSuggestions returning " + out.size() + " filtered items");
            }
            
            return out;
        }
        catch (Exception ex) {
            log.warn("Error getting descriptions suggestions", ex);
            // Return empty list on error to keep client stable
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

    private static String normalize(String s) throws UnsupportedEncodingException {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT);
    }
}
