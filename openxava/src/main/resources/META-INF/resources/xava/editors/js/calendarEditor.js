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

        $("#xava_calendar").ready(function() {
            console.log("dentro");
            var calendarEl = $('#xava_calendar')[0];
            var calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                headerToolbar: {
                    left: 'prev,today,next',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                events: createEvents(),
                editable: true,
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
                eventClick: function(e) {
                    if (!getSelection().toString()) {
                        openxava.executeAction(calendarRequestApplication, calendarRequestModule, false, false, calendarAction, 'row=' + parseInt(e.event.extendedProps.row));
                    }
                },
                dateClick: function(e) {
                    console.log(e);
                    console.log(e.dateStr);
                    reformatDate(e.dateStr);
                    let s = FullCalendar.formatDate(e.date.dateStr,{
                  month: 'long',
                  year: 'numeric',
                    day: 'numeric',
            })
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




        function createEvents() {
            // probar despues con return events directamente cuando es un modulo sin registros
            let listEvents = "";
            if (events.length > 0) {
                listEvents = events;
            }
            return JSON.parse(listEvents);
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

    }

});