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
	public void onMessage(String message, Session session) {
		try {
			log.info("Received message: " + message);
			
			if (Is.emptyString(message)) {
				session.getBasicRemote().sendText("Error: Message is required");
				return;
			}
			
			// Obtener o crear el asistente para esta sesión
			String sessionId = session.getId();
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
				var model = OpenAiChatModel.builder()
					.apiKey(apiKey)
					// .modelName("gpt-4o-mini")
					.modelName("gpt-5-mini")
					.build();
				
				// Crear memoria de chat para esta sesión (mantiene últimos 20 mensajes)
				ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
				chatMemories.put(sessionId, chatMemory);
				
				// Crear asistente con tools genéricos y memoria
				ModuleContext context = new ModuleContext();
				assistant = AiServices.builder(Assistant.class)
					.chatModel(model)
					.chatMemory(chatMemory)
					.tools(new EntityTools(context, httpSession, "chatvoice"))
					.build();
				
				assistants.put(sessionId, assistant);
			}
			
			// Procesar el mensaje con el asistente (que tiene memoria)
			String response = assistant.chat(message);
			
			// Convertir markdown a HTML
			String htmlResponse = markdownToHtml(response);
			
			// Send response back to client
			session.getBasicRemote().sendText(htmlResponse);
			
		} catch (IOException e) {
			log.error("Error sending message", e);
		} catch (Exception e) {
			log.error("Error processing AI message", e);
			try {
				session.getBasicRemote().sendText("Error: " + e.getMessage());
			} catch (IOException ioe) {
				log.error("Error sending error message", ioe);
			}
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		String sessionId = session.getId();
		sessions.remove(session);
		// Limpiar memoria y asistente de esta sesión
		chatMemories.remove(sessionId);
		assistants.remove(sessionId);
		log.info("WebSocket closed: " + sessionId);
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
