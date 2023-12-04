package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

/**
 * To test new tree library with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class TreeTest extends WebDriverTestBase{

	private Map<String, String> treeItemNodesId;
	private Map<String, String> treeItemTwoNodesId;
	
	public void testTreeLib() throws Exception {
		//all the tests are under the same, because the order must be respected
		goModule("TreeItem");
		String rootIdValue = getValueInList(0, 0);
		treeItemNodesId = addTreeIdValues(rootIdValue);
		
		goModule("TreeItemTwo");
		rootIdValue = getValueInList(0, 0);
		treeItemTwoNodesId = addTreeIdValues(rootIdValue);
		
		goModule("TreeContainer");
		execute("List.viewDetail", "row=0");
		createNewNodeSelecting(getDriver());	

		goModule("TreeItem");
		addNewNodeId(getDriver());
		
		goModule("TreeContainer");
		verifyCreatedNodesAndCheck(getDriver());
		editNodeWithDoubleClick(getDriver());
		deleteSelectedNode(getDriver());
		cutNode_treeState(getDriver());
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		dragAndDrop(getDriver()); 
		execute("Mode.list");
		execute("CRUD.deleteRow", "row=1");
		
		resetModule(getDriver());
		goModule("TreeContainer");
		execute("List.viewDetail", "row=0");
		createNodeWithPathSeparator_dnd(getDriver());
	}
	
	// Wait until the element is available and return it
	private WebElement findElement(WebDriver driver, By by) { 
		wait(driver, by);
		return driver.findElement(by);		
	}

	private void createNewNodeSelecting(WebDriver driver) throws Exception {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		execute("TreeView.new", "viewObject=xava_view_treeItems");
		setValue("description", "A");
		execute("TreeView.save");
		wait(driver);

		//need locate and click manually
		WebElement createNewButtonElement = driver.findElement(By.xpath("//a[@data-application='openxavatest' and @data-module='TreeContainer' and @data-action='TreeView.new' and @data-argv='viewObject=xava_view_treeItems']"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", createNewButtonElement); // must clicked manually because the element is not interactable
		Thread.sleep(500); //sometimes need
		acceptInDialogJS(driver);
		setValue("description","B");
		execute("TreeView.save");
		wait(driver);
	}
	
	private void editNodeWithDoubleClick(WebDriver driver) throws Exception {
		Thread.sleep(500); //sometimes need
		WebElement aElement = driver.findElement(By.id(treeItemNodesId.get("a") + "_anchor"));
		Actions actions = new Actions(driver);
		actions.doubleClick(aElement).perform();
		wait(driver);
		
		setValue("description", "AA");
		execute("TreeView.save");
		wait(driver);
		
		assertEquals("AA", findElement(driver, By.id(treeItemNodesId.get("a") + "_anchor")).getText()); 
	}
	
	private void verifyCreatedNodesAndCheck(WebDriver driver) throws InterruptedException {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		Thread.sleep(500); //sometimes need
		expandNode(driver, treeItemNodesId.get("child2"));
		WebElement newNodeB = driver.findElement(By.xpath("//a[@id='" + treeItemNodesId.get("b") + "_anchor']"));
		assertEquals("B", newNodeB.getText());
		WebElement newNodeACheckBox = driver.findElement(By.xpath("//a[@id='" + treeItemNodesId.get("a") + "_anchor']/i"));
		newNodeACheckBox.click();
	}
	
	private void deleteSelectedNode(WebDriver driver) throws Exception {
		List<WebElement> collectionButtons = driver.findElements(By.className("ox-collection-list-actions"));
		List<WebElement> firstCollectionButtons = collectionButtons.get(0).findElements(By.className("ox-button-bar-button"));
		WebElement removeButton = firstCollectionButtons.get(1).findElement(By.tagName("a"));
		removeButton.click();
		acceptInDialogJS(driver);
		wait(driver);

		boolean showMessages = !driver.findElements(By.cssSelector("td.ox-messages")).isEmpty();
		assertTrue(showMessages);
	}
	
	private void cutNode_treeState(WebDriver driver) throws Exception {
		WebElement bCheckBox = driver.findElement(By.xpath("//a[@id='" + treeItemNodesId.get("b") + "_anchor']/i"));
		bCheckBox.click();
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_treeItems");
		execute("Mode.list");
		execute("CRUD.new");
		setValue("description", "BB");
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_treeItems");
		WebElement bElement = findElement(driver, By.id(treeItemNodesId.get("b") + "_anchor")); 
		assertTrue(bElement.getText().equals("B"));
		
		WebElement bItemCheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("b") +"_anchor']/i")); 
		bItemCheckBox.click();
		Thread.sleep(500); // sometimes need
		execute("Navigation.first");

		assertFalse(driver.findElements(By.id(treeItemNodesId.get("child1") + "_anchor")).isEmpty()); 
	}
	
	private void dragAndDrop(WebDriver driver) throws Exception {
		executeDnd(driver, treeItemNodesId.get("child1sub2") + "_anchor", treeItemNodesId.get("child1sub1") + "_anchor");
		executeDndBetween(driver, treeItemNodesId.get("child1") + "_anchor", treeItemNodesId.get("root"));
		expandNode(driver, treeItemNodesId.get("child1sub1"));
		executeDnd(driver, treeItemNodesId.get("child3sub1") + "_anchor", treeItemNodesId.get("child1sub2") + "_anchor");
		expandNode(driver, treeItemNodesId.get("child1sub2"));
		executeDnd(driver, treeItemNodesId.get("child1") + "_anchor", treeItemNodesId.get("root") + "_anchor");
		driver.navigate().refresh();
		wait(driver);
		assertTrue(isElementInside(driver, treeItemNodesId.get("root"), treeItemNodesId.get("child1") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1"), treeItemNodesId.get("child1sub1") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1sub1"), treeItemNodesId.get("child1sub2") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1sub2"), treeItemNodesId.get("child3sub1") + "_anchor"));
		
		executeDnd(driver, treeItemNodesId.get("child3sub1") + "_anchor", treeItemNodesId.get("child3") + "_anchor");
		executeDndBetween(driver, treeItemNodesId.get("child1sub2") + "_anchor", treeItemNodesId.get("child1"));
	}
	
	private void createNodeWithPathSeparator_dnd(WebDriver driver) throws Exception {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemTwoNodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		execute("TreeView.new", "viewObject=xava_view_treeItemTwos");
		setValue("description", "A");
		execute("TreeView.save");
		wait(driver);
		
		expandNode(driver, treeItemTwoNodesId.get("child2"));
		driver.navigate().refresh();
		wait(driver);
		
		WebElement childElement = driver.findElement(By.id(treeItemTwoNodesId.get("child2"))).findElement(By.xpath(".//li"));
		String childElementId = childElement.getAttribute("id");
		assertEquals("A", childElement.getText());
		
		executeDnd(driver, childElementId + "_anchor", treeItemTwoNodesId.get("child3") + "_anchor");
		driver.navigate().refresh();
		wait(driver);
		expandNode(driver, treeItemTwoNodesId.get("child3"));
		assertTrue(isElementInside(driver, treeItemTwoNodesId.get("child3"), childElementId + "_anchor"));
	}
	
	private void executeDnd(WebDriver driver, String sourceElementId, String targetElementId) throws InterruptedException {
        WebElement sourceElement = findElement(driver, By.id(sourceElementId));
        WebElement targetElement = findElement(driver, By.id(targetElementId));		
        Actions actions = new Actions(driver);
        actions.dragAndDrop(sourceElement, targetElement).build().perform();
        Thread.sleep(500);// wait animation and html
	}
	
	private void executeDndBetween(WebDriver driver, String sourceElementId, String targetParentId) throws InterruptedException {
        Actions actions = new Actions(driver);
        actions.clickAndHold(driver.findElement(By.id(sourceElementId)))
               .build()
               .perform();
        actions.moveToElement(driver.findElement(By.id(targetParentId + "_anchor")))
               .release()
               .build()
               .perform();
        actions.moveToElement(driver.findElement(By.id(targetParentId)).findElement(By.tagName("ul")))
               .release()
               .build()
               .perform();
        Thread.sleep(500);// wait animation and html
	}
	
	private void expandNode(WebDriver driver, String id) throws InterruptedException {
		WebElement liElement = driver.findElement(By.id(id));
		String expanded = liElement.getAttribute("aria-expanded");
		if (expanded.equals("false")) {
			WebElement liIcon = liElement.findElement(By.cssSelector("i.jstree-icon.jstree-ocl"));
			liIcon.click();
		}
		Thread.sleep(500); // wait animation and fill html elements
	}
	
	private boolean isElementInside(WebDriver driver, String parentElementId, String childElementId) {
		WebElement parentElement = driver.findElement(By.id(parentElementId));
		WebElement childElement = parentElement.findElement(By.id(childElementId));
		return (childElement != null);
	}
	
	private Map<String, String> addTreeIdValues(String rootId) {
		Map<String, String> map = new HashMap<>();
		int root = Integer.valueOf(rootId);
		map.put("root", rootId);
		map.put("child1", String.valueOf(root+1));
		map.put("child2", String.valueOf(root+2));
		map.put("child3", String.valueOf(root+3));
		map.put("child1sub1", String.valueOf(root+4));
		map.put("child1sub2", String.valueOf(root+5));
		map.put("child3sub1", String.valueOf(root+6));
		
		return map;
	}
	
	private void addNewNodeId(WebDriver driver) throws Exception {
		treeItemNodesId.put("a", getValueInList(7, 0));
		treeItemNodesId.put("b", getValueInList(8, 0));
	}
	
}
