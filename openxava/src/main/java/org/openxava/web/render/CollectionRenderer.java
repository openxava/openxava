package org.openxava.web.render;

import org.openxava.model.meta.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * Resolves the collection editor and delegates to it.
 * The editor itself (e.g. collectionEditor.jsp) stays as JSP.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class CollectionRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		String collectionName = ctx.getParameter("collectionName");
		String viewObject = ctx.getViewObject();
		View view = ctx.getView(viewObject);
		MetaCollection collection = view.getMetaModel().getMetaCollection(collectionName);
		String editorUrl = "editors/" + WebEditors.getMetaEditorFor(collection, view.getViewName()).getUrl();
		return JspFragment.render(ctx, editorUrl);
	}

}
