package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.TreeItem;
import org.openxava.util.Is;


/**
 * @author Federico Alcantara
 */

public class TreeViewDefaultPathPropertyBasedTest extends TreeViewTestBase {
	
	public TreeViewDefaultPathPropertyBasedTest(String testName) {
		super(testName, "TreeViewDefaultPathPropertyBased");		
	}

	public void testIfActionsPresent() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertAction("TreeView.new");
		assertAction("TreeView.removeSelected");
		assertAction("Print.generatePdf"); 
		assertAction("Print.generateExcel");
		assertAction("TreeView.up");
		assertAction("TreeView.down");
		assertAction("TreeView.left");
		assertAction("TreeView.right");
		execute("Mode.list");
	}
	
	public void testTreeStructure() throws Exception {
		execute("List.viewDetail", "row=0");
		assertTreeViewRowCount("treeItems", 7);
		assertValueInTreeViewIgnoreCase("treeItems", 0, "ROOT ITEM 1"); 
		assertValueInTreeViewIgnoreCase("treeItems", 1, "CHILD ITEM 1");
		assertValueInTreeViewIgnoreCase("treeItems", 2, "SUBITEM 1 OF 1");
		assertValueInTreeViewIgnoreCase("treeItems", 3, "SUBITEM 2 OF 1");
		assertValueInTreeViewIgnoreCase("treeItems", 4, "CHILD ITEM 2");
		assertValueInTreeViewIgnoreCase("treeItems", 5, "CHILD ITEM 3");
		assertValueInTreeViewIgnoreCase("treeItems", 6, "SUBITEM 1 OF 3");
	}
	
	public void testTreeCRUD() throws Exception {  
		execute("List.viewDetail", "row=0");
		// Test simple Save
		execute("TreeView.new", "viewObject=xava_view_treeItems");
		setValue("description", "ROOT ITEM 2");
		execute("TreeView.save");
		assertNoErrors();
		// Check if it is there
		assertValueInTreeViewIgnoreCase("treeItems", 7, "ROOT ITEM 2");
		executeOnTreeViewItem("treeItems", "Collection.edit", 7);
		assertValue("description", "ROOT ITEM 2");
		// Delete it from data
		execute("TreeView.remove");
		assertNoErrors();
		// Modify value
		executeOnTreeViewItem("treeItems", "Collection.edit", 4);
		assertValue("description", "CHILD ITEM 2");
		setValue("description", "THIS CHILD 2");
		execute("TreeView.save");
		assertValueInTreeViewIgnoreCase("treeItems", 4, "THIS CHILD 2");
		// Revert to its original
		executeOnTreeViewItem("treeItems", "Collection.edit", 4);
		setValue("description", "CHILD ITEM 2");
		execute("TreeView.save");

		
		// delete Selected
		checkRowTreeView("treeItems", 6);
		execute("TreeView.removeSelected", "viewObject=xava_view_treeItems");
		assertNoErrors();
		assertTreeViewRowCount("treeItems", 6);
		
		// add child item to Item
		checkRowTreeView("treeItems", 5);
		execute("TreeView.new", "viewObject=xava_view_treeItems");
		setValue("description", "SUBITEM 1 OF CHILD ITEM 3");
		execute("TreeView.save");
		assertNoErrors();
		assertTreeViewRowCount("treeItems", 7);
		TreeItem parentItem = getTreeItemByDescription("CHILD ITEM 3");
		TreeItem item = getTreeItemByDescription("SUBITEM 1 OF CHILD ITEM 3");
		String childPath = parentItem.getPath()+ "/" + parentItem.getId();
		assertEquals(childPath, item.getPath());
		
		// check for tree deletion
		checkRowTreeView("treeItems", 1);
		execute("TreeView.removeSelected", "viewObject=xava_view_treeItems");
		assertNoErrors();
		assertTreeViewRowCount("treeItems", 4);
		
	}
	
	public void testItemMove() throws Exception {
		execute("List.viewDetail", "row=0");
		// get Child Item 2 current path
		TreeItem item = getTreeItemByDescription("CHILD ITEM 2");
		String originalPath = item.getPath();
		assertNotEquals("path should not be empty", "[" + originalPath + "]", "[]");
		// Move item 2 left
		checkRowTreeView("treeItems", 4);
		execute("TreeView.left", "viewObject=xava_view_treeItems");
		// lets get the new position it should be at the bottom
		assertValueInTreeViewIgnoreCase("treeItems", 6, "CHILD ITEM 2");
		// and its path should be blank and different 
		TreeItem itemMoved = getTreeItemByDescription("CHILD ITEM 2");
		String path = itemMoved.getPath();
		if (Is.emptyString(path)) {
			path = "";
		} else {
			System.out.println(path);
			fail ("path should be empty");
		}
		assertNotEquals("path should be different than before moving it", "[" + path + "]", "[" + originalPath +"]");
		
		// Now let's move it right
		checkRowTreeView("treeItems", 6);
		execute("TreeView.right", "viewObject=xava_view_treeItems");
		// should still be in the bottom, but with a path
		assertValueInTreeViewIgnoreCase("treeItems", 6, "CHILD ITEM 2");
		itemMoved = getTreeItemByDescription("CHILD ITEM 2");
		assertNotEquals("path should not be empty", "[" + itemMoved.getPath() + "]", "[]");
		// now let's move it up
		checkRowTreeView("treeItems", 6);
		execute("TreeView.up", "viewObject=xava_view_treeItems");
		// now should be the second (zero based = 1)
		assertValueInTreeViewIgnoreCase("treeItems", 4, "CHILD ITEM 2");
		// now let's move it down
		checkRowTreeView("treeItems", 4);
		execute("TreeView.down", "viewObject=xava_view_treeItems");
		// should be again in the bottom
		assertValueInTreeViewIgnoreCase("treeItems", 6, "CHILD ITEM 2");

		// Now let's move an item with children
		checkRowTreeView("treeItems", 1);
		// First down
		execute("TreeView.down", "viewObject=xava_view_treeItems");
		assertValueInTreeViewIgnoreCase("treeItems", 3, "CHILD ITEM 1");
		assertValueInTreeViewIgnoreCase("treeItems", 5, "SUBITEM 2 OF 1");
		// now left
		checkRowTreeView("treeItems", 3);
		execute("TreeView.left", "viewObject=xava_view_treeItems");
		item = getTreeItemByDescription("CHILD ITEM 1");
		assertEquals("path should be empty", "[" + item.getPath() + "]", "[]");
		itemMoved = getTreeItemByDescription("SUBITEM 1 OF 1");
		// its path should be equal to item 1 id
		assertEquals("It is not a child of Item 1", "[" + itemMoved.getPath() + "]", "[/" + item.getId() + "]");
		itemMoved = getTreeItemByDescription("SUBITEM 2 OF 1");
		// its path should be equal to item 1 id
		assertEquals("It is not a child of Item 1", "[" + itemMoved.getPath() + "]", "[/" + item.getId() + "]");
	}

	public void testTreeReaderImplementation() throws Exception {
		changeModule("TreeViewAlternateReader");
		execute("List.viewDetail", "row=0");
		assertTreeViewRowCount("treeItems", 7);
		assertValueInTreeViewIgnoreCase("treeItems", 0, "0. ROOT ITEM 1");
		assertValueInTreeViewIgnoreCase("treeItems", 1, "1. CHILD ITEM 1");
		assertValueInTreeViewIgnoreCase("treeItems", 2, "4. SUBITEM 1 OF 1");
		assertValueInTreeViewIgnoreCase("treeItems", 3, "5. SUBITEM 2 OF 1");
		assertValueInTreeViewIgnoreCase("treeItems", 4, "2. CHILD ITEM 2");
		assertValueInTreeViewIgnoreCase("treeItems", 5, "3. CHILD ITEM 3");
		assertValueInTreeViewIgnoreCase("treeItems", 6, "6. SUBITEM 1 OF 3");
	}

	private TreeItem getTreeItemByDescription(String description) throws Exception {
		TreeItem returnValue =
				(TreeItem)XPersistence.getManager().createQuery("select t from TreeItem t where " +
			"t.description=:description")
			.setParameter("description", description)
			.getSingleResult();
		XPersistence.getManager().refresh(returnValue);
		return returnValue;
	}


}
