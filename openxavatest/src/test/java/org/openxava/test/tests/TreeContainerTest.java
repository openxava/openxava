package org.openxava.test.tests;

/**
 * 
 * 
 * @author Federico Alcántara 
 */

public class TreeContainerTest extends TreeViewTestBase {
	public TreeContainerTest(String testName) {
		super(testName, "TreeContainer");		
	}

	public void testForDoubleEditors() throws Exception {
		execute("List.viewDetail", "row=0");
		//TODO: This seems to be not implemented in YUI2
		//assertValueInTreeView("treeItems", 0, "ROOT ITEM 1");
		//assertValueInTreeView("treeItemTwos", 0, "2-ROOT ITEM 1");
	}
}
