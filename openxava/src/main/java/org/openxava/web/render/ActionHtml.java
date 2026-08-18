package org.openxava.web.render;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Generates HTML for OpenXava actions (link, image, button) without JSP tags.
 * Mirrors the output of {@link org.openxava.web.taglib.LinkTag},
 * {@link org.openxava.web.taglib.ImageTag} and {@link org.openxava.web.taglib.ButtonTag}.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ActionHtml {

	public static String link(ViewRenderContext ctx, String action, String argv, String cssClass, boolean alwaysAvailable, String body) {
		if (Is.emptyString(action)) return "";
		MetaAction metaAction = MetaControllers.getMetaAction(action);
		String application = ctx.getApplication();
		String module = ctx.getModule();
		if (!isActionAvailable(ctx, metaAction, alwaysAvailable, argv)) return "";
		registerAction(ctx, action);

		HtmlWriter w = new HtmlWriter();
		w.append("<input name='").append(Ids.decorate(application, module, "action." + action)).append("' type='hidden'/>");
		w.append("<a ");
		if (Is.emptyString(argv)) {
			w.append("id='").append(Ids.decorate(application, module, action)).append("' ");
		}
		w.append("title='").append(filterApostrophes(tooltip(metaAction))).append("'");
		if (metaAction.isLosesChangedData()) w.append(" class='xava_action_loses_changed_data ");
		else w.append(" class='xava_action ");
		if (!Is.emptyString(cssClass)) w.append(cssClass);
		w.append("' data-application='").append(application);
		w.append("' data-module='").append(module);
		w.append("' data-confirm-message='").append(filterApostrophes(metaAction.getConfirmMessage(ctx.getRequest())));
		w.append("' data-takes-long='").append(metaAction.isTakesLong());
		w.append("' data-action='").append(action);
		if (!Is.emptyString(argv)) w.append("' data-argv='").append(argv);
		w.append("' data-in-new-window='").append(metaAction.inNewWindow()).append("'>");
		if (body != null) w.append(body);
		else w.text(metaAction.getLabel(ctx.getRequest()));
		w.append("</a>");
		return w.toString();
	}

	public static String link(ViewRenderContext ctx, String action, String argv, String cssClass, boolean alwaysAvailable) {
		return link(ctx, action, argv, cssClass, alwaysAvailable, null);
	}

	public static String image(ViewRenderContext ctx, String action, String argv, String cssClass, boolean alwaysAvailable) {
		if (Is.emptyString(action)) return "";
		MetaAction metaAction = MetaControllers.getMetaAction(action);
		String application = ctx.getApplication();
		String module = ctx.getModule();
		if (!isActionAvailable(ctx, metaAction, alwaysAvailable, argv)) return "";
		registerAction(ctx, action);
		Style style = ctx.getStyle();

		HtmlWriter w = new HtmlWriter();
		w.append("<input name='").append(Ids.decorate(application, module, "action." + action)).append("' type='hidden'/>");
		w.append("<a ");
		if (Is.emptyString(argv)) {
			w.append("id='").append(Ids.decorate(application, module, action)).append("' ");
		}
		w.append("title='").append(filterApostrophes(tooltip(metaAction))).append("'");
		if (metaAction.isLosesChangedData()) w.append(" class='xava_action_loses_changed_data ");
		else w.append(" class='xava_action ");
		if (!Is.emptyString(cssClass)) w.append(cssClass);
		w.append("' data-application='").append(application);
		w.append("' data-module='").append(module);
		String confirmMessage = Is.empty(argv)
			? metaAction.getConfirmMessage(ctx.getRequest())
			: metaAction.getConfirmMessage(ctx.getRequest(), argv);
		w.append("' data-confirm-message='").append(filterApostrophes(confirmMessage));
		w.append("' data-takes-long='").append(metaAction.isTakesLong());
		w.append("' data-action='").append(action);
		if (!Is.emptyString(argv)) w.append("' data-argv='").append(argv);
		w.append("' data-in-new-window='").append(metaAction.inNewWindow()).append("'>");
		if (metaAction.hasIcon() && (style.isUseIconsInsteadOfImages() || !metaAction.hasImage())) {
			w.append("<i class='mdi mdi-").append(metaAction.getIcon()).append("'></i>");
		} else {
			w.append("<img src='").append(ctx.getContextPath()).append("/").append(style.getImagesFolder()).append("/").append(metaAction.getImage());
			w.append("'\talt='").append(metaAction.getKeystroke()).append(" - ").append(metaAction.getDescription(ctx.getRequest()));
			w.append("'\tborder='0' align='absmiddle'/>");
		}
		w.append("</a>");
		return w.toString();
	}

	public static String button(ViewRenderContext ctx, String action, String argv) {
		if (Is.emptyString(action)) return "";
		MetaAction metaAction = MetaControllers.getMetaAction(action);
		String application = ctx.getApplication();
		String module = ctx.getModule();
		if (!isActionAvailable(ctx, metaAction, false, argv)) return "";
		registerAction(ctx, action);

		HtmlWriter w = new HtmlWriter();
		w.append("<input name='").append(Ids.decorate(application, module, "action." + action)).append("' type='hidden'/>");
		w.append("<input type='button' ");
		if (Is.emptyString(argv)) {
			w.append("id='").append(Ids.decorate(application, module, action)).append("' ");
		}
		w.append("tabindex='1' title='").append(filterApostrophes(tooltip(metaAction))).append("'");
		if (metaAction.isLosesChangedData()) w.append(" class='xava_action_loses_changed_data ");
		else w.append(" class='xava_action ");
		w.append("' value='").append(filterApostrophes(metaAction.getLabel(ctx.getRequest())));
		w.append("' data-application='").append(application);
		w.append("' data-module='").append(module);
		w.append("' data-confirm-message='").append(filterApostrophes(metaAction.getConfirmMessage(ctx.getRequest())));
		w.append("' data-takes-long='").append(metaAction.isTakesLong());
		w.append("' data-action='").append(action);
		if (!Is.emptyString(argv)) w.append("' data-argv='").append(argv);
		w.append("' data-in-new-window='").append(metaAction.inNewWindow()).append("'/>");
		return w.toString();
	}

	/**
	 * Renders an action using the same logic as {@code <xava:action>}: delegates to link, image or button.
	 */
	public static String action(ViewRenderContext ctx, String action, String argv, boolean alwaysAvailable) {
		if (Is.emptyString(action)) return "";
		Style style = ctx.getStyle();
		MetaAction metaAction = MetaControllers.getMetaAction(action);
		if (style.isUseLinkForNoButtonBarAction()) {
			return link(ctx, action, argv, style.getActionLink(), alwaysAvailable, null);
		}
		if (metaAction.hasImage() || metaAction.hasIcon()) {
			return image(ctx, action, argv, style.getActionImage(), alwaysAvailable);
		}
		if (XavaPreferences.getInstance().isButtonsForNoImageActions()) {
			return button(ctx, action, argv);
		}
		return link(ctx, action, argv, style.getActionLink(), alwaysAvailable, null);
	}

	public static String action(ViewRenderContext ctx, String action, String argv) {
		return action(ctx, action, argv, false);
	}

	private static boolean isActionAvailable(ViewRenderContext ctx, MetaAction metaAction, boolean alwaysAvailable, String argv) {
		if (alwaysAvailable) return true;
		ModuleManager manager = ctx.getManager();
		return manager.isActionAvailable(metaAction, ctx.getErrors(), ctx.getMessages(), argv, ctx.getRequest());
	}

	private static void registerAction(ViewRenderContext ctx, String qualifiedActionName) {
		ctx.getManager().registerAction(qualifiedActionName);
	}

	private static String tooltip(MetaAction metaAction) {
		StringBuffer result = new StringBuffer();
		result.append(metaAction.getLabel());
		if (!Is.emptyString(metaAction.getKeystroke())) {
			result.append(" - ").append(metaAction.getKeystroke());
		}
		String description = metaAction.getDescription();
		if (!Is.emptyString(description) && !description.equals(metaAction.getLabel())) {
			result.append(" - ").append(description);
		}
		return result.toString();
	}

	private static String filterApostrophes(String source) {
		return source == null ? "" : source.replace("'", "&#145;");
	}

}
