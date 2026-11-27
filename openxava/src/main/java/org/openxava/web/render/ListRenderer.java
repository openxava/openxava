package org.openxava.web.render;

import java.time.*;
import java.util.*;

import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * Renders list mode header (title, configurations, group-by, row count)
 * and delegates the tab editor to JSP.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ListRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		String tabObject = ctx.getParameter("tabObject", "xava_tab");
		Tab tab = (Tab) ctx.getModuleContext().get(ctx.getRequest(), tabObject);
		ModuleManager manager = ctx.getManager();
		String collection = ctx.getParameter("collection");
		String groupBy = tab.getGroupBy();
		boolean grouping = !Is.emptyString(groupBy);

		HtmlWriter w = new HtmlWriter();

		if (collection == null || collection.isEmpty()) {
			w.append("<table width=\"100%\">");
			w.append("<tr><td class=ox-list-title>");

			w.append(ListConfigurationsRenderer.render(ctx));

			if (!tab.isAllConfiguration()) {
				if (tab.isSaveConfigurationAllowed()) {
					w.append("<span id=\"xava_save_list_configuration\">");
					w.append(ActionHtml.link(ctx, "List.saveConfiguration", null, null, false));
					w.append("</span>");
				} else {
					w.append("<span id=\"xava_change_list_configuration\">");
					w.append(ActionHtml.link(ctx, "List.changeConfiguration", null, null, false));
					w.append("</span>");
				}
			}

			if (tab.isTitleVisible()) {
				w.append("<span id=\"list-title\">").append(tab.getTitle()).append("</span>");
			}

			if (manager.getDialogLevel() == 0) {
				w.append("<select class=\"xava_group_by\">");
				w.append("<option value=\"\">")
					.append(XavaResources.getString(ctx.getRequest(), "no_grouping"))
					.append("</option>");
				for (MetaProperty property : tab.getMetaPropertiesGroupBy()) {
					String selected = groupBy.equals(property.getQualifiedName()) ? "selected" : "";
					w.append("<option value=\"").append(property.getQualifiedName()).append("\" ")
						.append(selected).append(">")
						.append(XavaResources.getString(ctx.getRequest(), "group_by")).append(" ")
						.append(property.getQualifiedLabel(ctx.getRequest()).toLowerCase())
						.append("</option>");
					if (property.getType().isAssignableFrom(Date.class)
							|| property.getType().isAssignableFrom(LocalDate.class)) {
						if (groupBy.equals(property.getQualifiedName() + "[month]")) {
							selected = "selected";
						} else {
							selected = "";
						}
						w.append("<option value=\"").append(property.getQualifiedName())
							.append("[month]\" ").append(selected).append(">")
							.append(XavaResources.getString(ctx.getRequest(), "group_by_month_of")).append(" ")
							.append(property.getQualifiedLabel(ctx.getRequest()).toLowerCase())
							.append("</option>");
						if (groupBy.equals(property.getQualifiedName() + "[year]")) {
							selected = "selected";
						} else {
							selected = "";
						}
						w.append("<option value=\"").append(property.getQualifiedName())
							.append("[year]\" ").append(selected).append(">")
							.append(XavaResources.getString(ctx.getRequest(), "group_by_year_of")).append(" ")
							.append(property.getQualifiedLabel(ctx.getRequest()).toLowerCase())
							.append("</option>");
					}
				}
				w.append("</select>");
			}
			w.append("</td></tr>");
			w.append("</table>");
		}

		String editorUrl = WebEditors.getUrl(tab.getEditor(), tab.getMetaTab());
		String viewKeyEditable = ctx.getParameter("viewKeyEditable");
		if (viewKeyEditable != null) {
			editorUrl = editorUrl + (editorUrl.indexOf('?') >= 0 ? "&" : "?")
				+ "viewKeyEditable=" + viewKeyEditable;
		}
		// Pass through list-related request parameters that listEditor.jsp expects
		// (rowAction, singleSelection, onlyOneActionPerRow, collection, tabObject, viewObject)
		// via JspFragment context overlay / request parameters already present.
		w.append(JspFragment.render(ctx, editorUrl));
		return w.toString();
	}

}
