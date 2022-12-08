// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt
openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
    if (openxava.browser.htmlUnit) return;
    var withEnter = false;
    var enterDate;
    var onOpenDateTime;

    $('.xava_date > input').keydown(function(event) {
        var keycode = event.keyCode || event.which;
        if (keycode == 13) {
            enterDate = $(this).val();
            if ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && enterDate.length > 9) {
                withEnter = false;
            } else {
                withEnter = true;
            }
        }
    });
    
    $('.xava_date > input').change(function() {
        var dateFormat = $(this).parent().data("dateFormat");
        var date = withEnter?enterDate:$(this).val();
        if (date === "") return;
        date = date.trim();
        if (date.length > 5 && !date.includes(":")) {
            var separator = dateFormat.substr(1, 1);
            var idx = date.lastIndexOf(separator);
            if (idx < 0) {
                if (date.length % 2 != 0) date = " " + date;
                var inc = dateFormat.substr(0, 1) === 'Y'?2:0;
                var last = date.substring(4 + inc);
                var middle = date.substring(2 + inc, 4 + inc);
                var first = date.substring(0, 2 + inc);
                date = first + separator + middle + separator + last;
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
            onOpenDateTime = dateStr;
        },
        onChange: function(selectedDates, dateStr, instance) {
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
                    $(instance.input).attr('value', dateStr);
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
});