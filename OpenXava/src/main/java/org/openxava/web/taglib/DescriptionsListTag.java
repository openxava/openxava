package org.openxava.web.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.dwr.Module;


/**
 * 
 * @author Javier Paniza
 */

public class DescriptionsListTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(DescriptionsListTag.class);
	private String reference;
	private boolean readOnlyAsLabel; 
	
	public int doStartTag() throws JspException {
		try {			
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");			
									
			String viewObject = request.getParameter("viewObject");
			viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
			View view = (View) context.get(request, viewObject);

			MetaReference metaReference = view.getMetaReference(reference).cloneMetaReference();
			metaReference.setName(reference);
			String prefix = request.getParameter("propertyPrefix");
			prefix = prefix == null?"":prefix;
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			String referenceKey = Ids.decorate(application, module, prefix + reference); 
			request.setAttribute(referenceKey, metaReference);
			String readOnlyAsLabelSuffix = readOnlyAsLabel?"&readOnlyAsLabel=true":"";
			String editorURL = "reference.jsp?referenceKey=" + referenceKey + "&onlyEditor=true&frame=false&composite=false&descriptionsList=true" + readOnlyAsLabelSuffix; 
			String editorPrefix = Module.isPortlet()?"/WEB-INF/jsp/xava/":"/xava/";  
			try {
				pageContext.include(editorPrefix + editorURL); 
			}
			catch (ServletException ex) { 
				Throwable cause = ex.getRootCause() == null?ex:ex.getRootCause(); 
				log.error(cause.getMessage(), cause);
				pageContext.include(editorPrefix + "editors/notAvailableEditor.jsp"); 
			}
			catch (Exception ex) {	
				log.error(ex.getMessage(), ex);
				pageContext.include(editorPrefix + "editors/notAvailableEditor.jsp"); 
			}											
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("descriptionsList_tag_error", reference));
		}	
		return SKIP_BODY;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String property) {
		this.reference = property;		
	}

	public boolean isReadOnlyAsLabel() {
		return readOnlyAsLabel;
	}

	public void setReadOnlyAsLabel(boolean readOnlyAsLabel) {
		this.readOnlyAsLabel = readOnlyAsLabel;
	}
	
}