package org.openxava.web;

import javax.servlet.http.HttpServletRequest;

import org.openxava.controller.ModuleContext;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;

/**
 * Utility methods related to JPA schema resolution in web requests.
 * <p>
 * Centralizes the logic to set the default JPA schema for the current
 * request/session, to keep code DRY across servlets.
 * </p>
 *
 * @author Javier Paniza
 * @since 7.6
 */
public final class Schemas {

    private Schemas() {}

    /**
     * Sets the default JPA schema into {@link XPersistence} based on
     * organization or default schema information available in the current
     * request/session context.
     *
     * <ul>
     *   <li>If session attribute {@code naviox.organization} is present and not empty,
     *       it uses it as default schema.</li>
     *   <li>Otherwise, it tries with context attribute {@code xava_defaultSchema}.</li>
     * </ul>
     */
    public static void setDefaultSchema(HttpServletRequest request) {
        String organization = (String) request.getSession().getAttribute("naviox.organization");
        if (!Is.emptyString(organization)) {
            XPersistence.setDefaultSchema(organization);
            return;
        }
        ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
        String defaultSchema = context == null ? null : (String) context.get(request, "xava_defaultSchema");
        if (!Is.emptyString(defaultSchema)) XPersistence.setDefaultSchema(defaultSchema);
    }
}
