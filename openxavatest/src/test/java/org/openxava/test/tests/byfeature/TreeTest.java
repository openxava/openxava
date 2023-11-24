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
	
	private WebDriver driver;
	private Map<String, String> nodesId;

	public void setUp() throws Exception {
		setHeadless(true);
		driver = createWebDriver();
	}

	public void testTreeLib() throws Exception {
		//all the tests are under the same, because the order must be respected
		goModule(driver, "TreeItem");
		String rootIdValue = getValueInList(0, 0);
		addTreeIdValues(rootIdValue);
		
		goModule(driver, "TreeContainer");
		execute("List.viewDetail", "row=0");
		createNewNodeSelecting(driver);	

		goModule(driver, "TreeItem");
		addNewNodeId(driver);
		
		goModule(driver, "TreeContainer");
		verifyCreatedNodesAndCheck(driver);
		editNodeWithDoubleClick(driver);
		deleteSelectedNode(driver);
		cutNode(driver);
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		dragAndDrop(driver); 
		execute("Mode.list");
		execute("CRUD.deleteRow", "row=1");
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	// Wait until the element is available and return it
	private WebElement findElement(WebDriver driver, By by) { 
		wait(driver, by);
		return driver.findElement(by);		
	}

	private void createNewNodeSelecting(WebDriver driver) throws Exception {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ nodesId.get("child2") +"_anchor']/i")); 
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
		WebElement aElement = driver.findElement(By.id(nodesId.get("a") + "_anchor"));
		Actions actions = new Actions(driver);
		actions.doubleClick(aElement).perform();
		wait(driver);
		
		setValue("description", "AA");
		execute("TreeView.save");
		wait(driver);
		
		assertEquals("AA", findElement(driver, By.id(nodesId.get("a") + "_anchor")).getText()); 
	}
	
	private void verifyCreatedNodesAndCheck(WebDriver driver) throws InterruptedException {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ nodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		Thread.sleep(500); //sometimes need
		expandNode(driver, nodesId.get("child2"));
		WebElement newNodeB = driver.findElement(By.xpath("//a[@id='" + nodesId.get("b") + "_anchor']"));
		assertEquals("B", newNodeB.getText());
		WebElement newNodeACheckBox = driver.findElement(By.xpath("//a[@id='" + nodesId.get("a") + "_anchor']/i"));
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
	
	private void cutNode(WebDriver driver) throws Exception {
		WebElement bCheckBox = driver.findElement(By.xpath("//a[@id='" + nodesId.get("b") + "_anchor']/i"));
		bCheckBox.click();
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_treeItems");
		execute("Mode.list");
		execute("CRUD.new");
		setValue("description", "BB");
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_treeItems");
		WebElement bElement = findElement(driver, By.id(nodesId.get("b") + "_anchor")); 
		assertTrue(bElement.getText().equals("B"));
	}
	
	private void dragAndDrop(WebDriver driver) throws Exception {
		executeDnd(driver, nodesId.get("child1sub2") + "_anchor", nodesId.get("child1sub1") + "_anchor");
		executeDndBetween(driver, nodesId.get("child1") + "_anchor", nodesId.get("root"));
		expandNode(driver, nodesId.get("child1sub1"));
		executeDnd(driver, nodesId.get("child3sub1") + "_anchor", nodesId.get("child1sub2") + "_anchor");
		expandNode(driver, nodesId.get("child1sub2"));
		executeDnd(driver, nodesId.get("child1") + "_anchor", nodesId.get("root") + "_anchor");
		driver.navigate().refresh();
		wait(driver);
		assertTrue(isElementInside(driver, nodesId.get("root"), nodesId.get("child1") + "_anchor"));
		assertTrue(isElementInside(driver, nodesId.get("child1"), nodesId.get("child1sub1") + "_anchor"));
		assertTrue(isElementInside(driver, nodesId.get("child1sub1"), nodesId.get("child1sub2") + "_anchor"));
		assertTrue(isElementInside(driver, nodesId.get("child1sub2"), nodesId.get("child3sub1") + "_anchor"));
		
		executeDnd(driver, nodesId.get("child3sub1") + "_anchor", nodesId.get("child3") + "_anchor");
		executeDndBetween(driver, nodesId.get("child1sub2") + "_anchor", nodesId.get("child1"));
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
	
	private void addTreeIdValues(String rootId) {
		nodesId = new HashMap<>();
		int root = Integer.valueOf(rootId);
		nodesId.put("root", rootId);
		nodesId.put("child1", String.valueOf(root+1));
		nodesId.put("child2", String.valueOf(root+2));
		nodesId.put("child3", String.valueOf(root+3));
		nodesId.put("child1sub1", String.valueOf(root+4));
		nodesId.put("child1sub2", String.valueOf(root+5));
		nodesId.put("child3sub1", String.valueOf(root+6));
	}
	
	private void addNewNodeId(WebDriver driver) throws Exception {
		nodesId.put("a", getValueInList(7, 0));
		nodesId.put("b", getValueInList(8, 0));
	}
	
}
