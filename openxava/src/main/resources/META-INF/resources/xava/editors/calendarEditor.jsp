<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="org.openxava.web.dwr.Calendar"%>
<%@ page import="java.util.prefs.Preferences" %>
<%@ page import="org.openxava.util.Users" %>
<%@ page import="org.openxava.view.View"%>
<%@ page import="org.openxava.tab.Tab" %>
<%@ page import="org.openxava.controller.ModuleManager" %>
<%@ page import="org.openxava.controller.meta.MetaControllers"%>
<%@ page import="org.openxava.controller.meta.MetaAction"%>
<%@ page import="org.openxava.util.Dates"%>
    
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<% 
ModuleManager manager = (ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
View view = (View) context.get(request, "xava_view");
Tab tab = (Tab) context.get(request, "xava_tab");
String prefNodeName = tab.getPreferencesNodeName("datePref.");
Preferences preferences = Users.getCurrentPreferences();
String datePref = preferences.get(prefNodeName,"");
List<MetaProperty> metaPropertiesList = new ArrayList<>(view.getMetaPropertiesList());
List<String> datesProperties = Arrays.asList(
            "java.util.Date", "java.time.LocalDateTime", "java.sql.Timestamp", "java.time.LocalDate", 
			"java.util.Date", "java.sql.Date", "java.time.LocalDateTime", "java.sql.Timestamp");
String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
String version = org.openxava.controller.ModuleManager.getVersion();
String action = request.getParameter("rowAction");
action=action==null?manager.getEnvironment().getValue("XAVA_CALENDAR_VIEWEVENT_ACTION"):action;
String dateFormat = Dates.dateFormatForJSCalendar(true);
String actionNew = "";
boolean hasDateTime = view.getMetaModel().hasDateTimeProperty();
for (MetaAction ma: manager.getMetaActions()) {
   if (ma.getName().equals("new")) {
       actionNew = ma.getQualifiedName();
       break;
   }
}

if (dateFormat != null) {
    dateFormat = dateFormat.replace("n", "M")
                           .replace("m", "MM")
			               .replace("d", "dd")
				           .replace("j", "d")
						   .replace("H", "H")
						   .replace("h", "h")
						   .replace("G", "hh")
						   .replace("i", "mm")
				           .replace("Y", "yyyy");
}
%>

<div class="ox-layout-detail ox-layout-calendar">
	<div class="ox-layout-aligned-cell ox-label">Filtrar por</div>
    <select class="xava_list_date" id="listConfigurations" name="listConfigurations" title="<%=datePref%>">
        <option value=""><%=datePref%></option>
        <% 
        for (MetaProperty mp : metaPropertiesList){
            if (datesProperties.contains(mp.getTypeName()) && !mp.getLabel().equals(datePref)) {
        %>
            <option value="<%=mp.getSimpleName()%>"><%=mp.getLabel()%></option>
        <% 
            }
        }
        %>
    </select>
</div>

<div>
    <input type="hidden" id="xava_calendar_module" value="<%=request.getParameter("module")%>">
    <input type="hidden" id="xava_calendar_application" value="<%=request.getParameter("application")%>">
    <input type="hidden" id="xava_calendar_action" value="<%=action%>,<%=actionNew%>">
    <input type="hidden" id="xava_calendar_dateFormat" value="<%=dateFormat%>">
	<input type="hidden" id="xava_calendar_hasDateTime" value="<%=hasDateTime%>">
</div>
<div id='xava_calendar' class='xava_calendar'></div>

<script type='text/javascript' <xava:nonce/> src='<%=contextPath%>/dwr/interface/Calendar.js?ox=<%=version%>'></script>