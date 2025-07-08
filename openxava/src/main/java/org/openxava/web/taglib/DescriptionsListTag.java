package org.openxava.web.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.util.Collection;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;


/**
 * 
 * @author Javier Paniza
 */

public class DescriptionsListTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(DescriptionsListTag.class);
	private String reference;
	private boolean readOnlyAsLabel;
	private Object value; // Valor para el editor 
	
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
		
			System.out.println("[DescriptionsListTag.doStartTag] value=" + value); // tmr
			// If there is a value, set it as a request attribute
			if (value != null) {
				// Get the keys from the referenced model
				Collection<?> keys = metaReference.getMetaModelReferenced().getAllKeyPropertiesNames();
				if (keys.size() == 1) { // TMR ME QUEDÉ POR AQUÍ, CREO QUE POR ESTO NO LLEGA AL JSP
					// If there is only one key, set the value directly
					String keyProperty = keys.iterator().next().toString();
					String valueKey = referenceKey + "." + keyProperty + ".value";
					System.out.println("[DescriptionsListTag.doStartTag] valueKey=" + valueKey); // tmr
					System.out.println("[DescriptionsListTag.doStartTag] value=" + value); // tmr
					request.setAttribute(valueKey, value);
				}
			}
		
			String editorURL = "reference.jsp?referenceKey=" + referenceKey + "&onlyEditor=true&frame=false&composite=false&descriptionsList=true" + readOnlyAsLabelSuffix; 
			String editorPrefix = "/xava/";  
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
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
}