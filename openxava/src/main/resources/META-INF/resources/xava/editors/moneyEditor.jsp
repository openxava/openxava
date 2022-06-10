<%@page import="java.util.Currency"%>
<%@page import="java.util.Locale"%>

<% 
String symbol = null;
try {
	symbol = Currency.getInstance(Locale.getDefault()).getSymbol(); 
}
catch (Exception ex) { // Because Locale.getDefault() may not contain the country
	symbol = "?";
}
%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="<%=style.getMoney()%>">
<b><%=symbol%></b>
<jsp:include page="textEditor.jsp"/>
</span>
