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
 * @author Javier Paniza
 */

public class EditorTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(EditorTag.class);
	
	private String property;		
	private boolean editable; 
	private boolean explicitEditable = false; 
	private boolean throwPropertyChanged; 
	private boolean explicitThrowPropertyChanged; 
	private String viewObject;
	private boolean viewObjectSet;
	private String propertyPrefix;
	private boolean propertyPrefixSet;
	
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
			Object value = view.getValue(property);
			request.setAttribute(valueKey, value);
									
			Messages errors = (Messages) request.getAttribute("errors"); 	
			boolean throwsChanged=explicitThrowPropertyChanged?this.throwPropertyChanged:view.throwsPropertyChanged(property);
			
			String scriptFocus =  
				" onblur=\"openxava.onBlur(" +
				"'" + application + "'," +
				"'" + module + "'," +
				"'" + propertyKey + "'" +
				")\"" +
				" onfocus=\"openxava.onFocus(" +
				"'" + application + "'," +
				"'" + module + "'," +
				"'" + propertyKey + "'" +
				")\"";  
			
			String script = throwsChanged? 
				" onchange=\"openxava.throwPropertyChanged(" +
				"'" + application + "'," +
				"'" + module + "'," +
				"'" + propertyKey + "'" +
				")\""  
			:
			"";
			
			View rootView = view.getCollectionRootOrRoot();
			if (rootView.isPropertyUsedInCalculation(propertyPrefix + property)) { 
				script = Collections.sumPropertyScript(application, module, rootView, propertyPrefix + property); 
			}

			script = script + scriptFocus;

			boolean editable = explicitEditable?this.editable:view.isEditable(property);  
			
			boolean inElementCollection = property.contains(".");
			String viewName = inElementCollection?"":view.getViewName();
			String editorBaseURL = view.hasValidValues(property)?"editors/dynamicValidValuesEditor.jsp":org.openxava.web.WebEditors.getUrl(metaProperty, viewName); // We could move editors/dynamicValidValuesEditor.jsp to default-editors.xml
			StringBuffer editorURL = new StringBuffer(editorBaseURL);			
			char nexus = editorURL.toString().indexOf('?') < 0?'?':'&';
			String maxSize = "";
			int displaySize = view.getDisplaySizeForProperty(property);
			if (displaySize > -1) {
				maxSize = "maxSize=" + displaySize + "&";
			}
			
			editorURL.append(nexus)
				.append(maxSize)
				.append("script=")
				.append(script)
				.append("&editable=")
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
				String calculationKey = propertyKey + "_CALCULATION_";  
				pageContext.getOut().print("<input type='hidden' id='"); 
				pageContext.getOut().print(calculationKey);
				pageContext.getOut().print("' value=\"");
				pageContext.getOut().print(toJavaScriptExpression(metaProperty));
				pageContext.getOut().println("\"/>");
			}			
			if (org.openxava.web.WebEditors.hasMultipleValuesFormatter(metaProperty, viewName)) { 
				pageContext.getOut().print("<input type='hidden' name='");
				pageContext.getOut().print(Ids.decorate(application, module, "xava_multiple"));
				pageContext.getOut().print("' value='");
				pageContext.getOut().print(propertyKey);
				pageContext.getOut().println("'/>");				
			}		
			String prefix = Module.isPortlet()?"/WEB-INF/jsp/xava/":"/xava/";  
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
		}
		catch (Exception ex) {
			throw new JspException(XavaResources.getString("editor_tag_error", property), ex); 
		}	
		return SKIP_BODY;
	}
	
	private String toJavaScriptExpression(MetaProperty metaProperty) { 
		StringBuffer expression = new StringBuffer();
	    for (String property: metaProperty.getPropertiesNamesUsedForCalculation()) {
    		expression.append("var ");
    		expression.append(property.replace(".", "_"));
    		expression.append("=openxava.getNumber(application,module,'");
    		expression.append(property);
    		expression.append("');");
	    }
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
		this.propertyPrefixSet = true;
		this.propertyPrefix = propertyPrefix;
	}

	
}