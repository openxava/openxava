<% 
String propertyKey = request.getParameter("propertyKey"); 
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue"); 
String sPrefix = request.getParameter("prefix"); 
String sExtension = request.getParameter("extension"); 
%> 
 
<input id="<%=propertyKey%>" type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">  
<img src="<%=request.getContextPath()%>/xava/images/<%= sPrefix+fvalue+"."+sExtension%>" alt="<%=fvalue %>"/> 
