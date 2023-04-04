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
        var listaEventos = "";
        var calendarjQ;
        
        $("#xava_calendar").ready(function() {
            console.log("dentro");
            if (listaEventos === ""){
                listaEventos = createEvents();
            }
            var calendarEl = $('#xava_calendar')[0];
            var calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                headerToolbar: {
                    left: 'prev2,today,next2',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                events: listaEventos,
                editable: true,
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
                locale: navigator.language,
                customButtons: {
      next2: {
        text: 'next2',
        click: function() {
          //alert('clicked custom button 1!');
             //openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, 'Calendar.next', 'month=next');
            calendar.next();
            console.log(listaEventos);
            calendar._initialDate = "2023-05-01";
        }
      },
      prev2: {
        text: 'prev2',
        click: function() {
          //alert('clicked custom button 2!');
            //openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, 'Calendar.prev', 'month=prev');    
            getPrev();
            //calendar.refetchEvents();
            //calendarEl.load($('#xava_calendar')[0]);
            calendar.prev();
//            Calendar.test("hola");
            //calendar._initialDate = "2023-03-01";
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
            var calendarjQ = $(calendarEl);
            
        });


        function createEvents() {
            // probar despues con return events directamente cuando es un modulo sin registros
            let listEvents = "";
            if (events.length > 0) {
                listEvents = events;
            }
            console.log(listEvents);
            listaEventos = JSON.parse(listEvents);
            return listaEventos;
        }

        function reformatDate(date) {
            date = (date.toString().length < 11)? date + 'T00:00:00' : date;
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
        
        function getPrev(){
            //listaEventos =  createEvents();

            let json = 
                [
  {
    "start": "2023-03-28 00:00:00.0",
    "end": "2023-03-28 11:11:11.1",
    "title": "Año: 2023-Año: 2023-Número: 2",
    "extendedProps": {
      "row": "1"
    }
  },
  {
    "start": "2023-03-27 00:00:00.0",
    "end": "2023-03-27 11:11:11.1",
    "title": "Año: 2023-Año: 2023-Número: 3",
    "extendedProps": {
      "row": "2"
    }
  }
];
            let jsonString = JSON.stringify(json);
            listaEventos = JSON.parse(jsonString);
            //calendarjQ.load($('#xava_calendar')[0]);
            $('#xava_calendar')[0].load($('#xava_calendar')[0]);
            calendarjQ.load(calendarjQ);
            console.log("luego de load");
            Calendar.test(calendarRequestApplication,calendarRequestModule);
            //Calendar.getEvents(calendarRequestApplication,calendarRequestModule, tab, view);
        }

    }

});