package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * Tag to generate a nonce number for inline JavaScript. <p>
 * 
 * You can use in your inline JavaScript code in this way:
 * 
   <pre>
   &lt;script type="text/javascript" &lt;xava:nonce/&gt;&gt; 
     var button = document.getElementById('welcome_go_signin');
     button.onclick = function () { window.location='m/SignIn'; }
   &lt;/script&gt; 
   </pre>
 * Keep in mind that inline JavaScript does not work in any way 
 * in editor code or custom views.
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
			throw new JspException(XavaResources.getString("nonce_tag_error")); 				
		}
		return SKIP_BODY;
	}	

}