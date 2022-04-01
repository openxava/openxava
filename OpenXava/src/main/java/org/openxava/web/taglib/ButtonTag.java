package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

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
			pageContext.getOut().print(" class='");
			Style style = (Style) request.getAttribute("style");
			pageContext.getOut().print(style.getButton());
			if (metaAction.isLosesChangedData()) pageContext.getOut().print("'\tonclick='openxava.executeActionConfirmLosesChangedData(");
			else pageContext.getOut().print("'\tonclick='openxava.executeAction(");
			pageContext.getOut().print('"');				
			pageContext.getOut().print(application);
			pageContext.getOut().print('"');
			pageContext.getOut().print(", ");
			pageContext.getOut().print('"');				
			pageContext.getOut().print(module);
			pageContext.getOut().print('"');
			pageContext.getOut().print(", ");			
			pageContext.getOut().print('"');				
			pageContext.getOut().print(filterApostrophes(metaAction.getConfirmMessage(request))); 
			pageContext.getOut().print('"');
			pageContext.getOut().print(", ");
			pageContext.getOut().print(metaAction.isTakesLong());
			pageContext.getOut().print(", \"");
			pageContext.getOut().print(getAction());
			pageContext.getOut().print('"');
			if (!Is.emptyString(getArgv())) { 
				pageContext.getOut().print(", \"");
				pageContext.getOut().print(getArgv());
				pageContext.getOut().print('"');
			}
			if (metaAction.inNewWindow()) {
				if (Is.emptyString(getArgv())) {
					pageContext.getOut().print(", undefined, undefined, undefined, true");
				}
				else {
					pageContext.getOut().print(", undefined, undefined, true");
				}
			}
			pageContext.getOut().print(")' value='");
			pageContext.getOut().print(filterApostrophes(metaAction.getLabel(request))); 
			pageContext.getOut().println("'/>");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("button_tag_error"));				
		}
		return SKIP_BODY;
	}
	
}