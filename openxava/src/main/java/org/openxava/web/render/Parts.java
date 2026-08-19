package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.web.servlets.*;

/**
 * Registry mapping Hotwire part descriptors (e.g. {@code errors}) to Java renderers.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class Parts {

	private static final Map<String, PartRenderer> REGISTRY = new LinkedHashMap<>();
	private static final Map<String, PartRenderer> PHONE_REGISTRY = new LinkedHashMap<>();

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
	}

	private static void register(String name, PartRenderer renderer) {
		REGISTRY.put(name, renderer);
	}

	/**
	 * Registers a phone-specific renderer for a part.
	 * When the request has the {@code xava.phone} attribute set, this renderer
	 * is used instead of the desktop one.
	 * @since 8.0
	 */
	public static void registerPhone(String name, PartRenderer renderer) {
		PHONE_REGISTRY.put(name, renderer);
	}

	private static boolean isPhoneRequest(HttpServletRequest request) {
		return Boolean.TRUE.equals(request.getAttribute("xava.phone"));
	}

	/**
	 * Checks if a part descriptor is handled by a Java renderer.
	 * @param partDescriptor e.g. {@code errors} or {@code core?buttonBar=false}
	 */
	public static boolean isJavaRendered(String partDescriptor) {
		return isJavaRendered(partDescriptor, null);
	}

	/**
	 * Checks if a part descriptor is handled by a Java renderer.
	 * @param partDescriptor e.g. {@code errors} or {@code core?buttonBar=false}
	 * @param request if non-null and has {@code xava.phone} attribute, also checks the phone registry
	 * @since 8.0
	 */
	public static boolean isJavaRendered(String partDescriptor, HttpServletRequest request) {
		if (partDescriptor == null) return false;
		String name = ViewRenderContext.partName(partDescriptor);
		if (REGISTRY.containsKey(name)) return true;
		if (request != null && isPhoneRequest(request) && PHONE_REGISTRY.containsKey(name)) return true;
		return false;
	}

	/**
	 * Renders a part descriptor to HTML using the registered Java renderer.
	 */
	public static String render(HttpServletRequest request, HttpServletResponse response, String partDescriptor) throws Exception {
		String name = ViewRenderContext.partName(partDescriptor);
		PartRenderer renderer = null;
		if (isPhoneRequest(request) && PHONE_REGISTRY.containsKey(name)) {
			renderer = PHONE_REGISTRY.get(name);
		} else {
			renderer = REGISTRY.get(name);
		}
		if (renderer == null) return null;
		ViewRenderContext ctx = ViewRenderContext.forPart(request, response, partDescriptor);
		return renderer.render(ctx);
	}

}
