package org.openxava.web.render;

import java.util.*;

import org.openxava.view.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;

/**
 * Renders section tabs and the active section content.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class SectionsRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		String viewObject = ctx.getViewObject();
		View view = ctx.getView(viewObject);
		// Required before getSectionView(): section viewObject is derived from parent.viewObject.
		// On Hotwire section changes only SectionsRenderer runs, so the field may still be null
		// and section content would fall back to the root view → infinite sections/detail recursion.
		view.setViewObject(viewObject);
		List<MetaView> sections = view.getSections();
		int activeSection = view.getActiveSection();
		if (sections == null || sections.isEmpty()) return "";
		if (activeSection < 0 || activeSection >= sections.size()) activeSection = 0;

		HtmlWriter w = new HtmlWriter();

		w.append("<table width='100%' cellspacing='0' border='0' cellpadding='0'>");
		w.append("<tr><td>");

		w.append("<div class='ox-section'>");
		w.append("<table border='0' cellpadding='0' cellspacing='0'>");
		w.append("<tr>");
		w.append("<td>");

		Iterator<MetaView> itSections = sections.iterator();
		int i = 0;
		while (itSections.hasNext()) {
			MetaView section = itSections.next();
			View sectionView = view.getSectionView(i);
			String sectionName = sectionView.getTitle().equals("") ? section.getLabel(ctx.getRequest()) : sectionView.getTitle();
			String collectionCountLabel = sectionView.getLabelDecoration();
			String labelId = Ids.decorate(ctx.getRequest(), "label_" + sectionView.getViewObject() + "_sectionName");

			if (activeSection == i) {
				w.append(sectionTabStartDecoration(i == 0, !itSections.hasNext(), true));
					w.append("<span id='").append(labelId).append("'>").append(sectionName).append("</span> ");
				w.append("<span id='").append(ctx.decorateId(sectionView.getViewObject() + "_collectionSize")).append("' class='ox-section-count'>").append(collectionCountLabel).append("</span> ");
				w.append(SECTION_TAB_END);
				w.append(" ");
			} else {
				w.append(sectionTabStartDecoration(i == 0, !itSections.hasNext(), false));
				String viewObjectArgv = "xava_view".equals(viewObject) ? "" : ",viewObject=" + viewObject;
				StringBuilder linkBody = new StringBuilder();
				linkBody.append("<span id='").append(labelId).append("'>").append(sectionName).append("</span> ");
				linkBody.append("<span id='").append(ctx.decorateId(sectionView.getViewObject() + "_collectionSize")).append("' class='ox-section-count'>").append(collectionCountLabel).append("</span> ");
				w.append(ActionHtml.link(ctx, "Sections.change", "activeSection=" + i + viewObjectArgv, "ox-section-link", false, linkBody.toString()));
				w.append(SECTION_TAB_END);
				w.append(" ");
			}
			i++;
		}

		w.append("</td>");
		w.append("</tr>");
		w.append("</table>");
		w.append("</div>");

		w.append("</td></tr>");

		w.append("<tr><td class='ox-active-section ").append(view.isFlowLayout() ? "ox-flow-layout" : "").append("'>");

		View sectionView = view.getSectionView(activeSection);
		String sectionViewObject = sectionView.getViewObject();
		if (sectionViewObject == null || sectionViewObject.isEmpty()) {
			sectionViewObject = viewObject + "_section" + activeSection;
			sectionView.setViewObject(sectionViewObject);
		}
		ctx.getModuleContext().put(ctx.getRequest(), sectionViewObject, sectionView);

		ViewRenderContext subCtx = ctx.withParameters(Map.of(
			"viewObject", sectionViewObject,
			"representsSection", "true"
		));
		String sectionPartUrl = "detail?viewObject=" + sectionViewObject + "&representsSection=true";
		if (Parts.isJavaRendered(sectionPartUrl, ctx.getRequest())) {
			w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), sectionPartUrl));
		} else {
			w.append(JspFragment.render(subCtx, "detail.jsp?viewObject=" + sectionViewObject + "&representsSection=true"));
		}

		w.append("</td></tr>");
		w.append("</table>");
		w.append("<br>");

		return w.toString();
	}

	private static final String SECTION_TAB_END = "</span></span>";

	private static String sectionTabStartDecoration(boolean first, boolean last, boolean active) {
		StringBuffer r = new StringBuffer();
		r.append("<span class='");
		if (active) r.append("ox-active ");
		if (first) r.append("ox-first ");
		if (last) r.append("ox-last");
		r.append("'>");
		r.append("<span class='ox-section-tab'>");
		return r.toString();
	}

}
