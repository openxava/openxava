package org.openxava.web.servlets;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.editors.*;

/**
 * Servlet to download all files of a {@code @Files} property as a single ZIP.
 * <p>
 * It is invoked from the upload editor's "Download all" link and streams a ZIP
 * containing all files stored in the library identified by the current value of
 * the property in the active view.
 * </p>
 *
 * <p><b>Endpoint</b>: {@code /xava/zipfiles}</p>
 *
 * <p><b>Expected request parameters (query string)</b>:
 * <ul>
 *   <li>{@code application}: OpenXava application name.</li>
 *   <li>{@code module}: OpenXava module name.</li>
 *   <li>{@code propertyKey}: Decorated id of the {@code @Files} property (for example, {@code xava_App_Mod_prop}).</li>
 *   <li>{@code windowId}: Current window id (used by OpenXava context).</li>
 * </ul>
 * </p>
 *
 * <p><b>Behavior</b>:
 * <ul>
 *   <li>Initializes the OpenXava request context and default schema.</li>
 *   <li>Resolves the current {@link View} and obtains the library id from the property value.</li>
 *   <li>Loads all files via {@link FilePersistorFactory} and builds a ZIP on the fly.</li>
 *   <li>Sets {@code Content-Type} to {@code application/zip} and a safe file name in {@code Content-Disposition}.</li>
 * </ul>
 * </p>
 *
 * <p><b>Responses</b>:
 * <ul>
 *   <li>{@code 200 OK}: Streams the ZIP.</li>
 *   <li>{@code 400 Bad Request}: No active view.</li>
 *   <li>{@code 404 Not Found}: Property has no library id or library has no files.</li>
 *   <li>{@code 500 Internal Server Error}: Any unexpected error while building the ZIP.</li>
 * </ul>
 * </p>
 *
 * <p><b>Notes</b>:
 * <ul>
 *   <li>Zip entry names are sanitized and de-duplicated to avoid path traversal and collisions.</li>
 *   <li>This servlet relies on OpenXava context; it is not intended to be used directly without it.</li>
 * </ul>
 * </p>
 * 
 * @since 7.6
 * @author Javier Paniza
 */
@WebServlet("/xava/zipfiles")
public class ZipFilesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(ZipFilesServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Schemas.setDefaultSchema(request);

            String propertyKey = request.getParameter("propertyKey");
            String property = Ids.undecorate(propertyKey);
            View view = getCurrentView(request);
            if (view == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String libraryId = view.getValueString(property);
            if (Is.emptyString(libraryId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary(libraryId);
            if (files == null || files.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String fileName = buildZipFileName(view, property);
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                Set<String> usedNames = new HashSet<>();
                for (AttachedFile f : files) {
                    String entryName = sanitize(f.getName());
                    entryName = avoidCollision(entryName, usedNames);
                    usedNames.add(entryName);
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    zos.write(f.getData());
                    zos.closeEntry();
                }
                zos.finish();
            }
        }
        catch (Exception ex) {
            log.error(XavaResources.getString("zip_download_error"), ex);
            throw new ServletException(XavaResources.getString("zip_download_error"));
        }
    }

    private ModuleManager getManager(HttpServletRequest request) {
        ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
        return (ModuleManager) context.get(request, "manager");
    }

    private View getCurrentView(HttpServletRequest request) {
        ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
        return (View) context.get(request, "xava_view");
    }

    private String buildZipFileName(View view, String property) {
        try {
            String model = view.getModelName();
            MetaProperty mp = view.getMetaProperty(property);
            String base = (model != null ? model : "files") + "_" + (mp != null ? mp.getName() : property) + ".zip";
            return URLEncoder.encode(base, "UTF-8").replace("+", "%20");
        }
        catch (Exception ex) {
            return "files.zip";
        }
    }

    private String sanitize(String name) {
        if (name == null) return "file";
        // Remove path separators and control chars
        return name.replace('\\', '_').replace('/', '_');
    }

    private String avoidCollision(String name, Set<String> used) {
        if (!used.contains(name)) return name;
        String base = name;
        String ext = "";
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            base = name.substring(0, dot);
            ext = name.substring(dot);
        }
        int i = 1;
        String candidate;
        do {
            candidate = base + " (" + i + ")" + ext;
            i++;
        } while (used.contains(candidate));
        return candidate;
    }

}
