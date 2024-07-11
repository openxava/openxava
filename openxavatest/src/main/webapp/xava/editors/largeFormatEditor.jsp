<%-- tmr Mover a openxava --%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.Moneys" %>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String prefix = request.getParameter("prefix");
String suffix = request.getParameter("suffix"); 
if (Is.emptyStringAll(prefix, suffix)) {
	MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
	if (Moneys.isMoneyProperty(p)) {
		String symbol = Moneys.getCurrencySymbol();
		if (Moneys.isCurrencySymbolAtStart()) prefix = symbol;
		else suffix = symbol;
	}
}
%>
<div class="ox-large-format ox-frame"><span class="ox-large-format-prefix"><%=prefix%></span><%=fvalue%><span class="ox-large-format-suffix"><%=suffix%></span></div>