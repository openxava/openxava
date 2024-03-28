// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
if (timeCalendarEditor == null) var timeCalendarEditor = {};

openxava.addEditorInitFunction(function() {
    openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");
    if (openxava.browser.htmlUnit) return;

    timeCalendarEditor.onOpenDateTime;
    timeCalendarEditor.onChangeChecked = false;
    timeCalendarEditor.calendarOpen = false;
    timeCalendarEditor.valueOnFocus;
    timeCalendarEditor.stopChange;

    //for fix chinese trad timedate
    timeCalendarEditor.inputElementList = $('.xava_time > input').toArray();
    timeCalendarEditor.inputValueList = [];
    $('.xava_time > input').each(function() {
        var inputValue = $(this).val();
        timeCalendarEditor.inputValueList.push(inputValue);
    });
    timeCalendarEditor.isZh = false;
    timeCalendarEditor.lastValue;

    $('.xava_time > input').on('focus click', function() {
        timeCalendarEditor.valueOnFocus = $(this).val();
    });

    $('.xava_time > input').change(function() {
        console.log("change");
        if (timeCalendarEditor.stopChange) {
            $(this).val(timeCalendarEditor.valueOnFocus);
        }
    });

    $('.xava_time > input').on('blur', function() {
        console.log("blur");
        $(this).val(timeCalendarEditor.valueOnFocus);
        if (timeCalendarEditor.isZh) {
            timeCalendarEditor.stopChange = true;
        }
    });

    $('.xava_time').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
        enableTime: true,
        noCalendar: true,

        onOpen: function(selectedDates, dateStr, instance) {
            timeCalendarEditor.calendarOpen = true;
            timeCalendarEditor.onOpenDateTime = dateStr;
            timeCalendarEditor.stopChange = false;
            if (timeCalendarEditor.isZh && dateStr.includes('PM') && instance.amPM.innerHTML === 'AM') {
                instance.amPM.innerHTML = 'PM';
            }
        },
        onChange: function(selectedDates, dateStr, instance) {
            console.log("onchange");
            if (timeCalendarEditor.calendarOpen === true) {
                $(instance.input).data("changedCancelled", true);
            } else {
                console.log(timeCalendarEditor.valueOnFocus);
                console.log(instance.input.id);
                console.log(dateStr);
                if (timeCalendarEditor.valueOnFocus != null && timeCalendarEditor.valueOnFocus == dateStr) {
                    $(instance.input).data("changedCancelled", true);
                } else if (timeCalendarEditor.valueOnFocus != dateStr) {
                    if (timeCalendarEditor.isZh && $('#' + instance.input.id).attr('value') != timeCalendarEditor.valueOnFocus) {
                        $(instance.input).removeData("changedCancelled");
                        $(instance.input).change();
                    } else {
                        $(instance.input).data("changedCancelled", true);
                    }
                }
            }
            if (timeCalendarEditor.stopChange) {
                instance.input.value = timeCalendarEditor.valueOnFocus;
            }
        },
        onClose: function(selectedDates, dateStr, instance) {
            if (timeCalendarEditor.onOpenDateTime != dateStr) {
                $(instance.input).removeData("changedCancelled");
                $(instance.input).change();
            } else {
                $(instance.input).data("changedCancelled", true);
            }
            timeCalendarEditor.calendarOpen = false;
        },
        onReady: function(selectedDates, dateStr, instance) {
            if (openxava.language === 'zh') {
                timeCalendarEditor.isZh = true;
                for (var i = 0; i < timeCalendarEditor.inputElementList.length; i++) {
                    if (instance.input === timeCalendarEditor.inputElementList[i]) {
                        instance.input.value = timeCalendarEditor.inputValueList[i];
                    }
                }
            }
        }
    });

});