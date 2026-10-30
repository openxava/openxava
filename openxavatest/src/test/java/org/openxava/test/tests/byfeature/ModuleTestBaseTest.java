package org.openxava.test.tests.byfeature;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import org.htmlunit.*;
import org.htmlunit.html.*;
import org.htmlunit.http.*;
import org.htmlunit.util.*;
import org.openxava.tests.*;

import junit.framework.*;

/**
 * @since 8.0.2
 */
public class ModuleTestBaseTest extends TestCase {

	/** @since 8.0.2 */
	public void testCompiledScriptsAreReusedWithoutBrowserState() throws Exception {
		URL pageURL = new URL("http://localhost/" + UUID.randomUUID() + "/");
		URL scriptURL = new URL(pageURL, "script.js");
		Object compiled;
		try (WebClient first = new TestModule().newClient()) {
			MockWebConnection connection = createConnection(pageURL, scriptURL, "max-age=3600");
			first.setWebConnection(connection);
			HtmlPage page = first.getPage(pageURL);
			compiled = first.getCache().getCachedObject(new WebRequest(scriptURL));
			assertNotNull(compiled);
			page.executeJavaScript("window.privateValue = 'first'; localStorage.setItem('privateValue', 'first');");
			first.getCookieManager().addCookie(new Cookie("localhost", "privateValue", "first"));
			assertEquals(2, connection.getRequestCount());
		}
		try (WebClient second = new TestModule().newClient()) {
			MockWebConnection connection = createConnection(pageURL, scriptURL, "max-age=3600");
			second.setWebConnection(connection);
			HtmlPage page = second.getPage(pageURL);
			assertSame(compiled, second.getCache().getCachedObject(new WebRequest(scriptURL)));
			assertEquals(1, connection.getRequestCount());
			assertEquals(1, ((Number) page.executeJavaScript("window.scriptExecutions").getJavaScriptResult()).intValue());
			assertEquals("undefined", page.executeJavaScript("typeof window.privateValue").getJavaScriptResult());
			assertNull(page.executeJavaScript("localStorage.getItem('privateValue')").getJavaScriptResult());
			assertTrue(second.getCookieManager().getCookies().isEmpty());
		}
	}

	/** @since 8.0.2 */
	public void testHtmlAndNonCacheableScriptsAreNotShared() throws Exception {
		URL pageURL = new URL("http://localhost/" + UUID.randomUUID() + "/");
		URL scriptURL = new URL(pageURL, "script.js");
		for (int i = 0; i < 2; i++) {
			try (WebClient client = new TestModule().newClient()) {
				MockWebConnection connection = createConnection(pageURL, scriptURL, "no-store");
				client.setWebConnection(connection);
				client.getPage(pageURL);
				assertNull(client.getCache().getCachedResponse(new WebRequest(pageURL)));
				assertNull(client.getCache().getCachedObject(new WebRequest(scriptURL)));
				assertEquals(2, connection.getRequestCount());
			}
		}
	}

	/** @since 8.0.2 */
	public void testAjaxResponsesAreNotShared() throws Exception {
		URL url = new URL("http://localhost/" + UUID.randomUUID() + "/response");
		for (int i = 0; i < 2; i++) {
			try (WebClient client = new TestModule().newClient()) {
				MockWebConnection connection = new MockWebConnection();
				connection.setResponse(url, "response " + i, 200, "OK", "application/json", cacheHeaders("max-age=3600"));
				client.setWebConnection(connection);
				assertEquals("response " + i, client.getPage(url).getWebResponse().getContentAsString());
				assertNull(client.getCache().getCachedResponse(new WebRequest(url)));
			}
		}
	}

	/** @since 8.0.2 */
	public void testFormValuesReadTheElementsCollectionOnce() throws Exception {
		try (WebClient client = new TestModule().newClient()) {
			URL url = new URL("http://localhost/form-values/");
			MockWebConnection connection = new MockWebConnection();
			connection.setResponse(url, "<html><head>" +
				"<script src='jquery.js'></script><script src='openxava.js'></script>" +
				"</head><body><form id='values'>" +
				"<input name='text' value='Text'><input type='hidden' name='hidden' value='Hidden'>" +
				"<input type='checkbox' name='checked' value='yes' checked>" +
				"<input type='checkbox' name='unchecked' value='no'>" +
				"<input type='radio' name='radio' value='first'><input type='radio' name='radio' value='second' checked>" +
				"<select name='single'><option selected>Choice</option></select>" +
				"<select name='multiple' multiple><option selected value='one'>One</option></select>" +
				"<textarea name='notes'>Notes</textarea><input name='disabled' value='Disabled' disabled>" +
				"<input name='duplicate' value='first'><input name='duplicate' value='second'>" +
				"<input type='file' name='file'><input type='submit' name='submit' value='Submit'>" +
				"<button type='button' name='button'>Button</button><input value='unnamed'>" +
				"</form></body></html>");
			connection.setResponse(new URL(url, "jquery.js"), readScript("jquery.js"), "application/javascript");
			connection.setResponse(new URL(url, "openxava.js"), readScript("openxava.js"), "application/javascript");
			client.setWebConnection(connection);
			HtmlPage page = client.getPage(url);
			page.executeJavaScript(
				"var form = document.getElementById('values'); var elements = form.elements;" +
				"window.elementsReads = 0;" +
				"Object.defineProperty(form, 'elements', {get: function() { window.elementsReads++; return elements; }});" +
				"window.serializedValues = openxava.getFormValues(form);");
			assertEquals("{\"text\":\"Text\",\"hidden\":\"Hidden\",\"checked\":\"yes\",\"radio\":\"second\"," +
				"\"single\":\"Choice\",\"multiple\":[],\"notes\":\"Notes\",\"disabled\":\"Disabled\",\"duplicate\":\"first\"}",
				page.executeJavaScript("JSON.stringify(window.serializedValues)").getJavaScriptResult());
			assertEquals(1, ((Number) page.executeJavaScript("window.elementsReads").getJavaScriptResult()).intValue());
		}
	}

	private MockWebConnection createConnection(URL pageURL, URL scriptURL, String scriptCacheControl) {
		MockWebConnection connection = new MockWebConnection();
		connection.setResponse(pageURL, "<html><head><script src='" + scriptURL + "'></script></head><body></body></html>",
			200, "OK", "text/html", cacheHeaders("max-age=3600"));
		connection.setResponse(scriptURL, "window.scriptExecutions = (window.scriptExecutions || 0) + 1;",
			200, "OK", "application/javascript", cacheHeaders(scriptCacheControl));
		return connection;
	}

	private List<NameValuePair> cacheHeaders(String value) {
		return Collections.singletonList(new NameValuePair("Cache-Control", value));
	}

	private String readScript(String name) throws Exception {
		try (InputStream script = ModuleTestBase.class.getResourceAsStream("/META-INF/resources/xava/js/" + name)) {
			assertNotNull(name, script);
			return new String(script.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static class TestModule extends ModuleTestBase {
		TestModule() {
			super("browser", "openxavatest", "Carrier");
		}

		WebClient newClient() throws Exception {
			return createWebClient();
		}
	}
}
