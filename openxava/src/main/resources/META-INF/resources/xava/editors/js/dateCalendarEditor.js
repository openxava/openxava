// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
if (dateCalendarEditor == null) var dateCalendarEditor = {};

openxava.addEditorInitFunction(function() {
    openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");
    if (openxava.browser.htmlUnit) return;
    dateCalendarEditor.readInput = false;
    dateCalendarEditor.enterDate;
    dateCalendarEditor.onOpenDateTime;
    dateCalendarEditor.invalid = false;
    dateCalendarEditor.invalidDate;
    dateCalendarEditor.onChangeChecked = false;
    dateCalendarEditor.calendarClosed = false;

    //for fix chinese trad timedate
    dateCalendarEditor.inputElementList = $('.xava_date > input').toArray();
    dateCalendarEditor.inputValueList = [];
    $('.xava_date > input').each(function() {
        var inputValue = $(this).val();
        dateCalendarEditor.inputValueList.push(inputValue);
    });
    dateCalendarEditor.isZh = false;

    $('.xava_date > input').keydown(function(event) {
        var keycode = event.keyCode || event.which;
        if (keycode == 13) {
            dateCalendarEditor.enterDate = dateCalendarEditor.validInputOnlyDate($(this).val());
            dateCalendarEditor.readInput = ((dateCalendarEditor.enterDate.includes("/") || dateCalendarEditor.enterDate.includes(".") || dateCalendarEditor.enterDate.includes("-")) && dateCalendarEditor.enterDate.length > 9) ? false : true;
        }
    });

    $('.xava_date > input').on('blur', function() {
        dateCalendarEditor.enterDate = dateCalendarEditor.validInputOnlyDate($(this).val());
    });

    $('.xava_date > input').change(function() {
        if ($(this).val().length > 0 && $(this).val().length < 3) {
            $(this).val(dateCalendarEditor.formatTwoDigitDate($('.xava_date').data('date-format'), $(this).val()));
        }
        var dateFormat = $(this).parent().data("dateFormat");
        var date = dateCalendarEditor.readInput ? dateCalendarEditor.enterDate : $(this).val();
        if (date === "") return;
        date = date.trim();
        if (date.length < 6 && date.includes(":")) return;
        var separator = dateFormat.substr(1, 1);
        var idx = date.lastIndexOf(separator);
        if (idx < 0) {
            if (date.length % 2 != 0) date = " " + date;
            var inc = dateFormat.substr(0, 1) === 'Y' ? 2 : 0;
            var last = date.substring(4 + inc);
            var middle = date.substring(2 + inc, 4 + inc);
            var first = date.substring(0, 2 + inc);
            date = first + separator + middle + separator + last;
            date = date.trim();
        }
        dateCalendarEditor.validDate(date, dateFormat, separator);
        idx = date.lastIndexOf(separator);
        var idxSpace = date.indexOf(' ');
        var pureDate = date;
        var time = "";
        if (idxSpace >= 0) {
            time = date.substr(idxSpace);
            pureDate = date.substr(0, idxSpace);
        }
        var suffix = "";
        if (idx == pureDate.length - 1) {
            pureDate = pureDate.substring(0, idx);
            suffix = separator;
            idx = pureDate.lastIndexOf(separator);
        }
        if (dateFormat.indexOf('Y') > 0 && pureDate.length - idx < 4) {
            var dateNoYear = pureDate.substring(0, idx);
            var year = pureDate.substring(idx + 1);
            var prefix = year > 50 ? "19" : "20";
            date = dateNoYear + separator + prefix + year + suffix + time;
        }
        date = date.includes(".20 ") ? date.replace(".20 ", " ") : date;
        date = dateCalendarEditor.invalid ? dateCalendarEditor.invalidDate : date;
        $(this).val(date);
        dateCalendarEditor.enterDate = undefined;
        dateCalendarEditor.readInput = false;
        if (dateCalendarEditor.onChangeChecked == true && dateCalendarEditor.invalid == true) {
            dateCalendarEditor.onChangeChecked = false;
            dateCalendarEditor.invalid = false;
            dateCalendarEditor.invalidDate = undefined;
        }
    });
    $('.flatpickr-calendar').remove();
    $('.xava_date').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
        onOpen: function(selectedDates, dateStr, instance) {
			console.log("dateCalendar onOpen");
            dateCalendarEditor.onOpenDateTime = dateStr;
            if (dateCalendarEditor.isZh && dateStr.includes('PM') && instance.amPM.innerHTML === 'AM') {
                instance.amPM.innerHTML = 'PM';
            }
        },
        onChange: function(selectedDates, dateStr, instance) {
			console.log("dateCalendar onChange");
            dateStr = dateCalendarEditor.invalid ? dateCalendarEditor.invalidDate : dateStr;
            if (dateCalendarEditor.onOpenDateTime != null) {
                if (dateCalendarEditor.onOpenDateTime.length > 10) {
                    $(instance.input).data("changedCancelled", true);
                    dateCalendarEditor.onOpenDateTime = dateCalendarEditor.calendarClosed ? undefined : dateCalendarEditor.onOpenDateTime;
                } else {
                    if (dateCalendarEditor.onOpenDateTime == dateStr) {
                        $(instance.input).data("changedCancelled", true);
                    } else {
                        $(instance.input).removeData("changedCancelled");
                        $(instance.input).attr('value', dateStr);
                    }
                    dateCalendarEditor.onOpenDateTime = undefined;
                }
            } else {
                if (dateStr === $(instance.input).attr('value')) {
                    $(instance.input).data("changedCancelled", true);
                    dateCalendarEditor.onChangeChecked = dateCalendarEditor.invalid ? true : false;
                } else {
                    $(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                    dateCalendarEditor.onChangeChecked = dateCalendarEditor.invalid ? true : false;
                }
            }
        },
        onClose: function(selectedDates, dateStr, instance) {
			console.log("dateCalendar onClose");
            if (dateCalendarEditor.onOpenDateTime != null) {
                if (dateCalendarEditor.onOpenDateTime == dateStr) {
                    $(instance.input).data("changedCancelled", true);
                } else {
                    $(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                    $('.xava_date > input').change();
                }
                dateCalendarEditor.calendarClosed = true;
            }
        },
        onReady: function(selectedDates, dateStr, instance) {
			console.log("dateCalendar onReady");
            if (openxava.language === 'zh') {
                dateCalendarEditor.isZh = true;
                for (var i = 0; i < dateCalendarEditor.inputElementList.length; i++) {
                    if (instance.input === dateCalendarEditor.inputElementList[i]) instance.input.value = dateCalendarEditor.inputValueList[i];
                }
            }
        }
    });

    dateCalendarEditor.validInputOnlyDate = function(date) {
        var pattern = /[^.\-:\/\d\s]/g;
        if (pattern.test(date)) {
            dateCalendarEditor.invalidDate = date;
            dateCalendarEditor.invalid = true;
        }
        return date;
    }

    dateCalendarEditor.validDate = function(date, format, separator) {
        if (date.length > 11) {
            var dateWithTime = date.split(" ");
            date = dateWithTime[0];
        }
        var splittedDate = date.split(separator);
        if (format.substr(0, 1) === 'Y') {
            if (format.substr(2, 3) === 'd' || format.substr(2, 3) === 'j') {
                if (parseInt(splittedDate[1]) > 31 || parseInt(splittedDate[2]) > 12) {
                    dateCalendarEditor.invalid = true;
                    dateCalendarEditor.invalidDate = date;
                }
            } else {
                if (parseInt(splittedDate[1]) > 12 || parseInt(splittedDate[2]) > 31) {
                    dateCalendarEditor.invalid = true;
                    dateCalendarEditor.invalidDate = date;
                }
            }
        } else {
            if (format.substr(0, 1) === 'd' || format.substr(0, 1) === 'j') {
                if (parseInt(splittedDate[0]) > 31 || parseInt(splittedDate[1]) > 12) {
                    dateCalendarEditor.invalid = true;
                    dateCalendarEditor.invalidDate = date;
                }
            } else {
                if (parseInt(splittedDate[0]) > 12 || parseInt(splittedDate[1]) > 31) {
                    dateCalendarEditor.invalid = true;
                    dateCalendarEditor.invalidDate = date;
                }
            }
        }
    }

});

dateCalendarEditor.formatTwoDigitDate = function(dateFormat, number) {
    var today = new Date();
    var year = today.getFullYear();
    var month = today.getMonth();
    var date = new Date(year, month, number);
    var formattedDate = dateFormat
        .replace('d', ('0' + date.getDate()).slice(-2))
        .replace('j', date.getDate())
        .replace('m', ('0' + (date.getMonth() + 1)).slice(-2))
        .replace('n', date.getMonth() + 1)
        .replace('Y', date.getFullYear())
        .replace('H', "")
        .replace('h', "")
        .replace('G', "")
        .replace('i', "")
        .replace('K', "");
    return formattedDate;
}