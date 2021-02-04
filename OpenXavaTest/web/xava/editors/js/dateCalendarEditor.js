/* tmp */

// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
	console.log("[dateCalendarEditor.addEditorInitFunction] openxava.language=" + openxava.language); // tmp
	$('.xava_date > input').first().change(function() {
		// TMP Falta escribir prueba manual y mover a OpenXava  
		var dateFormat = $(this).parent().data("dateFormat");
		console.log("[dateCalendarEditor.change] dateFormat=" + dateFormat);
		if( dateFormat.substr(-1) === "Y" ) {
			var date = $(this).val();
			console.log("[dateCalendarEditor.change] date=" + date); // tmp
			var separator = dateFormat.substr(-2, 1);
			var idx = date.lastIndexOf(separator); 
			if (date.length - idx < 4) {
	  			var dateNoYear = date.substring(0, idx);
	  			var year = date.substring(idx + 1);
	  			var newDate = dateNoYear + separator + "20" + year;
	  			console.log("[dateCalendarEditor.change] newDate=" + newDate); // tmp
	  			$(this).val(newDate);	  			 
	  		}
  		}
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

