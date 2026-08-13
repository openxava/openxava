package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;
import org.openxava.web.meta.*;
import org.openxava.web.style.*;

/**
 * Renders a reference field (formerly reference.jsp).
 * <p>
 * Handles descriptions lists, composite and non-composite references, label/layout
 * decoration, and reference actions. The actual editor is still a JSP, included
 * via {@link JspFragment}.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ReferenceRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		HttpServletRequest request = ctx.getRequest();
		ModuleContext context = ctx.getModuleContext();
		Style style = ctx.getStyle();

		boolean onlyEditor = ctx.getBooleanParameter("onlyEditor", false);
		boolean frame = ctx.getBooleanParameter("frame", false);
		boolean composite = ctx.getBooleanParameter("composite", false);
		String viewObject = ctx.getParameter("viewObject", "xava_view");
		View view = ctx.getView(viewObject);
		String referenceKey = ctx.getParameter("referenceKey");
		MetaReference ref = (MetaReference) request.getAttribute(referenceKey);
		String refViewObject = ctx.getParameter("refViewObject");
		if (Is.emptyString(refViewObject)) refViewObject = viewObject;

		boolean descriptionsList = ctx.getBooleanParameter("descriptionsList", false);
		if (!descriptionsList) descriptionsList = view.displayAsDescriptionsList(ref);
		boolean descriptionsListAndReferenceView = descriptionsList || !composite ? false : view.displayAsDescriptionsListAndReferenceView(ref);
		if (descriptionsListAndReferenceView) {
			composite = false;
		}
		boolean inEditableList = ref.getName().contains("___");

		String editableKey = referenceKey + "_EDITABLE_";
		boolean editable = view.isEditable(ref.getName());
		int labelFormat = view.getLabelFormatForReference(ref);
		String labelStyle = view.getLabelStyleForReference(ref);
		if (Is.empty(labelStyle)) labelStyle = XavaPreferences.getInstance().getDefaultLabelStyle();
		String label = view.getLabelFor(ref);

		HtmlWriter w = new HtmlWriter();

		if (view.isFlowLayout()) {
			w.append("<div class='").append(frame ? "ox-flow-layout" : "").append("'>");
		}

		// Layout decoration (htmlTagsEditor.jsp)
		boolean first = !"false".equals(ctx.getParameter("first"));
		String preLabel = LayoutCells.preLabel(view, style, first);
		String postLabel = LayoutCells.postLabel();
		String preEditor = LayoutCells.preEditor(view, style, first);
		String postEditor = LayoutCells.postEditor();

		if (!onlyEditor) {
			w.append(preLabel);
			if (labelFormat == MetaPropertyView.NORMAL_LABEL) {
				w.append("<span id='").append(ctx.decorateId("label_" + view.getPropertyPrefix() + ref.getName()))
					.append("' class='").append(labelStyle).append("'>");
				w.text(label);
				w.append("</span>");
			}
			w.append(postLabel);
			w.append(preEditor);
			if (labelFormat == MetaPropertyView.SMALL_LABEL) {
				w.append("<span id='").append(ctx.decorateId("label_" + view.getPropertyPrefix() + ref.getName()))
					.append("' class='").append(style.getSmallLabel()).append(" ").append(labelStyle).append("'>");
				w.text(label);
				w.append("</span><br/>");
			}
		}

		// Resolve key properties
		Collection<String> keys = ref.getMetaModelReferenced().getAllKeyPropertiesNames();
		String keyProperty = "";
		String keyProperties = "";
		String propertyKey = null;
		if (keys.size() == 1) {
			keyProperty = keys.iterator().next();
			propertyKey = Ids.decorate(request, referenceKey + "." + keyProperty);
			if (!composite) {
				Map values = (Map) view.getValue(ref.getName());
				values = values == null ? java.util.Collections.EMPTY_MAP : values;
				Object value = values.get(keyProperty);
				String valueKey = propertyKey + ".value";
				request.setAttribute(valueKey, value);
				String fvalue = value == null ? "" : value.toString();
				request.setAttribute(propertyKey + ".fvalue", fvalue);
			}
		} else {
			propertyKey = referenceKey + DescriptionsLists.COMPOSITE_KEY_SUFFIX;
			Map values = null;
			if (!composite) {
				values = (Map) view.getValue(ref.getName());
				values = values == null ? java.util.Collections.EMPTY_MAP : values;
			}
			Iterator<String> it = keys.iterator();
			StringBuffer sb = new StringBuffer();
			while (it.hasNext()) {
				String property = it.next();
				if (!composite) {
					Object value = values.get(property);
					String valueKey = Ids.decorate(request, referenceKey + "." + property) + ".value";
					request.setAttribute(valueKey, value);
				}
				sb.append(property);
				if (it.hasNext()) sb.append(',');
			}
			if (!composite) {
				String key = ref.getMetaModelReferenced().toString(values);
				request.setAttribute(propertyKey + ".fvalue", key);
			}
			keyProperties = sb.toString();
		}

		// Reference editor span
		if (!composite) {
			String wrapperClass = view.isEditable() && ref.isRequired() ? style.getRequiredEditor() : "";
			wrapperClass = view.throwsReferenceChanged(ref) ? wrapperClass + " xava_onchange" : wrapperClass;
			wrapperClass = Is.emptyString(wrapperClass) ? "" : "class='" + wrapperClass + "'";
			w.append("<span id='").append(ctx.decorateId("reference_editor_" + view.getPropertyPrefix() + ref.getName()))
				.append("' ").append(wrapperClass).append(" data-property='").append(propertyKey).append("'>");
		}
		boolean notCompositeEditorClosed = false;
		w.append("<input type='hidden' name='").append(editableKey).append("' value='").append(editable).append("'/>");

		if (descriptionsList || descriptionsListAndReferenceView) {
			String descriptionProperty = view.getDescriptionPropertyInDescriptionsList(ref);
			String descriptionProperties = view.getDescriptionPropertiesInDescriptionsList(ref);
			String parameterValuesProperties = view.getParameterValuesPropertiesInDescriptionsList(ref);
			String condition = view.getConditionInDescriptionsList(ref);
			boolean orderByKey = view.isOrderByKeyInDescriptionsList(ref);
			String order = view.getOrderInDescriptionsList(ref);
			org.openxava.tab.meta.MetaTab metaTab = ref.getMetaModelReferenced().getMetaComponent().getMetaTab();
			String filter = view.getFilterInDescriptionsList(ref);
			if (Is.emptyString(filter) && metaTab.hasFilter()) {
				filter = metaTab.getMetaFilter().getClassName();
			}
			if (metaTab.hasBaseCondition()) {
				if (Is.emptyString(condition)) {
					condition = metaTab.getBaseCondition();
				} else {
					condition = metaTab.getBaseCondition() + " AND " + condition;
				}
			}
			condition = WebEditors.refineURLParam(condition);
			MetaEditor descriptionsListEditor = WebEditors.getMetaEditorForDescriptionsList(ref, view.getViewName());
			String editorURL = "editors/" + descriptionsListEditor.getUrl();
			String paramSeparator = editorURL.contains("?") ? "&" : "?";
			editorURL += paramSeparator
				+ "propertyKey=" + (propertyKey == null ? "" : propertyKey)
				+ "&editable=" + editable
				+ "&model=" + (ref.getReferencedModelName() == null ? "" : ref.getReferencedModelName())
				+ "&keyProperty=" + (keyProperty == null ? "" : keyProperty)
				+ "&keyProperties=" + (keyProperties == null ? "" : keyProperties)
				+ "&descriptionProperty=" + (descriptionProperty == null ? "" : descriptionProperty)
				+ "&descriptionProperties=" + (descriptionProperties == null ? "" : descriptionProperties)
				+ "&parameterValuesProperties=" + (parameterValuesProperties == null ? "" : parameterValuesProperties)
				+ "&condition=" + (condition == null ? "" : condition)
				+ "&orderByKey=" + orderByKey
				+ "&order=" + (order == null ? "" : order)
				+ "&filter=" + (filter == null ? "" : filter);
			w.append(JspFragment.render(ctx, editorURL));

			if (descriptionsListAndReferenceView) {
				w.append(ReferenceActionsRenderer.render(ctx, view, ref, propertyKey));
				notCompositeEditorClosed = true;
				w.append("</span>");

				String referenceEditorURL = "editors/" + MetaWebEditors.getMetaEditorFor(ref, view.getViewName()).getUrl()
					+ "?propertyKey=" + propertyKey
					+ "&viewObject=" + refViewObject
					+ "&editable=false";
				w.append(JspFragment.render(ctx, referenceEditorURL));
			}
		} else {
			String editorURL = "editors/" + WebEditors.getMetaEditorFor(ref, view.getViewName()).getUrl()
				+ "?propertyKey=" + propertyKey
				+ "&viewObject=" + refViewObject
				+ "&editable=" + editable;
			w.append(JspFragment.render(ctx, editorURL));
		}

		if (!frame && !inEditableList) {
			w.append(ReferenceActionsRenderer.render(ctx, view, ref, propertyKey));
			w.append(JspFragment.render(ctx, "referenceActionsExt.jsp"));
		}

		if (!composite && !notCompositeEditorClosed) {
			w.append("</span>");
		}

		if (!onlyEditor) {
			w.append(postEditor);
		}

		if (view.isFlowLayout()) {
			w.append("</div>");
		}

		return w.toString();
	}

}
