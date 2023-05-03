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
    calendarEditor.listEvents = JSON.parse(calendarEvents);
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
    let result = {
        "result": {
            "application": calendarEditor.outApplication,
            "module": calendarEditor.outModule
        }
    };
    calendarEditor.requesting = false;
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
        var calendarElement = document.getElementById('xava_calendar');

        calendarEditor.listEvents = [];
        calendarEditor.requesting = true;
        Calendar.getEvents(application, module, "", calendarEditor.setEvents);

        $("#xava_calendar").ready(function() {
            calendarEditor.calendarEl = $('#xava_calendar')[0];
            calendarEditor.calendar = new FullCalendar.Calendar(calendarEditor.calendarEl, {
                initialView: 'dayGridMonth',
                editable: true,
                locale: navigator.language,
                displayEventTime: false,
                events: calendarEditor.listEvents,
                editable: true,
                progressiveEventRendering: true,
                headerToolbar: {
                    left: 'prev,next title',
                    center: '',
                    right: ''
                },
                customButtons: {
                    next: {
                        click: function() {
                            getEvents("next");
                        }
                    },
                    prev: {
                        click: function() {
                            getEvents("prev");
                        }
                    },
                    today: {
                        click: function() {
                            if (calendarEditor.requesting) return;
                            calendarEditor.requesting = true;
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
                        openxava.executeAction(application, module, false, false, newAction, value);
                    }
                }
            });
            calendarEditor.calendar.render();
            formatTitle();
        });

        function reformatDate(date) {
            date = (date.toString().length < 11) ? date + 'T00:00:00' : date;
            let d = new Date(date);
            formattedDate = formatDate(d, dateFormat);
            return formattedDate;
        }

        function getEvents(month) {
            if (calendarEditor.requesting) return;
            calendarEditor.requesting = true;
            //if (openxava.isRequesting(application, module)) return;
            //openxava.setRequesting(application, module);
            let currentViewDate = calendarEditor.calendar.view.currentStart;
            let currentMonth = currentViewDate.getMonth();
            let currentYear = currentViewDate.getFullYear();
            cleanTitle();
            month === 'next' ? calendarEditor.calendar.next() : calendarEditor.calendar.prev();
            formatTitle();
            let newViewDate = calendarEditor.calendar.view.currentStart;
            let newMonth = newViewDate.getMonth();
            let newYear = newViewDate.getFullYear();
            let monthYear = (currentYear != newYear || currentMonth != newMonth) ? newMonth + "_" + newYear : "";
            Calendar.getEvents(application, module, monthYear, calendarEditor.setEvents);
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
        
        function formatTitle(){
            const h2 = calendarElement.querySelector(".fc-toolbar-title");
            const text = h2.textContent;
            const capitalizedTitle = text.charAt(0).toUpperCase() + text.slice(1);
            h2.textContent = capitalizedTitle;
        }
        
        function cleanTitle(){
            const h2 = calendarElement.querySelector(".fc-toolbar-title");
            h2.textContent = "";
        }
        
        calendarElement.addEventListener('mousewheel', function(event) {
            event.wheelDelta >= 0 ? getEvents('prev') : getEvents('next');
            event.preventDefault();
        });
        
        window.addEventListener('resize', function() {
            calendarElement.style.width = (window.innerHeight * 0.9)+ 'px';
            calendarElement.style.height = (window.innerHeight * 0.763)+ 'px';
        });
    }



});