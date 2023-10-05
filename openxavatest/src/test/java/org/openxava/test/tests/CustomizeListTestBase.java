package org.openxava.test.tests;

import static org.openxava.tests.HtmlUnitUtils.getHrefAttribute;

import org.htmlunit.html.*;
import org.openxava.tests.*;

/**
 * 
 * @since 5.2
 * @author Javier Paniza
 */
abstract public class CustomizeListTestBase extends ModuleTestBase {
	
	private String module;
	
	public CustomizeListTestBase(String testName, String module) {
		super(testName, module);
		this.module = module;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void removeColumn(int index) throws Exception {
		removeColumn("list", "xava_tab", index);
	}
	
	protected void removeColumn(String collection, int index) throws Exception {
		removeColumn(collection, "xava_collectionTab_" + collection, index);
	}
	
	protected void moveColumn(int from, int to) throws Exception { 
		moveColumn("list", from, to); 
	}
	
	protected void moveColumnNoDragAndDrop(int from, int to) throws Exception {
		// It's better to use moveColumn() if possible, because this is not an exact mirror of the real
		//   behavior. It does not do drag & drop and it does a reload().
		getHtmlPage().executeJavaScript("Tab.moveProperty('ox_openxavatest_" + module +"__list', " + from + ", " + to + ")");
		Thread.sleep(30);
		reload();		
	}
	
	protected void moveColumn(String collection, int from, int to) throws Exception { 
		// This method does not work for all "from, to" combinations, at least with HtmlUnit 2.15
		//   when it does not work you can use moveColumnNoDragAndDrop instead
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement fromCol = table.getRow(0).getCell(from + 2);
		HtmlElement handle = fromCol.getElementsByAttribute("i", "class", "xava_handle mdi mdi-cursor-move ui-sortable-handle").get(0); 
		handle.mouseDown();
		HtmlElement toCol = table.getRow(0).getCell(to + 2);
		HtmlElement elementTo = toCol.getElementsByAttribute("i", "class", "mdi mdi-rename-box").get(0);
		
		elementTo.mouseMove();
		elementTo.mouseUp();		
		Thread.sleep(700); 
	}

	
	private void removeColumn(String collection, String tabObject, int index) throws Exception {
		getWebClient().getOptions().setCssEnabled(true); 
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement header = table.getRow(0).getCell(index + 2);
		HtmlElement icon = header.getElementsByAttribute("i", "class", "mdi mdi-close-circle").get(0);
		HtmlElement removeLink = icon.getEnclosingElement("a");
		System.out.println("[CustomizeListTestBase.removeColumn] getHrefAttribute(removeLink)=" + getHrefAttribute(removeLink)); // tmr
		getHtmlPage().executeJavaScript(getHrefAttribute(removeLink)); // Because removeLink.click() does not work with HtmlUnit 2.70
		Thread.sleep(800); 
	}

}
