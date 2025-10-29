package org.openxava.chatvoice.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WebSocket endpoint for chat functionality.
 * 
 * @author Javier Paniza
 */
@ServerEndpoint("/chat-ws")
public class ChatEndpoint {
	
	private static final Log log = LogFactory.getLog(ChatEndpoint.class);
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
	
	static {
		System.out.println("ChatEndpoint class loaded!");
	}
	
	public ChatEndpoint() {
		System.out.println("ChatEndpoint instance created!");
	}
	
	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
		System.out.println("WebSocket opened: " + session.getId());
		log.info("WebSocket opened: " + session.getId());
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			log.info("Received message: " + message);
			
			// TODO: Replace with actual AI logic
			// For now, just echo the message
			String response = message;
			
			// Send response back to client
			session.getBasicRemote().sendText(response);
			
		} catch (IOException e) {
			log.error("Error sending message", e);
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
