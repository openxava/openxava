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
        //List events = new ArrayList();
    
    //tomar el nombre de la propiedad, setear condicion para cargar datos en el calendar
        String dateLabel = ""; 
        for (MetaProperty property : listProperty) {
            if (property.getTypeName().equals("java.time.LocalDate") || property.getTypeName().equals("java.util.Date")){
                System.out.println("hay");
                dateLabel = property.getLabel();
                break;
            }
        }
    //tab.setConditionValue(dateLabel, "14/08/2009");
    //System.out.println(tab.getConditionValues().toString());
    //System.out.println(tab.getConditionValuesTo().toString());
    
    //leer todas las filas de la tabla y cargar datos en el calendar
/*
    for (int i = 0; i < tab.getTableModel().getRowCount(); i++) {
        HashMap<String, Integer> numId = null;
        numId = (HashMap<String, Integer>) tab.getTableModel().getObjectAt(i);
        listaCliente.add(getCliente(numeroCliente.entrySet().iterator().next().getValue()));
    }
  */  
    
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
        {start:'2022-03-21', end:'2022-03-22', title: 'title 1'},
        {start:'2022-03-25', title: 'title 1'}
    ],
    views: {
            timeGridWeek: {pointer: true},
            resourceTimeGridWeek: {pointer: true}
        },
    editable: true,
    displayEventEnd: false,
    //eventTimeFormat: {dateStyle: 'short', hour:'numeric'},
                           
});
</script>
