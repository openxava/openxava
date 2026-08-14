package org.openxava.web.render;

import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders the collection frame header: size, new element action and totals.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class CollectionFrameHeaderRenderer {

	public static String render(ViewRenderContext ctx) {
		String collectionName = ctx.getParameter("collectionName");
		View view = ctx.getView(ctx.getViewObject());
		View collectionView = view.getSubview(collectionName);
		Style style = ctx.getStyle();
		String frameId = Ids.decorate(ctx.getRequest(), "frame_" + view.getPropertyPrefix() + collectionName);
		String hiddenStyle = view.isFrameClosed(frameId) ? "" : "ox-display-none";

		if (!collectionView.isCollectionFromModel()) {
			Tab tab = collectionView.getCollectionTab();
			if (ctx.getRequest().getAttribute(Tab.TAB_RESETED_PREFIX + tab) == null) {
				String collectionId = Collections.id(ctx.getRequest(), collectionName);
				String tabObject = Collections.tabObject(collectionId);
				tab.setTabObject(tabObject);
				tab.setRequest(ctx.getRequest());
				tab.setConditionParameters();
				tab.reset();
				ctx.getRequest().setAttribute(Tab.TAB_RESETED_PREFIX + tab, Boolean.TRUE);
			}
		}

		HtmlWriter w = new HtmlWriter();

		w.append("<span class='").append(hiddenStyle).append("'>");
		w.append("(").append(String.valueOf(collectionView.getCollectionSize())).append(")");
		if (collectionView.isCollectionEditable()) {
			String viewName = ctx.getViewObject() + "_" + collectionName;
			w.append(ActionHtml.image(ctx, collectionView.getNewCollectionElementAction(), "viewObject=" + viewName, null, false));
		}
		w.append("</span>");

		w.append("<span class='").append(style.getFrameTotals()).append(" ").append(hiddenStyle).append("'>");

		int totalRows = collectionView.getCollectionTotalsCount();
		int totalColumns = collectionView.getMetaPropertiesList().size();
		for (int row = 0; row < totalRows; row++) {
			for (int column = 0; column < totalColumns; column++) {
				if (collectionView.hasCollectionTotal(row, column)) {
					MetaProperty p = (MetaProperty) collectionView.getMetaPropertiesList().get(column);
					String ftotal = WebEditors.format(ctx.getRequest(), p,
						collectionView.getCollectionTotal(row, column),
						ctx.getErrors(), view.getViewName(), true);
					w.append("&nbsp;&nbsp;&nbsp;&nbsp;");
					w.append("<span class='").append(style.getFrameTotalsLabel()).append("'>");
					w.append(collectionView.getCollectionTotalLabel(row, column));
					w.append(":</span> ");
					w.append("<span class='").append(style.getFrameTotalsValue()).append("'>");
					w.append(ftotal);
					w.append("</span>");
				}
			}
		}

		if (!collectionView.isCollectionFromModel() && XavaPreferences.getInstance().isSummationInList()) {
			Tab tab = collectionView.getCollectionTab();
			int totalTabColumns = tab.getMetaProperties().size();
			for (int column = 0; column < totalTabColumns; column++) {
				if (!tab.isFixedTotal(column) && tab.hasTotal(column)) {
					MetaProperty p = tab.getMetaProperty(column);
					String ftotal = WebEditors.format(ctx.getRequest(), p,
						tab.getTotal(column), ctx.getErrors(), view.getViewName(), true);
					String label = XavaResources.getString(ctx.getRequest(), "sum_of", p.getLabel());
					w.append("&nbsp;&nbsp;&nbsp;&nbsp;");
					w.append("<span class='").append(style.getFrameTotalsLabel()).append("'>");
					w.append(label).append(":</span>");
					w.append("<span class='").append(style.getFrameTotalsValue()).append("'>");
					w.append(ftotal).append("</span>");
				}
			}
		}

		w.append("</span>");
		return w.toString();
	}

}
