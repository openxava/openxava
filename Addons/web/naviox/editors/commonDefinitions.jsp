<%
String propertyKey = request.getParameter("propertyKey");
String [] fvalues = (String []) request.getAttribute(propertyKey + ".fvalue");
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
String script = request.getParameter("script");
String agent = (String) request.getAttribute("xava.portlet.user-agent");
if (null != agent && agent.indexOf("MSIE")>=0) {
    script = org.openxava.util.Strings.change(script, "onchange", "onclick");
}
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
View view = (View) context.get(request, viewObject);
String moduleName = view.getValueString("module.name");
String applicationName = request.getParameter("application"); 
MetaModule module = MetaApplications.getMetaApplication(applicationName).getMetaModule(moduleName);
%>
