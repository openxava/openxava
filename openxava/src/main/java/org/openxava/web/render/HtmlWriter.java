package org.openxava.web.render;

/**
 * Appends HTML for the Java UI renderers.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class HtmlWriter {

	private final StringBuilder out = new StringBuilder(512);

	public HtmlWriter append(String html) {
		if (html != null) out.append(html);
		return this;
	}

	public HtmlWriter append(char c) {
		out.append(c);
		return this;
	}

	public HtmlWriter append(int n) {
		out.append(n);
		return this;
	}

	public HtmlWriter append(boolean b) {
		out.append(b);
		return this;
	}

	/** Appends text with HTML special characters escaped. */
	public HtmlWriter text(String text) {
		if (text == null) return this;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '&': out.append("&amp;"); break;
			case '<': out.append("&lt;"); break;
			case '>': out.append("&gt;"); break;
			case '"': out.append("&quot;"); break;
			case '\'': out.append("&#39;"); break;
			default: out.append(c);
			}
		}
		return this;
	}

	/** Escapes apostrophes the same way as the action taglibs. */
	public static String filterApostrophes(String source) {
		if (source == null) return "";
		return source.replace("'", "&#145;");
	}

	public boolean isEmpty() {
		return out.length() == 0;
	}

	@Override
	public String toString() {
		return out.toString();
	}

}
