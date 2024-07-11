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
String icon = request.getParameter("icon");
String iconHTML = Is.emptyString(icon)?"":"<i class='mdi mdi-" + icon + "'></i>";
Object value = request.getAttribute(propertyKey + ".value");
String negativeClass = "";
if (value instanceof Number) {
	Number number = (Number) value;
	if (number.longValue() < 0) negativeClass = "ox-large-format-negative"; 
}
%>
<div class="ox-large-format ox-frame <%=negativeClass%>"><%=iconHTML%><span class="ox-large-format-prefix"><%=prefix%></span><span class="ox-large-format-value"><%=fvalue%></span><span class="ox-large-format-suffix"><%=suffix%></span></div>