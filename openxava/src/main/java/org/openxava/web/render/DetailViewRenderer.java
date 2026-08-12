package org.openxava.web.render;

import java.util.*;

import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders the detail view by iterating meta members and dispatching to
 * property, reference, collection, group and sections renderers
 * (formerly detail.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class DetailViewRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		ModuleContext context = ctx.getModuleContext();
		String viewObject = ctx.getViewObject();
		View view = ctx.getView(viewObject);
		view.setViewObject(viewObject);

		String propertyPrefix = ctx.getParameter("propertyPrefix");
		if (propertyPrefix == null || "null".equals(propertyPrefix)) propertyPrefix = "";
		view.setPropertyPrefix(propertyPrefix);

		boolean onlySections = view.hasSections() && view.getMetaMembers().isEmpty();

		Style style = ctx.getStyle();
		HtmlWriter w = new HtmlWriter();

		if (!onlySections) {
			w.append(FrameLayout.openDiv(view));

			Iterator<MetaMember> it = view.getMetaMembers().iterator();
			String sfirst = ctx.getParameter("first");
			boolean first = !"false".equals(sfirst);

			while (it.hasNext()) {
				MetaMember m = it.next();
				int frameWidth = view.isVariousMembersInSameLine(m) ? 50 : 100;

				if (m instanceof MetaProperty) {
					MetaProperty p = (MetaProperty) m;
					if (!PropertiesSeparator.INSTANCE.equals(m)) {
						boolean hasFrame = WebEditors.hasFrame(p, view.getViewName());
						String propertyKey = Ids.decorate(
							ctx.getParameter("application"),
							ctx.getParameter("module"),
							propertyPrefix + p.getName());
						ctx.getRequest().setAttribute(propertyKey, p);

						boolean withFrame = hasFrame &&
							(!view.isSection() || view.getMetaMembers().size() > 1);

						if (withFrame) {
							String labelKey = Ids.decorate(
								ctx.getParameter("application"),
								ctx.getParameter("module"),
								"label_" + propertyPrefix + p.getName());
							String label = view.getLabelFor(p);

							w.append(FrameLayout.closeDivForFrame(view));
							w.append(style.getFrameHeaderStartDecoration(frameWidth));
							w.append(style.getFrameTitleStartDecoration());
							w.append("<span id='").append(labelKey).append("'>").append(label).append("</span>");
							w.append(style.getFrameTitleEndDecoration());
							w.append(style.getFrameActionsStartDecoration());

							String frameId = Ids.decorate(ctx.getRequest(), "frame_" + view.getPropertyPrefix() + p.getName());
							w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));

							w.append(style.getFrameActionsEndDecoration());
							w.append(JspFragment.render(ctx, "propertyActionsExt.jsp"));
							w.append(style.getFrameHeaderEndDecoration());
							w.append(style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));
						} else if (hasFrame) {
							w.append(FrameLayout.closeDivForFrame(view));
							w.append("<div class='ox-layout-hide-frame-in-section'>");
						}

						// Render the property editor
						w.append(PropertyEditorRenderer.render(ctx, view, p, propertyKey, first, hasFrame));

						if (withFrame) {
							w.append(style.getFrameContentEndDecoration());
							w.append(FrameLayout.openDivForFrame(view));
						} else if (hasFrame) {
							w.append("</div>");
							w.append(FrameLayout.openDivForFrame(view));
						}

						first = false;
					} else {
						// Properties separator
						if (!it.hasNext()) break;
						first = true;
						w.append("<div class='ox-layout-new-line'></div>");
					}
				}
				else if (m instanceof MetaReference) {
					MetaReference ref = (MetaReference) m;
					String referenceKey = Ids.decorate(
						ctx.getParameter("application"),
						ctx.getParameter("module"),
						propertyPrefix + ref.getName());
					ctx.getRequest().setAttribute(referenceKey, ref);

					if (view.displayReferenceWithNoFrameEditor(ref)) {
						// Reference without frame
						String urlReferenceEditor = "reference.jsp"
							+ "?referenceKey=" + referenceKey
							+ "&viewObject=" + viewObject
							+ "&first=" + first
							+ "&frame=false&composite=false&onlyEditor=false";
						w.append(JspFragment.render(ctx, urlReferenceEditor));
						first = false;
					} else {
						// Reference with frame
						String viewName = viewObject + "_" + ref.getName();
						View subview = view.getSubview(ref.getName());
						context.put(ctx.getRequest(), viewName, subview);
						subview.setViewObject(viewName);
						String propertyInReferencePrefix = propertyPrefix + ref.getName() + ".";
						boolean withFrame = subview.displayWithFrame();
						boolean firstForSubdetail = first || withFrame;

						if (withFrame) {
							String labelKey = Ids.decorate(
								ctx.getParameter("application"),
								ctx.getParameter("module"),
								"label_" + propertyPrefix + ref.getName());
							String label = view.getLabelFor(ref);

							w.append(FrameLayout.closeDivForFrame(view));
							w.append(style.getFrameHeaderStartDecoration(frameWidth));
							w.append(style.getFrameTitleStartDecoration());
							w.append("<span id='").append(labelKey).append("'>").append(label).append("</span>");

							if (!ref.isAggregate()) {
								ViewRenderContext refHeaderCtx = ctx.withParameters(Map.of(
									"referenceName", ref.getName(),
									"viewObject", viewObject
								));
								w.append(ReferenceFrameHeaderRenderer.render(refHeaderCtx));
							}

							w.append(style.getFrameTitleEndDecoration());
							w.append(style.getFrameActionsStartDecoration());

							String frameId = Ids.decorate(ctx.getRequest(), "frame_" + view.getPropertyPrefix() + ref.getName());
							w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));

							w.append(style.getFrameActionsEndDecoration());
							w.append(JspFragment.render(ctx, "referenceFrameHeaderExt.jsp"));
							w.append(style.getFrameHeaderEndDecoration());
							w.append(style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));
						}

						String urlReferenceEditor;
						if (view.displayReferenceWithNotCompositeEditor(ref)) {
							urlReferenceEditor = "reference.jsp"
								+ "?referenceKey=" + referenceKey
								+ "&viewObject=" + viewObject
								+ "&onlyEditor=true&frame=true&composite=false"
								+ "&first=" + first;
						} else {
							urlReferenceEditor = "reference.jsp"
								+ "?referenceKey=" + referenceKey
								+ "&viewObject=" + viewObject
								+ "&onlyEditor=true&frame=true&composite=true"
								+ "&refViewObject=" + viewName
								+ "&propertyPrefix=" + propertyInReferencePrefix
								+ "&first=" + firstForSubdetail;
						}
						w.append(JspFragment.render(ctx, urlReferenceEditor));

						if (withFrame) {
							w.append(style.getFrameContentEndDecoration());
							w.append(FrameLayout.openDivForFrame(view));
						}
					}
					first = false;
				}
				else if (m instanceof MetaCollection) {
					MetaCollection collection = (MetaCollection) m;
					boolean withFrame = !view.isSection() || view.getMetaMembers().size() > 1;
					boolean variousCollectionInLine = view.isVariousCollectionsInSameLine(m);

					w.append(FrameLayout.closeDivForFrame(view));

					if (withFrame) {
						w.append(style.getCollectionFrameHeaderStartDecoration(variousCollectionInLine ? 50 : frameWidth));
						w.append(style.getFrameTitleStartDecoration());
						w.append(collection.getLabel(ctx.getRequest()));

						String frameId = Ids.decorate(ctx.getRequest(), "frame_" + view.getPropertyPrefix() + collection.getName());
						String collectionHeaderId = frameId + "header";

						w.append("<span id='").append(ctx.decorateId(collectionHeaderId)).append("'>");
						ViewRenderContext colHeaderCtx = ctx.withParameters(Map.of(
							"collectionName", collection.getName(),
							"viewObject", viewObject
						));
						w.append(CollectionFrameHeaderRenderer.render(colHeaderCtx));
						w.append("</span>");

						w.append(style.getFrameTitleEndDecoration());
						w.append(style.getFrameActionsStartDecoration());
						w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));
						w.append(style.getFrameActionsEndDecoration());
						w.append(JspFragment.render(ctx, "collectionFrameHeaderExt.jsp"));
						w.append(style.getFrameHeaderEndDecoration());
						w.append(style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));
					}

					String collectionPrefix = propertyPrefix == null
						? collection.getName() + "."
						: propertyPrefix + collection.getName() + ".";

					w.append("<div id='").append(ctx.decorateId("collection_" + collectionPrefix)).append("'>");
					String collectionUrl = "collection.jsp"
						+ "?collectionName=" + collection.getName()
						+ "&viewObject=" + viewObject;
					if (Parts.isJavaRendered(collectionUrl)) {
						w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), collectionUrl));
					} else {
						w.append(JspFragment.render(ctx, collectionUrl));
					}
					w.append("</div>");

					if (withFrame) {
						w.append(style.getFrameContentEndDecoration());
					}

					w.append(FrameLayout.openDivForFrame(view));
				}
				else if (m instanceof MetaGroup) {
					MetaGroup group = (MetaGroup) m;
					String viewName = viewObject + "_" + group.getName();
					View subview = view.getGroupView(group.getName());
					context.put(ctx.getRequest(), viewName, subview);
					if (view.isFlowLayout() && view.isVariousMembersInSameLine(group)) frameWidth = 50;

					w.append(FrameLayout.closeDivForFrame(view));

					if (view.isFlowLayout() && view.isVariousMembersInSameLine(group) && view.isFirstInLine(group)) {
						w.append("<div class='ox-flow-layout-new-line'/>");
					}

					w.append(style.getFrameHeaderStartDecoration(frameWidth));
					w.append(style.getFrameTitleStartDecoration());

					String labelId = Ids.decorate(ctx.getRequest(), "label_" + view.getPropertyPrefix() + group.getName());
					String labelGroup = Is.emptyString(subview.getTitle()) ? group.getLabel(ctx.getRequest()) : subview.getTitle();
					w.append("<span id='").append(labelId).append("'>").append(labelGroup).append("</span>");

					w.append(style.getFrameTitleEndDecoration());
					w.append(style.getFrameActionsStartDecoration());

					String frameId = Ids.decorate(ctx.getRequest(), "frame_group_" + view.getPropertyPrefix() + group.getName());
					w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));

					w.append(style.getFrameActionsEndDecoration());
					w.append(style.getFrameHeaderEndDecoration());
					w.append(style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));

					if (view.isFlowLayout()) {
						w.append("<div class='ox-flow-layout'>");
					}

					ViewRenderContext groupCtx = ctx.withParameters(Map.of(
						"viewObject", viewName
					));
					w.append(DetailViewRenderer.render(groupCtx));

					if (view.isFlowLayout()) {
						w.append("</div>");
					}

					w.append(style.getFrameContentEndDecoration());
					w.append(FrameLayout.openDivForFrame(view));
				}
			}

			w.append(FrameLayout.closeDiv(view));
		}

		if (view.hasSections()) {
			w.append("<div id='").append(ctx.decorateId("sections_" + viewObject)).append("' class='").append(style.getSections()).append("'>");
			w.append(SectionsRenderer.render(ctx));
			w.append("</div>");
		}

		return w.toString();
	}

	private static String renderFrameActions(ViewRenderContext ctx, String frameId, boolean closed) {
		ViewRenderContext faCtx = ctx.withParameters(Map.of(
			"frameId", frameId,
			"closed", String.valueOf(closed)
		));
		return FrameActionsRenderer.render(faCtx);
	}

}
