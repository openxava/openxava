<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="java.lang.reflect.AnnotatedElement" %>
<%@ page import="org.openxava.test.annotations.Colorful" %>

<%
// tmp
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
AnnotatedElement e = p.getAnnotatedElement();
Colorful colorful = e.getAnnotation(Colorful.class);
String background = request.getParameter("background");
String highlight = request.getParameter("highlight");
%>

Mi color es: <%=colorful.color()%><br>
y el fondo es: <%=background%>, con highlight: <%=highlight%> 