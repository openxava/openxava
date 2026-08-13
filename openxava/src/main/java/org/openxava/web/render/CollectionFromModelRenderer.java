package org.openxava.web.render;

import java.util.*;

import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders a collection-from-model inline table with headers, row actions,
 * checkboxes and formatted values.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class CollectionFromModelRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		String collectionName = ctx.getParameter("collectionName");
		String viewObject = ctx.getViewObject();
		View view = ctx.getView(viewObject);
		View subview = view.getSubview(collectionName);
		String viewName = viewObject + "_" + collectionName;
		String idCollection = org.openxava.web.Collections.id(ctx.getRequest(), collectionName);
		String propertyPrefixAccumulated = ctx.getParameter("propertyPrefix");
		String propertyPrefix = propertyPrefixAccumulated == null
			? collectionName + "."
			: propertyPrefixAccumulated + collectionName + ".";

		boolean collectionEditable = subview.isCollectionEditable();
		boolean collectionMembersEditables = subview.isCollectionMembersEditables();
		String lineAction;
		if (collectionEditable || collectionMembersEditables) {
			lineAction = subview.getEditCollectionElementAction();
		} else {
			lineAction = subview.getViewCollectionElementAction();
		}

		ModuleManager manager = ctx.getManager();
		Style style = ctx.getStyle();
		Messages errors = ctx.getErrors();

		ctx.getMessages();

		String tabObject = ctx.getParameter("tabObject", "xava_tab");
		String onSelectCollectionElementAction = subview.getOnSelectCollectionElementAction();
		manager.registerAction(onSelectCollectionElementAction);
		MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction)
			? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
		boolean resizeColumns = style.allowsResizeColumns() && XavaPreferences.getInstance().isResizeColumns();
		boolean sortable = subview.isCollectionSortable();

		HtmlWriter w = new HtmlWriter();

		if (resizeColumns) {
			w.append("<div class=\"").append(Ids.decorate(ctx.getRequest(), "collection_scroll"))
				.append(" ox-overflow-auto\">");
		}

		w.append("<table id=\"").append(Ids.decorate(ctx.getRequest(), idCollection))
			.append("\" class=\"ox-list\" ").append(style.getListCellSpacing()).append(">");

		if (sortable) {
			w.append("<tbody class=\"xava_sortable_row\">");
		}

		// Header row
		w.append("<tr class=\"ox-list-header\">");

		if (lineAction != null) {
			w.append("<th class=\"ox-list-header\"></th>");
		}

		w.append("<th class=\"ox-list-header\" width=\"5\">");
		w.append("<input type=\"checkbox\" name=\"")
			.append(Ids.decorate(ctx.getRequest(), "xava_selected_all"))
			.append("\" value=\"").append(propertyPrefix).append("selected_all\"")
			.append(" data-on-select-collection-element-action=\"").append(onSelectCollectionElementAction).append("\"")
			.append(" data-view-object=\"").append(idCollection).append("\"")
			.append(" data-prefix=\"").append(propertyPrefix).append("\"")
			.append(" data-tab-object=\"").append(tabObject).append("\"/>");
		w.append("</th>");

		// Header columns
		int columnIndex = 0;
		for (Iterator<MetaProperty> it = subview.getMetaPropertiesList().iterator(); it.hasNext(); columnIndex++) {
			MetaProperty p = it.next();
			String label = p.getQualifiedLabel(ctx.getRequest());
			int columnWidth = subview.getCollectionColumnWidth(columnIndex);
			String width = columnWidth < 0 || !resizeColumns ? "" : "data-width=" + columnWidth;
			String widthClass = width.equals("") ? "ox-list-default-column-width" : "";

			w.append("<th class=\"ox-list-header ox-padding-right-0\">");
			w.append("<div id=\"").append(Ids.decorate(ctx.getRequest(), idCollection))
				.append("_col").append(columnIndex).append("\" class=\"");
			if (resizeColumns) w.append("xava_resizable ");
			w.append(widthClass).append("\" ").append(width).append(">");
			w.append(label).append("&nbsp;");
			w.append("</div>");
			w.append("</th>");
		}
		w.append("</tr>");

		// Data rows
		Collection aggregates = subview.getCollectionValues();
		View parent = view.getParent();
		boolean parentHasSections = parent != null && parent.hasSections();
		boolean condition = view.isKeyEditable() && parentHasSections && !view.isRepresentsEntityReference();
		if (aggregates == null || condition) aggregates = java.util.Collections.EMPTY_LIST;

		int f = 0;
		for (Iterator itAggregates = aggregates.iterator(); itAggregates.hasNext(); f++) {
			Map row = (Map) itAggregates.next();
			String cssClass = f % 2 == 0 ? "ox-list-pair" : "ox-list-odd";
			String cssCellClass = cssClass;
			if (f == subview.getCollectionEditingRow()) {
				String selectedClass = f % 2 == 0 ? style.getListPairSelected() : style.getListOddSelected();
				cssClass = cssClass + " " + selectedClass;
				if (style.isApplySelectedStyleToCellInList()) {
					cssCellClass = cssCellClass + " " + selectedClass;
				}
			}
			String idRow = Ids.decorate(ctx.getRequest(), propertyPrefix) + f;
			String events = f % 2 == 0 ? style.getListPairEvents() : style.getListOddEvents();

			w.append("<tr id=\"").append(idRow).append("\" class=\"").append(cssClass).append("\" ")
				.append(events).append(">");

			// Line action cell
			if (lineAction != null) {
				w.append("<td class=\"").append(cssCellClass).append(" ox-list-action-cell\">");
				w.append("<nobr>");
				if (sortable) {
					w.append("<i class=\"xava_handle mdi mdi-swap-vertical\"></i>");
				}
				w.append(ActionHtml.action(ctx, lineAction, "row=" + f + ",viewObject=" + viewName));

				if (style.isSeveralActionsPerRow()) {
					String argv = "row=" + f + ",viewObject=" + viewName;
					Collection<String> rowActionNames = view.removeUnavailableActionFromRow(subview.getRowActionsNames(), argv);
					boolean hasIconOrImage = view.isRowActionHaveIcon(rowActionNames);
					if (rowActionNames.size() < XavaPreferences.getInstance().getRowActionsPopupThreshold() - 1) {
						for (String rowAction : rowActionNames) {
							w.append(ActionHtml.action(ctx, rowAction, argv, true));
						}
					} else {
						w.append("<a class=\"ox-image-link xava_popup_menu_icon\">");
						w.append("<i class=\"mdi mdi-dots-vertical\"></i>");
						w.append("</a>");
						w.append("<ul class=\"ox-popup-menu ox-image-link ox-display-none\">");
						for (String rowActionString : rowActionNames) {
							w.append("<li>");
							Map<String, String> buttonParams = new HashMap<>();
							buttonParams.put("action", rowActionString);
							buttonParams.put("addSpaceWithoutImage", String.valueOf(hasIconOrImage));
							buttonParams.put("argv", argv);
							buttonParams.put("alwaysAvailable", "true");
							w.append(ButtonRenderer.render(ctx.withParameters(buttonParams)));
							w.append("</li>");
						}
						w.append("</ul>");
					}
				}
				w.append("</nobr>");
				w.append("</td>");
			}

			// Checkbox cell
			w.append("<td class=\"").append(cssCellClass).append("\" width=\"5\">");
			w.append("<input class=\"xava_selected\" type=\"checkbox\" name=\"")
				.append(Ids.decorate(ctx.getRequest(), "xava_selected"))
				.append("\" value=\"").append(propertyPrefix).append("__SELECTED__:").append(f).append("\"")
				.append(" data-on-select-collection-element-action=\"").append(onSelectCollectionElementAction).append("\"")
				.append(" data-row-id=\"").append(idRow).append("\"")
				.append(" data-row=\"").append(f).append("\"")
				.append(" data-view-object=\"").append(viewName).append("\"")
				.append(" data-tab-object=\"").append(tabObject).append("\"")
				.append(" data-confirm-message=\"");
			if (onSelectCollectionElementMetaAction != null) {
				w.append(onSelectCollectionElementMetaAction.getConfirmMessage());
			}
			w.append("\" data-takes-long=\"")
				.append(onSelectCollectionElementMetaAction != null && onSelectCollectionElementMetaAction.isTakesLong())
				.append("\"/>");
			w.append("</td>");

			// Data cells
			columnIndex = 0;
			for (Iterator<MetaProperty> it = subview.getMetaPropertiesList().iterator(); it.hasNext(); columnIndex++) {
				MetaProperty p = it.next();
				String align = p.isNumber() && !p.hasValidValues() ? "ox-text-align-right" : "";
				int columnWidth = subview.getCollectionColumnWidth(columnIndex);
				String width = columnWidth < 0 || !resizeColumns ? "" : "data-width=" + columnWidth;
				String widthClass = width.equals("") ? "ox-list-default-column-width" : "";
				String propertyName = p.getName();
				Object value = Maps.getValueFromQualifiedName(row, propertyName);
				String fvalue = WebEditors.format(ctx.getRequest(), p, value, errors, view.getViewName(), true);
				Object title = WebEditors.formatTitle(ctx.getRequest(), p, value, errors, view.getViewName(), true);

				w.append("<td class=\"").append(cssCellClass).append(" ").append(align)
					.append(" ox-list-data-cell\">");

				StringBuilder body = new StringBuilder();
				body.append("<div title=\"").append(title).append("\" class=\"")
					.append(Ids.decorate(ctx.getRequest(), "tipable")).append(" ")
					.append(Ids.decorate(ctx.getRequest(), idCollection)).append("_col").append(columnIndex)
					.append(" ").append(widthClass).append("\" ").append(width).append(">");
				if (resizeColumns) body.append("<nobr>");
				body.append(fvalue).append("&nbsp; ");
				if (resizeColumns) body.append("</nobr>");
				body.append("</div>");

				if (Is.emptyString(lineAction)) {
					w.append(body.toString());
				} else {
					w.append(ActionHtml.link(ctx, lineAction, "row=" + f + ",viewObject=" + viewName,
						null, false, body.toString()));
				}

				w.append("</td>");
			}
		}
		w.append("</tr>");

		// Collection totals
		w.append(JspFragment.render(ctx, "editors/collectionTotals.jsp"));

		if (sortable) {
			w.append("</tbody>");
		}
		w.append("</table>");

		if (resizeColumns) {
			w.append("</div>");
		}

		return w.toString();
	}

}
