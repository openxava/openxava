package org.openxava.web.render;

import java.util.*;

import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;

/**
 * Renders the detail view by iterating meta members and dispatching to
 * property, reference, collection, group and sections renderers.
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
							w.append(frameHeaderStartDecoration(frameWidth, false));
							w.append(FRAME_TITLE_START);
							w.append("<span id='").append(labelKey).append("'>").append(label).append("</span>");
							w.append(FRAME_TITLE_END);
							w.append(FRAME_ACTIONS_START);

							String frameId = Ids.decorate(ctx.getRequest(), "frame_" + view.getPropertyPrefix() + p.getName());
							w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));

							w.append(FRAME_ACTIONS_END);
							w.append(JspFragment.render(ctx, "propertyActionsExt.jsp"));
							w.append(FRAME_HEADER_END);
							w.append(frameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));
						} else if (hasFrame) {
							w.append(FrameLayout.closeDivForFrame(view));
							w.append("<div class='ox-layout-hide-frame-in-section'>");
						}

						// Render the property editor
						w.append(PropertyEditorRenderer.render(ctx, view, p, propertyKey, first, hasFrame));

						if (withFrame) {
							w.append(FRAME_CONTENT_END);
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
						String urlReferenceEditor = "reference"
							+ "?referenceKey=" + referenceKey
							+ "&viewObject=" + viewObject
							+ "&first=" + first
							+ "&frame=false&composite=false&onlyEditor=false";
						w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), urlReferenceEditor));
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
							w.append(frameHeaderStartDecoration(frameWidth, false));
							w.append(FRAME_TITLE_START);
							w.append("<span id='").append(labelKey).append("'>").append(label).append("</span>");

							if (!ref.isAggregate()) {
								ViewRenderContext refHeaderCtx = ctx.withParameters(Map.of(
									"referenceName", ref.getName(),
									"viewObject", viewObject
								));
								w.append(ReferenceFrameHeaderRenderer.render(refHeaderCtx));
							}

							w.append(FRAME_TITLE_END);
							w.append(FRAME_ACTIONS_START);

							String frameId = Ids.decorate(ctx.getRequest(), "frame_" + view.getPropertyPrefix() + ref.getName());
							w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));

							w.append(FRAME_ACTIONS_END);
							w.append(JspFragment.render(ctx, "referenceFrameHeaderExt.jsp"));
							w.append(FRAME_HEADER_END);
							w.append(frameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));
						}

						String urlReferenceEditor;
						if (view.displayReferenceWithNotCompositeEditor(ref)) {
							urlReferenceEditor = "reference"
								+ "?referenceKey=" + referenceKey
								+ "&viewObject=" + viewObject
								+ "&onlyEditor=true&frame=true&composite=false"
								+ "&first=" + first;
						} else {
							urlReferenceEditor = "reference"
								+ "?referenceKey=" + referenceKey
								+ "&viewObject=" + viewObject
								+ "&onlyEditor=true&frame=true&composite=true"
								+ "&refViewObject=" + viewName
								+ "&propertyPrefix=" + propertyInReferencePrefix
								+ "&first=" + firstForSubdetail;
						}
						w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), urlReferenceEditor));

						if (withFrame) {
							w.append(FRAME_CONTENT_END);
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
						w.append(frameHeaderStartDecoration(variousCollectionInLine ? 50 : frameWidth, true));
						w.append(FRAME_TITLE_START);
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

						w.append(FRAME_TITLE_END);
						w.append(FRAME_ACTIONS_START);
						w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));
						w.append(FRAME_ACTIONS_END);
						w.append(JspFragment.render(ctx, "collectionFrameHeaderExt.jsp"));
						w.append(FRAME_HEADER_END);
						w.append(frameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));
					}

					String collectionPrefix = propertyPrefix == null
						? collection.getName() + "."
						: propertyPrefix + collection.getName() + ".";

					w.append("<div id='").append(ctx.decorateId("collection_" + collectionPrefix)).append("'>");
					String collectionUrl = "collection"
						+ "?collectionName=" + collection.getName()
						+ "&viewObject=" + viewObject
						+ "&propertyPrefix=" + propertyPrefix;
					if (Parts.isJavaRendered(collectionUrl, ctx.getRequest())) {
						w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), collectionUrl));
					} else {
						w.append(JspFragment.render(ctx, collectionUrl));
					}
					w.append("</div>");

					if (withFrame) {
						w.append(FRAME_CONTENT_END);
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

					w.append(frameHeaderStartDecoration(frameWidth, false));
					w.append(FRAME_TITLE_START);

					String labelId = Ids.decorate(ctx.getRequest(), "label_" + view.getPropertyPrefix() + group.getName());
					String labelGroup = Is.emptyString(subview.getTitle()) ? group.getLabel(ctx.getRequest()) : subview.getTitle();
					w.append("<span id='").append(labelId).append("'>").append(labelGroup).append("</span>");

					w.append(FRAME_TITLE_END);
					w.append(FRAME_ACTIONS_START);

					String frameId = Ids.decorate(ctx.getRequest(), "frame_group_" + view.getPropertyPrefix() + group.getName());
					w.append(renderFrameActions(ctx, frameId, view.isFrameClosed(frameId)));

					w.append(FRAME_ACTIONS_END);
					w.append(FRAME_HEADER_END);
					w.append(frameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)));

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

					w.append(FRAME_CONTENT_END);
					w.append(FrameLayout.openDivForFrame(view));
				}
			}

			w.append(FrameLayout.closeDiv(view));
		}

		if (view.hasSections()) {
			w.append("<div id='").append(ctx.decorateId("sections_" + viewObject)).append("' class='ox-sections'>");
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

	private static final String FRAME_TITLE_START = "<span class='ox-frame-title-label'>\n";
	private static final String FRAME_TITLE_END = "</span>";
	private static final String FRAME_ACTIONS_START = "<span class='ox-frame-actions'>";
	private static final String FRAME_ACTIONS_END = "</span>";
	private static final String FRAME_HEADER_END = "</div>";
	private static final String FRAME_CONTENT_END = "\n</div></div></div>";

	private static String frameHeaderStartDecoration(int width, boolean collection) {
		StringBuffer r = new StringBuffer();
		r.append("<div  class='ox-frame");
		if (collection) r.append(" ox-collection");
		if (width == 100) r.append(" ox-full-frame");
		else if (width == 50) r.append(" ox-half-frame");
		r.append("'>");
		r.append("<div class='ox-frame-title'>");
		r.append("\n");
		return r.toString();
	}

	private static String frameContentStartDecoration(String id, boolean closed) {
		StringBuffer r = new StringBuffer();
		r.append("<div id='");
		r.append(id);
		r.append("' ");
		if (closed) r.append("class='ox-display-none'");
		r.append("><div class='ox-frame-content'>\n");
		return r.toString();
	}

}
