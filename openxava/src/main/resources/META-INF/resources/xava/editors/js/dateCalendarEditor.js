// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
if (dateCalendarEditor == null) var dateCalendarEditor = {};

openxava.addEditorInitFunction(function() {
	openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");
    if (openxava.browser.htmlUnit) return;
    var readInput = false;
    var enterDate;
    var onOpenDateTime;
    var invalid = false;
    var invalidDate;
    var onChangeChecked = false;
	var calendarClosed = false;
	 
	//for fix chinese trad timedate
	var inputElementList = $('.xava_date > input').toArray();
	var inputValueList = [];
	$('.xava_date > input').each(function() {
		var inputValue = $(this).val();
		inputValueList.push(inputValue);
	});
	var isZh = false;
	
    $('.xava_date > input').keydown(function(event) {
        var keycode = event.keyCode || event.which;
        if (keycode == 13) {
            enterDate = dateCalendarEditor.validInputOnlyDate($(this).val());
            readInput = ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && enterDate.length > 9)?false:true;
        }
    });
    
    $('.xava_date > input').on('blur', function() {
        enterDate = dateCalendarEditor.validInputOnlyDate($(this).val());
    });
    
    $('.xava_date > input').change(function() {
		if ($(this).val().length > 0 && $(this).val().length < 3) {
			$(this).val(dateCalendarEditor.formatTwoDigitDate($('.xava_date').data('date-format'), $(this).val()));
		}
        var dateFormat = $(this).parent().data("dateFormat");
        var date = readInput?enterDate:$(this).val(); 
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
        date = invalid ? invalidDate : date;
        $(this).val(date);
        enterDate = undefined;
        readInput = false;
        if (onChangeChecked == true && invalid == true){
            onChangeChecked = false;
            invalid = false;
            invalidDate = undefined;
        }
    });
    $('.flatpickr-calendar').remove();
    $('.xava_date').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
        onOpen: function(selectedDates, dateStr, instance) {
            onOpenDateTime = dateStr;
			if (isZh && dateStr.includes('PM') && instance.amPM.innerHTML === 'AM'){
					instance.amPM.innerHTML = 'PM';
			} 
        },
        onChange: function(selectedDates, dateStr, instance) {
            dateStr = invalid?invalidDate:dateStr;
            if (onOpenDateTime != null) {
                if (onOpenDateTime.length > 10) {
                    $(instance.input).data("changedCancelled", true);
					onOpenDateTime = calendarClosed ? undefined : onOpenDateTime;
                } else {
                    if (onOpenDateTime == dateStr) {
                        $(instance.input).data("changedCancelled", true);
                    } else {
						$(instance.input).removeData("changedCancelled");
                        $(instance.input).attr('value', dateStr);
                    }
					onOpenDateTime = undefined;
                }
            } else {
                if (dateStr === $(instance.input).attr('value')) {
                    $(instance.input).data("changedCancelled", true);
                    onChangeChecked = invalid?true:false;
                } else {	
					$(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                    onChangeChecked = invalid?true:false;
                }
            }
        },
        onClose: function(selectedDates, dateStr, instance) {
            if (onOpenDateTime != null) {
                if (onOpenDateTime == dateStr) {
                    $(instance.input).data("changedCancelled", true);
                } else {
					$(instance.input).removeData("changedCancelled");
                    $(instance.input).attr('value', dateStr);
                    $('.xava_date > input').change();
                }
				calendarClosed = true;
            }
        },
		onReady: function(selectedDates, dateStr, instance) {
			if (openxava.language === 'zh'){
				isZh = true;
				for (var i = 0; i < inputElementList.length; i++) {
					if (instance.input === inputElementList[i]) instance.input.value = inputValueList[i];
				}
			}
		}
    });

    dateCalendarEditor.validInputOnlyDate = function(date) {
        var pattern = /[^.\-:\/\d\s]/g;
        if (pattern.test(date)) {
            invalidDate = date;
            invalid = true;
        }
        return date;
    }

    dateCalendarEditor.validDate = function(date, format, separator) {
        if (date.length > 11){
            var dateWithTime = date.split(" ");
            date = dateWithTime[0];
        }
        var splittedDate = date.split(separator);
        if (format.substr(0, 1) === 'Y') {
            if (format.substr(2, 3) === 'd' || format.substr(2, 3) === 'j') {
                if (parseInt(splittedDate[1]) > 31 || parseInt(splittedDate[2]) > 12) {
                    invalid = true;
                    invalidDate = date;
                }
            } else {
                if (parseInt(splittedDate[1]) > 12 || parseInt(splittedDate[2]) > 31) {
                    invalid = true;
                    invalidDate = date;
                }
            }
        } else {
            if (format.substr(0, 1) === 'd' || format.substr(0, 1) === 'j') {
                if (parseInt(splittedDate[0]) > 31 || parseInt(splittedDate[1]) > 12) {
                    invalid = true;
                    invalidDate = date;
                }
            } else {
                if (parseInt(splittedDate[0]) > 12 || parseInt(splittedDate[1]) > 31) {
                    invalid = true;
                    invalidDate = date;
                }
            }
        }
    }
	
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
    
});