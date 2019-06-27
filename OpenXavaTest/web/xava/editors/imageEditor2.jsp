<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
boolean editable="true".equals(request.getParameter("editable"));
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
long dif=System.currentTimeMillis(); // to avoid browser caching
String url = request.getContextPath() + "/xava/ximage?application=" + applicationName + "&module=" + module + "&property=" + propertyKey + "&dif=" + dif;
// TMP ME QUEDÉ POR AQUÍ: YA FUNCIONA OBTENER IMAGEN. FALTA UN DETALLE CUANDO NO HAY IMAGEN SALE MAL. DESPUÉS HACER CAMBIAR Y QUITAR IMAGEN, Y QUE SE GRABE.
%>

<input type="file" class="xava_image" data-url="<%=url%>"/> <%-- tmp Comprobar que lo de xava_image está bien, ver otros casos --%>

