package org.openxava.test.tests.byfeature;

import java.time.*;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * To test lists and list features in collections with Selenium.
 * 
 * @author Javier Paniza
 */
public class ListTest extends WebDriverTestBase {
	
	public ListTest(String testName) {
		super(testName);
	}
	
	private final static String ACTION_PREFIX = "action";

	@Override
	protected boolean isHeadless() { // tmr
		return false;
	}

	protected void tearDown() throws Exception { // tmr
	}
	
	public void testListAndCollection() throws Exception {
		/* tmr 
		goModule("Author");
		assertShowHideFilterInList();
		assertMoveColumns();
		assertRemoveColumnAfterFiltering(); 
		
		assertNoFilterInCollectionByDefault();
		*/

		// TMR ME QUEDÉ POR AQUÍ. EL ARREGLO FUNCIONA EN FF Y CHROME
		// TMR  EL TEST MODIFICADO POR WINDSURF DA ROJO CON EL BUG
		// TMR  Y VERDE SIN ÉL.
		// TMR  ES DECIR ESTÁ HECHO. FALTARÍA PROBAR EL TEST COMPLETO
		// TMR    Y ANTES QUITAR EL FONDO BLANCO DE LA X 
		goModule("Carrier");
		assertEnableDisableCustomizeList(); 
		// tmr assertCustomizeCollection(); 

		/* tmr
		goModule("CustomerWithSection");
		assertCustomizeList();
		assertCustomizeList_addAndResetModule(); 
		
		goModule("Invoice");
		assertRemoveSeveralColumns();
		*/
	}
	
	public void _testListFormatIsSelectable() throws Exception { // tmr
		//has-type tested with CalendarTest
		goModule("City");
		assertTrue(hasClockIcon());
		goModule("Subfamily");
		assertTrue(hasClockIcon());
	}
		
	private void assertNoFilterInCollectionByDefault() throws Exception {
		execute("CRUD.new");		
		assertCollectionFilterNotDisplayed();
		getDriver().findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute("MyGoListMode.list");
		execute("List.viewDetail", "row=1");
		assertCollectionFilterNotDisplayed();
		getDriver().findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute("CRUD.new");
		assertCollectionFilterNotDisplayed();
		getDriver().findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		getDriver().findElement(By.id("ox_openxavatest_Author__hide_filter_humans")).click();
		Thread.sleep(1000);
		assertCollectionFilterNotDisplayed();
	}
	
	private void assertMoveColumns() throws Exception { 
		// To test a specific bug moving columns
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");
		
		showCustomizeControls();
		moveColumn(0, 1);
		assertLabelInList(0, "Biography"); 
		assertLabelInList(1, "Author");
		
		resetModule(getDriver()); 
		assertLabelInList(0, "Biography");
		assertLabelInList(1, "Author");
		
		showCustomizeControls();
		moveColumn(1, 0);
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");		
	}
	
	private void assertRemoveColumnAfterFiltering() throws Exception {
		assertListRowCount(2);
		assertListColumnCount(2);
		setConditionValue("J", 0);
		execute("List.filter");
		assertListRowCount(1);
		showCustomizeControls();
		removeColumn(1);
		assertListRowCount(1);
		assertListColumnCount(1); 
		execute("List.addColumns"); 
		execute("AddColumns.restoreDefault"); 
		assertListColumnCount(2);
		assertListRowCount(1);
		clearListCondition();
	}
	
	private void assertShowHideFilterInList() throws Exception {
		goModule("Author");
		assertFalse(getElementById("show_filter_list").isDisplayed()); 
		assertTrue(getElementById("hide_filter_list").isDisplayed()); 
		assertTrue(getElementById("list_filter_list").isDisplayed());
		getElementById("hide_filter_list").click();
		Thread.sleep(700); 
		assertTrue(getElementById("show_filter_list").isDisplayed());
		assertFalse(getElementById("hide_filter_list").isDisplayed());
		assertFalse(getElementById("list_filter_list").isDisplayed());  
		getElementById("show_filter_list").click();
		Thread.sleep(700); 
		assertFalse(getElementById("show_filter_list").isDisplayed()); 
		assertTrue(getElementById("hide_filter_list").isDisplayed());
		assertTrue(getElementById("list_filter_list").isDisplayed());
	}
	
	private void assertEnableDisableCustomizeList() throws Exception {
		WebElement addColumns = getDriver().findElement(By.id("ox_openxavatest_Carrier__List___addColumns")); 
		WebElement column0 = getDriver().findElement(By.id("ox_openxavatest_Carrier__list_col0"));		
		WebElement moveColumn0 = column0.findElement(By.cssSelector("i[class='xava_handle mdi mdi-cursor-move ui-sortable-handle']"));		
		WebElement removeColumn0 = getDriver().findElement(By.cssSelector(".xava_remove_column[data-column='ox_openxavatest_Carrier__list_col0']"));
		WebElement renameColumn0 = getDriver().findElement(By.cssSelector("a[data-action='List.changeColumnName'][data-argv='property=number']"));
		WebElement column1 = getDriver().findElement(By.id("ox_openxavatest_Carrier__list_col1"));
		WebElement moveColumn1 = column1.findElement(By.cssSelector("i[class='xava_handle mdi mdi-cursor-move ui-sortable-handle']")); 
		WebElement removeColumn1 = getDriver().findElement(By.cssSelector(".xava_remove_column[data-column='ox_openxavatest_Carrier__list_col1']"));
		WebElement renameColumn1 = getDriver().findElement(By.cssSelector("a[data-action='List.changeColumnName'][data-argv='property=name']"));
		
		assertFalse(addColumns.isDisplayed());
		assertFalse(moveColumn0.isDisplayed());		
		assertFalse(removeColumn0.isDisplayed());
		assertFalse(isElementTrulyVisible(renameColumn0));
		assertFalse(moveColumn1.isDisplayed());
		assertFalse(removeColumn1.isDisplayed());
		assertFalse(isElementTrulyVisible(renameColumn1));
		
		WebElement customize = getDriver().findElement(By.id("ox_openxavatest_Carrier__customize_list")); 
		customize.click();
		assertTrue(addColumns.isDisplayed());
		assertTrue(moveColumn0.isDisplayed());
		assertTrue(removeColumn0.isDisplayed());
		assertTrue(isElementTrulyVisible(renameColumn0));
		assertTrue(moveColumn1.isDisplayed());
		assertTrue(removeColumn1.isDisplayed());
		assertTrue(isElementTrulyVisible(renameColumn1));
		
		customize.click();
		Thread.sleep(3000); // It needs time to fade out 
		assertFalse(addColumns.isDisplayed()); 
		assertFalse(moveColumn0.isDisplayed());
		assertFalse(removeColumn0.isDisplayed());
		assertFalse(isElementTrulyVisible(renameColumn0));
		assertFalse(moveColumn1.isDisplayed()); 
		assertFalse(removeColumn1.isDisplayed());
		assertFalse(isElementTrulyVisible(renameColumn1));
	}
	
	private void showCustomizeControls() {
		showCustomizeControls("list");
	}
	
	private void showCustomizeControls(String collection) {
		getDriver().findElement(By.id("ox_openxavatest_" + getModule() + "__customize_" + collection)).click();
	}
	
	private WebElement getElementById(String id) {
		return getDriver().findElement(By.id(Ids.decorate("openxavatest", getModule(), id)));
	}

	private void assertNoAction(String qualifiedAction) {
		String [] action = qualifiedAction.split("\\.");
		String name = "ox_openxavatest_" + getModule() + "__action___" + action[0] + "___" + action[1];
		assertTrue(XavaResources.getString("action_found_in_ui", action), getDriver().findElements(By.name(name)).isEmpty());
	}
	
	private void assertCollectionFilterDisplayed() { 
		assertTrue(getDriver().findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}
	
	private void assertCollectionFilterNotDisplayed() { 
		assertFalse(getDriver().findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}
	
	private void checkRow(String id, String value) throws Exception {
		WebElement checkbox = getDriver().findElement(By.cssSelector("input[value='" + id + ":" + value + "']"));
		checkbox.click();
		wait(getDriver());
	}
	
	private void assertDialog() throws Exception { 
		assertTrue(XavaResources.getString("dialog_must_be_displayed"), getTopDialog() != null); 
	}
	
	private String getTopDialog() throws Exception { 
		int level = 0;
		for (level = 10; level > 0; level--) {
			try {
				WebElement el = getDriver().findElement(By.id(Ids.decorate("openxavatest", getModule(), "dialog" + level)));
				if (el != null && !el.findElements(By.xpath("./*")).isEmpty()) break;
			}
			catch (NoSuchElementException ex) {
			}			
		}
		if (level == 0) return null;
		return "dialog" + level;		
	}
	
	protected void assertActions(String [] expectedActions) throws Exception {
		Collection<String> actionsInForm = getActions();		
		Collection<String> left = new ArrayList<>();		
		for (int i = 0; i < expectedActions.length; i++) {
			String expectedAction = expectedActions[i];
			if (actionsInForm.contains(expectedAction)) {
				actionsInForm.remove(expectedAction);
			}
			else {
				left.add(expectedAction);
			}
		}			

		if (!left.isEmpty()) {
			fail(XavaResources.getString("actions_expected", left));
		}
		if (!actionsInForm.isEmpty()) {
			fail(XavaResources.getString("actions_not_expected", actionsInForm));
		}
	} 
	
	private Collection<String> getActions() throws Exception { 
		String dialog = getTopDialog();
		if (dialog == null) return getActions(getElementById("core"));
		return getActions(getElementById(dialog));		
	}	
	
	private Collection<String> getActions(WebElement el) { 		
		Collection<WebElement> hiddens = getDriver().findElements(By.cssSelector("input[type='hidden']"));
		Set actions = new HashSet();		
		for (WebElement input: hiddens) {
			if (!input.getAttribute("name").startsWith(Ids.decorate("openxavatest", getModule(), ACTION_PREFIX))) continue;
			String actionName = removeActionPrefix(input.getAttribute("name"));
			actions.add(removeActionPrefix(input.getAttribute("name")));
		}	
		return actions;				
	}
		
	private String removeActionPrefix(String action) {
		String bareAction = Ids.undecorate(action);
		return bareAction.substring(ACTION_PREFIX.length() + 1);
	}
	
	private void assertContentTypeForPopup(String expectedContentType) {
		for (String windowHandle : getDriver().getWindowHandles()) {
			getDriver().switchTo().window(windowHandle);
        }
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofMillis(3000));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("embed")));
		String contentType = getDriver().findElement(By.tagName("embed")).getAttribute("type"); // This works for PDF with Chrome
		assertEquals(expectedContentType, contentType);
	}
	
	private boolean hasClockIcon() {
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-clock"));
		return !iconElements.isEmpty();
	}
	
	private boolean isElementTrulyVisible(WebElement element) {
		if (!element.isDisplayed()) {
			return false;
		}
		
		try {
			// Usamos JavaScript para verificar si el elemento es realmente visible
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			
			// Verificar si el elemento tiene dimensiones visibles
			Boolean hasSize = (Boolean) js.executeScript(
				"var rect = arguments[0].getBoundingClientRect();" +
				"return (rect.width > 0 && rect.height > 0);", 
				element);
			
			if (!hasSize) {
				return false;
			}
			
			// Verificar si el elemento está en el viewport
			Boolean isInViewport = (Boolean) js.executeScript(
				"var rect = arguments[0].getBoundingClientRect();" +
				"return (" +
				"    rect.top >= 0 &&" +
				"    rect.left >= 0 &&" +
				"    rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&" +
				"    rect.right <= (window.innerWidth || document.documentElement.clientWidth)" +
				");", 
				element);
				
			if (!isInViewport) {
				return false;
			}
			
			// Verificar si el elemento está oculto por otro elemento
			Boolean isNotCovered = (Boolean) js.executeScript(
				"var el = arguments[0];" +
				"var rect = el.getBoundingClientRect();" +
				"var x = rect.left + rect.width / 2;" +
				"var y = rect.top + rect.height / 2;" +
				"var elementAtPoint = document.elementFromPoint(x, y);" +
				"return (elementAtPoint === el || el.contains(elementAtPoint));",
				element);
				
			return isNotCovered;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void assertRemoveSeveralColumns() throws Exception {
		assertListColumnCount(8); 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");

		showCustomizeControls();
		removeColumn(2);
		assertListColumnCount(7); 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "V.A.T.");
		assertLabelInList(4, "Details count");
		assertLabelInList(5, "Paid");
		assertLabelInList(6, "Importance");
		
		removeColumn(3); // VAT
		assertListColumnCount(6);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "Details count");
		assertLabelInList(4, "Paid");
		assertLabelInList(5, "Importance");
		
		execute("List.filter");
		assertListColumnCount(6);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "Details count");
		assertLabelInList(4, "Paid");
		assertLabelInList(5, "Importance");

		showCustomizeControls();
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");		
	}
}
