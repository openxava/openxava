// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
    
    if (openxava.browser.htmlUnit) return;
    
    var withEnter = false;
    var enterDate;
	
	$('.xava_date > input').keydown(function(event) {
	    var keycode = event.keyCode || event.which;	
	    if(keycode == 13) {
	        enterDate = $(this).val();
			if ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && (enterDate.length > 9)){
				console.log("false");
				withEnter = false;
			}else{
				console.log("true");
				withEnter = true;
			}
	    }
	});
	$('.xava_date > input').change(function() { 
		console.log(".xava_date > input" + enterDate);
		var dateFormat = $(this).parent().data("dateFormat");
		var date = withEnter?enterDate:$(this).val();
		if (date === "") return;
		date = date.trim(); 
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
  		$(this).val(date);
		enterDate = undefined;
		withEnter = false;
	});
	$('.flatpickr-calendar').remove();
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,  
	    wrap: true,
	    locale: openxava.language, 
	    onChange: function(selectedDates, dateStr, instance) {
			console.log("onChange dateStr " + dateStr);
            console.log("onChange $(instance.input).attr('value') " + $(instance.input).attr('value'));
            console.log("onChange enterDate " + enterDate);
        	if (!$(instance.input).data("datePopupJustClosed") || dateStr === $(instance.input).attr('value')) {
        		$(instance.input).data("changedCancelled", true);
        	}
        	$(instance.input).attr('value', dateStr);
        	$(instance.input).removeData("datePopupJustClosed");
    	},
    	onClose: function(selectedDates, dateStr, instance) {
	    	$(instance.input).data("datePopupJustClosed", true);
    	},    	 
	});
});
