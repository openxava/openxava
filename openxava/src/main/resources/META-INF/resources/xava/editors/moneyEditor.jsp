<%@page import="org.openxava.util.Moneys"%>


<span class="ox-money">
<b><%=Moneys.getCurrencySymbol()%></b>
<jsp:include page="textEditor.jsp"/>
</span>
