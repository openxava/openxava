package org.openxava.test.tests.byfeature;

import java.util.*;
import java.util.stream.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

/**
 * To test new tree library with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class TreeTest extends WebDriverTestBase{
	
	public TreeTest(String testName) {
		super(testName);
	}
	
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
		
		goModule("Step"); // tree as reference
		execute("List.viewDetail","row=0");
		assertTreeIsPresent(getDriver()); 
		
		goModule("TreeContainer"); // tree in dialog
		execute("TreeContainer.showTree");
		assertTreeIsPresent(getDriver());
		WebElement closeButton = getDriver().findElement(By.className("ui-dialog-titlebar-close"));
		closeButton.click();
		Thread.sleep(200);
		execute("List.viewDetail", "row=0");
		idPropertiesNotDuplicated(getDriver());
		
		
		rootWithPath(getDriver());
		createNewNodeSelecting_state(getDriver());	
		
		goModule("TreeItem");
		addNewNodeId(getDriver());
		
		goModule("TreeContainer");
		verifyCreatedNodesAndCheck(getDriver());
		editNodeWithDoubleClick(getDriver()); 
		deleteSelectedNode(getDriver());
		cutNode_treeState(getDriver()); 
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		dragAndDrop_verifyOrder(getDriver()); 
		execute("Mode.list");
		execute("CRUD.deleteRow", "row=1");
		
		resetModule(getDriver());
		//goModule("TreeContainer");
		execute("List.viewDetail", "row=0");
		createNodeWithPathSeparator_dnd(getDriver());
		
		
	}
	
	// Wait until the element is available and return it
	private WebElement findElement(WebDriver driver, By by) { 
		wait(driver, by);
		return driver.findElement(by);		
	}
	
	private void rootWithPath(WebDriver driver) {
		assertFalse(isElementInside(driver, "2", ("1" + "_anchor")));
		assertFalse(isElementInside(driver, "3", ("1" + "_anchor")));
	}
	
	private void idPropertiesNotDuplicated(WebDriver driver) throws Exception {
		execute("TreeView.new", "viewObject=xava_view_treeItemNoIdGeneration");
		setValue("id", "6");
		setValue("code", "15");
		setValue("way", "0");
		setValue("description", "A");
		setValue("theOrder", "0");
		Thread.sleep(300);
		execute("TreeView.save");
		assertErrors();
		execute("Collection.hideDetail");
	}

	private void createNewNodeSelecting_state(WebDriver driver) throws Exception {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		execute("TreeView.new", "viewObject=xava_view_treeItems");
		Thread.sleep(1000);
		setValue("description", "A");
		execute("TreeView.save");
		wait(driver);
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		WebElement childItem2Element = findElement(driver, By.id(treeItemNodesId.get("child2")));
		List<WebElement> ulElements = childItem2Element.findElements(By.tagName("ul"));
		assertTrue(ulElements.isEmpty());
		
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
	
	private void dragAndDrop_verifyOrder(WebDriver driver) throws Exception {
		executeDnd(driver, treeItemNodesId.get("child1sub2") + "_anchor", treeItemNodesId.get("child1sub1") + "_anchor");
		executeDndBetween(driver, treeItemNodesId.get("child1") + "_anchor", treeItemNodesId.get("root"));
		expandNode(driver, treeItemNodesId.get("child1sub1"));
		driver.navigate().refresh(); // manually not need refresh
		wait(driver);
		executeDnd(driver, treeItemNodesId.get("child3sub1") + "_anchor", treeItemNodesId.get("child1sub2") + "_anchor");
		expandNode(driver, treeItemNodesId.get("child1sub2"));
		driver.navigate().refresh(); // manually not need refresh
		wait(driver);
		executeDnd(driver, treeItemNodesId.get("child1") + "_anchor", treeItemNodesId.get("root") + "_anchor");
		driver.navigate().refresh();
		wait(driver);
		assertTrue(isElementInside(driver, treeItemNodesId.get("root"), treeItemNodesId.get("child1") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1"), treeItemNodesId.get("child1sub1") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1sub1"), treeItemNodesId.get("child1sub2") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1sub2"), treeItemNodesId.get("child3sub1") + "_anchor"));
		
		executeDnd(driver, treeItemNodesId.get("child3sub1") + "_anchor", treeItemNodesId.get("child3") + "_anchor");
		executeDndBetween(driver, treeItemNodesId.get("child1sub2") + "_anchor", treeItemNodesId.get("child1"));
		Thread.sleep(5000);
		
		List<String> childs = new ArrayList<>(Arrays.asList(treeItemNodesId.get("child1sub2"), treeItemNodesId.get("child1sub1")));
		verifyChildsOrder(driver, treeItemNodesId.get("child1"), childs);
		
		executeDnd(driver, "14_anchor", "11_anchor");
		driver.navigate().refresh();
		Thread.sleep(1500); // wait all trees
		assertTrue(isElementInside(driver, "14", "15_anchor"));
		executeDndBetween(driver, "14_anchor", "11");
		driver.navigate().refresh();
		Thread.sleep(1500); // wait all trees
		assertTrue(isElementInside(driver, "14", "15_anchor"));
		executeDnd(driver, "14_anchor", "12_anchor");
		driver.navigate().refresh();
		Thread.sleep(1500); // wait all trees
		assertTrue(isElementInside(driver, "14", "15_anchor"));
		
		executeDnd(driver, treeItemNodesId.get("child1sub1")+ "_anchor", treeItemNodesId.get("child3sub1") + "_anchor");
		executeDnd(driver, treeItemNodesId.get("child3sub1")+ "_anchor", treeItemNodesId.get("child1") + "_anchor");
		driver.navigate().refresh();
		Thread.sleep(1500); // wait all trees
		expandNode(driver, treeItemNodesId.get("child3sub1"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1"), treeItemNodesId.get("child3sub1") + "_anchor"));
		assertTrue(isElementInside(driver, treeItemNodesId.get("child3sub1"), treeItemNodesId.get("child1sub1") + "_anchor"));
		executeDnd(driver, treeItemNodesId.get("child3sub1") + "_anchor", treeItemNodesId.get("child3") + "_anchor");
		expandNode(driver, treeItemNodesId.get("child3"));
		executeDnd(driver, treeItemNodesId.get("child1sub1") + "_anchor", treeItemNodesId.get("child1") + "_anchor");
		
		//order multiple node
		WebElement childItem1CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child1") +"_anchor']/i")); 
		WebElement childItem3Sub1CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child3sub1") +"_anchor']/i"));
		childItem1CheckBox.click();
		childItem3Sub1CheckBox.click();
		Thread.sleep(500);
		executeDndBetween(driver, treeItemNodesId.get("child1") + "_anchor", treeItemNodesId.get("root"));

		driver.navigate().refresh();
		Thread.sleep(1500);
		childs = new ArrayList<>(Arrays.asList(treeItemNodesId.get("child1"), treeItemNodesId.get("child3sub1"), treeItemNodesId.get("root")));
		verifyChildsOrder(driver, "#", childs);
		assertTrue(isElementInside(driver, treeItemNodesId.get("child1"), treeItemNodesId.get("child1sub1") + "_anchor"));
		
		childItem1CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child1") +"_anchor']/i"));
		childItem3Sub1CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemNodesId.get("child3sub1") +"_anchor']/i"));
		childItem1CheckBox.click();
		childItem3Sub1CheckBox.click();
		
		executeDnd(driver, treeItemNodesId.get("child3sub1") + "_anchor", treeItemNodesId.get("child3") + "_anchor");
		executeDnd(driver, treeItemNodesId.get("child1") + "_anchor", treeItemNodesId.get("root") + "_anchor");
		expandNode(driver, treeItemNodesId.get("child3"));
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
		
		childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ treeItemTwoNodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		expandNode(driver, treeItemTwoNodesId.get("child2"));
		WebElement childElement = driver.findElement(By.id(treeItemTwoNodesId.get("child2"))).findElement(By.xpath(".//li"));
		String childElementId = childElement.getAttribute("id");
		assertEquals("A, D", childElement.getText());
		
		executeDnd(driver, childElementId + "_anchor", treeItemTwoNodesId.get("child3") + "_anchor");
		driver.navigate().refresh();
		wait(driver);
		expandNode(driver, treeItemTwoNodesId.get("child3"));
		assertTrue(isElementInside(driver, treeItemTwoNodesId.get("child3"), childElementId + "_anchor"));
		
		WebElement aCheckBox = findElement(driver, By.xpath("//a[@id='"+ childElementId +"_anchor']/i")); 
		aCheckBox.click();
		execute("TreeView.removeSelected", "viewObject=xava_view_treeItemTwos");
		
		// Verify that drag and drop is disabled when allowMoveNodes is false
		verifyDragAndDropDisabledWhenAllowsMoveNodesFalse(driver);
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
		try {
			WebElement parentElement = driver.findElement(By.id(parentElementId));
			WebElement childElement = parentElement.findElement(By.id(childElementId));
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}
	
	private boolean isElementInsideXPath(WebDriver driver, String parentElementId, String childElementId) {
		try {
			WebElement parentElement = driver.findElement(By.xpath("//*[@id='" + parentElementId + "']"));
			WebElement childElement = parentElement.findElement(By.xpath(".//*[@id='" + childElementId + "']"));
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
	        return false;
	    }
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
	
	private void assertTreeIsPresent(WebDriver driver) throws Exception {
		wait(driver);  
		List<WebElement> rootElement = driver.findElements(By.xpath("//a[@id='"+ treeItemNodesId.get("root") +"_anchor']/i")); 
		assertFalse(rootElement.isEmpty());
	}
	
	private void verifyChildsOrder(WebDriver driver, String parentId, List<String> expectedOrderIds) {
		WebElement parentElement;
		List<WebElement> childElements;
		if (parentId == "#") {
			//case root
			parentElement = driver.findElement(By.cssSelector("div[data-table-id='ox_openxavatest_TreeContainer__treeItems']"));
			childElements = parentElement.findElements(By.xpath("./ul[@class='jstree-container-ul jstree-children jstree-no-icons']/li"));
	    } else {
	    	parentElement = driver.findElement(By.id(parentId));
	    	childElements = parentElement.findElements(By.xpath("./ul[@class='jstree-children']/li"));
	    }
	   
	    List<String> actualOrderIds = childElements.stream()
	            .map(child -> child.getAttribute("id"))
	            .collect(Collectors.toList());
	    
	    for (int i = 0; i < expectedOrderIds.size(); i++) {
	        String expectedId = expectedOrderIds.get(i);
	        if (!actualOrderIds.contains(expectedId)) {
	            throw new AssertionError("The ID " + expectedId + " is not among the children of " + parentId);
	        }
	        int actualIndex = actualOrderIds.indexOf(expectedId);
	        if (i != actualIndex) {
	            throw new AssertionError("Order is incorrect. Expected ID " + expectedId + " in the position " + i + " but was " + actualIndex);
	        }
	    }
	}
	
	private void verifyDragAndDropDisabledWhenAllowsMoveNodesFalse(WebDriver driver) throws Exception {
		// The treeItemNoIdGeneration collection is already visible in the detail view
		// We just need to scroll to it to ensure it's in view
		WebElement treeItemNoIdGenerationContainer = driver.findElement(By.cssSelector("div[data-table-id='ox_openxavatest_TreeContainer__treeItemNoIdGeneration']"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", treeItemNoIdGenerationContainer);
		wait(driver);
		
		// Simple wait to ensure elements are loaded
		Thread.sleep(1000);
		
		// Get all tree items
		List<WebElement> treeItems = driver.findElements(By.cssSelector(".jstree-anchor"));
		assertTrue("There should be at least two tree items for the test", treeItems.size() >= 2);
		
		// Find CHILD 1 and CHILD 2 elements
		WebElement sourceElement = null;
		WebElement targetElement = null;
		int sourceIndex = -1;
		int targetIndex = -1;
		
		for (int i = 0; i < treeItems.size(); i++) {
			String text = treeItems.get(i).getText();
			if (text.equals("CHILD 1")) {
				sourceElement = treeItems.get(i);
				sourceIndex = i;
			} else if (text.equals("CHILD 2")) {
				targetElement = treeItems.get(i);
				targetIndex = i;
			}
		}
		
		assertNotNull("Could not find 'CHILD 1' element", sourceElement);
		assertNotNull("Could not find 'CHILD 2' element", targetElement);
		
		// Store the original text of the elements to verify later
		String sourceText = sourceElement.getText();
		String targetText = targetElement.getText();
		
		
		// Try to drag and drop using a more reliable implementation
		try {
			Actions actions = new Actions(driver);
			actions.clickAndHold(sourceElement)
				.moveToElement(targetElement)
				.release()
				.perform();
			
			// Wait for any animations to complete
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		
		// Refresh the page to ensure we see the updated state
		driver.navigate().refresh();
		wait(driver);
		
		// Wait for elements to load after refresh
		Thread.sleep(1000);
		
		// Get all tree items again
		List<WebElement> updatedTreeItems = driver.findElements(By.cssSelector(".jstree-anchor"));
		
		// Find the updated positions of CHILD 1 and CHILD 2
		int updatedSourceIndex = -1;
		int updatedTargetIndex = -1;
		
		for (int i = 0; i < updatedTreeItems.size(); i++) {
			String text = updatedTreeItems.get(i).getText();
			if (text.equals(sourceText)) {
				updatedSourceIndex = i;
			} else if (text.equals(targetText)) {
				updatedTargetIndex = i;
			}
		}
		
		// Print debug information
		System.out.println("After drag and drop:");
		System.out.println("Source element: " + sourceText + " at index " + updatedSourceIndex);
		System.out.println("Target element: " + targetText + " at index " + updatedTargetIndex);
		
		// Verify that the order hasn't changed (elements should be in the same position)
		// If allowMoveNodes=false, the order should not change
		assertEquals("When allowMoveNodes=false, the order should not change after drag and drop attempt", 
				sourceIndex, updatedSourceIndex);
		assertEquals("When allowMoveNodes=false, the order should not change after drag and drop attempt", 
				targetIndex, updatedTargetIndex);
	}
	
	private int getElementIndex(WebElement element) {
		// Get the parent element
		WebElement parent = (WebElement) ((JavascriptExecutor) getDriver()).executeScript(
				"return arguments[0].parentNode;", element);
		
		// Get all child elements of the same type
		List<WebElement> children = parent.findElements(By.xpath("./*[local-name()='" + element.getTagName() + "']"));
		
		// Find the index of the element
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).equals(element)) {
				return i;
			}
		}
		
		return -1; // Element not found
	}
}
