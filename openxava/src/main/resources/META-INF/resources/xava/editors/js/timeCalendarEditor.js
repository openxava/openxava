// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
if (timeCalendarEditor == null) var timeCalendarEditor = {};

openxava.addEditorInitFunction(function() {
	openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");
    if (openxava.browser.htmlUnit) return;

    timeCalendarEditor.onOpenDateTime;
    timeCalendarEditor.onChangeChecked = false;
    timeCalendarEditor.calendarClosed = false;
	
	//for fix chinese trad timedate
	timeCalendarEditor.inputElementList = $('.xava_time > input').toArray();
	timeCalendarEditor.inputValueList = [];
	$('.xava_time > input').each(function() {
		var inputValue = $(this).val();
		timeCalendarEditor.inputValueList.push(inputValue);
	});
	timeCalendarEditor.isZh = false;

    $('.flatpickr-calendar').remove();
	
	$('.xava_time').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
		enableTime: true,
		noCalendar: true,
		
        onOpen: function(selectedDates, dateStr, instance) {
            timeCalendarEditor.onOpenDateTime = dateStr;
			if (timeCalendarEditor.isZh && dateStr.includes('PM') && instance.amPM.innerHTML === 'AM'){
					instance.amPM.innerHTML = 'PM';
			} 
        },
		onChange: function(selectedDates, dateStr, instance) {
            if (timeCalendarEditor.onOpenDateTime != null) {
                if (timeCalendarEditor.onOpenDateTime == dateStr) {
                    $(instance.input).data("changedCancelled", true);
                } else {
                    $(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                }
                //dateCalendarEditor.onOpenDateTime = undefined;
            } else {
                if (dateStr === $(instance.input).attr('value')) {
                    $(instance.input).data("changedCancelled", true);
                } else {
                    $(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                }
            }
        },
		onClose: function(selectedDates, dateStr, instance) {
            if (timeCalendarEditor.onOpenDateTime != null) {
                if (timeCalendarEditor.onOpenDateTime == dateStr) {
                    $(instance.input).data("changedCancelled", true);
                } else {
                    $(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                    $('.xava_time > input').change();
                }
                //dateCalendarEditor.calendarClosed = true;
            }
        },
		onReady: function(selectedDates, dateStr, instance) {
			if (openxava.language === 'zh'){
				timeCalendarEditor.isZh = true;
				for (var i = 0; i < timeCalendarEditor.inputElementList.length; i++) {
					if (instance.input === timeCalendarEditor.inputElementList[i]) instance.input.value = timeCalendarEditor.inputValueList[i];
				}
			}
		}
    });
    
});