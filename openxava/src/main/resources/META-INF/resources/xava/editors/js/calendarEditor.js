if (calendarEditor == null) var calendarEditor = {};

var calendarEl;
var calendar;
var listEvents;
var setEvents = true;


calendarEditor.setEvents = function(calendarEvents) {
    console.log("entra");
    //console.log(calendarEvents);
    // sacar el if cuando tab funcione bien
    var events = calendar.getEvents();
    console.log("old events");
    console.log(events);
    events.forEach(function(event) {
        event.remove();
    });
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
        listEvents = [];
    }
}


openxava.addEditorInitFunction(function() {
    
    

    if ($("#xava_calendar").length) {
        
var application = $('#xava_calendar_application').val();
var module = $('#xava_calendar_module').val();
var dateFormat = $('#xava_calendar_dateFormat').val();
var newAction = $("#xava_calendar_action").val().split(",")[1];
var selectAction = $("#xava_calendar_action").val().split(",")[0];
        
        
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
        //Calendar.getEvents(application, module, "", calendarEditor.setEvents);


        $("#xava_calendar").ready(function() {
            console.log("dentro");
            calendarEl = $('#xava_calendar')[0];
            calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                editable: true,
                locale: navigator.language,
                showNonCurrentDates: false,
                displayEventTime: false,
                events: JSON.parse(events),
                editable: true,
                //initialDate: initial,
                progressiveEventRendering: true,
                headerToolbar: {
                    left: 'prev2,today,next2',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
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

                            console.log("after remove");
                            console.log(calendar.getEvents());
                            //debo cambiar ahora el mes para obtener el mes actual en la vista
                            let currentViewDate = calendar.view.activeStart;
                            let currentMonth = currentViewDate.getMonth();
                            calendar.prev();
                            let prevViewDate = calendar.view.activeStart;
                            let prevMonth = prevViewDate.getMonth();
                            let month = (currentMonth != prevMonth) ? prevMonth : "";
                            //console.log(month);
                            //obtener los eventos del nuevo filtro en un array con dwr
                            //y agregarlos con calendar.add
                            Calendar.getEvents(application, module, month, calendarEditor.setEvents);
                            //setEvents = true;

                            console.log("new events");
                            console.log(calendar.getEvents());


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