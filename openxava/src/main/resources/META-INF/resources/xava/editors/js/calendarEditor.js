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
    listEvents = JSON.parse(calendarEvents);
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
}

openxava.addEditorInitFunction(function() {
    if ($("#xava_calendar").length) {
        var application = $('#xava_calendar_application').val();
        var module = $('#xava_calendar_module').val();
        var dateFormat = $('#xava_calendar_dateFormat').val();
        var newAction = $("#xava_calendar_action").val().split(",")[1];
        var selectAction = $("#xava_calendar_action").val().split(",")[0];
        
        //tiempo
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
                //events: JSON.parse(events),
                events: listEvents,
                editable: true,
                //initialDate: initial,
                progressiveEventRendering: true,
                headerToolbar: {
                    left: 'prev,next',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                views: {
                    dayGrid: {
                        eventTimeFormat: noTime
                    },
                    timeGrid: {
                        eventTimeFormat: onlyTime
                    },
                    day: {
                        eventTimeFormat: onlyTime
                    }
                },
                customButtons: {
                    next: {
                        click: function() {
                            let currentViewDate = calendar.view.currentStart;
                            let currentMonth = currentViewDate.getMonth();
                            calendar.next();
                            let nextViewDate = calendar.view.currentStart;
                            let nextMonth = nextViewDate.getMonth();
                            let month = (currentMonth != nextMonth) ? nextMonth : "";
                            Calendar.getEvents(application, module, month, calendarEditor.setEvents);
                        }
                    },
                    prev: {
                        click: function() {
                            let currentViewDate = calendar.view.currentStart;
                            let currentMonth = currentViewDate.getMonth();
                            calendar.prev();
                            let prevViewDate = calendar.view.currentStart;
                            let prevMonth = prevViewDate.getMonth();
                            let month = (currentMonth != prevMonth) ? prevMonth : "";
                            Calendar.getEvents(application, module, month, calendarEditor.setEvents);
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