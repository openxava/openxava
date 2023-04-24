if (calendarEditor == null) var calendarEditor = {};

var calendarEl;
var calendar;
var listEvents;
var setEvents = true;
var startName;

calendarEditor.setEvents = function(calendarEvents) {
    var events = calendar.getEvents();
    events.forEach(function(event) {
        event.remove();
    });
    console.log("old vacio" + events);
    listEvents = JSON.parse(calendarEvents);
    console.log(listEvents);
    for (let e of listEvents) {
        calendar.addEvent({
            title: e.title,
            start: e.start,
            end: e.end,
            "extendedProps": {
                "key": e.key
            }
        });
        listEvents = [];
        startName = e.startName;
    }
    console.log(events);
}

openxava.addEditorInitFunction(function() {
    if ($("#xava_calendar").length) {
        var application = $('#xava_calendar_application').val();
        var module = $('#xava_calendar_module').val();
        var dateFormat = $('#xava_calendar_dateFormat').val();
        var newAction = $("#xava_calendar_action").val().split(",")[1];
        var selectAction = $("#xava_calendar_action").val().split(",")[0];
        var formattedDate = "";
        //var listaEventos = JSON.parse(events);

        listEvents = [];
        console.log(application + " " + module + " " + dateFormat + " " + newAction + " " + selectAction);
        
        Calendar.getEvents(application, module, "", calendarEditor.setEvents);


        $("#xava_calendar").ready(function() {
            console.log("dentro");
            calendarEl = $('#xava_calendar')[0];
            calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                editable: true,
                locale: navigator.language,
                //showNonCurrentDates: false,
                displayEventTime: false,
                events: listEvents,
                editable: true,
                progressiveEventRendering: true,
                headerToolbar: {
                    left: 'prev,next',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                customButtons: {
                    next: {
                        click: function() {
                            //if (openxava.isRequesting(application, module)) return;
                            let currentViewDate = calendar.view.currentStart;
                            console.log()
                            let currentMonth = currentViewDate.getMonth();
                            let currentYear = currentViewDate.getFullYear();
                            calendar.next();
                            let nextViewDate = calendar.view.currentStart;
                            let nextMonth = nextViewDate.getMonth();
                            let nextYear = nextViewDate.getFullYear();
                            let monthYear = (currentYear != nextYear || currentMonth != nextMonth) ? nextMonth + "_" + nextYear : "";
                            console.log("next " + monthYear);
                            Calendar.getEvents(application, module, monthYear, calendarEditor.setEvents);
                        }
                    },
                    prev: {
                        click: function() {
                            //if (openxava.isRequesting(application, module)) return;
                            let currentViewDate = calendar.view.currentStart;
                            let currentMonth = currentViewDate.getMonth();
                            let currentYear = currentViewDate.getFullYear();
                            calendar.prev();
                            let prevViewDate = calendar.view.currentStart;
                            let prevMonth = prevViewDate.getMonth();
                            let prevYear = prevViewDate.getFullYear();
                            console.log(prevYear);
                            let monthYear = (currentYear != prevYear || currentMonth != prevMonth) ? prevMonth + "_" + prevYear : "";
                            console.log("prev " + monthYear);
                            Calendar.getEvents(application, module, monthYear, calendarEditor.setEvents);
                        }
                    },
                    today: {
                        click: function() {
                            let currentDate = new Date();
                            let currentMonth = currentDate.getMonth();
                            calendar.today();
                            Calendar.getEvents(application, module, currentMonth, calendarEditor.setEvents);

                        }
                    }
                },
                eventClick: function(e) {
                    if (!getSelection().toString()) {
                        openxava.executeAction(application, module, false, false, selectAction, 'calendarKey=' + e.event.extendedProps.key);
                    }
                },
                dateClick: function(e) {
                    let selectedDate = reformatDate(e.dateStr);
                    let value = {
                        "dates": {
                            "name": startName,
                            "date": selectedDate
                        }
                    };
                    if (!getSelection().toString()) {
                        openxava.executeAction(application, module, false, false, newAction, 'value=' + JSON.stringify(value).replaceAll(",", "_"));
                    }
                }
            });
            calendar.render();
        });

        function reformatDate(date) {
            date = (date.toString().length < 11) ? date + 'T00:00:00' : date;
            let d = new Date(date);
            formattedDate = formatDate(d, dateFormat);
            return formattedDate;
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