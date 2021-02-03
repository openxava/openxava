/* tmp */

// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
	$('.xava_date > input').first().change(function() {
		var dateFormat = $(this).parent().data("dateFormat");
		console.log("[dateCalendarEditor.change] dateFormat=" + dateFormat);
		if( dateFormat.substr(-1) === "Y" ) {
			var date = $(this).val();
			console.log("[dateCalendarEditor.change] date=" + date); // tmp
			var separator = dateFormat.substr(-2);
			console.log("[dateCalendarEditor.change] separator=" + separator); // tmp
			// TMP ME QUEDÉ POR AQUÍ. INTENTANDO QUE RELLENO EL 20 DEL SIGLO
			/*
			int idx = date.lastIndexOf(separator); 
			console.log("[dateCalendarEditor.change] idx=" + idx); // tmp
			console.log("[dateCalendarEditor.change] date.length()=" + date.length()); // tmp
	  		*/
	  		$(this).val("01/01/2017");
  		}
	});
	$('.flatpickr-calendar').remove();
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,  
	    wrap: true,
	    allowInvalidPreload: true,
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

