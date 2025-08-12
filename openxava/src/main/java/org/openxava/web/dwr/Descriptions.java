package org.openxava.web.dwr;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.calculators.DescriptionsCalculator;
import org.openxava.converters.IConverter;
import org.openxava.filters.IFilter;
import org.openxava.filters.IRequestFilter;
import org.openxava.formatters.IFormatter;
import org.openxava.mapping.PropertyMapping;
import org.openxava.model.meta.MetaProperty;
import org.openxava.util.Is;
import org.openxava.util.XavaException;
import org.openxava.util.XavaResources;
import org.openxava.view.View;
import org.openxava.web.Ids;
import org.openxava.web.WebEditors;
import org.openxava.util.KeyAndDescription;

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
            String descriptionProperty, String descriptionProperties
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

            // Log effective configuration (debug)
            if (log.isDebugEnabled()) {
                log.debug("Descriptions.getSuggestions effective config => propertyKey=" + propertyKey
                        + ", model=" + (calculator==null?"":calculator.getModel())
                        + ", keyProperty=" + (calculator==null?"":calculator.getKeyProperty())
                        + ", keyProperties=" + (calculator==null?"":calculator.getKeyProperties())
                        + ", descriptionProperty=" + (calculator==null?"":calculator.getDescriptionProperty())
                        + ", descriptionProperties=" + (calculator==null?"":calculator.getDescriptionProperties())
                        + ", condition=" + condition + ", orderByKey=" + orderByKey + ", order=" + order
                );
            }

            // Get descriptions and filter by term
            int max = sanitizeLimit(limit);
            String qt = normalize(term);
            Collection descriptions = calculator.getDescriptions();
            if (log.isDebugEnabled()) {
                int size = 0;
                try { size = descriptions==null?0:descriptions.size(); } catch(Exception ignore) {}
                log.debug("Descriptions.getSuggestions base descriptions size=" + size + ", term='" + term + "'");
            }

            int count = 0;
            java.util.Iterator it = descriptions.iterator();
            while (it.hasNext()) {
                KeyAndDescription kd = (KeyAndDescription) it.next();
                String label = formatter == null ? String.valueOf(kd.getDescription()) : formatter.format(request, kd.getDescription());
                if (qt.isEmpty() || normalize(label).contains(qt)) {
                    Map<String, String> item = new HashMap<>(2);
                    item.put("label", label);
                    item.put("value", String.valueOf(kd.getKey()));
                    out.add(item);
                    count++;
                    if (count >= max) break;
                }
            }
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
        if (limit <= 0) return 30;
        return Math.min(limit, 100);
    }

    private static String normalize(String s) throws UnsupportedEncodingException {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT);
    }
}
