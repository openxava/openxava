<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.openxava.web.editors.CalendarEvent"%>
<%@ page import="org.openxava.web.editors.CalendarEventIterator"%>
    <%@ page import="org.openxava.web.dwr.OXCalendar"%>
<%@ page import="org.openxava.util.ToJson"%>
<%@ page import="org.json.*"%>
    
<%@ page import="org.openxava.tab.Tab"%>
<%@ page import="org.openxava.view.View"%>
<%@ page import="org.openxava.controller.ModuleManager" %>
<%@ page import="org.openxava.controller.meta.MetaControllers"%>
<%@ page import="org.openxava.controller.meta.MetaAction"%>
<%@ page import="org.openxava.util.Dates"%>
    
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
    
<% 
ModuleManager manager = (ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
Tab tab = (Tab) context.get(request, "xava_tab");
View view = (View) context.get(request, "xava_view");
String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
String version = org.openxava.controller.ModuleManager.getVersion();
String action = request.getParameter("rowAction");
String hola = "alohaa";
action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;
Collection<String> editors = org.openxava.web.WebEditors.getEditors(tab.getMetaTab());
String actionNew = "";
for (MetaAction ma: manager.getMetaActions()) {
   if (ma.getName().equals("new")) {
       actionNew = ma.getQualifiedName();
       break;
   }
}
//List<MetaProperty> listProperty = tab.getMetaProperties();
//List<CalendarEvent> listEvent = new ArrayList<>();
String dateFormat = Dates.dateFormatForJSCalendar();
String events = "";
String rows = "";
OXCalendar.setData(view, errors);
//CalendarEventIterator  it = new CalendarEventIterator(tab, view, request, errors);
//System.out.println(tab.getFilter());
//String initDate = it.getInitDate();
//listEvent = it.getEvents();
    /*
StringBuilder sb = new StringBuilder();
sb.append("[");
for (int i = 0; i < listEvent.size(); i++) {
    CalendarEvent evento = listEvent.get(i);
    sb.append("{");
    sb.append("\"start\":\"" + evento.getStart() + "\",");
    sb.append("\"end\":\"" + evento.getEnd() + "\",");
    sb.append("\"title\":\"" + evento.getTitle() + "\",");
    sb.append("\"extendedProps\":{ \"row\":\"" + evento.getRow() + "\"}");
    sb.append("}");
    if (i < listEvent.size() - 1) {
        sb.append(",");
    }
}
sb.append("]");
events = sb.toString();
*/
if (dateFormat != null) {
    dateFormat = dateFormat.replace("n", "M")
                           .replace("m", "MM")
			               .replace("d", "dd")
				           .replace("j", "d")
				           .replace("Y", "yyyy");
    }

  
%>
    <div>CALENDARIO
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