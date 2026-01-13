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
    protected boolean isHeadless() { // tmr
        return false;
    }

    @Override
    public void tearDown() throws Exception { // tmr
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
}
