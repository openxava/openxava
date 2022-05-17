package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.TreeContainerNoOrder;
import org.openxava.test.model.TreeItemNoOrder;

/**
 * 
 * @author Federico Alcantara
 */

public abstract class TreeViewNoOrderTestBase extends TreeTestBase {
	
	public TreeViewNoOrderTestBase(String testName, String application, String module) {
		super(testName, application, module);
	}
	
	public TreeViewNoOrderTestBase(String testName, String module) {
		super(testName, module);		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		populateTreeNoOrder();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected void populateTreeNoOrder() throws Exception {
		XPersistence.getManager().createQuery("delete from TreeItemNoOrder").executeUpdate();
		XPersistence.commit();
		TreeContainerNoOrder parent = XPersistence.getManager().find(TreeContainerNoOrder.class, 1);
		if (parent == null) {
			parent = new TreeContainerNoOrder();
			parent.setId(1);
			parent.setDescription("MY OWN TREE");
			parent = XPersistence.getManager().merge(parent);
		}
 		TreeItemNoOrder root = createTreeItem(parent, null, "ROOT ITEM 1");
		TreeItemNoOrder child1 = createTreeItem(parent, root, "CHILD ITEM 1");
		createTreeItem(parent, root, "CHILD ITEM 2");
		TreeItemNoOrder child3 = createTreeItem(parent, root, "CHILD ITEM 3");		
		createTreeItem(parent, child1, "SUBITEM 1 OF 1");
		createTreeItem(parent, child1, "SUBITEM 2 OF 1");
		createTreeItem(parent, child3, "SUBITEM 1 OF 3");		
		
		XPersistence.commit();
	}

	private TreeItemNoOrder createTreeItem(TreeContainerNoOrder container, TreeItemNoOrder parentTree, String description) throws Exception { 
		TreeItemNoOrder item = new TreeItemNoOrder();
		String path = "";
		if (parentTree != null) {
			path = parentTree.getPath() + "/" + parentTree.getId();
		}
		item.setPath(path);
		item.setDescription(description);
		item.setParentContainer(container);
		return XPersistence.getManager().merge(item);
	}		
}
