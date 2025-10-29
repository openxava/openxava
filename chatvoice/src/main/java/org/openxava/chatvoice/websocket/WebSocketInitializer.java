package org.openxava.chatvoice.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

/**
 * Initializes WebSocket endpoints for embedded Tomcat.
 * 
 * @author Javier Paniza
 */
@WebListener
public class WebSocketInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("WebSocketInitializer: Initializing WebSocket endpoints...");
		
		ServerContainer serverContainer = (ServerContainer) sce.getServletContext()
				.getAttribute("javax.websocket.server.ServerContainer");
		
		if (serverContainer != null) {
			try {
				serverContainer.addEndpoint(ChatEndpoint.class);
				System.out.println("WebSocketInitializer: ChatEndpoint registered successfully at /chat-ws");
			} catch (DeploymentException e) {
				System.err.println("WebSocketInitializer: Failed to register ChatEndpoint");
				e.printStackTrace();
			}
		} else {
			System.err.println("WebSocketInitializer: ServerContainer not found!");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("WebSocketInitializer: Context destroyed");
	}

}
