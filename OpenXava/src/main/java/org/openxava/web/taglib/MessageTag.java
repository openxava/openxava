package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class MessageTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(MessageTag.class);
	
	private String key;
	private Object param;
	private int intParam = Integer.MIN_VALUE; // because java 1.4 haven't autoboxing
	private Object param1; 
	private Object param2;
	private Object param3;
	private Object param4;

	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			String string = null;
			if (getIntParam() > Integer.MIN_VALUE) {
				string = XavaResources.getString(request, getKey(), new Integer(getIntParam())); 
			}
			else if (!Is.empty(getParam4())) {
				string = XavaResources.getString(getKey(), getParam(), getParam1(), getParam2(), getParam3(), getParam4()); 
			}			
			else if (!Is.empty(getParam3())) {
				string = XavaResources.getString(getKey(), getParam(), getParam1(), getParam2(), getParam3()); 
			}
			else if (!Is.empty(getParam2())) {
				string = XavaResources.getString(getKey(), getParam(), getParam1(), getParam2()); 
			}
			else if (!Is.empty(getParam1())) {
				string = XavaResources.getString(getKey(), getParam(), getParam1()); 
			}
			else if (!Is.empty(getParam())) {
				string = XavaResources.getString(getKey(), getParam()); 
			}
			else {
				string = XavaResources.getString(getKey()); 
			}			
			pageContext.getOut().print(string);
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

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}
	
	public Object getParam1() {
		return param1;
	}

	public void setParam1(Object param1) {
		this.param1 = param1;
	}

	public Object getParam2() {
		return param2;
	}

	public void setParam2(Object param2) {
		this.param2 = param2;
	}

	public Object getParam3() {
		return param3;
	}

	public void setParam3(Object param3) {
		this.param3 = param3;
	}

	public Object getParam4() {
		return param4;
	}

	public void setParam4(Object param4) {
		this.param4 = param4;
	}

	public int getIntParam() {
		return intParam;
	}

	public void setIntParam(int intParam) {
		this.intParam = intParam;
	}

}