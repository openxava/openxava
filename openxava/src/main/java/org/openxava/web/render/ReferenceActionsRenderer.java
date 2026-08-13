package org.openxava.web.render;

import java.util.*;

import org.openxava.model.meta.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * Renders the actions for a reference.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ReferenceActionsRenderer {

	/**
	 * Renders reference actions given the already-resolved view, reference and propertyKey.
	 */
	public static String render(ViewRenderContext ctx, View view, MetaReference ref, String propertyKey) {
		String keyPropertyForAction = Ids.undecorate(propertyKey);
		boolean editable = view.isEditable(ref.getName());

		HtmlWriter w = new HtmlWriter();

		if (editable && view.isCreateNewForReference(ref)) {
			String newAction = view.getNewActionForReference(ref);
			if (newAction == null) {
				w.append(ActionHtml.action(ctx, "Reference.createNew",
					"model=" + ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction));
			} else {
				w.append(ActionHtml.action(ctx, newAction,
					"model=" + ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction));
			}
		}

		if (editable && view.isModifyForReference(ref)) {
			String editAction = view.getEditActionForReference(ref);
			if (editAction == null) {
				w.append(ActionHtml.action(ctx, "Reference.modify",
					"model=" + ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction));
			} else {
				w.append(ActionHtml.action(ctx, editAction,
					"model=" + ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction));
			}
		}

		if (editable) {
			w.append(ActionHtml.action(ctx, "Reference.clear", ",keyProperty=" + keyPropertyForAction));
		}

		for (Iterator<String> it = view.getActionsNamesForReference(ref, editable).iterator(); it.hasNext(); ) {
			String action = it.next();
			w.append(ActionHtml.action(ctx, action, null));
		}

		return w.toString();
	}

}
