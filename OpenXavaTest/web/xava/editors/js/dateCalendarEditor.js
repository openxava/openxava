/* tmp */
openxava.addEditorInitFunction(function() {
	// TMP ME QUEDÉ POR AQUÍ: EL /OpenXavaTest NO SE PUEDE QUEDAR
	$.getScript("/OpenXavaTest/xava/editors/js/lang/ru.js").done(function( script, textStatus ) {
	    console.log( "DONE.1: textStatus: " + textStatus );
	  })
	  .fail(function( jqxhr, settings, exception ) {
	    console.log( "FAILED.1");
	});
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,  
	    wrap: true,
	    locale: "ru", 
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

