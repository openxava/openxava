package org.openxava.chat;

import javax.servlet.http.HttpSession;

/**
 * Interface for AI chat service implementations.
 * This interface allows the chat functionality to work with different AI providers.
 * The default implementation requires Java 17 and LangChain4j, available in openxava-7.7-chat-jdk17.
 * 
 * @author Javier Paniza
 * @since 7.7
 */
public interface IChatService {
	
	/**
	 * Process a chat message and return the AI response as HTML.
	 * 
	 * @param sessionId The HTTP session ID for maintaining conversation context
	 * @param httpSession The HTTP session for accessing application context
	 * @param message The user message to process
	 * @return The AI response as HTML, or null if the message was a command
	 * @throws ChatException if there's an error processing the message
	 */
	String chat(String sessionId, HttpSession httpSession, String message) throws ChatException;
	
	/**
	 * Clear the conversation memory for a session.
	 * 
	 * @param sessionId The HTTP session ID
	 */
	void clearMemory(String sessionId);
	
	/**
	 * Get pending filter values after a chat response (for UI filtering).
	 * 
	 * @param sessionId The HTTP session ID
	 * @return Map of filter values, or null if none pending
	 */
	java.util.Map<String, String> consumePendingFilterValues(String sessionId);
	
	/**
	 * Check if UI refresh is needed after a chat response.
	 * 
	 * @param sessionId The HTTP session ID
	 * @return true if refresh is needed
	 */
	boolean consumeRefreshUINeeded(String sessionId);
	
}
