package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.TreeItemNoOrder;
import org.openxava.util.Is;


/**
 * 
 * @author Federico Alcantara
 */

public class TreeContainerNoOrderTest extends TreeViewNoOrderTestBase {
	
	public TreeContainerNoOrderTest(String testName) {
		super(testName, "TreeContainerNoOrder");		
	}

	public void testIfActionsPresent() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertAction("TreeView.new");
		assertAction("TreeView.removeSelected");
		assertAction("Print.generatePdf"); 
		assertAction("Print.generateExcel");
		assertNoAction("TreeView.up");
		assertNoAction("TreeView.down");
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
		TreeItemNoOrder parentItem = getTreeItemByDescription("CHILD ITEM 3");
		TreeItemNoOrder item = getTreeItemByDescription("SUBITEM 1 OF CHILD ITEM 3");
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
		TreeItemNoOrder item = getTreeItemByDescription("CHILD ITEM 2");
		String originalPath = item.getPath();
		assertNotEquals("path should not be empty", "[" + originalPath + "]", "[]");
		// Move item 2 left
		checkRowTreeView("treeItems", 4);
		execute("TreeView.left", "viewObject=xava_view_treeItems");
		// lets get the new position it should be at the bottom
		assertValueInTreeViewIgnoreCase("treeItems", 6, "CHILD ITEM 2");
		// and its path should be blank and different 
		TreeItemNoOrder itemMoved = getTreeItemByDescription("CHILD ITEM 2");
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
		// now it moves up to position 4 because it is a root item
		assertValueInTreeViewIgnoreCase("treeItems", 4, "CHILD ITEM 2");
		itemMoved = getTreeItemByDescription("CHILD ITEM 2");
		assertNotEquals("path should not be empty", "[" + itemMoved.getPath() + "]", "[]");
	}

	private TreeItemNoOrder getTreeItemByDescription(String description) throws Exception {
		TreeItemNoOrder returnValue =
				(TreeItemNoOrder)XPersistence.getManager().createQuery("select t from TreeItemNoOrder t where " +
			"t.description=:description")
			.setParameter("description", description)
			.getSingleResult();
		XPersistence.getManager().refresh(returnValue);
		return returnValue;
	}


}
