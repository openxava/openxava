package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders a subcontroller button with its dropdown actions (formerly subButton.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class SubButtonRenderer {

	public static String render(ViewRenderContext ctx) {
		ModuleManager manager = ctx.getManager();
		Style style = ctx.getStyle();
		String controllerName = ctx.getParameter("controller");
		MetaController metaController = MetaControllers.getMetaController(controllerName);

		String image = ctx.getParameter("image");
		if (Is.empty(image)) image = metaController.getImage();
		String icon = ctx.getParameter("icon");
		if (Is.empty(icon)) icon = metaController.getIcon();
		String mode = ctx.getParameter("xava_mode");
		if (mode == null) mode = manager.getModeName();
		String argv = ctx.getParameter("argv");
		if (Is.empty(argv)) argv = "";

		String id = Ids.decorate(ctx.getRequest(), "sc-" + controllerName + "_" + mode);
		String containerId = Ids.decorate(ctx.getRequest(), "sc-container-" + controllerName + "_" + mode);
		String buttonId = Ids.decorate(ctx.getRequest(), "sc-button-" + controllerName + "_" + mode);
		String imageId = Ids.decorate(ctx.getRequest(), "sc-image-" + controllerName + "_" + mode);
		String aId = Ids.decorate(ctx.getRequest(), "sc-a-" + controllerName + "_" + mode);
		String spanId = Ids.decorate(ctx.getRequest(), "sc-span-" + controllerName + "_" + mode);

		HtmlWriter w = new HtmlWriter();
		w.append("<span id='").append(containerId).append("'>");
		w.append("<span id='").append(buttonId).append("' class=\"ox-button-bar-button ox-subcontroller-button\">");
		w.append("<a class=\"xava_subcontroller\" id='").append(aId).append("'");
		w.append(" data-id='").append(id).append("' data-container='").append(containerId).append("' data-button='").append(buttonId).append("'");
		w.append(" data-image='").append(imageId).append("' data-a='").append(aId).append("' data-span='").append(spanId).append("'>");
		w.append("<nobr> ");
		if (!Is.emptyString(icon) && (style.isUseIconsInsteadOfImages() || Is.emptyString(image))) {
			w.append("<i class=\"mdi mdi-").append(icon).append("\"></i>");
		} else {
			w.append("<img src=\"").append(ctx.getContextPath()).append("/").append(style.getImagesFolder()).append("/").append(image).append("\"/>");
		}
		w.append(" ").append(Labels.get(controllerName));
		w.append(" <i id='").append(imageId).append("' class=\"mdi mdi-menu-down\"></i>&nbsp;");
		w.append("</nobr></a></span>");

		w.append("<div id=\"").append(id).append("\" class=\"ox-subcontroller\"><table>");
		Collection<MetaAction> actions = manager.getSubcontrollerMetaActions(controllerName);
		for (MetaAction action : actions) {
			if (!action.appliesToMode(mode)) continue;
			w.append("<tr><td>");
			Map<String, String> params = new HashMap<>();
			params.put("action", action.getQualifiedName());
			params.put("addSpaceWithoutImage", "true");
			params.put("argv", argv);
			ViewRenderContext buttonCtx = new ViewRenderContext(ctx.getRequest(), ctx.getResponse(), params);
			w.append(ButtonRenderer.render(buttonCtx));
			w.append("</td></tr>");
		}
		w.append("</table></div></span>");
		return w.toString();
	}

}
