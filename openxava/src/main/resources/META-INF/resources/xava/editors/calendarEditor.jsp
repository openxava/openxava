<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.openxava.web.editors.CalendarEvent"%>
<%@ page import="org.openxava.web.editors.CalendarEventIterator"%>
    
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
String action = request.getParameter("rowAction");

action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;
Collection<String> editors = org.openxava.web.WebEditors.getEditors(tab.getMetaTab());
String actionNew = "";
for (MetaAction ma: manager.getMetaActions()) {
   if (ma.getName().equals("new")) {
       actionNew = ma.getQualifiedName();
       break;
   }
}
List<MetaProperty> listProperty = tab.getMetaProperties();
List<CalendarEvent> listEvent = new ArrayList<>();
String dateFormat = Dates.dateFormatForJSCalendar();
String events = "";
String rows = "";
CalendarEventIterator  it = new CalendarEventIterator(tab, view, request, errors);
    

listEvent = it.getEvents();
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

if (dateFormat != null) {
    dateFormat = dateFormat.replace("n", "M")
								   .replace("j", "d")
								   .replace("m", "MM")
								   .replace("d", "dd")
								   .replace("Y", "yyyy");
    } else {
    System.out.println("es null"); 
    }
  
%>

<Calendar id="ec"/>


<script type="text/javascript">
    var onlyDate={month: 'numeric', day: 'numeric'};
    var onlyTime={hour: 'numeric', minute: '2-digit'};
    var noTime = {};
    var clicked = false;
    var dateFormat = '<%=dateFormat%>';
    console.log(dateFormat);
    
let ec = new EventCalendar(document.getElementById('ec'), {
    view: 'dayGridMonth',
    height: '800px',
    headerToolbar: {
            start: 'prev,today,next',
            center: 'title',
            end: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
    events: createEvents(),
    views: {
            dayGridMonth: {eventTimeFormat: noTime},
            timeGridWeek: {eventTimeFormat: onlyTime},
            timeGridDay: {eventTimeFormat: onlyTime}
        },
    editable: true,
    displayEventEnd: false,
    pointer: true,
    // para ver solo dia fecha año
    locale: navigator.language,
    eventClick: function(e){
        if (!getSelection().toString()) {
            openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', false, false, '<%=action%>', 'row=' +   parseInt(e.event.extendedProps.row));
        }
    },
    dateClick: function(e){
        console.log(JSON.stringify(e));    
        reformatDate(e.dateStr);
        if (!getSelection().toString()) {
            openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', false, false, '<%=actionNew%>', 'value='+ e.dateStr);
        }
    }
});
    
function createEvents() {
    let listEvents = ""; 
    <%
    if (events.length() > 0) {
    %>
       listEvents = <%=events%>;
    <% } %>
    //console.log(listEvents);
    return listEvents;
}
    
function reformatDate(date){
    console.log("reformat");
    let d = new Date(date);
    console.log(d);

    let mes = d.getMonth() + 1; // getMonth() devuelve el mes en base 0, por eso sumamos 1
    let dia = d.getDate();
    let anio = d.getFullYear();
    let fechaFormateada = mes + "/" + dia + "/" + anio;
    
    console.log("fechaFormateada " + fechaFormateada); // muestra la fecha formateada
    
    //console.log("format" + dateFormat);
}
    
</script>
