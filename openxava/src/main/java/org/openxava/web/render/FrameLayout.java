package org.openxava.web.render;

import org.openxava.view.*;

/**
 * Open/close layout divs used around framed members in the detail view.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class FrameLayout {

	public static String openDiv(View view) {
		if (view.isFlowLayout()) return "";
		return view.isFrame() ? "<div class='ox-layout-detail'>" : "";
	}

	public static String closeDiv(View view) {
		if (view.isFlowLayout()) return "";
		return view.isFrame() ? "</div>" : "";
	}

	public static String openDivForFrame(View view) {
		if (view.isFrame()) return openDiv(view);
		return "<div>" + openDiv(view);
	}

	public static String closeDivForFrame(View view) {
		if (view.isFrame()) return closeDiv(view);
		return closeDiv(view) + "</div>";
	}

}
