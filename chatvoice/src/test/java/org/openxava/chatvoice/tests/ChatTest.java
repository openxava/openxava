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
    
    public void testHowManyCustomers() throws Exception {
        goModule("Customer");
        
        sendChatMessage("How many customers are there?");
        
        String response = waitForChatResponse();
        assertTrue("Response should contain '9'", response.contains("9"));
        assertTrue("Response should contain 'customers'", response.toLowerCase().contains("customers"));
        
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
}
