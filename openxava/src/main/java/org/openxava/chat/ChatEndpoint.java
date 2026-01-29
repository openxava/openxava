package org.openxava.chat;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;
import org.openxava.util.XavaResources;

/**
 * WebSocket endpoint for chat functionality.
 * Uses ServiceLoader to find an IChatService implementation.
 * If no implementation is found (Java 17 module not present), returns a generic message.
 * 
 * @author Javier Paniza
 */
@ServerEndpoint(value = "/chat-ws", configurator = GetHttpSessionConfigurator.class)
public class ChatEndpoint {
	
	private static final Log log = LogFactory.getLog(ChatEndpoint.class);
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
	private static IChatService chatService;
	private static boolean chatServiceInitialized = false;
	private HttpSession httpSession;
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		sessions.add(session);
		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		log.info("WebSocket opened: " + session.getId());
	}
	
	@OnMessage
	public void onMessage(String rawMessage, Session session) {
		long startTime = System.currentTimeMillis();
		try {
			log.info("Received message: " + rawMessage);
			
			if (Is.emptyString(rawMessage)) {
				session.getBasicRemote().sendText("Error: Message is required");
				return;
			}
			
			String sessionId = httpSession.getId();
			
			// Handle new conversation command
			if ("__NEW_CONVERSATION__".equals(rawMessage)) {
				IChatService service = getChatService();
				if (service != null) {
					service.clearMemory(sessionId);
				}
				log.info("Chat memory cleared for session: " + sessionId);
				return;
			}
			
			// Check if chat service is available
			IChatService service = getChatService();
			if (service == null) {
				// No implementation available - return generic message
				String message = XavaResources.getString("chat_requires_java17");
				session.getBasicRemote().sendText("<p>" + message + "</p>");
				return;
			}
			
			// Process the message with the chat service (returns HTML)
			String htmlResponse = service.chat(sessionId, httpSession, rawMessage);
			
			if (htmlResponse != null) {
				// Send response back to client
				session.getBasicRemote().sendText(htmlResponse);
			}
			
			// Check if filter list is needed
			Map<String, String> filterValues = service.consumePendingFilterValues(sessionId);
			if (filterValues != null) {
				StringBuilder json = new StringBuilder("{");
				boolean first = true;
				for (Map.Entry<String, String> entry : filterValues.entrySet()) {
					if (!first) json.append(",");
					json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
					first = false;
				}
				json.append("}");
				session.getBasicRemote().sendText("__FILTER_LIST__:" + json);
			}
			// Check if UI refresh is needed after update
			else if (service.consumeRefreshUINeeded(sessionId)) {
				session.getBasicRemote().sendText("__REFRESH_UI__");
			}
			
		} catch (ChatException e) {
			log.error("Error processing AI message", e);
			String sessionId = httpSession.getId();
			// If the error is due to orphan tool_calls in memory, clear and retry
			if (e.getMessage() != null && e.getMessage().contains("tool_call") && 
				e.getMessage().contains("did not have response messages")) {
				log.info("Detected orphan tool_calls in memory, clearing session and retrying: " + sessionId);
				IChatService service = getChatService();
				if (service != null) {
					service.clearMemory(sessionId);
				}
				try {
					// Retry the message with clean memory
					onMessage(rawMessage, session);
					return;
				} catch (Exception retryEx) {
					log.error("Retry also failed", retryEx);
				}
			}
			try {
				session.getBasicRemote().sendText("Error: " + e.getMessage());
			} catch (IOException ioe) {
				log.error("Error sending error message", ioe);
			}
		} catch (IOException e) {
			log.error("Error sending message", e);
		} catch (Exception e) {
			log.error("Error processing AI message", e);
			try {
				session.getBasicRemote().sendText("Error: " + e.getMessage());
			} catch (IOException ioe) {
				log.error("Error sending error message", ioe);
			}
		} finally {
			XPersistence.commit();
			log.info("onMessage() took " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
		// Don't clear memory or assistant to maintain context between module changes
		log.info("WebSocket closed: " + session.getId());
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		log.error("WebSocket error for session " + session.getId(), throwable);
	}
	
	/**
	 * Gets the chat service implementation using ServiceLoader.
	 * Returns null if no implementation is available.
	 */
	private static synchronized IChatService getChatService() {
		if (!chatServiceInitialized) {
			chatServiceInitialized = true;
			try {
				ServiceLoader<IChatService> loader = ServiceLoader.load(IChatService.class);
				Iterator<IChatService> iterator = loader.iterator();
				if (iterator.hasNext()) {
					chatService = iterator.next();
					log.info("Chat service implementation found: " + chatService.getClass().getName());
				} else {
					log.info("No chat service implementation found. AI chat requires openxava-7.7-chat-jdk17 dependency.");
				}
			} catch (Exception e) {
				log.error("Error loading chat service", e);
			}
		}
		return chatService;
	}
	
	
}
