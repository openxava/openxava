package org.openxava.web.servlets;

import javax.servlet.annotation.*;

@WebServlet(
	urlPatterns = "/dwr/*",
	loadOnStartup = 12,
	initParams = {
		@WebInitParam(name = "debug", value = "false"),
		@WebInitParam(name = "crossDomainSessionSecurity", value = "false")
	} 
)
public class DWRServlet extends org.directwebremoting.servlet.DwrServlet {

}
