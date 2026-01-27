package org.openxava.chatvoice.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

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
