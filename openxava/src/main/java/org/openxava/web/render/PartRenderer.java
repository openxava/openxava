package org.openxava.web.render;

/**
 * Renders a UI part to HTML.
 *
 * @author Javier Paniza
 * @since 8.0
 */
@FunctionalInterface
public interface PartRenderer {

	String render(ViewRenderContext ctx) throws Exception;

}
