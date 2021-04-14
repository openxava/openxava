// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return; 
	$('.xava_date > input').first().change(function() {
		var dateFormat = $(this).parent().data("dateFormat");
  		var date = $(this).val();
		if (date === "") return;
		var separator = dateFormat.substr(-2, 1);
		var idx = date.lastIndexOf(separator);
		if (idx < 0) {
			if (date.length % 2 != 0) date = " " + date;
			var year = date.substring(4);
			var middle = date.substring(2, 4);
			var first = date.substring(0, 2);
			date = first + separator + middle + separator + year;			
		}	
		idx = date.lastIndexOf(separator);
		if (dateFormat.substr(-1) === "Y" && date.length - idx < 4) {
  			var dateNoYear = date.substring(0, idx);
  			var year = date.substring(idx + 1);
  			var prefix = year > 50?"19":"20";
  			date = dateNoYear + separator + prefix + year;	  			 
  		}	
  		$(this).val(date);
	});
	$('.flatpickr-calendar').remove();
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,  
	    wrap: true,
	    locale: openxava.language, 
	    onChange: function(selectedDates, dateStr, instance) {
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

