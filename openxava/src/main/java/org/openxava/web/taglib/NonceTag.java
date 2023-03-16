package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * tmr doc <p>
 * 
 * @since 7.1
 * @author Javier Paniza
 */

public class NonceTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(NonceTag.class);
	
	public int doStartTag() throws JspException {		
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			pageContext.getOut().print("nonce='");
			pageContext.getOut().print(Nonces.get(request));
			pageContext.getOut().print("'");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("nonce_tag_error")); // tmr i18n				
		}
		return SKIP_BODY;
	}	

}