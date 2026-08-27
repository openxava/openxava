package org.openxava.web.render;

import java.util.*;

import org.openxava.view.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

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
		Style style = ctx.getStyle();
		List<MetaView> sections = view.getSections();
		int activeSection = view.getActiveSection();
		if (sections == null || sections.isEmpty()) return "";
		if (activeSection < 0 || activeSection >= sections.size()) activeSection = 0;

		HtmlWriter w = new HtmlWriter();

		w.append("<table width='100%' cellspacing='0' border='0' cellpadding='0'>");
		w.append("<tr><td>");

		w.append("<div class='").append(style.getSection()).append("'>");
		w.append("<table ").append(style.getSectionTableAttributes()).append(">");
		w.append("<tr>");
		w.append(style.getSectionBarStartDecoration());

		Iterator<MetaView> itSections = sections.iterator();
		int i = 0;
		while (itSections.hasNext()) {
			MetaView section = itSections.next();
			View sectionView = view.getSectionView(i);
			String sectionName = sectionView.getTitle().equals("") ? section.getLabel(ctx.getRequest()) : sectionView.getTitle();
			String collectionCountLabel = sectionView.getLabelDecoration();
			String labelId = Ids.decorate(ctx.getRequest(), "label_" + sectionView.getViewObject() + "_sectionName");

			if (activeSection == i) {
				w.append(style.getActiveSectionTabStartDecoration(i == 0, !itSections.hasNext()));
					w.append("<span id='").append(labelId).append("'>").append(sectionName).append("</span> ");
				w.append("<span id='").append(ctx.decorateId(sectionView.getViewObject() + "_collectionSize")).append("'>").append(collectionCountLabel).append("</span> ");
				w.append(style.getActiveSectionTabEndDecoration());
				w.append(" ");
			} else {
				w.append(style.getSectionTabStartDecoration(i == 0, !itSections.hasNext()));
				String viewObjectArgv = "xava_view".equals(viewObject) ? "" : ",viewObject=" + viewObject;
				StringBuilder linkBody = new StringBuilder();
				linkBody.append("<span id='").append(labelId).append("'>").append(sectionName).append("</span> ");
				linkBody.append("<span id='").append(ctx.decorateId(sectionView.getViewObject() + "_collectionSize")).append("'>").append(collectionCountLabel).append("</span> ");
				w.append(ActionHtml.link(ctx, "Sections.change", "activeSection=" + i + viewObjectArgv, "ox-section-link", false, linkBody.toString()));
				w.append(style.getSectionTabEndDecoration());
				w.append(" ");
			}
			i++;
		}

		w.append(style.getSectionBarEndDecoration());
		w.append("</tr>");
		w.append("</table>");
		w.append("</div>");

		w.append("</td></tr>");

		w.append("<tr><td class='").append(style.getActiveSection()).append(" ").append(view.isFlowLayout() ? "ox-flow-layout" : "").append("'>");

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

}
