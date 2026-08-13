package org.openxava.web.render;

import java.util.*;

import org.openxava.model.meta.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * Renders the actions next to a property.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class PropertyActionsRenderer {

	public static String render(ViewRenderContext ctx) {
		boolean lastSearchKey = ctx.getBooleanParameter("lastSearchKey", false);
		boolean editable = ctx.getBooleanParameter("editable", false);
		View view = ctx.getView(ctx.getViewObject());
		String propertyKey = Ids.undecorate(ctx.getParameter("propertyKey"));
		String propertyName = ctx.getParameter("propertyName");
		MetaProperty p = view.getMetaProperty(propertyName);

		HtmlWriter w = new HtmlWriter();

		if (lastSearchKey) {
			String referencedModel = p.getMetaModel().getName();
			if (view.isSearch() && editable) {
				w.append(ActionHtml.action(ctx, view.getSearchAction(), "keyProperty=" + propertyKey));
			}
			if (view.isCreateNew() && editable) {
				String newAction = view.getNewAction();
				if (newAction == null) {
					w.append(ActionHtml.action(ctx, "Reference.createNew", "model=" + referencedModel + ",keyProperty=" + propertyKey));
				} else {
					w.append(ActionHtml.action(ctx, newAction, "model=" + referencedModel + ",keyProperty=" + propertyKey));
				}
			}
			if (view.isModify() && editable) {
				String editAction = view.getEditAction();
				if (editAction == null) {
					w.append(ActionHtml.action(ctx, "Reference.modify", "model=" + referencedModel + ",keyProperty=" + propertyKey));
				} else {
					w.append(ActionHtml.action(ctx, editAction, "model=" + referencedModel + ",keyProperty=" + propertyKey));
				}
			}
			if (editable) {
				w.append(ActionHtml.action(ctx, "Reference.clear", "keyProperty=" + propertyKey));
			}
		}

		if (editable) {
			for (Iterator<String> it = view.getActionsNamesForReference(lastSearchKey).iterator(); it.hasNext(); ) {
				String action = it.next();
				w.append(ActionHtml.action(ctx, action, null));
			}
		}

		for (Iterator<String> it = view.getActionsNamesForProperty(p, editable).iterator(); it.hasNext(); ) {
			String action = it.next();
			w.append(ActionHtml.action(ctx, action, "xava.keyProperty=" + propertyKey));
		}

		return w.toString();
	}

}
