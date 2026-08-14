package org.openxava.web;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * Executes the module request lifecycle: bind view/tab, run actions, etc.
 * <p>
 * Replaces the former execute.jsp, which never rendered HTML.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ModuleExecutor {

	/**
	 * Executes using {@code loadingModulePage} from the request parameter when present.
	 */
	public static void execute(HttpServletRequest request) {
		execute(request, "true".equals(request.getParameter("loadingModulePage")));
	}

	/**
	 * @param loadingModulePage {@code true} when loading the full module page (e.g. from module.jsp)
	 */
	public static void execute(HttpServletRequest request, boolean loadingModulePage) {
		Messages errors = getOrCreateMessages(request, "errors");
		Messages messages = getOrCreateMessages(request, "messages");
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");

		ModuleManager manager = (ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
		manager.setSession(request.getSession());

		Tab tab = (Tab) context.get(request, "xava_tab");
		request.setAttribute("tab", tab);

		View view = (View) context.get(request, "xava_view");
		String[] deselected = request.getParameterValues("deselected");
		if (deselected != null) {
			for (int i = 0; i < deselected.length; i++) {
				String d = deselected[i];
				if (d.contains("xava_tab")) tab.friendExecuteJspDeselect(d);
				else if (d.contains("xava_collectionTab")) {
					view.deselectCollection(d);
				}
			}
		}

		if (!"false".equals(request.getAttribute("xava.sendParametersToTab"))) {
			tab.setSelected(request.getParameterValues("selected"));
		}

		manager.setApplicationName(request.getParameter("application"));
		manager.setModuleName(request.getParameter("module"));
		view.setRequest(request);
		view.setErrors(errors);
		view.setMessages(messages);
		if (!loadingModulePage) manager.executeBeforeEachRequestActions(request, errors, messages);

		@SuppressWarnings("unchecked")
		Stack<View> previousViews = (Stack<View>) context.get(request, "xava_previousViews");
		for (Iterator<View> it = previousViews.iterator(); it.hasNext(); ) {
			View previousView = it.next();
			previousView.setRequest(request);
			previousView.setErrors(errors);
			previousView.setMessages(messages);
		}

		tab.setRequest(request);
		tab.setErrors(errors);
		tab.setMessages(messages);
		if (manager.isListMode()) {
			tab.setModelName(manager.getModelName());
			if (tab.getTabName() == null) {
				tab.setTabName(manager.getTabName());
			}
		}
		boolean hasProcessRequest = manager.hasProcessRequest(request);
		manager.preInitModule(request);
		if (manager.isXavaView(request)) {
			if (hasProcessRequest) {
				view.assignValuesToWebView();
			}
		}
		if (!(loadingModulePage && manager.isCoreViaHotwire(request))) {
			manager.initModule(request, errors, messages);
			manager.executeOnEachRequestActions(request, errors, messages);
			if (hasProcessRequest) {
				manager.execute(request, errors, messages);
				if (manager.isListMode()) {
					tab.setModelName(manager.getModelName());
					if (tab.getTabName() == null) {
						tab.setTabName(manager.getTabName());
					}
				}
			}
			manager.executeAfterEachRequestActions(request, errors, messages);
		}

		if ("true".equals(request.getParameter("firstRequest")) && manager.isCoreViaHotwire(request)) {
			manager.executeBeforeLoadPage(request, errors, messages);
		}
		if (manager.isDetailMode()) {
			view = (View) context.get(request, "xava_view");
			view.setRequest(request);
		}
	}

	private static Messages getOrCreateMessages(HttpServletRequest request, String name) {
		Messages messages = (Messages) request.getAttribute(name);
		if (messages == null) {
			messages = new Messages();
			request.setAttribute(name, messages);
		}
		return messages;
	}

}
