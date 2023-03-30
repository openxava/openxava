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
                    reformatDate(e.date);
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