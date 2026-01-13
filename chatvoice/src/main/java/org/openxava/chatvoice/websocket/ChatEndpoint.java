package org.openxava.chatvoice.websocket;

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
import org.openxava.chatvoice.tools.EntityTools;
import org.openxava.controller.ModuleContext;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * WebSocket endpoint for chat functionality.
 * 
 * @author Javier Paniza
 */
@ServerEndpoint(value = "/chat-ws", configurator = GetHttpSessionConfigurator.class)
public class ChatEndpoint {
	
	interface Assistant {
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
			
			// Manejar comando de nueva conversación
			if ("__NEW_CONVERSATION__".equals(rawMessage)) {
				String sessionId = httpSession.getId();
				chatMemories.remove(sessionId);
				assistants.remove(sessionId);
				entityToolsMap.remove(sessionId);
				log.info("Chat memory cleared for session: " + sessionId);
				return;
			}
			
			// Obtener o crear el asistente para esta sesión HTTP (persiste entre cambios de módulo)
			String message = rawMessage;
			String sessionId = httpSession.getId();
			Assistant assistant = assistants.get(sessionId);
			
			if (assistant == null) {
				// Obtener API key desde xava.properties
				String apiKey = System.getProperty("openai.apiKey");
				if (Is.emptyString(apiKey)) {
					apiKey = System.getenv("OPENAI_API_KEY");
				}
				if (Is.emptyString(apiKey)) {
					session.getBasicRemote().sendText("Error: OpenAI API key not configured");
					return;
				}
				
				// Crear modelo de chat de OpenAI
                System.out.println("[ChatEndpoint] gpt-5-mini + priority"); // tmr
				var model = OpenAiChatModel.builder()
					.apiKey(apiKey)
					//.modelName("gpt-4o-mini") // Más rápido pero más tonto
					.modelName("gpt-5-mini")
					//.modelName("gpt-5-nano") // No es más rápido que el mini
					.serviceTier("priority") // Doble de rápido, doble de caro
					.build();
				
				// Crear memoria de chat para esta sesión (mantiene últimos 20 mensajes)
				ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
				chatMemories.put(sessionId, chatMemory);
				
				// Crear asistente con tools genéricos y memoria
				// Obtener el ModuleContext existente de la sesión (no crear uno nuevo)
				ModuleContext moduleContext = (ModuleContext) httpSession.getAttribute("context");
				if (moduleContext == null) {
					moduleContext = new ModuleContext();
					httpSession.setAttribute("context", moduleContext);
				}
				EntityTools entityTools = new EntityTools(moduleContext, httpSession, "chatvoice");
				entityToolsMap.put(sessionId, entityTools);
				assistant = AiServices.builder(Assistant.class)
					.chatModel(model)
					.chatMemory(chatMemory)
					.tools(entityTools)
					.build();
				
				assistants.put(sessionId, assistant);
			}
			
			// Procesar el mensaje con el asistente (que tiene memoria)
			String response = assistant.chat(message);
			
			// Convertir markdown a HTML
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
			// Si el error es por tool_calls huérfanos en la memoria, limpiar y reintentar
			if (e.getMessage() != null && e.getMessage().contains("tool_call") && 
				e.getMessage().contains("did not have response messages")) {
				log.info("Detected orphan tool_calls in memory, clearing session and retrying: " + sessionId);
				chatMemories.remove(sessionId);
				assistants.remove(sessionId);
				entityToolsMap.remove(sessionId);
				try {
					// Reintentar el mensaje con memoria limpia
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
		// No limpiar memoria ni asistente para mantener contexto entre cambios de módulo
		log.info("WebSocket closed: " + session.getId());
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		log.error("WebSocket error for session " + session.getId(), throwable);
	}
	
	/**
	 * Convierte markdown a HTML usando commonmark.
	 */
	private String markdownToHtml(String markdown) {
		var document = markdownParser.parse(markdown);
		return htmlRenderer.render(document);
	}
	
}
