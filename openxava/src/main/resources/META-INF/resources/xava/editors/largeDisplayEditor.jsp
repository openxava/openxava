<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.Moneys" %>
<%@ page import="org.openxava.util.MDIIconMapper" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String label = view.getLabelFor(p);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String prefix = request.getParameter("prefix");
String suffix = request.getParameter("suffix"); 
if (Is.emptyStringAll(prefix, suffix)) {
	if (Moneys.isMoneyProperty(p)) {
		String symbol = Moneys.getCurrencySymbol();
		if (Moneys.isCurrencySymbolAtStart()) prefix = symbol;
		else suffix = symbol;
	}
}
String icon = request.getParameter("icon");
icon = MDIIconMapper.map(icon);
String iconHTML = Is.emptyString(icon)?"":"<i class='mdi mdi-" + icon + "'></i>";
Object value = request.getAttribute(propertyKey + ".value");
String negativeClass = "";
if (value instanceof Number) {
	Number number = (Number) value;
	if (number.longValue() < 0) negativeClass = "ox-large-display-negative"; 
}
%>
<div id="<xava:id name='<%="label_" + view.getPropertyPrefix() + p.getName()%>'/>" class="ox-large-display-label"><%=label%></div>
<div class="ox-large-display ox-frame <%=negativeClass%>"><%=iconHTML%><span class="ox-large-display-prefix"><%=prefix%></span><span class="ox-large-display-value"><%=fvalue%></span><span class="ox-large-display-suffix"><%=suffix%></span></div>
<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">
