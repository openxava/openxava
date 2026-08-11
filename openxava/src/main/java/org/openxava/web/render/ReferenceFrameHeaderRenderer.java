package org.openxava.web.render;

import org.openxava.view.*;
import org.openxava.web.*;

/**
 * Renders the header content for reference frames (formerly referenceFrameHeader.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ReferenceFrameHeaderRenderer {

	public static String render(ViewRenderContext ctx) {
		String referenceName = ctx.getParameter("referenceName");
		View view = ctx.getView(ctx.getViewObject());
		View referenceView = view.getSubview(referenceName);
		boolean editable = referenceView.isKeyEditable();
		String propertyKey = referenceView.getPropertyPrefix() + referenceView.getSearchKeyName();

		HtmlWriter w = new HtmlWriter();
		w.append("<span id='").append(ctx.decorateId("property_actions_" + propertyKey)).append("'>");
		// Delegate to PropertyActionsRenderer with the right parameters
		ViewRenderContext subCtx = ctx.withParameters(java.util.Map.of(
			"propertyKey", propertyKey,
			"propertyName", referenceView.getSearchKeyName(),
			"lastSearchKey", "true",
			"editable", String.valueOf(editable),
			"viewObject", referenceView.getViewObject(),
			"referenceActions", "true",
			"propertyActions", "false"
		));
		w.append(PropertyActionsRenderer.render(subCtx));
		w.append("</span>");
		return w.toString();
	}

}
