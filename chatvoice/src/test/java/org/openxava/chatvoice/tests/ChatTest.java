package org.openxava.chatvoice.tests;

import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * Tests for the chat functionality.
 * 
 * @author Javier Paniza
 */
public class ChatTest extends WebDriverTestBase {

    @Override
    protected boolean isHeadless() {
        return true;
    }

    @Override
    public void tearDown() throws Exception {
        if (isHeadless()) {
            super.tearDown();
        }
    }

    public ChatTest(String testName) {
        super(testName);
    }
    
    public void testConversationPersistenceAndPanelState() throws Exception {
        goModule("Customer");
        
        assertChatPanelHidden();
        openChatPanel();
        assertChatPanelVisible();

        sendChatMessage("How many customers are there?");
        String response = waitForChatResponse();
        assertTrue("Response should contain '9'", response.contains("9"));
        assertTrue("Response should contain 'customers'", response.toLowerCase().contains("customers"));
        
        goModule("Product");
        assertChatPanelVisible();
        
        assertChatPanelContains("How many customers are there?");
        
        sendChatMessage("List them");
        
        response = waitForChatResponse();
        assertTrue("Response should contain 'Wim Mertens'", response.contains("Wim Mertens"));
        assertTrue("Response should contain 'Xavier Corcobado'", response.contains("Javier Corcobado"));
        assertTrue("Response should contain 'Juanito Valderrama'", response.contains("Juanito Valderrama"));
        assertTrue("Response should contain 'John Cage'", response.contains("John Cage"));
        assertTrue("Response should contain 'Bill Gates'", response.contains("Bill Gates"));
        assertTrue("Response should contain 'Marissa Mayer'", response.contains("Marissa Mayer"));
        assertTrue("Response should contain 'Juan Antonio Cabrera López'", response.contains("Juan Antonio Cabrera López"));
        assertTrue("Response should contain 'Carlos Ann'", response.contains("Carlos Ann"));
        assertTrue("Response should contain 'Luigi Nono'", response.contains("Luigi Nono"));
        
        clickNewConversation();
        assertChatPanelNotContains("How many customers are there?");
        
        sendChatMessage("Show me the details of the first one");
        
        response = waitForChatResponse();
        assertFalse("Response should NOT contain 'Wim Mertens'", response.contains("Wim Mertens"));
        
        closeChatPanel();
        assertChatPanelHidden();
        
        goModule("Customer");
        assertChatPanelHidden();
        
        openChatPanelWithShowButton();
        assertChatPanelVisible();
    }
    
    protected void openChatPanel() throws Exception {
        WebDriver driver = getDriver();
        WebElement chatButton = driver.findElement(By.id("module_header_chat_button"));
        if (chatButton.isDisplayed()) {
            chatButton.click();
            Thread.sleep(300); // Wait for animation
        }
    }
    
    protected void sendChatMessage(String message) throws Exception {
        openChatPanel();
        
        WebDriver driver = getDriver();
        WebElement chatInput = driver.findElement(By.id("chatInput"));
        chatInput.sendKeys(message);
        
        WebElement sendBtn = driver.findElement(By.id("chatSendBtn"));
        sendBtn.click();
    }
    
    protected String waitForChatResponse() throws Exception {
        WebDriver driver = getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.ignoring(StaleElementReferenceException.class);
        
        // Wait for typing indicator to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("typingIndicator")));
        
        // Get the last assistant message
        List<WebElement> assistantMessages = driver.findElements(
            By.cssSelector("#chatMessages .ox-chat-message.assistant .ox-chat-message-content"));
        
        if (assistantMessages.isEmpty()) {
            throw new RuntimeException("No assistant response found");
        }
        
        return assistantMessages.get(assistantMessages.size() - 1).getText();
    }
    
    protected void assertChatPanelContains(String text) throws Exception {
        openChatPanel();
        WebDriver driver = getDriver();
        WebElement chatMessages = driver.findElement(By.id("chatMessages"));
        String content = chatMessages.getText();
        assertTrue("Chat panel should contain '" + text + "'", content.contains(text));
    }
    
    protected void assertChatPanelNotContains(String text) throws Exception {
        openChatPanel();
        WebDriver driver = getDriver();
        WebElement chatMessages = driver.findElement(By.id("chatMessages"));
        String content = chatMessages.getText();
        assertFalse("Chat panel should NOT contain '" + text + "'", content.contains(text));
    }
    
    protected void clickNewConversation() throws Exception {
        openChatPanel();
        WebDriver driver = getDriver();
        WebElement newConversationBtn = driver.findElement(By.id("chatNewConversationBtn"));
        newConversationBtn.click();
        Thread.sleep(300);
    }
    
    protected void closeChatPanel() throws Exception {
        WebDriver driver = getDriver();
        WebElement hideButtonIcon = driver.findElement(By.cssSelector("#chat_panel_hide i"));
        hideButtonIcon.click();
        Thread.sleep(300);
    }
    
    protected void openChatPanelWithShowButton() throws Exception {
        WebDriver driver = getDriver();
        WebElement showButton = driver.findElement(By.id("chat_panel_show"));
        if (showButton.isDisplayed()) {
            showButton.click();
            Thread.sleep(300);
        }
    }
    
    protected void assertChatPanelVisible() throws Exception {
        WebDriver driver = getDriver();
        WebElement chatPanel = driver.findElement(By.id("chat_panel"));
        assertTrue("Chat panel should be visible", chatPanel.isDisplayed());
        
        WebElement headerButton = driver.findElement(By.id("module_header_chat_button"));
        assertFalse("module_header_chat_button should be hidden when panel is visible", headerButton.isDisplayed());
        
        WebElement showButton = driver.findElement(By.id("chat_panel_show"));
        assertFalse("chat_panel_show should be hidden when panel is visible", showButton.isDisplayed());
        
        WebElement hideButton = driver.findElement(By.id("chat_panel_hide"));
        assertTrue("chat_panel_hide should be visible when panel is visible", hideButton.isDisplayed());
    }
    
    public void testTabBaseConditionAppliedToChat_recordsNoInListRecognized_multilingual() throws Exception {
        goModule("Invoice");
        
        setConditionValue("2025", 0);
        execute("List.filter");
        assertListRowCount(3);
        
        sendChatMessage("Dime de que años tengo facturas");
        String response = waitForChatResponse();
        assertTrue("Response should contain '2021'", response.contains("2021"));
        assertTrue("Response should contain '2022'", response.contains("2022"));
        assertTrue("Response should contain '2023'", response.contains("2023"));
        assertTrue("Response should contain '2024'", response.contains("2024"));
        assertTrue("Response should contain '2025'", response.contains("2025"));
        assertFalse("Response should NOT contain '2020'", response.contains("2020"));
    }
    
    public void testUseJustUpdatedData_consultFieldsNotShownInList_queryDataFromAModuleNotOpenedYet() throws Exception {
        goModule("Customer");
        assertListRowCount(9);
        
        sendChatMessage("How many customers do I have?");
        String response = waitForChatResponse();
        assertTrue("Response should contain '9'", response.contains("9"));
        
        execute("CRUD.new");
        setValue("number", "66");
        setValue("name", "Test Customer");
        execute("CRUD.save");
        execute("Mode.list");
        assertListRowCount(10);

        sendChatMessage("How many customers do I have now?"); // The "now" is need so the LLM does not use its own memory
        response = waitForChatResponse();
        assertTrue("Response should contain '10'", response.contains("10"));
        
        execute("CRUD.deleteRow", "row=9");
        assertListRowCount(9);

        // Address is not displayed in list, but we can ask for it
        sendChatMessage("Give me the address of customer number 2");
        response = waitForChatResponse();
        assertTrue("Response should contain 'Calle de la Unión'", response.contains("Calle de la Unión"));

        // Products is other module, not opened yet, but we can ask for its data
        sendChatMessage("How many products do we have registered in the system?");
        response = waitForChatResponse();
        assertTrue("Response should contain '11'", response.contains("11"));
    }
    
    public void testAccessElementCollectionData() throws Exception {
        goModule("Invoice");
        
        sendChatMessage("Tell me what products have been invoiced in invoice 2021/1");
        String response = waitForChatResponse();
        assertTrue("Response should contain 'XavaPro Enterprise'", response.contains("XavaPro Enterprise"));
        assertTrue("Response should contain 'Aprende OpenXava con muchos ejemplos'", response.contains("Aprende OpenXava con ejemplos"));
        
        clickNewConversation();
        
        sendChatMessage("What is the product that has been invoiced the most in all history? Inform me only of the product description and the total number of units sold.");
        response = waitForChatResponse();
        assertTrue("Response should contain 'Aprende OpenXava con muchos ejemplos'", response.contains("Aprende OpenXava con ejemplos"));
        assertTrue("Response should contain '142'", response.contains("142"));
    }
    
    public void testModifyData() throws Exception {
        assertModifyNumberFromListAndDetail();
        assertModifyDate();
        assertModifyOnlyEditableFields();
    }
    
    private void assertModifyNumberFromListAndDetail() throws Exception {
        goModule("Product");
        
        assertValueInList(9, 1, "BMW 330i");
        assertValueInList(9, 2, "47,000.00");
        
        sendChatMessage("Update the price of the BMW 330i to 51k");
        waitForChatResponse();
        
        assertValueInList(9, 2, "51,000.00");
        
        execute("List.viewDetail", "row=9");
        assertValue("unitPrice", "51,000.00");
        
        clickNewConversation();
        sendChatMessage("Set the price to 47000");
        waitForChatResponse();
        
        assertValue("unitPrice", "47,000.00");
    }
    
    private void assertModifyDate() throws Exception {
        goModule("Invoice");
        clickNewConversation();
        
        assertValueInList(0, 0, "2021");
        assertValueInList(0, 1, "1");
        assertValueInList(0, 2, "6/13/2021");
        
        sendChatMessage("Cambia la fecha de la factura 2021/1 al 15 de junio del 2021");
        waitForChatResponse();
        
        assertValueInList(0, 2, "6/15/2021");
        
        sendChatMessage("Change the date of invoice 2021/1 to June 13, 2021");
        waitForChatResponse();

        assertValueInList(0, 2, "6/13/2021");
    }
    
    private void assertModifyOnlyEditableFields() throws Exception {
        goModule("Customer");
        clickNewConversation();
        
        assertValueInList(0, 0, "1");
        assertValueInList(0, 1, "Wim Mertens");
        assertValueInList(0, 2, "");
        assertValueInList(0, 3, "Boomgaardstraat, 17");
        assertValueInList(0, 4, "Neerpelt");
        assertValueInList(0, 5, "Belgium");
        
        sendChatMessage("Set the address of customer 1 to 'Main Street, 100', the city to 'Brussels' and the country to 'Spain'");
        waitForChatResponse();
        
        assertValueInList(0, 3, "Main Street, 100");
        assertValueInList(0, 4, "Neerpelt");
        assertValueInList(0, 5, "Belgium");
        
        sendChatMessage("Set the address of customer 1 to 'Boomgaardstraat, 17'");
        waitForChatResponse();
        
        assertValueInList(0, 3, "Boomgaardstraat, 17");
    }
    
    protected void assertChatPanelHidden() throws Exception {
        WebDriver driver = getDriver();
        WebElement chatPanel = driver.findElement(By.id("chat_panel"));
        assertFalse("Chat panel should be hidden", chatPanel.isDisplayed());
        
        WebElement headerButton = driver.findElement(By.id("module_header_chat_button"));
        assertTrue("module_header_chat_button should be visible when panel is hidden", headerButton.isDisplayed());
        
        WebElement showButton = driver.findElement(By.id("chat_panel_show"));
        assertTrue("chat_panel_show should be visible when panel is hidden", showButton.isDisplayed());
        
        WebElement hideButton = driver.findElement(By.id("chat_panel_hide"));
        assertFalse("chat_panel_hide should be hidden when panel is hidden", hideButton.isDisplayed());
    }
    
    public void testFilterList() throws Exception {
        assertFilterListInModule();
        assertFilterListInDetailModeReturnsInChat();
        assertFilterListFromDifferentModuleReturnsInChat();
        assertFilterByMultipleFields();
        assertFilterByDate();
        assertFilterWithComparators();
    }
    
    private void assertFilterListInModule() throws Exception {
        goModule("Invoice");
        assertListRowCount(10);
        
        // Filter by year - should filter the visible list
        sendChatMessage("Filtra las facturas del año 2025");
        waitForChatResponse();
        Thread.sleep(500); // Wait for filter to apply
        
        assertListRowCount(3);
        assertValueInList(0, 0, "2025");
        assertValueInList(1, 0, "2025");
        assertValueInList(2, 0, "2025");
        
        // Clear filter
        sendChatMessage("Quita el filtro");
        waitForChatResponse();
        Thread.sleep(500);
        
        assertListRowCount(10);
    }
    
    private void assertFilterListInDetailModeReturnsInChat() throws Exception {
        goModule("Invoice");
        clickNewConversation();
        
        // Enter detail mode
        execute("List.viewDetail", "row=0");
        assertValue("year", "2021");
        
        // Ask to filter - should return data in chat since we're in detail mode
        sendChatMessage("Show me invoices from 2024");
        String response = waitForChatResponse();
        
        // Should contain data in the chat response, not filter the list
        assertTrue("Response should contain '2024'", response.contains("2024"));
        assertTrue("Response should contain 'Carlos Ann'", response.contains("Carlos Ann"));
        
        // Go back to list mode
        execute("Mode.list");
    }
    
    private void assertFilterListFromDifferentModuleReturnsInChat() throws Exception {
        goModule("Customer");
        clickNewConversation();
        
        // Ask about invoices while in Customer module - should return in chat
        sendChatMessage("Show me invoices from year 2023");
        String response = waitForChatResponse();
        
        // Should contain invoice data in the chat
        assertTrue("Response should contain '2023'", response.contains("2023"));
        assertTrue("Response should contain 'Marissa Mayer' or 'Carlos Ann'", 
            response.contains("Marissa Mayer") || response.contains("Carlos Ann"));
    }
    
    private void assertFilterByMultipleFields() throws Exception {
        goModule("Invoice");
        clickNewConversation();
        assertListRowCount(10);
        
        // Filter by year and customer
        sendChatMessage("Muestra las facturas del año 2025 del cliente John Cage");
        waitForChatResponse();
        Thread.sleep(500);
        
        assertListRowCount(1);
        assertValueInList(0, 0, "2025");
        assertValueInList(0, 3, "John Cage");
        
        // Clear filter
        sendChatMessage("Limpia el filtro");
        waitForChatResponse();
        Thread.sleep(500);
        
        assertListRowCount(10);
    }
    
    private void assertFilterByDate() throws Exception {
        goModule("Invoice");
        clickNewConversation();
        assertListRowCount(10);
        
        // Filter by specific date
        sendChatMessage("Filter invoices with date 8/13/2024");
        waitForChatResponse();
        Thread.sleep(500);

        assertListRowCount(1);
        assertValueInList(0, 2, "8/13/2024");
        assertValueInList(0, 3, "Carlos Ann");
        //assertValue("conditionValue___2", "8/13/2024"); // Waiting to solve: https://openxava.org/xavaprojects/o/OpenXava/m/Issue?detail=ff80808182a6fae10182f389ede3006d
        
        // Clear and filter by month
        clickNewConversation();
        sendChatMessage("Show invoices from August, from all years");
        waitForChatResponse();
        Thread.sleep(500);
        
        // Should show invoices from August (month 8): 8/13/2024, 8/11/2023, 8/17/2025
        assertListRowCount(3);
        
        // Clear filter
        sendChatMessage("Clear the filter");
        waitForChatResponse();
        Thread.sleep(500);
        
        assertListRowCount(10);
    }
    
    private void assertFilterWithComparators() throws Exception {
        goModule("Invoice");
        clickNewConversation();
        assertListRowCount(10);
        
        // Filter with greater than comparator
        sendChatMessage("Muestra las facturas con total mayor a 60000");
        waitForChatResponse();
        Thread.sleep(500);
        
        // Should show invoices with total > 60000: 62920, 64657.56, 61710, 62920, 123420 = 5 invoices
        assertListRowCount(5);
        
        // Clear and filter with less than
        clickNewConversation();
        sendChatMessage("Filtra las facturas con total menor a 20000");
        waitForChatResponse();
        Thread.sleep(500);
        
        // Should show invoices with total < 20000: 15258.10, 6353.71, 17198.94 = 3 invoices
        assertListRowCount(3);
        
        // Clear filter
        sendChatMessage("Limpia el filtro");
        waitForChatResponse();
        Thread.sleep(500);
        
        assertListRowCount(10);
    }
}
