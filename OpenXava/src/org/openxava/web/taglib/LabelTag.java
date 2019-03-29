package org.openxava.web.taglib;

import java.util.Locale;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * JSP Tag <code>label</code> to get a translated label with the OpenXava translation mechanism. 
 * Typical usage: <code>&lt;xava:label key="depth" /&gt;</code>
 * 
 * @author Florian Hof
 */

public class LabelTag extends TagSupport {
	
	private static final long serialVersionUID = -7655080937898076815L;
	private static Log log = LogFactory.getLog(LabelTag.class);
	
	private String key;

	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			Locale locale = XavaResources.getLocale(request);
			pageContext.getOut().print(Labels.get(getKey(), locale)); 
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("message_tag_error", getKey()));				
		}
		return SKIP_BODY;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String string) {
		key = string;
	}

}