package org.openxava.chat;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.openxava.controller.ModuleContext;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;
import org.openxava.util.XavaPreferences;

import com.openxava.naviox.impl.MetaModuleFactory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

/**
 * WebSocket endpoint for chat functionality.
 * 
 * @author Javier Paniza
 */
@ServerEndpoint(value = "/chat-ws", configurator = GetHttpSessionConfigurator.class)
public class ChatEndpoint {
	
	interface Assistant {
		@SystemMessage("""
			You are a helpful assistant for an OpenXava business application.
			
			IMPORTANT RULES:
			1. ALWAYS remember the user's original question throughout the conversation.
			2. Before loading data, THINK about what you really need. Don't load all entities - only the ones required to answer the question.
			3. For aggregation questions (max, min, sum, count, most, least), use findEntitiesByCondition with appropriate conditions instead of loading all data.
			4. Keep your responses concise and focused on answering the user's question.
			5. CRITICAL: When user asks to show/display/filter data, ALWAYS call getCurrentModule() first to check which module they are viewing. If they are viewing the SAME entity they ask about, use filterList. The user may have changed modules since the last message.
			""")
		String chat(String userMessage);
	}
	
	private static final Log log = LogFactory.getLog(ChatEndpoint.class);
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
	private static Map<String, ChatMemory> chatMemories = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Assistant> assistants = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, EntityTools> entityToolsMap = Collections.synchronizedMap(new HashMap<>());
	private HttpSession httpSession;
	private static final Parser markdownParser = Parser.builder().build();
	private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
	
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
			
			// Handle new conversation command
			if ("__NEW_CONVERSATION__".equals(rawMessage)) {
				String sessionId = httpSession.getId();
				chatMemories.remove(sessionId);
				assistants.remove(sessionId);
				entityToolsMap.remove(sessionId);
				log.info("Chat memory cleared for session: " + sessionId);
				return;
			}
			
			// Get or create the assistant for this HTTP session (persists between module changes)
			String message = rawMessage;
			String sessionId = httpSession.getId();
			Assistant assistant = assistants.get(sessionId);
			
			if (assistant == null) {
				// Get API key from system properties or environment
				String apiKey = System.getProperty("openai.apiKey");
				if (Is.emptyString(apiKey)) {
					apiKey = System.getenv("OPENAI_API_KEY");
				}
				if (Is.emptyString(apiKey)) {
					session.getBasicRemote().sendText("Error: OpenAI API key not configured");
					return;
				}
				
				// Create OpenAI chat model
				String modelName = XavaPreferences.getInstance().getChatModelName();
				var modelBuilder = OpenAiChatModel.builder()
					.apiKey(apiKey)
					.modelName(modelName);
				if (XavaPreferences.getInstance().isChatPriorityServiceTier()) {
					modelBuilder.serviceTier("priority");
				}
				var model = modelBuilder.build();
				
				// Create chat memory for this session (keeps last 50 messages)
				// 50 messages to prevent tool calls with large responses from displacing the original question
				ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(50);
				chatMemories.put(sessionId, chatMemory);
				
				// Create assistant with generic tools and memory
				// Get the existing ModuleContext from the session (don't create a new one)
				ModuleContext moduleContext = (ModuleContext) httpSession.getAttribute("context");
				if (moduleContext == null) {
					moduleContext = new ModuleContext();
					httpSession.setAttribute("context", moduleContext);
				}
				String application = MetaModuleFactory.getApplication();
				EntityTools entityTools = new EntityTools(moduleContext, httpSession, application);
				entityToolsMap.put(sessionId, entityTools);
				assistant = AiServices.builder(Assistant.class)
					.chatModel(model)
					.chatMemory(chatMemory)
					.tools(entityTools)
					.build();
				
				assistants.put(sessionId, assistant);
			}
			
			// Process the message with the assistant (which has memory)
			String response = assistant.chat(message);
			
			// Convert markdown to HTML
			String htmlResponse = markdownToHtml(response);
			
			// Send response back to client
			session.getBasicRemote().sendText(htmlResponse);
			
			// Check if filter list is needed
			EntityTools entityTools = entityToolsMap.get(sessionId);
			if (entityTools != null) {
				Map<String, String> filterValues = entityTools.consumePendingFilterValues();
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
				else if (entityTools.consumeRefreshUINeeded()) {
					session.getBasicRemote().sendText("__REFRESH_UI__");
				}
			}
			
		} catch (IOException e) {
			log.error("Error sending message", e);
		} catch (Exception e) {
			log.error("Error processing AI message", e);
			String sessionId = httpSession.getId();
			// If the error is due to orphan tool_calls in memory, clear and retry
			if (e.getMessage() != null && e.getMessage().contains("tool_call") && 
				e.getMessage().contains("did not have response messages")) {
				log.info("Detected orphan tool_calls in memory, clearing session and retrying: " + sessionId);
				chatMemories.remove(sessionId);
				assistants.remove(sessionId);
				entityToolsMap.remove(sessionId);
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
	 * Converts markdown to HTML using commonmark.
	 */
	private String markdownToHtml(String markdown) {
		var document = markdownParser.parse(markdown);
		return htmlRenderer.render(document);
	}
	
}
