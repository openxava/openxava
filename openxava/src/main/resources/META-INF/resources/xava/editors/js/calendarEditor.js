if (calendarEditor == null) var calendarEditor = {};

var calendarEl;
var calendar;
var listEvents;

calendarEditor.setEvents = function(calendarEvents){
    console.log("entra");
    console.log(calendarEvents);
    listEvents = JSON.parse(calendarEvents);
    for (let e of listEvents) {
        calendar.addEvent({
            title: e.title,
            start: e.start,
            end: e.end,
            "extendedProps": {
                        "row": e.row
            }
        });
    }
    
}

openxava.addEditorInitFunction(function() {
    
    if ($("#xava_calendar").length) {
        console.log("si");
        var onlyDate = {
            month: 'numeric',
            day: 'numeric'
        };
        var onlyTime = {
            hour: 'numeric',
            minute: '2-digit'
        };
        var noTime = {};
        var formattedDate = "";
        var segundavez = false;
        //var listaEventos = JSON.parse(events);
        var initial = '2023-04-07';

        listEvents = [];
        Calendar.getEvents(calendarRequestApplication, calendarRequestModule, "", calendarEditor.setEvents);
        
        
        $("#xava_calendar").ready(function() {
            console.log("dentro");
            calendarEl = $('#xava_calendar')[0];
            calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                editable: true,
                locale: navigator.language,
                showNonCurrentDates: false,
                displayEventTime: false,
                events: listEvents,
                editable: true,
                //initialDate: initial,
                //progressiveEventRendering: true,
                headerToolbar: {
                    left: 'prev2,today,next2',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                locale: navigator.language,
                showNonCurrentDates: false,
                displayEventTime: false,
                views: {
                    dayGridMonth: {
                        eventTimeFormat: noTime
                    },
                    timeGridWeek: {
                        eventTimeFormat: onlyTime
                    },
                    timeGridDay: {
                        eventTimeFormat: onlyTime
                    }
                },
                customButtons: {
                    next2: {
                        text: 'next',
                        click: function() {
                            //alert('clicked custom button 1!');
                            //openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, 'Calendar.next', 'month=next');
                            calendar.next();
                            //calendar._initialDate = "2023-05-01";
                        }
                    },
                    prev2: {
                        text: 'prev',
                        click: function() {
                            //hacer una consulta y obtener un json como respuesta
                            //agregar los nuevos eventos con calendar.add, al parecer es uno por uno
                            //elimino todos los eventos existentes en la memoria
                            var events = calendar.getEvents();
                            events.forEach(function(event) {
                                event.remove();
                            });
                            
                            //debo cambiar ahora el mes para obtener el mes actual en la vista
                            let currentViewDate = calendar.view.activeStart;
                            let currentMonth = prevViewDate.getMonth();
                            console.log(currentMonth);
                            calendar.prev();
                            let prevViewDate = calendar.view.activeStart;
                            let prevMonth = prevViewDate.getMonth();
                            console.log(prevMonth);
                            let month = (currentMonth != prevMonth) ? prevMonth : "";
                            //obtener los eventos del nuevo filtro en un array con dwr
                            Calendar.getEvents(calendarRequestApplication, calendarRequestModule, month, calendarEditor.setEvents);
                            
                            //recorrer el array y agregarlos con calendar.add

                            calendar.addEvent({
                                title: 'dynamic event',
                                start: '2023-03-25T00:00:00',
                                allDay: true,
                                "extendedProps": {
                                        "row": "0"
                                    }
                            });
                            //testear si se necesita
                            //calendar.refetchEvents();
                           
                        }
                    }
                },
                eventClick: function(e) {
                    if (!getSelection().toString()) {
                        openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, calendarAction, 'row=' + parseInt(e.event.extendedProps.row));
                    }
                },
                dateClick: function(e) {
                    console.log(e);
                    console.log(e.dateStr);
                    reformatDate(e.dateStr);
                    let value = {
                        "dates": [{
                                "label": "date",
                                "date": formattedDate
                            },
                            {
                                "label": "date",
                                "date": formattedDate
                            }
                        ]
                    };
                    console.log(JSON.stringify(value).replaceAll(",", "_"));
                    if (!getSelection().toString()) {
                        openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, calendarNewAction, 'value=' + JSON.stringify(value).replaceAll(",", "_"));
                    }
                }
            });
            calendar.render();
        });
    

        /*
                function createEvents() {
                    // probar despues con return events directamente cuando es un modulo sin registros
                    let listEvents = "";
                    console.log("create")
                    if (!segundavez) {
                        listEvents = events;
                    }
                    if (segundavez) {
                        let e = [{
                                "start": "2023-03-28 00:00:00.0",
                                "end": "2023-03-28 11:11:11.1",
                                "title": "Nombre: Javi-Año: 2023-Número: 1",
                                "extendedProps": {
                                    "row": "0"
                                }
                            },
                            {
                                "start": "2023-03-27 00:00:00.0",
                                "end": "2023-03-27 11:11:11.1",
                                "title": "Año: 2023-Año: 2023-Número: 2",
                                "extendedProps": {
                                    "row": "1"
                                }
                            }
                        ];
                        listEvents = e;
                    }
                    console.log(events);
                    segundavez = true;
                    return JSON.parse(listEvents);
                }
        */
        function reformatDate(date) {
            date = (date.toString().length < 11) ? date + 'T00:00:00' : date;
            let d = new Date(date);
            console.log(d);
            console.log(dateFormat);
            formattedDate = formatDate(d, dateFormat);
        }

        function getSeparator(date) {
            const separatorRegex = /[^A-Za-z0-9]/g;
            const separator = dateString.match(separatorRegex)[0];
            return separator;
        }

        function formatDate(date, format) {
            const map = {
                M: date.getMonth() + 1,
                MM: ('0' + (date.getMonth() + 1)).slice(-2),
                d: date.getDate(),
                dd: ('0' + date.getDate()).slice(-2),
                h: date.getHours(),
                hh: ('0' + date.getHours()).slice(-2),
                m: date.getMinutes(),
                mm: ('0' + date.getMinutes()).slice(-2),
                s: date.getSeconds(),
                ss: ('0' + date.getSeconds()).slice(-2),
                yyyy: date.getFullYear(),
                yy: date.getFullYear().toString().slice(-2)
            };

            return format.replace(/(M+|d+|h+|m+|s+|yyyy|yy)/gi, matched => {
                return map[matched];
            });
        }

    }

});