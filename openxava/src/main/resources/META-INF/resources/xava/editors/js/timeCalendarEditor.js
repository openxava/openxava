// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.addEditorInitFunction(function() {
	openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");
    if (openxava.browser.htmlUnit) return;

    var onOpenDateTime;
    var invalid = false;
    var invalidDate;
    var onChangeChecked = false;
	
	//for fix chinese trad timedate
	var inputElementList = $('.xava_time > input').toArray();
	var inputValueList = [];
	$('.xava_time > input').each(function() {
		var inputValue = $(this).val();
		inputValueList.push(inputValue);
	});
	var isZh = false;

    $('.flatpickr-calendar').remove();
	
	$('.xava_time').flatpickr({
        allowInput: true,
        clickOpens: false,
        wrap: true,
        locale: openxava.language,
		enableTime: true,
		noCalendar: true,
		
        onOpen: function(selectedDates, dateStr, instance) {
            onOpenDateTime = dateStr;
			if (isZh2 && dateStr.includes('PM') && instance.amPM.innerHTML === 'AM'){
					instance.amPM.innerHTML = 'PM';
			} 
        },
		onReady: function(selectedDates, dateStr, instance) {
			if (openxava.language === 'zh'){
				isZh2 = true;
				for (var i = 0; i < inputElementList.length; i++) {
					if (instance.input === inputElementList[i]) instance.input.value = inputValueList[i];
				}
			}
		}
    });
    
});