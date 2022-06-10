package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.style.*;

/**
 * @author Javier Paniza
 */

public class ActionTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(ActionTag.class);
	
	private IActionTag actionTag;
	private String action;
	private String argv;
	
	public int doStartTag() throws JspException {
		try {
			if (Is.emptyString(getAction())) {
				actionTag = null; 
				return SKIP_BODY;
			}
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			Style style = (Style) request.getAttribute("style"); 
			MetaAction metaAction = MetaControllers.getMetaAction(getAction());
			if (style.isUseLinkForNoButtonBarAction()) {
				LinkTag linkTag = new LinkTag();
				linkTag.setCssClass(style.getActionLink()); 
				actionTag = linkTag;
			}
			else if (metaAction.hasImage() || metaAction.hasIcon()) {  
				ImageTag imageTag = new ImageTag();
				imageTag.setCssClass(style.getActionImage()); 
				actionTag = imageTag;   
			}
			else if (XavaPreferences.getInstance().isButtonsForNoImageActions()) actionTag = new ButtonTag();
			else {			
				LinkTag linkTag = new LinkTag();
				linkTag.setCssClass(style.getActionLink()); 
				actionTag = linkTag;
			}
			actionTag.setPageContext(pageContext);
			actionTag.setAction(action);
			actionTag.setArgv(argv);
			return actionTag.doStartTag();			
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("action_tag_error", getAction()));
		}		
	}

	public int doAfterBody() throws JspException { 
		return actionTag==null?super.doAfterBody():actionTag.doAfterBody();					
	}

	public int doEndTag() throws JspException { 
		return actionTag==null?super.doEndTag():actionTag.doEndTag();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String string) {
		action = string;		
	}

	public String getArgv() {
		return argv;
	}

	public void setArgv(String string) {
		argv = string;
	}

}