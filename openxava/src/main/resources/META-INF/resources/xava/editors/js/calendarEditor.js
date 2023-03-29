openxava.addEditorInitFunction(function() {
    
    //alert(da);
    if ($("calendar").length) {
//    var dateFormat = '<%=dateFormat%>';
//    var calendarRequestApplication = '<%=request.getParameter("application")%>';
//    var calendarRequestModule = '<%=request.getParameter("module")%>';
//    var calendarAction = '<%=action%>';
//    var calendarNewAction = '<%=actionNew%>';
//    var events = '<%=events%>';
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

        let ec = new EventCalendar($("#xava_calendar")[0], {
            view: 'dayGridMonth',
            height: '800px',
            headerToolbar: {
                start: 'prev,today,next',
                center: 'title',
                end: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            events: createEvents(),
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
            editable: true,
            displayEventEnd: false,
            pointer: true,
            // para ver solo dia fecha año
            locale: navigator.language,
            eventClick: function(e) {
                if (!getSelection().toString()) {
                    openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, calendarAction, 'row=' + parseInt(e.event.extendedProps.row));
                }
            },
            dateClick: function(e) {
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

        function createEvents() {
            
            // probar despues con return events directamente cuando es un modulo sin registros
            let listEvents = ""; 
            if (events.length > 0) {
                listEvents = events ; 
            } 
            return JSON.parse(listEvents);
        }

        function reformatDate(date) {
            let d = new Date(date);
            console.log(d);
            console.log(dateFormat);
            formattedDate = formatDate(d, dateFormat);
        }

        function getSeparator(date) {
            const separatorRegex = /[^0-9]/g;
            const separator = dateString.match(separatorRegex)[0];
            return separator;
        }

        function formatDate(date, format) {
            const map = {
                M: date.getMonth() + 1,
                d: date.getDate(),
                h: date.getHours(),
                m: date.getMinutes(),
                s: date.getSeconds(),
                y: date.getFullYear().toString().slice(-2),
                yyyy: date.getFullYear()
            };

            return format.replace(/M|d|h|m|s|yyyy|y/gi, matched => {
                return map[matched];
            });
        }

    }

});