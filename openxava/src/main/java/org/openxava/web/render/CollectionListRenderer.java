package org.openxava.web.render;

import java.util.*;

import org.openxava.tab.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Prepares a collection tab (styles, context) and renders the list
 * (formerly collectionList.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class CollectionListRenderer {

	/**
	 * @param ctx the render context; expects collection editor variables via parameters
	 *            or uses the same request parameters as collectionList.jsp's parent.
	 * @param idCollection collection DOM id
	 * @param subview collection subview
	 * @param lineAction row line action
	 * @param viewName view object name for the collection subview
	 * @param view parent view (for keyEditable)
	 */
	public static String render(ViewRenderContext ctx, String idCollection, View subview,
			String lineAction, String viewName, View view) throws Exception {
		String tabObject = org.openxava.web.Collections.tabObject(idCollection);
		Tab tab = subview.getCollectionTab();

		tab.clearStyle();
		int selectedRow = subview.getCollectionEditingRow();
		if (selectedRow >= 0) {
			Style style = ctx.getStyle();
			String cssClass = selectedRow % 2 == 0
				? style.getListPairSelected()
				: style.getListOddSelected();
			tab.setStyle(selectedRow, cssClass);
		}
		ctx.getModuleContext().put(ctx.getRequest(), tabObject, tab);

		Map<String, String> params = new HashMap<>();
		params.put("collection", idCollection);
		params.put("rowAction", lineAction == null ? "" : lineAction);
		params.put("tabObject", tabObject);
		params.put("viewObject", viewName);
		params.put("viewKeyEditable", String.valueOf(view.isKeyEditable()));
		return ListRenderer.render(ctx.withParameters(params));
	}

}
