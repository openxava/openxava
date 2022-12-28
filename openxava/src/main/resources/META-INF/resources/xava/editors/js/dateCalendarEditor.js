// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");


openxava.addEditorInitFunction(function() {
    if (openxava.browser.htmlUnit) return;
    var readInput = false;
    var enterDate;
    var onOpenDateTime;
    var invalid = false;
    var invalidDate;
    var onChangeChecked = false;

    $('.xava_date > input').keydown(function(event) {
        var keycode = event.keyCode || event.which;
        if (keycode == 13) {
            enterDate = validInputOnlyDate($(this).val());
            readInput = ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && enterDate.length > 9)?false:true;
        }
    });
    
    $('.xava_date > input').on('blur', function() {
        console.log("onBlur");
        enterDate = validInputOnlyDate($(this).val());
        readInput = ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && enterDate.length > 9)?false:true;
    });
    
    $('.xava_date > input').change(function() {
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
        validDate(date, dateFormat, separator);
        console.log("invalid after validDate " + invalid);
        // if (invalid == true) {
        //     $(this).val(invalidDate);
        //     invalid = false;
        //     invalidDate = undefined;
        //     enterDate = undefined;
        //     readInput = false;
        //     return;
        // }
        console.log("before");
        console.log(date);
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
        console.log("after: " + invalid);
        console.log(date);
        $(this).val(date);
        enterDate = undefined;
        readInput = false;
        if (onChangeChecked == true && invalid == true){
            onChangeChecked = false;
            invalid = false;
            invalidDate = undefined;
        }
        console.log("invalid final:" + invalid);
    });
    $('.flatpickr-calendar').remove();
    $('.xava_date').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
        onOpen: function(selectedDates, dateStr, instance) {
            onOpenDateTime = dateStr;
        },
        onChange: function(selectedDates, dateStr, instance) {
            dateStr = invalid?invalidDate:dateStr;
            console.log("onChange" + dateStr + " " +$(instance.input).attr('value'));
            if (onOpenDateTime != null) {
                if (onOpenDateTime.length > 10) {
                    $(instance.input).data("changedCancelled", true);
                } else {
                    if (onOpenDateTime == dateStr) {
                        $(instance.input).data("changedCancelled", true);
                    } else {
                        $(instance.input).attr('value', dateStr);
                    }
                }
            } else {
                if (dateStr === $(instance.input).attr('value')) {
                    $(instance.input).data("changedCancelled", true);
                    onChangeChecked = invalid?true:false;
                } else {
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
                    $(instance.input).attr('value', dateStr);
                    $('.xava_date > input').change();
                }
            }
        },
    });

    function validInputOnlyDate(date) {
        var pattern = /[^.\-:\/\d\s]/g;
        if (pattern.test(date)) {
            invalidDate = date;
            invalid = true;
        }
        return date;
    }

    function validDate(date, format, separator) {
        //console.log(date);
        if (date.length > 11){
            var dateWithTime = date.split(" ");
            date = dateWithTime[0];
        }
        var splittedDate = date.split(separator);
        if (format.substr(0, 1) === 'Y') {
            if (format.substr(2, 3) === 'd' || format.substr(2, 3) === 'j') {
                if (parseInt(splittedDate[1]) > 31 || parseInt(splittedDate[2] > 12)) {
                    invalid = true;
                    invalidDate = date;
                }
            } else {
                if (parseInt(splittedDate[1]) > 12 || parseInt(splittedDate[2] > 31)) {
                    invalid = true;
                    invalidDate = date;
                }
            }
        } else {
            if (format.substr(0, 1) === 'd' || format.substr(0, 1) === 'j') {
                if (parseInt(splittedDate[0]) > 31 || parseInt(splittedDate[1] > 12)) {
                    invalid = true;
                    invalidDate = date;
                }
            } else {
                if (parseInt(splittedDate[0]) > 12 || parseInt(splittedDate[1] > 31)) {
                    invalid = true;
                    invalidDate = date;
                }
            }
        }
    }
    
});