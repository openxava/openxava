package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.TreeContainer;
import org.openxava.test.model.TreeItem;
import org.openxava.test.model.TreeItemTwo;

/**
 * 
 * @author Federico Alcantara
 */

public abstract class TreeViewTestBase extends TreeTestBase {
	
	public TreeViewTestBase(String testName, String application, String module) {
		super(testName, application, module);
	}
	
	public TreeViewTestBase(String testName, String module) {
		super(testName, module);		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		populateTree();
		populateTreeTwo();
	}
	
	@Override
	protected void tearDown() throws Exception {
		populateTree();
		populateTreeTwo();
		super.tearDown();
	}
	
	protected void populateTree() throws Exception {
		XPersistence.getManager().createQuery("delete from TreeItem").executeUpdate();
		XPersistence.commit();
		TreeContainer parent = XPersistence.getManager().find(TreeContainer.class, 1);		
		TreeItem root = createTreeItem(parent, null, "ROOT ITEM 1",    0);
		TreeItem child1 = createTreeItem(parent, root, "CHILD ITEM 1",   2);
		createTreeItem(parent, root, "CHILD ITEM 2",   4);
		TreeItem child3 = createTreeItem(parent, root, "CHILD ITEM 3",   6);		
		createTreeItem(parent, child1, "SUBITEM 1 OF 1", 2);
		createTreeItem(parent, child1, "SUBITEM 2 OF 1", 4);
		createTreeItem(parent, child3, "SUBITEM 1 OF 3", 6);		
		
		XPersistence.commit();
	}

	protected void populateTreeTwo() throws Exception {
		XPersistence.getManager().createQuery("delete from TreeItemTwo").executeUpdate();
		XPersistence.commit();
		TreeContainer parent = XPersistence.getManager().find(TreeContainer.class, 1);		
		TreeItemTwo root = createTreeItemTwo(parent, null, "ROOT ITEM 1",    0);
		TreeItemTwo child1 = createTreeItemTwo(parent, root, "CHILD ITEM 1",   2);
		createTreeItemTwo(parent, root, "CHILD ITEM 2",   4);
		TreeItemTwo child3 = createTreeItemTwo(parent, root, "CHILD ITEM 3",   6);		
		createTreeItemTwo(parent, child1, "SUBITEM 1 OF 1", 2);
		createTreeItemTwo(parent, child1, "SUBITEM 2 OF 1", 4);
		createTreeItemTwo(parent, child3, "SUBITEM 1 OF 3", 6);		
		
		XPersistence.commit();
	}

	private TreeItemTwo createTreeItemTwo(TreeContainer container, TreeItemTwo parentTree, String description, int treeOrder) throws Exception { 
		TreeItemTwo item = new TreeItemTwo();
		String path = "";
		if (parentTree != null) {
			path = parentTree.getFolder() + "/" + parentTree.getId();
		}
		item.setFolder(path);
		item.setDescription(description);
		item.setTreeOrder(treeOrder);
		item.setParentContainer(container);
		return XPersistence.getManager().merge(item);
	}		

	private TreeItem createTreeItem(TreeContainer container, TreeItem parentTree, String description, int treeOrder) throws Exception { 
		TreeItem item = new TreeItem();
		String path = "";
		if (parentTree != null) {
			path = parentTree.getPath() + "/" + parentTree.getId();
		}
		item.setPath(path);
		item.setDescription(description);
		item.setTreeOrder(treeOrder);
		item.setParentContainer(container);
		return XPersistence.getManager().merge(item);
	}		
}
