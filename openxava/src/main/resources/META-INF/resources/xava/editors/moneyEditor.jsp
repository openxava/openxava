<%-- tmr
<%@page import="java.util.Currency"%>
<%@page import="java.util.Locale"%>
--%>
<%-- tmr ini --%>
<%@page import="org.openxava.util.Moneys"%>
<%-- tmr fin --%>

<%-- tmr 
String symbol = null;
try {
	symbol = Currency.getInstance(Locale.getDefault()).getSymbol(); 
}
catch (Exception ex) { // Because Locale.getDefault() may not contain the country
	symbol = "?";
}
--%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="<%=style.getMoney()%>">
<%-- tmr
<b><%=symbol%></b>
--%>
<%-- tmr ini --%>
<b><%=Moneys.getCurrencySymbol()%></b>
<%-- tmr fin --%>
<jsp:include page="textEditor.jsp"/>
</span>
