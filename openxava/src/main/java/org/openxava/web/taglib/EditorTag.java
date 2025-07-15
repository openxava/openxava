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
import org.openxava.web.meta.*;


/**
 * @author Javier Paniza
 */

public class EditorTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(EditorTag.class);
	
	private String property;
	private boolean editable;
	private boolean explicitEditable;
	private boolean throwPropertyChanged;
	private boolean explicitThrowPropertyChanged;
	private String viewObject;
	private boolean viewObjectSet;
	private String propertyPrefix;
	private boolean propertyPrefixSet;
	private Object value;
	private boolean valueSet;
	
	public EditorTag() {
		viewObjectSet = false;
		propertyPrefixSet = false;
	}
	
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
									
			String viewObject = request.getParameter("viewObject");
			if (viewObjectSet) {
				viewObject = getViewObject();
			}
			viewObject = (viewObject == null || viewObject.equals("") || viewObject.equals("null"))?"xava_view":viewObject; 
			View view = (View) context.get(request, viewObject);

			MetaProperty metaProperty = view.getMetaProperty(property);

			String propertyPrefix = request.getParameter("propertyPrefix");
			if (propertyPrefixSet) {
				propertyPrefix = getPropertyPrefix();
			}
			propertyPrefix = propertyPrefix == null?"":propertyPrefix; 
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			String propertyKey = Ids.decorate(application, module, propertyPrefix + property);
			String valueKey = propertyKey + ".value";
			request.setAttribute(propertyKey, metaProperty);
			Object value = valueSet ? this.value : view.getValue(property);
			request.setAttribute(valueKey, value);
									
			Messages errors = (Messages) request.getAttribute("errors"); 	
			boolean throwsChanged=explicitThrowPropertyChanged?this.throwPropertyChanged:view.throwsPropertyChanged(property);
						
			View rootView = view.getCollectionRootOrRoot();

			boolean editable = explicitEditable?this.editable:view.isEditable(property);  
			boolean inElementCollection = property.contains(".");
			String viewName = inElementCollection?"":view.getViewName();
			MetaEditor metaEditor = WebEditors.getMetaEditorFor(metaProperty, viewName);
			String editorBaseURL = org.openxava.web.WebEditors.getUrl(metaProperty, viewName);
			if (view.hasValidValues(property)) {
				editorBaseURL = (!metaEditor.getName().equalsIgnoreCase("") && !metaEditor.getUrl().equalsIgnoreCase("textEditor.jsp")) ? 
							"editors/" + metaEditor.getUrl() :
							"editors/dynamicValidValuesEditor.jsp";
			}
			StringBuffer editorURL = new StringBuffer(editorBaseURL);			
			char nexus = editorURL.toString().indexOf('?') < 0?'?':'&';
			String maxSize = "";
			int displaySize = view.getDisplaySizeForProperty(property);
			if (displaySize > -1) {
				maxSize = "maxSize=" + displaySize + "&";
			}
			
			if (editorURL.toString().contains("#")) {
				String replaced = editorURL.toString().replaceAll("#", "%23");
				editorURL.setLength(0);
				editorURL.append(replaced);
			}

			editorURL.append(nexus)
				.append(maxSize)
				.append("editable=") 
				.append(editable)
				.append("&propertyKey=")
				.append(propertyKey)
				.append("&viewObject=")
				.append(viewObject);			
			
			if (org.openxava.web.WebEditors.mustToFormat(metaProperty, viewName)) { 
				Object fvalue = org.openxava.web.WebEditors.formatToStringOrArray(request, metaProperty, value, errors, viewName, false); 
				request.setAttribute(propertyKey + ".fvalue", fvalue);
			}
						
			String editableKey = propertyKey + "_EDITABLE_";  
			pageContext.getOut().print("<input type='hidden' name='");
			pageContext.getOut().print(editableKey);
			pageContext.getOut().print("' value='");
			pageContext.getOut().print(editable);
			pageContext.getOut().println("'/>");
			if (metaProperty.hasCalculation()) { 
				String collectionPrefix = inElementCollection?getCollectionPrefix():"";
				pageContext.getOut().print("<script type='text/javascript' nonce='"); 
				pageContext.getOut().print(Nonces.get(request));
				pageContext.getOut().print("'>");
				pageContext.getOut().print("openxava.calculations.");
				pageContext.getOut().print(propertyKey);
				pageContext.getOut().print("=function(application, module) {");
				pageContext.getOut().print(toJavaScriptExpression(metaProperty, collectionPrefix));
				pageContext.getOut().print("}");
			    pageContext.getOut().print("</script>");
			}			
			if (org.openxava.web.WebEditors.hasMultipleValuesFormatter(metaProperty, viewName)) { 
				pageContext.getOut().print("<input type='hidden' name='");
				pageContext.getOut().print(Ids.decorate(application, module, "xava_multiple"));
				pageContext.getOut().print("' value='");
				pageContext.getOut().print(propertyKey);
				pageContext.getOut().println("'/>");				
			}		
			boolean propertyUsedInCalculation = rootView.isPropertyUsedInCalculation(propertyPrefix + property); 
			if (propertyUsedInCalculation) {
				pageContext.getOut().print("<span class='xava_onchange_calculate' ");
				pageContext.getOut().print(EditorsEvents.onChangeCalculateDataAttributes(application, module, rootView, propertyPrefix + property));
				pageContext.getOut().print(">");
			}
			if (throwsChanged) {
				pageContext.getOut().print("<span class='xava_onchange' data-property='");
				pageContext.getOut().print(propertyKey);
				pageContext.getOut().print("'>");
			}
			String prefix = "/xava/";  
			try {
				pageContext.include(prefix + editorURL); 
			}
			catch (ServletException ex) { 
				Throwable cause = ex.getRootCause() == null?ex:ex.getRootCause(); 
				log.error(cause.getMessage(), cause);
				pageContext.include(prefix + "editors/notAvailableEditor.jsp"); 
			}
			catch (Exception ex) {	
				log.error(ex.getMessage(), ex);
				pageContext.include(prefix + "editors/notAvailableEditor.jsp"); 
			}
			if (throwsChanged) pageContext.getOut().print("</span>"); 
			if (propertyUsedInCalculation) pageContext.getOut().print("</span>"); 
		}
		catch (Exception ex) {
			throw new JspException(XavaResources.getString("editor_tag_error", property), ex); 
		}	
		return SKIP_BODY;
	}
	
	private String getCollectionPrefix() {
		int idx = property.indexOf(".");
		int idx2 = property.indexOf(".", idx+1);
		return property.substring(0, idx2+1);
	}

	private String toJavaScriptExpression(MetaProperty metaProperty, String collectionPrefix) {   
		StringBuffer expression = new StringBuffer();
	    for (String property: metaProperty.getPropertiesNamesUsedForCalculation()) {
    		expression.append("var ");
    		expression.append(property.replace(".", "_"));
    		expression.append("=openxava.getNumber(application,module,'");
    		expression.append(collectionPrefix + property); 
    		expression.append("');");
	    }
	    expression.append("return "); 
	    expression.append(
	    	metaProperty.getCalculation() 
    		.replaceAll("[Ss][Uu][Mm]\\((.*)\\)", "$1_SUM_")
			.replaceAll("([a-zA-Z_][a-zA-Z\\d_]*)\\.([a-zA-Z_][a-zA-Z\\d_]*)\\.([a-zA-Z_][a-zA-Z\\d_]*)\\.([a-zA-Z_][a-zA-Z\\d_]*)", "$1_$2_$3_$4")
			.replaceAll("([a-zA-Z_][a-zA-Z\\d_]*)\\.([a-zA-Z_][a-zA-Z\\d_]*)\\.([a-zA-Z_][a-zA-Z\\d_]*)", "$1_$2_$3")
    		.replaceAll("([a-zA-Z_][a-zA-Z\\d_]*)\\.([a-zA-Z_][a-zA-Z\\d_]*)", "$1_$2")	    
    	);
	    return expression.toString();
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;		
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		this.explicitEditable = true;
	}

	public boolean isThrowPropertyChanged() {
		return throwPropertyChanged;
	}

	public void setThrowPropertyChanged(boolean throwPropertyChanged) {
		this.throwPropertyChanged = throwPropertyChanged;
		this.explicitThrowPropertyChanged = true;
	}


	/**
	 * @return the viewObject
	 */
	public String getViewObject() {
		return viewObject;
	}


	/**
	 * @param viewObject the viewObject to set
	 */
	public void setViewObject(String viewObject) {
		this.viewObjectSet = true;
		this.viewObject = viewObject;
	}

	/**
	 * @return the propertyPrefix
	 */
	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	/**
	 * @param propertyPrefix the propertyPrefix to set
	 */
	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
		this.propertyPrefixSet = true;
	}
	
	public void setValue(Object value) {
		this.value = value;
		this.valueSet = true;
	}
	
}