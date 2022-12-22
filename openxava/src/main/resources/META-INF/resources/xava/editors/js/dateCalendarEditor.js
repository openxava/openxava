// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
    if (openxava.browser.htmlUnit) return;
    var withEnter = false;
    var enterDate;
    var onOpenDateTime;
    var validDate = false;
    var regExp = /[0-9]+:\.\/-/g;
    
    $('.xava_date > input').keydown(function(event) {
        console.log("keydown");
        var keycode = event.keyCode || event.which;
        if (keycode == 13) {
            enterDate = $(this).val();
            validDate = regExp.test(enterDate)?true:false;
            if ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && enterDate.length > 9) {
                withEnter = false;
            } else {
                console.log("enter");
                withEnter = true;
            }
        }
    });
    
    $('.xava_date > input').on('blur', function() {
        enterDate = $(this).val();
        validDate = regExp.test(enterDate)?true:false;
        console.log(validDate);
    });
    
    $('.xava_date > input').change(function() {
        var dateFormat = $(this).parent().data("dateFormat");
        var date = withEnter?enterDate:$(this).val();
        if (date === "") return;
        date = date.trim();
        if (date.length < 6 && date.includes(":")) {
        } else {
            var separator = dateFormat.substr(1, 1);
            var idx = date.lastIndexOf(separator);
            if (idx < 0) {
                if (date.length % 2 != 0) date = " " + date;
                var inc = dateFormat.substr(0, 1) === 'Y'?2:0;
                console.log(inc);
                var last = date.substring(4 + inc);
                console.log(last);
                var middle = date.substring(2 + inc, 4 + inc);
                console.log(middle);
                var first = date.substring(0, 2 + inc);
                console.log(first);
                date = first + separator + middle + separator + last;
                console.log(date);
                date = date.trim();
            }
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
                var prefix = year > 50?"19":"20";
                date = dateNoYear + separator + prefix + year + suffix + time;
            }
            date = date.includes(".20 ") ? date.replace(".20 ", " ") : date;
            $(this).val(date);
            console.log("function");
            console.log(date);
            enterDate = undefined;
            withEnter = false;
        }
    });
    $('.flatpickr-calendar').remove();
    $('.xava_date').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
        onOpen: function(selectedDates, dateStr, instance) {
            console.log("onOpen");
            console.log(selectedDates);
            console.log(dateStr);
            onOpenDateTime = dateStr;
        },
        onChange: function(selectedDates, dateStr, instance) {
            dateStr = validDate?dateStr:$('.xava_date > input').val('');
            selectedDates  = validDate?selectedDates:$('.xava_date > input').val('');
            console.log("onChange");
            console.log(selectedDates);
            console.log(dateStr);
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
                } else {
                    console.log("else");
                    console.log(dateStr);
                    $(instance.input).attr('value', dateStr);
                }
            }
        },
        onClose: function(selectedDates, dateStr, instance) {
            console.log("onClose");
            console.log(selectedDates);
            console.log(dateStr);
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
});