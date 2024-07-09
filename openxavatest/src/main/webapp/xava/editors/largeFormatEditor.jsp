<%-- tmr Mover a openxava --%>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String symbol = request.getParameter("symbol"); // TMR ME QUEDÉ POR AQUÍ, HACIENDO LO DEL SYMBOL
%>
<div class="ox-large-format ox-frame"><%=fvalue%><span class="ox-large-format-symbol"><%=symbol%></span></div>