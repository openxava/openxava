package org.openxava.chat;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

/**
 * Configurator to capture HttpSession during WebSocket handshake.
 * 
 * @author Javier Paniza
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
	
	@Override
	public void modifyHandshake(ServerEndpointConfig config, 
	                            HandshakeRequest request, 
	                            HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		config.getUserProperties().put(HttpSession.class.getName(), httpSession);
	}
	
}
