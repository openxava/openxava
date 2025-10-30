package org.openxava.chatvoice.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
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
import org.openxava.chatvoice.tools.EntityTools;
import org.openxava.controller.ModuleContext;
import org.openxava.util.Is;

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
	private HttpSession httpSession;
	
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
			
			// Obtener API key desde xava.properties
			String apiKey = System.getProperty("openai.apiKey");
			apiKey = "demo"; // tmr
			if (Is.emptyString(apiKey)) {
				apiKey = System.getenv("OPENAI_API_KEY");
			}
			if (Is.emptyString(apiKey)) {
				session.getBasicRemote().sendText("Error: OpenAI API key not configured");
				return;
			}
			
			// Crear modelo de chat de OpenAI
			var model = OpenAiChatModel.builder()
				.baseUrl("http://langchain4j.dev/demo/openai/v1")
				.apiKey(apiKey)
				.modelName("gpt-4o-mini")
				.build();
			
			// Crear asistente con tools genéricos
			ModuleContext context = new ModuleContext();
			Assistant assistant = AiServices.builder(Assistant.class)
				.chatModel(model)
				.tools(new EntityTools(context, httpSession, "chatvoice"))
				.build();
			
			// Procesar el mensaje con el asistente
			String response = assistant.chat(message);
			
			// Send response back to client
			session.getBasicRemote().sendText(response);
			
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
		sessions.remove(session);
		log.info("WebSocket closed: " + session.getId());
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		log.error("WebSocket error for session " + session.getId(), throwable);
	}
	
}
