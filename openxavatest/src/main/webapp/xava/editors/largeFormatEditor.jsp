<%-- tmr Mover a openxava --%>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String prefix = request.getParameter("prefix");
String suffix = request.getParameter("suffix"); 
// TMR ME QUEDÉ POR AQUÍ: USAR Moneys PARA prefix Y suffix AUTOMÁTICO
%>
<div class="ox-large-format ox-frame"><span class="ox-large-format-prefix"><%=prefix%></span><%=fvalue%><span class="ox-large-format-suffix"><%=suffix%></span></div>