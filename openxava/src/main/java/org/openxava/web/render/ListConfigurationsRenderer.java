package org.openxava.web.render;

import org.openxava.tab.*;
import org.openxava.web.*;

/**
 * Renders the list configurations dropdown (formerly listConfigurations.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ListConfigurationsRenderer {

	public static String render(ViewRenderContext ctx) {
		String tabObject = ctx.getParameter("tabObject", "xava_tab");
		Tab tab = (Tab) ctx.getModuleContext().get(ctx.getRequest(), tabObject);
		tab.setRequest(ctx.getRequest());
		String confName = tab.getConfigurationName();

		HtmlWriter w = new HtmlWriter();
		w.append("<select class=\"xava_list_configurations\" name='")
			.append(ctx.decorateId("listConfigurations"))
			.append("' title=\"").append(confName).append("\">");
		w.append("<option value=\"\">").append(confName).append("</option>");
		int count = 1;
		for (Tab.Configuration conf : tab.getConfigurations()) {
			if (!confName.equals(conf.getName())) {
				if (++count > Tab.MAX_CONFIGURATIONS_COUNT) break;
				w.append("<option value=\"").append(conf.getId()).append("\">").append(conf.getName()).append("</option>");
			}
		}
		w.append("</select>");
		return w.toString();
	}

}
