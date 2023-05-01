if (calendarEditor == null) var calendarEditor = {};

calendarEditor.calendarEl;
calendarEditor.calendar;
calendarEditor.listEvents;
calendarEditor.startName;
calendarEditor.outApplication;
calendarEditor.outModule;
calendarEditor.requesting = false;

calendarEditor.setEvents = function(calendarEvents) {
    var events = calendarEditor.calendar.getEvents();
    events.forEach(function(event) {
        event.remove();
    });
    console.log("old vacio" + events);
    calendarEditor.listEvents = JSON.parse(calendarEvents);
    console.log(calendarEditor.listEvents);
    for (let e of calendarEditor.listEvents) {
        calendarEditor.calendar.addEvent({
            title: e.title,
            start: e.start,
            end: e.end,
            "extendedProps": {
                "key": e.key
            }
        });
        calendarEditor.listEvents = [];
        calendarEditor.startName = e.startName;
    }
    console.log(events);
    let result = {
        "result": {
            "application": calendarEditor.outApplication,
            "module": calendarEditor.outModule
        }
    };
    console.log(result);
    calendarEditor.requesting = false;
    //openxava.resetRequesting(result);
    //if (openxava.isRequesting(outApplication, outModule)) openxava.resetRequesting(result);
    //console.log("isRequesting = " + openxava.isRequesting(outApplication, outModule));
    
}

openxava.addEditorInitFunction(function() {
    if ($("#xava_calendar").length) {
        var application = $('#xava_calendar_application').val();
        var module = $('#xava_calendar_module').val();
        var dateFormat = $('#xava_calendar_dateFormat').val();
        var newAction = $("#xava_calendar_action").val().split(",")[1];
        var selectAction = $("#xava_calendar_action").val().split(",")[0];
        var formattedDate = "";
        calendarEditor.outApplication = application;
        calendarEditor.outModule = module;
        
        calendarEditor.listEvents = [];
        console.log(application + " " + module + " " + dateFormat + " " + newAction + " " + selectAction);
        calendarEditor.requesting = true;
        Calendar.getEvents(application, module, "", calendarEditor.setEvents);
        //openxava.setRequesting(application, module);

        $("#xava_calendar").ready(function() {
            console.log("dentro");
            calendarEditor.calendarEl = $('#xava_calendar')[0];
            calendarEditor.calendar = new FullCalendar.Calendar(calendarEditor.calendarEl, {
                initialView: 'dayGridMonth',
                editable: true,
                locale: navigator.language,
                //showNonCurrentDates: false,
                displayEventTime: false,
                events: calendarEditor.listEvents,
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
                            if (calendarEditor.requesting) return;
                            calendarEditor.requesting = true;
                            //console.log("isRequesting = " + openxava.isRequesting(application, module));
                            //if (openxava.isRequesting(application, module)) return;
                            //openxava.setRequesting(application, module);
                            let currentViewDate = calendarEditor.calendar.view.currentStart;
                            let currentMonth = currentViewDate.getMonth();
                            let currentYear = currentViewDate.getFullYear();
                            calendarEditor.calendar.next();
                            let nextViewDate = calendarEditor.calendar.view.currentStart;
                            let nextMonth = nextViewDate.getMonth();
                            let nextYear = nextViewDate.getFullYear();
                            let monthYear = (currentYear != nextYear || currentMonth != nextMonth) ? nextMonth + "_" + nextYear : "";
                            console.log("next " + monthYear);
                            Calendar.getEvents(application, module, monthYear, calendarEditor.setEvents);
                        }
                    },
                    prev: {
                        click: function() {
                            if (calendarEditor.requesting) return;
                            calendarEditor.requesting = true;
                            //console.log("isRequesting = " +  openxava.isRequesting(application, module));
                            //if (openxava.isRequesting(application, module)) return;
                            //openxava.setRequesting(application, module);
                            let currentViewDate = calendarEditor.calendar.view.currentStart;
                            let currentMonth = currentViewDate.getMonth();
                            let currentYear = currentViewDate.getFullYear();
                            calendarEditor.calendar.prev();
                            let prevViewDate = calendarEditor.calendar.view.currentStart;
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
                            calendarEditor.calendar.today();
                            Calendar.getEvents(application, module, currentMonth, calendarEditor.setEvents);

                        }
                    }
                },
                eventClick: function(e) {
                    if (calendarEditor.requesting) return;
                    if (!getSelection().toString()) {
                        openxava.executeAction(application, module, false, false, selectAction, 'calendarKey=' + e.event.extendedProps.key);
                    }
                },
                dateClick: function(e) {
                    if (calendarEditor.requesting) return;
                    let selectedDate = reformatDate(e.dateStr);
                    let value = 'startDate=' + calendarEditor.startName + '_' + selectedDate;
                    if (!getSelection().toString()) {
                        //openxava.executeAction(application, module, false, false, newAction, 'value=' + JSON.stringify(value).replaceAll(",", "_"));
                        openxava.executeAction(application, module, false, false, newAction, value);
                    }
                }
            });
            calendarEditor.calendar.render();
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