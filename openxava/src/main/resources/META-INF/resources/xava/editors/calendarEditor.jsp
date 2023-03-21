<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
    
<% 
   String tabObject = request.getParameter("tabObject");
		tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
		org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
		Collection<String> editors = org.openxava.web.WebEditors.getEditors(tab.getMetaTab());
        List<MetaProperty> listProperty = tab.getMetaProperties();
    String dateLabel = "";
           
        for (MetaProperty property : listProperty) {
            if (property.getTypeName().equals("java.time.LocalDate") || property.getTypeName().equals("java.util.Date")){
                System.out.println("hay");
                dateLabel = property.getLabel();
                break;
            }
        }
    tab.setConditionValue(dateLabel, "14/08/2009");
    //System.out.println(tab.getConditionValues().toString());
    System.out.println(tab.getConditionValuesTo().toString());
   %>

<Calendar id="ec"/>


<script type="text/javascript">
let ec = new EventCalendar(document.getElementById('ec'), {
    view: 'dayGridMonth',
    height: '800px',
    headerToolbar: {
            start: 'prev,today,next',
            center: 'title',
            end: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
    events: [
        // your list of events
    ],
    views: {
            timeGridWeek: {pointer: true},
            resourceTimeGridWeek: {pointer: true}
        },
    editable: true
});
</script>
