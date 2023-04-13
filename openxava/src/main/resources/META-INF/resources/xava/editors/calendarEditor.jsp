<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.openxava.web.dwr.OXCalendar"%>
<%@ page import="org.openxava.web.editors.CalendarEventIterator"%>
<%@ page import="org.openxava.util.ToJson"%>
<%@ page import="org.json.*"%>
    
<%@ page import="org.openxava.view.View"%>
<%@ page import="org.openxava.controller.ModuleManager" %>
<%@ page import="org.openxava.controller.meta.MetaControllers"%>
<%@ page import="org.openxava.controller.meta.MetaAction"%>
<%@ page import="org.openxava.util.Dates"%>
<%@ page import="com.fasterxml.jackson.databind.*"%>
<%@ page import="com.fasterxml.jackson.core.io.*"%>
    
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
    
<% 
ModuleManager manager = (ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
View view = (View) context.get(request, "xava_view");
String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
String version = org.openxava.controller.ModuleManager.getVersion();
String action = request.getParameter("rowAction");
action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;

OXCalendar.setOX(view,errors);

String actionNew = "";
for (MetaAction ma: manager.getMetaActions()) {
   if (ma.getName().equals("new")) {
       actionNew = ma.getQualifiedName();
       break;
   }
}

String dateFormat = Dates.dateFormatForJSCalendar();

if (dateFormat != null) {
    dateFormat = dateFormat.replace("n", "M")
                           .replace("m", "MM")
			               .replace("d", "dd")
				           .replace("j", "d")
				           .replace("Y", "yyyy");
    }
   
%>
<div>CALENDARIO
    <input type="hidden" id="xava_calendar_module" value="<%=request.getParameter("module")%>">
    <input type="hidden" id="xava_calendar_application" value="<%=request.getParameter("application")%>">
    <input type="hidden" id="xava_calendar_action" value="<%=action%>,<%=actionNew%>">
    <input type="hidden" id="xava_calendar_dateFormat" value="<%=dateFormat%>">
</div>
    
<div id='xava_calendar'></div>

<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Calendar.js?ox=<%=version%>'></script>	    
    
<script type="text/javascript" <xava:nonce/>>
    var dateFormat = '<%=dateFormat%>';
    var calendarRequestApplication = '<%=request.getParameter("application")%>';
    var calendarRequestModule = '<%=request.getParameter("module")%>';
    var calendarAction = '<%=action%>';
    var calendarNewAction = '<%=actionNew%>';
</script>