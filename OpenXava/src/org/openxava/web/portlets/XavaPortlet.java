package org.openxava.web.portlets;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.portlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.portlet.*;
import org.apache.commons.logging.*;

import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Allows define an OpenXava as a standard JSR-168 portlet. <p>
 * 
 * You only need to define the OpenXava application and module.<br> 
 * In this way:
 *
 * <pre>
 *  <!-- Portlet Preferences -->
 *  <portlet-preferences>
 *    <preference>
 *      <description>OpenXava application name</description>
 *      <name>Application</name>
 *      <value>MyApplication</value>
 *      <non-modifiable/>
 *    </preference>
 *    <preference>
 *      <description>OpenXava module name</description>
 *      <name>Module</name>
 *      <value>MyModule</value>
 *      <non-modifiable/>
 *    </preference>
 *  </portlet-preferences>
 * </pre>
 *
 * @author  Javier Paniza
 * @author  Guy de Pourtales
 * @author  Yerik Alarcón
 */

public class XavaPortlet extends GenericPortlet {
	
	private static Log log = LogFactory.getLog(XavaPortlet.class);

	/**
	 * Name of portlet preference for OpenXava application. 
	 */
	public static final String PARAM_APPLICATION = "Application";
	
	/**
	 * Name of portlet preference for OpenXava module. 
	 */
	public static final String PARAM_MODULE = "Module";
	
	
	private static Style style;
	private String moduleURL;
	private String application; 
	private String module; 
	
	
	
	public void init(PortletConfig config) throws PortletException {
		super.init(config);		
		this.application = config.getInitParameter(PARAM_APPLICATION);
		this.module = config.getInitParameter(PARAM_MODULE);		
		// Calling directly to module.jsp does not work well in Liferay (see portlet.jsp doc)		
		this.moduleURL = "/WEB-INF/jsp/xava/portlet.jsp?xava.portlet.application=" +		
			application + "&xava.portlet.module=" + module;
	}

	/**
	 * Executes the OpenXava module as defined by the init parameters PARAM_APPLICATION
	 * and PARAM_MODULE.
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		Style.setPotalInstance(getStyle(request)); 
		// setTitle(request, response);	// lost the title on reload inside portal liferay 
		request.getPortletSession().setAttribute(Ids.decorate(application, module, "xava.portlet.uploadActionURL"), response.createActionURL().toString(), PortletSession.APPLICATION_SCOPE); 
		request.setAttribute("xava.upload.fileitems", request.getPortletSession().getAttribute("xava.upload.fileitems", PortletSession.PORTLET_SCOPE));  
		request.setAttribute("xava.upload.error", request.getPortletSession().getAttribute("xava.upload.error", PortletSession.PORTLET_SCOPE)); 
		request.getPortletSession().removeAttribute("xava.upload.fileitems", PortletSession.PORTLET_SCOPE); 
		request.getPortletSession().removeAttribute("xava.upload.error", PortletSession.PORTLET_SCOPE); 
		
		request.getPortletSession().setAttribute("xava.portal.locale", request.getLocale(), PortletSession.APPLICATION_SCOPE);
		
		request.removeAttribute("xava.portal.user");
		request.removeAttribute("xava.portal.userinfo");
		
		setContextPath(request);
		
		Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);			
		if (userInfo != null) {
			UserInfo user = new UserInfo();
			String email = (String) userInfo.get("user.home-info.online.email");			
			if (XavaPreferences.getInstance().isEMailAsUserNameInPortal()) {							
				if (!Is.emptyString(email)) {
					request.getPortletSession().setAttribute("xava.portal.user", email, PortletSession.APPLICATION_SCOPE);
					user.setId(email);
				}			
			}			
			else {
				user.setId(request.getRemoteUser());
			}			
			user.setGivenName((String) userInfo.get("user.name.given"));
			user.setFamilyName((String) userInfo.get("user.name.family"));
			user.setEmail(email);
			user.setJobTitle((String) userInfo.get("user.jobtitle"));
			user.setMiddleName((String) userInfo.get("user.name.middle"));
			user.setNickName((String) userInfo.get("user.name.nickName"));
			user.setBirthDateYear(userInfo.get("user.bdate.ymd.year"));
			user.setBirthDateMonth(userInfo.get("user.bdate.ymd.month"));
			user.setBirthDateDay(userInfo.get("user.bdate.ymd.day"));
			
			request.getPortletSession().setAttribute("xava.portal.userinfo", user, PortletSession.APPLICATION_SCOPE);
		}
		
		
		/*
		 * In Liferay 5.0.1, the MimeHeaders are not correctly dispatched in the JSP request,
		 * so we put the required headers in request attributes
		 */		
		if (request.getClass().getName().equals("com.liferay.portlet.RenderRequestImpl")) {
			try {
				// Implementation tries to resolve the servlet request without a formal dependency to Liferay's libraries
				HttpServletRequest servletRequest = (HttpServletRequest) request.getClass().getMethod("getHttpServletRequest", (Class<?> []) null).invoke(request, (Object []) null); 
				if (servletRequest != null) {
					String userAgent = servletRequest.getHeader("user-agent");
					if (userAgent != null) {
						request.setAttribute("xava.portlet.user-agent", userAgent);
					}
				}
			} catch (IllegalArgumentException e) {
				// Do nothing and assume that the headers will be resolved normally
			} catch (SecurityException e) {
//				 Do nothing and assume that the headers will be resolved normally
			} catch (IllegalAccessException e) {
//				 Do nothing and assume that the headers will be resolved normally
			} catch (InvocationTargetException e) {
//				 Do nothing and assume that the headers will be resolved normally
			} catch (NoSuchMethodException e) {
//				 Do nothing and assume that the headers will be resolved normally
			}
		}
		
		PortletContext context = getPortletContext();
		PortletRequestDispatcher rd = context.getRequestDispatcher(moduleURL);		
		rd.include(request, response);		
	}
		
	private void setContextPath(RenderRequest request) {		
		if (isJetspeed22(request)) {	
			request.setAttribute("xava.contextPath", "/" + application);  
		}		
	}
	
	private boolean isJetspeed22(PortletRequest request) { 
		return request.getPortalContext().getPortalInfo().toLowerCase().contains("jetspeed/2.2");
	}

	public void processAction(ActionRequest request, ActionResponse response) throws PortletException {
		propagateParameters(request, response);
		
		PortletMode mode = request.getPortletMode();
		if (mode.equals(PortletMode.EDIT)) {
			response.setPortletMode(PortletMode.VIEW);
		}		
		
		processMultipartForm(request);
		
		
	}

	private void processMultipartForm(ActionRequest request) {
		String contentType = request.getContentType();		
		if (contentType != null && contentType.indexOf("multipart/form-data") >= 0) {
			try {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(1000000);		
				PortletFileUpload upload = new PortletFileUpload(factory);
				List fileItems = upload.parseRequest(request);					
				request.getPortletSession().setAttribute("xava.upload.fileitems", fileItems, PortletSession.PORTLET_SCOPE);  
				request.getPortletSession().removeAttribute("xava.upload.error", PortletSession.PORTLET_SCOPE);  
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				request.getPortletSession().removeAttribute("xava.upload.fileitems", PortletSession.PORTLET_SCOPE); 
				request.getPortletSession().setAttribute("xava.upload.error", "upload_error", PortletSession.PORTLET_SCOPE); 				
			}				
		}
	} 	
		
	private void propagateParameters(ActionRequest request, ActionResponse response) {
		// This is needed as indicated in section 11.1.1 of JSR-168
		
		for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
			 String name = (String) en.nextElement();
			 String [] values = request.getParameterValues(name);			 
			 for (int i=0; i<values.length; i++) {
				 if ("".equals(values[i])) values[i] = " "; // Jetspeed 2.1.2 does not like empty string
			 }			 
			 response.setRenderParameter(name, values);			 
		 }
	}

	private Style getStyle(RenderRequest request) {
		try{
			if (style == null) {
				String styleClass = "";
				XavaPreferences preferences = XavaPreferences.getInstance();
				
				// Maybe moving this to a XML file (as style-portal.xml) could be
				// a good idea
				String portal = request.getPortalContext().getPortalInfo().toLowerCase();
				if (portal.indexOf("liferay") >= 0) {
					if (portal.indexOf("4.1.") >= 0 || portal.indexOf("4.2.") >= 0) styleClass = preferences.getLiferay41StyleClass();
					else if (portal.indexOf("4.3.") >= 0 || portal.indexOf("4.4.") >= 0 || portal.indexOf("5.0.") >= 0) styleClass = preferences.getLiferay43StyleClass();
					else if (portal.indexOf("5.1.") >= 0 || portal.indexOf("5.2.") >= 0) styleClass = preferences.getLiferay51StyleClass();
					else styleClass = preferences.getLiferay6StyleClass();
				}
				else if (portal.indexOf("websphere portal/6.1") >= 0) styleClass = preferences.getWebSpherePortal61StyleClass();
				else if (portal.indexOf("websphere portal") >= 0) styleClass = preferences.getWebSpherePortal8StyleClass();
				else if (portal.indexOf("jetspeed") >= 0) styleClass = preferences.getJetSpeed2StyleClass();
				else style = Style.getInstance();
				
				if (style == null) style = (Style) XObjects.execute(Class.forName(styleClass), "getInstance"); 
				
				style.setInsidePortal(true); 				
			}			
		}
		catch(Exception ex){
			ex.printStackTrace();
			style = Style.getInstance();
		}
		
		return style;
	}
	
	/**
	 * sets the portlet-title
	 */
	private void setTitle(RenderRequest request, RenderResponse response){ 
		String title = null;
		try {
			ModuleContext context = (ModuleContext) request.getPortletSession().getAttribute("context", PortletSession.APPLICATION_SCOPE);
			if (context == null) return;
			ModuleManager moduleManager = (ModuleManager)context.get(application, module, "manager");
			title = moduleManager.getModuleDescription();
		} 
		catch (Exception ex) {			
			log.warn(XavaResources.getString("portlet_title_warning")); // We don't show the ex, because this happens always the very first time the app is executed 
		}
		// title will only be set if no Exception occurs	
		if(title != null){
			response.setTitle(title);
		}
	}


	
}
