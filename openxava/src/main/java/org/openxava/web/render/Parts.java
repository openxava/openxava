package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.web.servlets.*;

/**
 * Registry mapping Hotwire part descriptors (e.g. {@code errors.jsp}) to Java renderers.
 * <p>
 * Replaces the former pattern of mapping DOM ids to JSP URLs in {@code HotwireServlet}.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class Parts {

	private static final Map<String, PartRenderer> REGISTRY = new LinkedHashMap<>();
	private static final Map<String, String> JSP_ALIASES = new HashMap<>();

	static {
		register("errors", ErrorsRenderer::render);
		register("messages", MessagesRenderer::render);
		register("frameActions", FrameActionsRenderer::render);
		register("listConfigurations", ListConfigurationsRenderer::render);
		register("themeChooser", ThemeChooserRenderer::render);
		register("barButton", ButtonRenderer::render);
		register("subButton", SubButtonRenderer::render);
		register("buttonBar", ButtonBarRenderer::render);
		register("bottomButtons", BottomButtonsRenderer::render);
		register("core", CoreRenderer::render);
		register("detail", DetailViewRenderer::render);
		register("sections", SectionsRenderer::render);
		register("propertyActions", PropertyActionsRenderer::render);
		register("collectionFrameHeader", CollectionFrameHeaderRenderer::render);
		register("list", ListRenderer::render);
		register("collection", CollectionRenderer::render);
		register("collectionFromModel", CollectionFromModelRenderer::render);
		register("referenceSearch", ReferenceSearchRenderer::render);
		register("addToCollection", AddToCollectionRenderer::render);
		register("reference", ReferenceRenderer::render);

		// JSP file name ? part name, for HotwireServlet compatibility
		JSP_ALIASES.put("errors.jsp", "errors");
		JSP_ALIASES.put("messages.jsp", "messages");
		JSP_ALIASES.put("frameActions.jsp", "frameActions");
		JSP_ALIASES.put("listConfigurations.jsp", "listConfigurations");
		JSP_ALIASES.put("themeChooser.jsp", "themeChooser");
		JSP_ALIASES.put("barButton.jsp", "barButton");
		JSP_ALIASES.put("subButton.jsp", "subButton");
		JSP_ALIASES.put("buttonBar.jsp", "buttonBar");
		JSP_ALIASES.put("bottomButtons.jsp", "bottomButtons");
		JSP_ALIASES.put("core.jsp", "core");
		JSP_ALIASES.put("detail.jsp", "detail");
		JSP_ALIASES.put("sections.jsp", "sections");
		JSP_ALIASES.put("propertyActions.jsp", "propertyActions");
		JSP_ALIASES.put("collectionFrameHeader.jsp", "collectionFrameHeader");
		JSP_ALIASES.put("list.jsp", "list");
		JSP_ALIASES.put("collection.jsp", "collection");
		JSP_ALIASES.put("collectionFromModel.jsp", "collectionFromModel"); // JSP deleted, alias kept for descriptor matching
		JSP_ALIASES.put("referenceSearch.jsp", "referenceSearch"); // JSP deleted, alias kept for descriptor matching
		JSP_ALIASES.put("addToCollection.jsp", "addToCollection"); // JSP deleted, alias kept for descriptor matching
		JSP_ALIASES.put("reference.jsp", "reference");
	}

	private static void register(String name, PartRenderer renderer) {
		REGISTRY.put(name, renderer);
	}

	/**
	 * Checks if a part descriptor is handled by a Java renderer.
	 * @param partDescriptor e.g. {@code errors.jsp} or {@code core.jsp?buttonBar=false}
	 */
	public static boolean isJavaRendered(String partDescriptor) {
		if (partDescriptor == null) return false;
		String name = ViewRenderContext.partName(partDescriptor);
		return REGISTRY.containsKey(name);
	}

	/**
	 * Renders a part descriptor to HTML using the registered Java renderer.
	 */
	public static String render(HttpServletRequest request, HttpServletResponse response, String partDescriptor) throws Exception {
		String name = ViewRenderContext.partName(partDescriptor);
		PartRenderer renderer = REGISTRY.get(name);
		if (renderer == null) return null;
		ViewRenderContext ctx = ViewRenderContext.forPart(request, response, partDescriptor);
		return renderer.render(ctx);
	}

	/**
	 * Returns the JSP alias for a part name, or null if not aliased.
	 */
	public static String jspAlias(String partName) {
		return JSP_ALIASES.get(partName);
	}

}
