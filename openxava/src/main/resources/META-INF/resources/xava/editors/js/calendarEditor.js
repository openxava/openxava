if (calendarEditor == null) var calendarEditor = {};

calendarEditor.calendarEl;
calendarEditor.calendar;
calendarEditor.listEvents;
calendarEditor.startName;
calendarEditor.outApplication;
calendarEditor.outModule;
calendarEditor.requesting = false;

calendarEditor.setEvents = function(calendarEvents) {
	var arr = JSON.parse(calendarEvents);
	calendarEditor.startName = arr[0].startName;
	calendarEditor.calendar.addEventSource(arr);
    calendarEditor.requesting = false;
}


openxava.addEditorInitFunction(function() {
    if ($("#xava_calendar").length) {
        var application = $('#xava_calendar_application').val();
        var module = $('#xava_calendar_module').val();
        var dateFormat = $('#xava_calendar_dateFormat').val();
        var newAction = $("#xava_calendar_action").val().split(",")[1];
        var selectAction = $("#xava_calendar_action").val().split(",")[0];
		var moduleHasDateTime = $('#xava_calendar_hasDateTime').val();
		var calendarViews = moduleHasDateTime === 'true' ? 'dayGridMonth,timeGridWeek,timeGridDay' : '';
		var displayTime = calendarViews === 'dayGridMonth,timeGridWeek,timeGridDay' ? 'true' : 'false'; 
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
                displayEventTime: true,
                events: calendarEditor.listEvents,
				dayMaxEventRows: true,
                editable: true,
                progressiveEventRendering: true,
				eventColor: 'var(--color)',
				defaultTimedEventDuration: '00:30',
				allDaySlot: false,
				eventTimeFormat: {
					hour: 'numeric',
					minute: '2-digit',
				},
				viewClassNames: function(info){
					if (info.view.type === 'timeGridWeek' || info.view.type === 'timeGridDay'){
						calendarEditor.calendar.setOption('displayEventTime', true);
						const h2 = calendarElement.querySelector(".fc-toolbar-title");
						if (h2.textContent !== info.view.title) formatTitle(info.view.title);
					} else if (info.view.type === 'dayGridMonth') {
						if (displayTime === 'true') {
							calendarEditor.calendar.setOption('displayEventTime', true);
						} else {
							calendarEditor.calendar.setOption('displayEventTime', false);
						}
						const h2 = calendarElement.querySelector(".fc-toolbar-title");
						if (h2.textContent !== info.view.title) formatTitle(info.view.title);
					}
				},
				headerToolbar: {
					left: 'prev,next title',
					center: '',
					right: calendarViews
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
						$(e.el).css('z-index', 8);
						$('.fc-event-tooltip').remove();
                        openxava.executeAction(application, module, false, false, selectAction, 'calendarKey=' + e.event.extendedProps.key);
                    }
                },
                dateClick: function(e) {
                    if (calendarEditor.requesting) return;
                    let selectedDate = reformatDate(e.dateStr);
                    let value = 'defaultValues=' + calendarEditor.startName + ':' + selectedDate;
                    if (!getSelection().toString()) {
                        openxava.executeAction(application, module, false, false, newAction, value);
                    }
                },
				 eventMouseEnter: function(info) {
					var tis=info.el;
					var popup=info.event.title;
					var tooltip = document.createElement('div');
					tooltip.className = 'fc-event-tooltip';
					tooltip.style.top = ($(tis).offset().top - 5) + 'px';
					tooltip.style.left = ($(tis).offset().left + ($(tis).width()) / 2) + 'px';
					var contentDiv = document.createElement('div');
					contentDiv.textContent = popup;
					tooltip.appendChild(contentDiv);
					document.body.appendChild(tooltip);
				},
				eventMouseLeave: function(info) {
					$(info.el).css('z-index', 8);
					$('.fc-event-tooltip').remove();
				}
            });
            calendarEditor.calendar.render();
            formatTitle(null);
        });

        function reformatDate(date) {
            date = (date.toString().length < 11) ? date + 'T00:00:00' : date;
            let d = new Date(date);
            formattedDate = formatDate(d, dateFormat);
            return formattedDate;
        }

        function getEvents(month) {
            if (calendarEditor.requesting) return;
            let currentViewDate = calendarEditor.calendar.view.currentStart;
            let currentMonth = currentViewDate.getMonth();
            let currentYear = currentViewDate.getFullYear();
            cleanTitle();
            month === 'next' ? calendarEditor.calendar.next() : calendarEditor.calendar.prev();
            formatTitle(null);
            let newViewDate = calendarEditor.calendar.view.currentStart;
            let newMonth = newViewDate.getMonth();
            let newYear = newViewDate.getFullYear();
            let monthYear = (currentYear != newYear || currentMonth != newMonth) ? newMonth + "_" + newYear : "";
			
            if (monthYear !== "")  { 
				calendarEditor.requesting = true;
				calendarEditor.calendar.getEventSources().forEach(function (source) {
					source.remove();
				});
				Calendar.getEvents(application, module, monthYear, calendarEditor.setEvents);
			}
        }

        function formatDate(date, format) {
			
            const map = {
                M: date.getMonth() + 1,
                MM: ('0' + (date.getMonth() + 1)).slice(-2),
                d: date.getDate(),
                dd: ('0' + date.getDate()).slice(-2),
				H: date.getHours(), //24hs format
				HH: ('0' + date.getHours()).slice(-2), //24hs format 2 digit
				h: date.getHours() % 12 || 12, //12hs format
				hh: date.getHours() === 0 ? '00' : ('0' + (date.getHours() % 12 || 12)).slice(-2), //12hs format 2 digit
                m: date.getMinutes(),
                mm: ('0' + date.getMinutes()).slice(-2),
                s: date.getSeconds(),
                ss: ('0' + date.getSeconds()).slice(-2),
				K: date.getHours() >= 12 ? 'PM' : 'AM',
                yyyy: date.getFullYear(),
                yy: date.getFullYear().toString().slice(-2)
            };
            
            return format.replace(/(M+|d+|h+|m+|s+|yyyy|yy|K)/gi, matched => {
                return map[matched];
            });
        }
        
        function formatTitle(title){
			const h2 = calendarElement.querySelector(".fc-toolbar-title");
			if (title !== null){
				h2.textContent = title;
			}
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

    }

});