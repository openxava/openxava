package org.openxava.test.tests;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

public class TreeTest extends WebDriverTestBase{
	
	private WebDriver driver;

	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void testNavigation() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/TreeContainer");
		wait(driver);
		acceptInDialogJS(driver);
		execute(driver, "TreeContainer", "List.viewDetail", "row=0");
		createNewNodeSelecting(driver);
		verifyCreatedNodesAndCheck(driver);
		editNodeWithDoubleClick(driver);
		deleteSelectedNode(driver);
		cutNode(driver);
		execute(driver, "TreeContainer", "Mode.list");
		execute(driver, "TreeContainer", "List.viewDetail", "row=0");
		dragAndDrop(driver);
		
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	private void createNewNodeSelecting(WebDriver driver) throws Exception {
		WebElement childItem2CheckBox = driver.findElement(By.xpath("//a[@id='208108_anchor']/i"));
		childItem2CheckBox.click();
		execute(driver, "TreeContainer", "TreeView.new", "viewObject=xava_view_treeItems");
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description", "A", false);
		WebElement save = driver.findElement(By.id("ox_openxavatest_TreeContainer__TreeView___save"));
		save.click();
		wait(driver);

		WebElement nuevoElemento = driver.findElement(By.xpath("//a[contains(@onclicke, 'TreeView.new') and contains(@onclicke, 'viewObject=xava_view_treeItems')]"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", nuevoElemento); // must clicked manually because the element is not interactable
		Thread.sleep(500); //sometimes need
		Alert alert = driver.switchTo().alert();
		//these alerts only occur in the selenium test, because we clicked on a non-interactable element
	    alert.accept();
	    alert.dismiss();
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description","B", false);
		save = driver.findElement(By.id("ox_openxavatest_TreeContainer__TreeView___save"));
		save.click();
		wait(driver);
	}
	
	private void editNodeWithDoubleClick(WebDriver driver) throws Exception {
		Thread.sleep(500); //sometimes need
		WebElement aElement = driver.findElement(By.id("208113_anchor"));
		Actions actions = new Actions(driver);
		actions.doubleClick(aElement).perform();
		wait(driver);
		
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description", "AA", true);
		WebElement save = driver.findElement(By.id("ox_openxavatest_TreeContainer__TreeView___save"));
		save.click();
		wait(driver);
		
		assertEquals("AA", driver.findElement(By.id("208113_anchor")).getText());
	}
	
	private void verifyCreatedNodesAndCheck(WebDriver driver) throws InterruptedException {
		WebElement childItem2CheckBox = driver.findElement(By.xpath("//a[@id='208108_anchor']/i"));
		childItem2CheckBox.click();
		Thread.sleep(500); //sometimes need
		expandNode(driver, "208108");
		WebElement newNodeB = driver.findElement(By.xpath("//a[@id='208114_anchor']"));
		assertEquals("B", newNodeB.getText());
		WebElement newNodeACheckBox = driver.findElement(By.xpath("//a[@id='208113_anchor']/i"));
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
		WebElement bCheckBox = driver.findElement(By.xpath("//a[@id='208114_anchor']/i"));
		bCheckBox.click();
		execute(driver, "TreeContainer", "CollectionCopyPaste.cut", "viewObject=xava_view_treeItems");
		execute(driver, "TreeContainer", "Mode.list");
		execute(driver, "TreeContainer", "CRUD.new");
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description", "BB", false);
		execute(driver, "TreeContainer", "CollectionCopyPaste.paste", "viewObject=xava_view_treeItems");
		WebElement bElement = driver.findElement(By.id("208114_anchor"));
		assertTrue(bElement.getText().equals("B"));
	}
	
	private void dragAndDrop(WebDriver driver) throws Exception {
		executeDnd(driver, "208111_anchor", "208110_anchor");
		executeDndBetween(driver, "208107_anchor", "208106");
		expandNode(driver, "208110");
		executeDnd(driver, "208112_anchor", "208111_anchor");
		expandNode(driver, "208111");
		executeDnd(driver, "208107_anchor", "208106_anchor");
		driver.navigate().refresh();
		wait(driver);
		assertTrue(isElementInside(driver, "208106", "208107_anchor"));
		assertTrue(isElementInside(driver, "208107", "208110_anchor"));
		assertTrue(isElementInside(driver, "208110", "208111_anchor"));
		assertTrue(isElementInside(driver, "208111", "208112_anchor"));
	}
	
	private void executeDnd(WebDriver driver, String sourceElementId, String targetElementId) throws InterruptedException {
        WebElement sourceElement = driver.findElement(By.id(sourceElementId));
        WebElement targetElement = driver.findElement(By.id(targetElementId));
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
}
