package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;


/**
 * @author Javier Paniza
 */

public class ImageTag extends ActionTagBase { 
	
	private static Log log = LogFactory.getLog(ImageTag.class);
	
	private String cssClass;
	private String cssStyle;
	
	
	public int doStartTag() throws JspException {
		try {	
			if (Is.emptyString(getAction())) { 
				return SKIP_BODY;
			}
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			MetaAction metaAction = MetaControllers.getMetaAction(getAction());
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			if (!isActionAvailable(metaAction, application, module, request)) {
				return SKIP_BODY;
			}
			Style style = (Style) request.getAttribute("style");
			pageContext.getOut().print("<input name='");
			pageContext.getOut().print(Ids.decorate(application, module, "action." + getAction())); 
			pageContext.getOut().println("' type='hidden'/>");			
			pageContext.getOut().print("<a ");
			if (Is.emptyString(getArgv())) { 
				pageContext.getOut().print("id='");
				pageContext.getOut().print(Ids.decorate(application, module, getAction())); 
				pageContext.getOut().println("'");
			}
			if (!Is.emptyString(getCssClass())) {
				pageContext.getOut().print(" class='");
				pageContext.getOut().print(getCssClass());
				pageContext.getOut().print("'");	
			}
			if (!Is.emptyString(getCssStyle())) {
				pageContext.getOut().print(" style='");
				pageContext.getOut().print(getCssStyle());
				pageContext.getOut().print("'");	
			}			
			pageContext.getOut().print(" title='");
			pageContext.getOut().print(filterApostrophes(getTooltip(metaAction))); 
			pageContext.getOut().print("'");
			if (metaAction.isLosesChangedData()) pageContext.getOut().print(" href=\"javascript:openxava.executeActionConfirmLosesChangedData(");
			else pageContext.getOut().print(" href=\"javascript:openxava.executeAction(");
			pageContext.getOut().print("'");				
			pageContext.getOut().print(application);
			pageContext.getOut().print("', '");
			pageContext.getOut().print(module);
			pageContext.getOut().print("', ");									
			pageContext.getOut().print("'");
			if (!Is.empty(getArgv())) pageContext.getOut().print(filterApostrophes(metaAction.getConfirmMessage(request, getArgv())));	
			else pageContext.getOut().print(filterApostrophes(metaAction.getConfirmMessage(request)));			
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
			if (metaAction.hasIcon() && (style.isUseIconsInsteadOfImages() || !metaAction.hasImage())) {  
				pageContext.getOut().print("<i class='mdi mdi-");
				pageContext.getOut().print(metaAction.getIcon());
				pageContext.getOut().print("'></i>");  
			}
			else {
				pageContext.getOut().print("<img src='");
				pageContext.getOut().print(request.getContextPath() + "/" + style.getImagesFolder() + "/"+ metaAction.getImage());
				pageContext.getOut().println("'");
				pageContext.getOut().print("\talt='");
				pageContext.getOut().print(metaAction.getKeystroke() + " - " +  metaAction.getDescription(request));
				pageContext.getOut().println("'"); 
				pageContext.getOut().print("\tborder='0' align='absmiddle'/>");				
			}
			pageContext.getOut().print("</a>");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("image_tag_error"));				
		}
		return SKIP_BODY;
	}
	
	protected String getActionDescription(MetaAction metaAction) { 
		String description = metaAction.getDescription();
		return Is.emptyString(description)?metaAction.getLabel():description;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getCssStyle() {
		return cssStyle;
	}

}