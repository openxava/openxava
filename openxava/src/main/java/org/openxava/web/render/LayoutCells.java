package org.openxava.web.render;

import org.openxava.view.*;
import org.openxava.web.style.*;

/**
 * Layout decoration around labels and editors (formerly htmlTagsEditor.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class LayoutCells {

	public static String preLabel(View view, Style style, boolean first) {
		return "<div class='" + labelClass(view, first) + " " + style.getLabel() + "'>";
	}

	public static String postLabel() {
		return "</div>";
	}

	public static String preEditor(View view, Style style, boolean first) {
		return "<div class='" + editorClass(view) + " " + style.getEditorWrapper() + "'>";
	}

	public static String postEditor() {
		return "</div>";
	}

	private static String labelClass(View view, boolean first) {
		if (view.isAlignedByColumns()) return "ox-layout-aligned-cell";
		return first ? "ox-layout-aligned-cell" : "ox-layout-not-aligned-cell";
	}

	private static String editorClass(View view) {
		if (view.isAlignedByColumns()) return "ox-layout-aligned-cell";
		return "ox-layout-not-aligned-cell";
	}

}
