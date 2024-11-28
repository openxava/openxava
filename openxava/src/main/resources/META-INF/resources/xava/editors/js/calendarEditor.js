if (calendarEditor == null) var calendarEditor = {};

calendarEditor.calendarEl;
calendarEditor.calendar;
calendarEditor.listEvents;
calendarEditor.startName;
calendarEditor.outApplication;
calendarEditor.outModule;
calendarEditor.requesting = false;

calendarEditor.setEvents = function(calendarEvents) {
    try {
        var arr = JSON.parse(calendarEvents);
		calendarEditor.showCalendar();
        calendarEditor.startName = arr[0].startName;
        calendarEditor.calendar.addEventSource(arr);
    } catch (e) {
        calendarEditor.hideCalendar();
    }
    calendarEditor.requesting = false;
}

calendarEditor.hideCalendar = function () {
    if (calendarEditor.calendarEl) {
        calendarEditor.calendarEl.classList.add('ox-display-none');
		var errorElement = $('.ox-calendar-errors');
        errorElement[0].classList.remove('ox-display-none');
    }
}

calendarEditor.showCalendar = function () {
    if (calendarEditor.calendarEl.classList.contains('ox-display-none')) {
        calendarEditor.calendarEl.classList.remove('ox-display-none');
		var errorElement = $('.ox-calendar-errors');
        errorElement[0].classList.add('ox-display-none');
    }
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
		
		const savedState = loadCalendarState(application, module);
        const initialDate = savedState ? savedState.defaultDate : new Date().toISOString().split('T')[0];
        const initialView = savedState ? savedState.defaultView : 'dayGridMonth';

        calendarEditor.outApplication = application;
        calendarEditor.outModule = module;
        var calendarElement = document.getElementById('xava_calendar');

        calendarEditor.listEvents = [];
        calendarEditor.requesting = true;
        var selectedValue = $('#xava_calendar_date_preferences').val();
		
		const dateParts = initialDate.split("-");
		const initialDateYear = dateParts[0];
		const initialDateMonth = parseInt(dateParts[1]) - 1;
		const initialDateMonthYear = initialDateMonth + "_" + initialDateYear;
		
		Calendar.getEvents(application, module, initialDateMonthYear, selectedValue, {
			callback: function(events) {
				saveCalendarState(application, module, calendarEditor.calendar.getDate().toISOString().split('T')[0], calendarEditor.calendar.view.type);
				calendarEditor.setEvents(events);
			},
			errorHandler: function(error) {
				calendarEditor.hideCalendar();
			}
		});
		
        $("#xava_calendar").ready(function() {
            calendarEditor.calendarEl = $('#xava_calendar')[0];
            calendarEditor.calendar = new FullCalendar.Calendar(calendarEditor.calendarEl, {
				initialDate: initialDate,
				initialView: initialView,
                eventStartEditable: true,
                locale: navigator.language,
                displayEventTime: true,
                events: calendarEditor.listEvents,
                dayMaxEventRows: true,
                progressiveEventRendering: true,
                eventColor: 'var(--color)',
                defaultTimedEventDuration: '00:30',
                allDaySlot: false,
                eventTimeFormat: {
                    hour: 'numeric',
                    minute: '2-digit',
                },
                viewClassNames: function(info) {
					saveCalendarState(application, module, calendarEditor.calendar.getDate().toISOString().split('T')[0], info.view.type);
                    if (info.view.type === 'timeGridWeek' || info.view.type === 'timeGridDay') {
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
                    left: 'prevYear,prev,next,nextYear title',
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
                    nextYear: {
                        hint: 'test',
                        click: function() {
                            getEvents("nextYear");
                        }
                    },
                    prevYear: {
                        click: function() {
                            getEvents("prevYear");
                        }
                    },
                    today: {
                        click: function() {
                            if (calendarEditor.requesting) return;
                            calendarEditor.requesting = true;
                            let currentDate = new Date();
                            let currentMonth = currentDate.getMonth();
                            calendarEditor.calendar.today();
                            Calendar.getEvents(application, module, currentMonth, "", calendarEditor.setEvents);
                        }
                    }
                },
                eventClick: function(e) {
                    if (calendarEditor.requesting) return;
					saveCalendarState(application, module, calendarEditor.calendar.getDate().toISOString().split('T')[0], calendarEditor.calendar.view.type);
                    if (!getSelection().toString()) {
                        hideTooltip();
                        openxava.executeAction(application, module, false, false, selectAction, 'calendarKey=' + e.event.extendedProps.key);
                    }
                },
                dateClick: function(e) {
                    if (calendarEditor.requesting) return;
					saveCalendarState(application, module, calendarEditor.calendar.getDate().toISOString().split('T')[0], calendarEditor.calendar.view.type);
                    let selectedDate = reformatDate(e.dateStr);
                    let value = 'defaultValues=' + calendarEditor.startName + ':' + selectedDate;
                    if (!getSelection().toString()) {
                        openxava.executeAction(application, module, false, false, newAction, value);
                    }
                },
                eventMouseEnter: function(info) {
					showTooltip(info.el, info.event.title);
                },
                eventMouseLeave: function(info) {
					hideTooltip();
                },
                eventDrop: function(e) {
                    Calendar.dragAndDrop(application, module, e.event.extendedProps.key, reformatDate(e.event.startStr), e.event.extendedProps.startName);
                },
            });
            calendarEditor.calendar.render();
            formatTitle(null);
            var prevYearButton = document.querySelector('.fc-prevYear-button');
            var nextYearButton = document.querySelector('.fc-nextYear-button');
            prevYearButton.addEventListener('mouseenter', function() {
                showTooltip(this, $('#xava_calendar_prevYear').val());
            });
            prevYearButton.addEventListener('mouseleave', function() {
                hideTooltip();
            });
            nextYearButton.addEventListener('mouseenter', function() {
                showTooltip(this, $('#xava_calendar_nextYear').val());
            });
            nextYearButton.addEventListener('mouseleave', function() {
                hideTooltip();
            });
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
            if (month.length > 4) {
                month === 'nextYear' ? calendarEditor.calendar.nextYear() : calendarEditor.calendar.prevYear();
            } else {
                month === 'next' ? calendarEditor.calendar.next() : calendarEditor.calendar.prev();
            }
            formatTitle(null);
            let newViewDate = calendarEditor.calendar.view.currentStart;
            let newMonth = newViewDate.getMonth();
            let newYear = newViewDate.getFullYear();
            let monthYear = (currentYear != newYear || currentMonth != newMonth) ? newMonth + "_" + newYear : "";

            if (monthYear !== "") {
                calendarEditor.requesting = true;
                calendarEditor.calendar.getEventSources().forEach(function(source) {
                    source.remove();
                });
                var selectedValue = $('#xava_calendar_date_preferences').val();
                Calendar.getEvents(application, module, monthYear, selectedValue, calendarEditor.setEvents);
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

        function formatTitle(title) {
            const h2 = calendarElement.querySelector(".fc-toolbar-title");
            if (title !== null) {
                h2.textContent = title;
            }
            const text = h2.textContent;
            const capitalizedTitle = text.charAt(0).toUpperCase() + text.slice(1);
            h2.textContent = capitalizedTitle;
        }

        function cleanTitle() {
            const h2 = calendarElement.querySelector(".fc-toolbar-title");
            h2.textContent = "";
        }

        function showTooltip(element, text) {
            var span = element.querySelector('span');
            var el = (span === null) ? element : span.getBoundingClientRect();
            var tooltip = document.createElement('div');
            tooltip.className = 'fc-event-tooltip';
            tooltip.style.top = (span === null ? $(element).offset().top : el.top) - 5 + 'px';
            tooltip.style.left = (span === null) ? ($(element).offset().left + ($(element).width()) / 2) + 'px' : (el.left + (el.width / 2)) + 'px';
            var contentDiv = document.createElement('div');
            contentDiv.textContent = text;
            tooltip.appendChild(contentDiv);
            document.body.appendChild(tooltip);
        }

        function hideTooltip() {
            var tooltip = document.querySelector('.fc-event-tooltip');
            if (tooltip) {
                tooltip.parentNode.removeChild(tooltip);
            }
        }

        calendarElement.addEventListener('mousewheel', function(event) {
            event.wheelDelta >= 0 ? getEvents('prev') : getEvents('next');
            event.preventDefault();
        });

        $('.xava_calendar_date_preferences').on('change', function() {
            var selectedOption = $(this).find('option:selected');
            var selectedValue = selectedOption.val();
            var selectedText = selectedOption.text();
            calendarEditor.calendar.getEventSources().forEach(function(source) {
                source.remove();
            });
            let currentViewDate = calendarEditor.calendar.view.currentStart;
            let currentMonth = currentViewDate.getMonth();
            let currentYear = currentViewDate.getFullYear();
            let monthYear = currentMonth + "_" + currentYear;
            Calendar.changeDateProperty(application, module, selectedValue, selectedText, monthYear, calendarEditor.setEvents);
        });
		
		function saveCalendarState(application, module, defaultDate, defaultView) {
			const key = application + '_' + module + '_calendarState';
			const state = { defaultDate, defaultView };
			localStorage.setItem(key, JSON.stringify(state));
		}	

		function loadCalendarState(application, module) {
			const key = application + '_' + module + '_calendarState';
			const state = localStorage.getItem(key);
			return state ? JSON.parse(state) : null;
		}

    }

});