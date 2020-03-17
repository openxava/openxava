package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.tagext.*;

import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;

/**
 * @since 5.8
 * @author Javier Paniza
 */
public class ActionTagBase extends TagSupport implements IActionTag {
	
	private String action;
	private String argv;
	
	/** @since 5.9 */
	protected boolean isActionAvailable(MetaAction metaAction, String application, String module, HttpServletRequest request) { 
		ModuleManager manager = (ModuleManager) getContext(request).get(application, module, "manager");
		Messages errors = (Messages) request.getAttribute("errors");
		Messages messages = (Messages) request.getAttribute("messages");
		return manager.isActionAvailable(metaAction, errors, messages, getArgv(), request);
	}
	
	protected String getTooltip(MetaAction metaAction) {  
		String description = getActionDescription(metaAction);
		StringBuffer result = new StringBuffer();
		result.append(metaAction.getKeystroke());
		if (result.length() > 0 && !Is.emptyString(description)) result.append(" - ");
		result.append(description);
		return result.toString();
	}
	
	protected String getActionDescription(MetaAction metaAction) {
		return metaAction.getDescription();
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

	private static ModuleContext getContext(HttpServletRequest request) { 
		return (ModuleContext) request.getSession().getAttribute("context");
	}
	
	protected String filterApostrophes(String source) { 
		return source.replace("'", "&#145;");
	}

}
