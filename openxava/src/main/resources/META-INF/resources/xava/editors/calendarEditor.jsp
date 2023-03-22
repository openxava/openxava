<%@ include file="../imports.jsp"%>
    
<%@ include file="./cardsEditor.jsp"%>
    
<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
    
<% 
   String tabObject = request.getParameter("tabObject");
		tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
		//org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
		Collection<String> editors = org.openxava.web.WebEditors.getEditors(tab.getMetaTab());
        List<MetaProperty> listProperty = tab.getMetaProperties();
        List<String> listTitle = new ArrayList<>();
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
    
    for (Card card: new CardIterator(tab, view, request, errors)) {
        String s = card.getSubheader() + " - " + card.getHeader();
            System.out.println(s);
            listTitle.add(s);

}
    
      StringBuilder sb = new StringBuilder();
  sb.append("[");
  for (int j = 0; j < listTitle.size(); j++) {
    sb.append("\"").append(listTitle.get(j)).append("\"");
    if (j != listTitle.size() - 1) {
      sb.append(",");
    }
  }
  sb.append("]");
  String listTitleJson = sb.toString();
    
    
    
    
    
    
    
   %>

<Calendar id="ec"/>


<script type="text/javascript">
    var onlyDate={year: 'numeric', month: 'numeric', day: 'numeric'};
    var listCards = [];
    
    var events = [];
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
            timeGridWeek: {pointer: true},
            resourceTimeGridWeek: {pointer: true}
        },
    editable: true,
    displayEventEnd: false,
    // para ver solo dia fecha año
    eventTimeFormat: onlyDate,
                           
});
    function createEvents() {
        let days = [];
        listCards = <%=listTitleJson%>;
        //console.log(listCards);
        for (let i = 0; i<listCards.length; i++){
            let event = new Object();
            event.start = '2023-03-2' + i + ' 00:00';
            event.title = listCards[i];
            console.log(event);
            events.push(event);
        }
        console.log(events);
        return events;
    }
    
</script>
