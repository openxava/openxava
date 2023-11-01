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

public class ButtonTag extends ActionTagBase { 
	
	private static Log log = LogFactory.getLog(ButtonTag.class);

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
			pageContext.getOut().print("<input name='");
			pageContext.getOut().print(Ids.decorate(application, module, "action." + getAction())); 
			pageContext.getOut().println("' type='hidden'/>");			
			pageContext.getOut().print("<input type='button' "); 
			if (Is.emptyString(getArgv())) { 
				pageContext.getOut().print("id='"); 
				pageContext.getOut().print(Ids.decorate(application, module, getAction()));
				pageContext.getOut().print("'");
			}
			pageContext.getOut().print(" tabindex='1'"); 
			pageContext.getOut().print(" title='");  
			pageContext.getOut().print(filterApostrophes(getTooltip(metaAction))); 
			pageContext.getOut().print("'");
			if (metaAction.isLosesChangedData()) pageContext.getOut().print(" class='xava_button_loses_changed_data ");
			else pageContext.getOut().print(" class='xava_button ");

			pageContext.getOut().print("' value='");
			pageContext.getOut().print(filterApostrophes(metaAction.getLabel(request))); 
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
			pageContext.getOut().println("'/>");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("button_tag_error"));				
		}
		return SKIP_BODY;
	}
	
}