package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;


/**
 * @author Javier Paniza
 */

public class LinkTag extends ActionTagBase implements IActionTag {  

	private static Log log = LogFactory.getLog(LinkTag.class);
	
	private String cssClass;
	private String cssStyle;
	private boolean hasBody;
	private boolean available; 
		
	public int doStartTag() throws JspException {		
		try {
			if (Is.emptyString(getAction())) {  
				return EVAL_BODY_INCLUDE; 
			}
			hasBody=false;
			available=true; 
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			MetaAction metaAction = MetaControllers.getMetaAction(getAction());
			
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			
			if (!isActionAvailable(metaAction, application, module, request)) {
				available = false;
				return SKIP_BODY;
			}
						 		
			pageContext.getOut().print("<input name='");
			pageContext.getOut().print(Ids.decorate(application, module, "action." + getAction())); 
			pageContext.getOut().print("' type='hidden'/>");
			
			pageContext.getOut().print("<a ");
			if (Is.emptyString(getArgv())) { 
				pageContext.getOut().print("id='");
				pageContext.getOut().print(Ids.decorate(application, module, getAction())); 
				pageContext.getOut().print("'");
			}
			/* tmr
			if (!Is.emptyString(getCssClass())) {
				pageContext.getOut().print(" class='");
				pageContext.getOut().print(getCssClass());
				pageContext.getOut().print("'");	
			}
			*/
			if (!Is.emptyString(getCssStyle())) {
				// We still add it but ignored because of CSP
				pageContext.getOut().print(" style='");
				pageContext.getOut().print(getCssStyle());
				pageContext.getOut().print("'");	
				log.warn(XavaResources.getString("style_attribute_not_supported_in_tag", "Image", getAction(), getCssStyle()));
			}
			pageContext.getOut().print(" title='");
			pageContext.getOut().print(filterApostrophes(getTooltip(metaAction))); 
			pageContext.getOut().print("'");		
			/*
			if (metaAction.isLosesChangedData()) pageContext.getOut().print(" href=\"javascript:openxava.executeActionConfirmLosesChangedData(");
			else pageContext.getOut().print(" href=\"javascript:openxava.executeAction(");
			pageContext.getOut().print("'");				
			pageContext.getOut().print(request.getParameter("application"));
			pageContext.getOut().print("', '");
			pageContext.getOut().print(request.getParameter("module"));
			pageContext.getOut().print("', ");						
			pageContext.getOut().print("'");
			pageContext.getOut().print(filterApostrophes(metaAction.getConfirmMessage(request))); 
			pageContext.getOut().print("'");
			pageContext.getOut().print(", ");			
			pageContext.getOut().print(metaAction.isTakesLong());
			pageContext.getOut().print(", '");
			pageContext.getOut().print(getAction());
			pageContext.getOut().print("'");
			if (!Is.emptyString(getArgv())) {
				pageContext.getOut().print(", '");
				pageContext.getOut().print(getArgv());
				pageContext.getOut().print("'");
			}
			if (metaAction.inNewWindow()) {
				if (Is.emptyString(getArgv())) {
					pageContext.getOut().print(", undefined, undefined, undefined, true");
				}
				else {
					pageContext.getOut().print(", undefined, undefined, true");
				}
			}
			pageContext.getOut().print(")\">");
			*/
			// tmr ini
			if (metaAction.isLosesChangedData()) pageContext.getOut().print(" class='xava_action_loses_changed_data ");
			else pageContext.getOut().print(" class='xava_action ");
			if (!Is.emptyString(getCssClass())) pageContext.getOut().print(getCssClass());

			pageContext.getOut().print("' value='");
			pageContext.getOut().print(filterApostrophes(metaAction.getLabel(request)));
			pageContext.getOut().print("' data-application='");
			pageContext.getOut().print(application);
			pageContext.getOut().print("' data-module='");
			pageContext.getOut().print(module);
			pageContext.getOut().print("' data-confirm-message='");
			pageContext.getOut().print(filterApostrophes(metaAction.getConfirmMessage(request)));
			pageContext.getOut().print("' data-takes-long='");
			pageContext.getOut().print(metaAction.isTakesLong());
			pageContext.getOut().print("' data-action='");
			pageContext.getOut().print(getAction());
			if (!Is.emptyString(getArgv())) { 
				pageContext.getOut().print("' data-argv='");
				pageContext.getOut().print(getArgv());
			}			
			pageContext.getOut().print("' data-in-new-window='");
			pageContext.getOut().print(metaAction.inNewWindow());
			
			pageContext.getOut().print("'>");
			// tmr fin

			

		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("link_tag_error", getAction()));
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doAfterBody() throws JspException {					
		hasBody = true;
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		if (!available) return super.doEndTag(); 
		try {
			if (!hasBody && !Is.emptyString(getAction())) {
				pageContext.getOut().print(
					MetaControllers.getMetaAction(getAction()).getLabel(
						pageContext.getRequest()));								
			}
			if (!Is.emptyString(getAction())) {
				pageContext.getOut().print("</a>");
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("link_tag_error", getAction()));
		}
		return super.doEndTag();
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	
	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}
	

}