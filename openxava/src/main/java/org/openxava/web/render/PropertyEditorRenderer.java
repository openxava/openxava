package org.openxava.web.render;

import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders a property editor with label, layout decoration and property actions.
 * <p>
 * The actual editor is still a JSP, included via {@link JspFragment} using
 * {@code editorWrapper.jsp} which contains the {@code <xava:editor>} tag.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class PropertyEditorRenderer {

	/**
	 * Renders a property editor for the given view and meta property.
	 *
	 * @param ctx         the render context
	 * @param view        the view that owns the property
	 * @param p           the meta property to render
	 * @param propertyKey the decorated property key
	 * @param first       whether this is the first member in the line
	 * @param hasFrame    whether the editor has a frame
	 */
	public static String render(ViewRenderContext ctx, View view, MetaProperty p,
			String propertyKey, boolean first, boolean hasFrame) throws Exception {

		Style style = ctx.getStyle();
		boolean editable = view.isEditable(p);
		boolean lastSearchKey = view.isLastSearchKey(p);
		boolean throwPropertyChanged = view.throwsPropertyChanged(p);
		int labelFormat = view.getLabelFormatForProperty(p);
		String labelStyle = view.getLabelStyleForProperty(p);
		if (Is.empty(labelStyle)) labelStyle = XavaPreferences.getInstance().getDefaultLabelStyle();
		String label = view.getLabelFor(p);
		if (first && !view.isAlignedByColumns()) label = Strings.change(label, " ", "&nbsp;");

		HtmlWriter w = new HtmlWriter();

		if (view.isFlowLayout()) w.append("<div>");

		// Layout decoration (htmlTagsEditor.jsp)
		String preLabel = LayoutCells.preLabel(view, style, first);
		String postLabel = LayoutCells.postLabel();
		String preEditor = LayoutCells.preEditor(view, style, first);
		String postEditor = LayoutCells.postEditor();

		if (!hasFrame) {
			w.append(preLabel);
			if (labelFormat == MetaPropertyView.NORMAL_LABEL) {
				w.append("<span id='").append(ctx.decorateId("label_" + view.getPropertyPrefix() + p.getName()))
					.append("' class='").append(labelStyle).append("'>");
				w.append(label);
				w.append("</span>");
			}
			w.append(postLabel);
			w.append(preEditor);
			if (labelFormat == MetaPropertyView.SMALL_LABEL) {
				w.append("<span id='").append(ctx.decorateId("label_" + view.getPropertyPrefix() + p.getName()))
					.append("' class='").append(style.getSmallLabel()).append(" ").append(labelStyle).append("'>");
				w.append(label);
				w.append("</span><br/>");
			}
		}

		// Editor span
		String placeholder = !Is.empty(p.getPlaceholder()) ? "data-placeholder='" + p.getPlaceholder() + "'" : "";
		String required = view.isEditable() && p.isRequired() ? style.getRequiredEditor() : "";
		String transientClass = p.isTransient() ? "xava_transient" : "";

		w.append("<span id='").append(ctx.decorateId("editor_" + view.getPropertyPrefix() + p.getName()))
			.append("' class='xava_editor ").append(required).append(" ").append(transientClass)
			.append("' ").append(placeholder).append(">");

		// Delegate the actual editor to editorWrapper.jsp (uses <xava:editor> tag)
		String editorWrapperUrl = "editorWrapper.jsp"
			+ "?propertyName=" + p.getName()
			+ "&editable=" + editable
			+ "&throwPropertyChanged=" + throwPropertyChanged
			+ "&viewObject=" + view.getViewObject()
			+ "&propertyPrefix=" + view.getPropertyPrefix();
		w.append(JspFragment.render(ctx, editorWrapperUrl));

		w.append("</span>");

		// Property actions
		if (!(lastSearchKey && view.displayWithFrame())) {
			w.append("<span id='").append(ctx.decorateId("property_actions_" + view.getPropertyPrefix() + p.getName())).append("'>");
			if (view.propertyHasActions(p)) {
				ViewRenderContext subCtx = ctx.withParameters(java.util.Map.of(
					"propertyKey", propertyKey,
					"propertyName", p.getName(),
					"lastSearchKey", String.valueOf(lastSearchKey),
					"editable", String.valueOf(editable),
					"viewObject", view.getViewObject()
				));
				w.append(PropertyActionsRenderer.render(subCtx));
			}
			w.append("</span>");
		}

		if (!hasFrame) {
			// propertyActionsExt.jsp (empty hook, kept as JSP include for user extensions)
			w.append(JspFragment.render(ctx, "propertyActionsExt.jsp"));
			w.append(postEditor);
		}

		if (view.isFlowLayout()) w.append("</div>");

		return w.toString();
	}

}
