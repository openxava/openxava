package org.openxava.web.render;

import java.util.*;

import org.openxava.util.*;

/**
 * Renders the messages block: warnings, messages and infos.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class MessagesRenderer {

	public static String render(ViewRenderContext ctx) {
		Messages messages = ctx.getMessages();
		if (!messages.contains()) return "";
		HtmlWriter w = new HtmlWriter();
		w.append("<div class='ox-messages-wrapper'>");

		w.append("<table id='").append(ctx.decorateId("warnings_table")).append("'>");
		for (Iterator<String> it = messages.getWarningsStrings(ctx.getRequest()).iterator(); it.hasNext(); ) {
			w.append("<tr><td class=ox-warnings>");
			w.append("<div class='ox-message-box'><i class=\"mdi mdi-close\"></i><i class=\"mdi mdi-alert-outline ox-message-icon\"></i>");
			w.append(it.next());
			w.append("</div></td></tr>");
		}
		w.append("</table>");

		w.append("<table id='").append(ctx.decorateId("messages_table")).append("'>");
		for (Iterator<String> it = messages.getMessagesStrings(ctx.getRequest()).iterator(); it.hasNext(); ) {
			w.append("<tr><td class=ox-messages>");
			w.append("<div class='ox-message-box'><i class=\"mdi mdi-close\"></i><i class=\"mdi mdi-check-circle-outline ox-message-icon\"></i>");
			w.append(it.next());
			w.append("</div></td></tr>");
		}
		w.append("</table>");

		w.append("<table id='").append(ctx.decorateId("infos_table")).append("'>");
		for (Iterator<String> it = messages.getInfosStrings(ctx.getRequest()).iterator(); it.hasNext(); ) {
			w.append("<tr><td class=ox-infos>");
			w.append("<div class='ox-message-box'><i class=\"mdi mdi-close\"></i><i class=\"mdi mdi-information-outline ox-message-icon\"></i>");
			w.append(it.next());
			w.append("</div></td></tr>");
		}
		w.append("</table>");

		w.append("</div>");
		return w.toString();
	}

}
