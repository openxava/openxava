<%@ include file="imports.jsp"%>

<%@page import="org.openxava.jpa.XPersistence"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.util.EmailNotifications"%>

<%
String email=request.getParameter("email");
String module=request.getParameter("module");
String key=request.getParameter("key");
if (Is.emptyString(email, module)) {
%>
	<xava:message key="incorrect_url"/>
<%	
}
else {
	try {
		if (Is.emptyString(key)) {
			EmailNotifications.unsubscribeAllEntitiesOfModule(email, module);
%>
			<xava:message key="email_unsubscription_all_records" param="<%=module%>"/>
<%
		}
		else {
			EmailNotifications.unsubscribeFromEntity(email, module, key);
%>		
			<xava:message key="email_unsubscription_one_record" param='<%=key.replace("::", "")%>' param1="<%=module%>"/>
<%		
		}
	}
	finally {
		XPersistence.commit();
	}
}
%>
