package org.openxava.web.render;

import org.openxava.view.*;

/**
 * Layout decoration around labels and editors.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class LayoutCells {

	public static String preLabel(View view, boolean first) {
		return "<div class='" + labelClass(view, first) + " ox-label'>";
	}

	public static String postLabel() {
		return "</div>";
	}

	public static String preEditor(View view, boolean first) {
		return "<div class='" + editorClass(view) + " ox-editor-wrapper'>";
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
