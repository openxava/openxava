<%@page import="org.openxava.util.Moneys"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="<%=style.getMoney()%>">
<b><%=Moneys.getCurrencySymbol()%></b>
<jsp:include page="textEditor.jsp"/>
</span>
