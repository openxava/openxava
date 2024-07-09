<%-- tmr Mover a openxava --%>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
%>
<div class="ox-large-format ox-frame"><%=fvalue%></div>